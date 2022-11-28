package com.jing.bilibilitv.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jing.bilibilitv.GlobalState
import com.jing.bilibilitv.danmaku.proto.DanmakuProto
import com.jing.bilibilitv.http.api.BilibiliApi
import com.jing.bilibilitv.http.data.PageX
import com.jing.bilibilitv.http.data.VideoSnapshotResponse
import com.jing.bilibilitv.http.data.VideoUrlResponse
import com.jing.bilibilitv.resource.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class VideoPlayBackViewModel @Inject constructor(
    private val bilibiliApi: BilibiliApi
) : ViewModel() {

    private val TAG = VideoPlayBackViewModel::class.java.simpleName

    private var avid: String? = null

    private var bvid: String? = null

    private val cidState: MutableStateFlow<Long> = MutableStateFlow(-1)

    private val _titleSate: MutableStateFlow<String> = MutableStateFlow("")

    val titleState: StateFlow<String>
        get() = _titleSate

    private var loadDanmakuJob: Job? = null

    @Volatile
    private var videoPages: List<PageX> = emptyList()

    private var _danmakuData: MutableStateFlow<Resource<List<DanmakuProto.DanmakuElem>>> =
        MutableStateFlow(Resource.Loading())

    val danmakuData: StateFlow<Resource<List<DanmakuProto.DanmakuElem>>>
        get() = _danmakuData

    private var _snapshotResponse: MutableStateFlow<Resource<VideoSnapshotResponse>> =
        MutableStateFlow(Resource.Loading())

    val snapshotResponse: StateFlow<Resource<VideoSnapshotResponse>>
        get() = _snapshotResponse

    private val _videoUrlState: MutableStateFlow<Resource<VideoUrlResponse>> =
        MutableStateFlow(Resource.Loading())

    val playerDelegate = VideoPlayerDelegate(viewModelScope, this::uploadVideoProgress)

    val viewUrlState: StateFlow<Resource<VideoUrlResponse>>
        get() = _videoUrlState

    init {
        viewModelScope.launch {
            cidState.collectLatest {
                onCidChange(it)
            }
        }
    }

    private fun onCidChange(cid: Long) {
        loadDanmaku(cid)
        loadVideoUrl(cid)
    }

    private fun changePage(page: PageX) {
        viewModelScope.launch(Dispatchers.Default) {
            _titleSate.emit(page.part)
            cidState.emit(page.cid)
        }
    }

    private fun loadVideoUrl(cid: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _videoUrlState.emit(Resource.Loading())
            try {
                bilibiliApi.getPlayUrl(
                    aid = avid,
                    bvid = bvid,
                    cid = cid
                ).data?.let {
                    _videoUrlState.emit(Resource.Success(it))
                }
            } catch (ex: Exception) {
                _videoUrlState.emit(Resource.Error("加载视频失败:${ex.message}"))
            }
        }
    }

    fun init(avid: String?, bvid: String?) {
        this.avid = avid
        this.bvid = bvid
        loadSnapshot(avid, bvid)
        viewModelScope.launch(Dispatchers.IO) {
            bilibiliApi.getVideoDetail(bvid, avid).data?.view?.let { detail ->
                videoPages = detail.pages
                if (videoPages.size > 1) {
                    bilibiliApi.getLastPlayInfo(avid, bvid, detail.cid).data?.let { lastPlay ->
                        if (lastPlay.lastPlayCid > 0) {
                            cidState.emit(lastPlay.lastPlayCid)
                            _titleSate.emit(videoPages.find { it.cid == lastPlay.lastPlayCid }!!.part)
                        } else {
                            cidState.emit(videoPages[0].cid)
                            _titleSate.emit(videoPages[0].part)
                        }
                    }
                } else {
                    cidState.emit(videoPages[0].cid)
                    _titleSate.emit(videoPages[0].part)
                }
            }
        }
    }


    private fun loadDanmaku(cid: Long) {
        loadDanmakuJob?.cancel()
        loadDanmakuJob = viewModelScope.launch(Dispatchers.IO) {
            _danmakuData.emit(Resource.Loading())
            try {
                val reply =
                    DanmakuProto.DmWebViewReply.parseFrom(bilibiliApi.getVideoDanmaku(cid).bytes())
                if (reply.hasDmSge()) {
                    val list = (1..reply.dmSge.total).map { segmentIndex ->
                        async {
                            try {
                                DanmakuProto.DmSegMobileReply.parseFrom(
                                    bilibiliApi.getVideoDanmakuSegment(
                                        cid,
                                        segmentIndex
                                    ).bytes()
                                ).elemsList
                            } catch (e: Exception) {
                                if (e is CancellationException) {
                                    throw e
                                }
                                emptyList()
                            }
                        }
                    }.awaitAll()
                        .flatten()
                    _danmakuData.emit(Resource.Success(list))
                }
            } catch (e: Exception) {
                _danmakuData.emit(Resource.Error(e.message))
            }
        }
    }

    /**
     * 上传视频播放进度
     * @param progress 视频播放进度,毫秒
     */
    private suspend fun uploadVideoProgress(progress: Long) {
        if (avid == null || cidState.value == -1L) {
            return
        }
        bilibiliApi.updateVideoHistory(
            aid = avid!!,
            cid = cidState.value,
            csrf = GlobalState.csrfToken,
            progress = progress / 1000
        )
    }

    fun loadSnapshot(avid: String?, bvid: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            _snapshotResponse.emit(Resource.Loading())
            try {
                bilibiliApi.getVideoSnapshot(aid = avid, bvid = bvid).data?.let { data ->
                    _snapshotResponse.emit(Resource.Success(data))
                }
            } catch (e: Exception) {
                _snapshotResponse.emit(Resource.Error(e.message))
                Log.e(TAG, "loadSnapshot: load snapshot error", e)
            }
        }
    }


}

class VideoPlayerDelegate(
    private val scope: CoroutineScope,
    private val uploadProgress: suspend (Long) -> Unit = {}
) {

    var resumePosition: Long = -1L
    var progress: Long = 0

    private var uploadProgressJob: Job? = null

    var isPlaying = false

    fun updateProgress(progress: Long) {
        this.progress = progress
    }

    fun play() {
        isPlaying = true
        startListenVideoProgress()
    }

    fun pause() {
        isPlaying = false
        uploadProgressJob?.cancel()
        scope.launch(Dispatchers.Default) {
            uploadProgress(progress)
        }
    }


    private fun startListenVideoProgress(interval: Long = 5000) {
        uploadProgressJob?.cancel()
        uploadProgressJob = scope.launch(Dispatchers.Default) {
            while (isActive) {
                delay(interval)
                uploadProgress(progress)
            }
        }
    }

}