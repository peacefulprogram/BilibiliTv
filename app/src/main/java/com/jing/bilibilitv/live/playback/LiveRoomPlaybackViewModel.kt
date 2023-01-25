package com.jing.bilibilitv.live.playback

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.jing.bilibilitv.ext.decompressBrotli
import com.jing.bilibilitv.ext.unzip
import com.jing.bilibilitv.http.api.LiveApi
import com.jing.bilibilitv.http.data.LiveRoomDetail
import com.jing.bilibilitv.http.data.LiveRoomWsResponse
import com.jing.bilibilitv.http.data.LiveStreamUrlResponse
import com.jing.bilibilitv.resource.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import okhttp3.*
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.nio.ByteBuffer

class LiveRoomPlaybackViewModel(
    private val roomId: Long,
    private val liveApi: LiveApi,
    private val okHttpClient: OkHttpClient
) : ViewModel() {

    private val TAG = LiveRoomPlaybackViewModel::class.java.simpleName

    private var webSocket: WebSocket? = null

    private val _webSocketServer: MutableStateFlow<Resource<LiveRoomWsResponse>> =
        MutableStateFlow(Resource.Loading())

    private val _liveStreamUrl: MutableStateFlow<Resource<LiveStreamUrlResponse>> =
        MutableStateFlow(Resource.Loading())

    val liveStreamUrl: StateFlow<Resource<LiveStreamUrlResponse>>
        get() = _liveStreamUrl

    private val danmuChannel: Channel<LiveRoomDanmu> =
        Channel(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val danmuFlow: Flow<LiveRoomDanmu>
        get() = danmuChannel.receiveAsFlow()

    private val danmuEnabled = MutableStateFlow(true)

    private var wsHeatBeatJob: Job? = null

    private val _liveRoomDetail: MutableStateFlow<Resource<LiveRoomDetail>> =
        MutableStateFlow(Resource.Loading())

    val liveRoomDetail: StateFlow<Resource<LiveRoomDetail>>
        get() = _liveRoomDetail

    private val _popularityCount = MutableStateFlow(-1)

    init {

        queryWsServer()
        queryRoomDetail()
        queryLiveStreamUrl()

        viewModelScope.launch {
            _webSocketServer.collectLatest {
                if (it is Resource.Success) {
                    initWebSocket(it.data)
                }
            }
        }
    }

    private fun queryLiveStreamUrl() {
        viewModelScope.launch(Dispatchers.IO) {
            _liveStreamUrl.emit(Resource.Loading())
            try {
                _liveStreamUrl.emit(
                    Resource.Success(
                        liveApi.queryLiveStreamUrl(
                            roomId = roomId,
                            platform = "web"
                        ).data!!
                    )
                )
            } catch (e: Exception) {
                _liveStreamUrl.emit(Resource.Error("加载视频流失败:${e.message}", e))
            }
        }

    }

    private fun queryRoomDetail() {
        viewModelScope.launch(Dispatchers.IO) {
            _liveRoomDetail.emit(Resource.Loading())
            try {
                _liveRoomDetail.emit(Resource.Success(liveApi.queryLiveRoomDetail(roomId).data!!))
            } catch (e: Exception) {
                _liveRoomDetail.emit(Resource.Error("查询直播间详情失败:${e.message}", e))
            }
        }
    }

    private fun queryWsServer() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _webSocketServer.emit(Resource.Success(liveApi.queryLiveRoomWsServer(roomId).data!!))
            } catch (e: Exception) {
                _webSocketServer.emit(Resource.Error("加载弹幕服务器地址失败:${e.message}", e))
            }
        }
    }


    private fun initWebSocket(server: LiveRoomWsResponse) {
        stopWebSocketConnection()
        if (server.hostList.isEmpty()) {
            return
        }
        val host = server.hostList[0]
        webSocket = okHttpClient.newWebSocket(
            Request.Builder().url("wss://${host.host}:${host.wssPort}/sub").build(),
            LiveRoomWebSocketListener(server.token)
        )
    }

    fun changePopularityCount(count: Int) {
        viewModelScope.launch {
            _popularityCount.emit(count)
        }
    }

    fun stopWebSocketConnection() {
        webSocket?.cancel()
        wsHeatBeatJob?.cancel()
        webSocket = null
        wsHeatBeatJob = null
    }

    fun startWebSocketConnection() {
        if (webSocket != null) {
            return
        }
        val server = _webSocketServer.value
        if (server is Resource.Success) {
            initWebSocket(server.data)
        }
    }

    private fun startSendHeartbeat() {
        wsHeatBeatJob?.cancel()
        wsHeatBeatJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                webSocket?.send(encodeWsPacket("hb", PROTOCOL_JSON, OPERATION_HEARTBEAT))
                delay(30000L)
            }
        }
    }


    private inner class LiveRoomWebSocketListener(private val token: String) : WebSocketListener() {

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "onFailure: ", t)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            try {
                val byteBuffer = bytes.asByteBuffer()
                val packetLength = byteBuffer.getInt(0)
                val headLength = byteBuffer.getShort(4)
                val protoVersion = byteBuffer.getShort(6)
                val operationType = byteBuffer.getInt(8)
                val sequence = byteBuffer.getInt(12)
                if (operationType == OPERATION_AUTH_REPLY) {
                    startSendHeartbeat()
                    return
                }
                when (protoVersion) {
                    PROTOCOL_JSON -> {
                        if (operationType == OPERATION_CMD && danmuEnabled.value) {
                            byteBuffer.position(16)
                            val content = byteBuffer.slice().toByteString().utf8()
                            processCmdMessage(content)
                        } else if (operationType == OPERATION_HEARTBEAT_REPLY) {
                            changePopularityCount(byteBuffer.getInt(16))
                        }
                    }
                    PROTOCOL_ZIP -> if (danmuEnabled.value) {
                        byteBuffer.position(16)
                        val contentBytes = ByteArray(packetLength - 16)
                        byteBuffer.get(contentBytes)
                        val content = contentBytes.unzip().toByteString().utf8()
                        processCmdMessage(content)
                    }
                    PROTOCOL_BROTLI -> if (danmuEnabled.value) {
                        byteBuffer.position(16)
                        val contentBytes = ByteArray(packetLength - 16)
                        byteBuffer.get(contentBytes)
                        val bf = ByteBuffer.wrap(contentBytes.decompressBrotli())
                        val length = bf.getInt(0)
                        val decompressedContent = ByteArray(length - 16)
                        bf.position(16)
                        bf.get(decompressedContent)
                        val content = decompressedContent.toByteString().utf8()
                        processCmdMessage(content)
                    }
                    else -> {
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "message handle error: ", e)
            }
        }

        fun processCmdMessage(json: String) {
            val map = gson.fromJson(json, Map::class.java)
            when (map["cmd"] ?: "") {
                MESSAGE_CMD_DANMU -> if (danmuEnabled.value) {
                    val info = map["info"]
                    info ?: return
                    val dmInfo = info as List<Any?>
                    val first = dmInfo[0] as List<Any?>
                    val textSize = (first[2] as Number).toFloat()
                    val textColor = (0xff000000).toInt() or (first[3] as Number).toInt()
                    val textShadowTransparent = "true" == first[11]
                    val text = dmInfo[1] as String
                    viewModelScope.launch(Dispatchers.Default) {
                        danmuChannel.trySend(
                            LiveRoomDanmu(
                                text = text,
                                fontSize = textSize,
                                color = textColor,
                                transparentShadow = textShadowTransparent
                            )
                        )
                    }
                }
                else -> {}
            }

        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            val packet = WsAuthPacket(
                roomId = roomId,
                key = token
            )
            webSocket.send(encodeWsPacket(gson.toJson(packet), PROTOCOL_JSON, OPERATION_AUTH))
        }

    }

    private data class WsAuthPacket(
        @SerializedName("roomid")
        val roomId: Long,
        @SerializedName("key")
        val key: String,
        @SerializedName("protover")
        val protoVersion: Int = 3,
        @SerializedName("uid")
        val uid: Long = 0L
    )

    companion object {

        private const val PROTOCOL_JSON: Short = 0
        private const val PROTOCOL_ZIP: Short = 2
        private const val PROTOCOL_BROTLI: Short = 3


        private const val OPERATION_HEARTBEAT = 2
        private const val OPERATION_HEARTBEAT_REPLY = 3
        private const val OPERATION_CMD = 5
        private const val OPERATION_AUTH = 7
        private const val OPERATION_AUTH_REPLY = 8

        private const val MESSAGE_CMD_DANMU = "DANMU_MSG"

        private val gson = Gson()


        fun encodeWsPacket(body: String, protoVersion: Short, operation: Int): ByteString {
            val bytes = body.toByteArray(Charsets.UTF_8)
            val packet = ByteBuffer.allocate(16 + bytes.size)
            packet.putInt(16 + bytes.size)
            packet.putShort(16.toShort())
            packet.putShort(protoVersion)
            packet.putInt(operation)
            packet.putInt(1)
            packet.put(bytes)
            packet.compact()
            return packet.toByteString()

        }

        fun createViewModelFactory(roomId: Long, liveApi: LiveApi, okHttpClient: OkHttpClient) =
            viewModelFactory {
                initializer {
                    LiveRoomPlaybackViewModel(roomId, liveApi, okHttpClient)
                }
            }
    }
}

data class LiveRoomDanmu(
    val text: String,
    val fontSize: Float,
    val color: Int,
    val transparentShadow: Boolean
)