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

    private val _currentQnState = MutableStateFlow(112) // 默认1080 高码率

    val currentQn: Int
        get() = _currentQnState.value

    private val _cidState: MutableStateFlow<Long> = MutableStateFlow(-1)

    val currentCid: Long
        get() = _cidState.value

    var lastPlayTime: Long = 0

    private var _duration: Long = 0

    val duration: Long
        get() = _duration

    private var _qualityList = emptyList<Pair<Int, String>>()

    val qualityList: List<Pair<Int, String>>
        get() = _qualityList

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

    private val _selectedVideoAndAudio: MutableStateFlow<Resource<VideoUrlAndQuality>> =
        MutableStateFlow(Resource.Loading())
    val selectedVideoAndAudio: StateFlow<Resource<VideoUrlAndQuality>>
        get() = _selectedVideoAndAudio

    val playerDelegate = VideoPlayerDelegate(viewModelScope, this::uploadVideoProgress)

    init {
        viewModelScope.launch {
            _cidState.collectLatest {
                onCidChange(it)
            }
        }

        viewModelScope.launch {
            _currentQnState.collectLatest { qn ->
                val selectedVideo = _selectedVideoAndAudio.value
                if (selectedVideo is Resource.Success && selectedVideo.data.qn == qn) {
                    return@collectLatest
                }
                onVideoUrlOrQnChange(_videoUrlState.value, qn)
            }
        }

        viewModelScope.launch {
            _videoUrlState.collectLatest { resp ->
                if (resp is Resource.Success) {
                    _qualityList =
                        resp.data.supportFormats.map { Pair(it.quality, it.newDescription) }
                }
                onVideoUrlOrQnChange(
                    resp,
                    _currentQnState.value
                )
            }
        }
    }

    private suspend fun onVideoUrlOrQnChange(urlResource: Resource<VideoUrlResponse>, qn: Int) {
        try {
            if (urlResource is Resource.Success) {
                val video = urlResource.data.dash!!.video.find { it.id <= qn }
                    ?: throw RuntimeException("找不到该清晰度的视频")
                val audioUrl = urlResource.data.dash.audio.minByOrNull { it.bandwidth }?.baseUrl
                    ?: throw RuntimeException("无音频资源")
                _selectedVideoAndAudio.emit(
                    Resource.Success(
                        VideoUrlAndQuality(
                            qn = video.id,
                            videoUrl = video.baseUrl,
                            audioUrl = audioUrl
                        )
                    )
                )
                if (currentQn != video.id) {
                    _currentQnState.emit(video.id)
                }
            }
            if (urlResource is Resource.Error) {
                throw RuntimeException(urlResource.message, urlResource.exception)
            }
        } catch (e: Exception) {
            _selectedVideoAndAudio.emit(Resource.Error(e.message, exception = e))
        }
    }

    private fun onCidChange(cid: Long) {
        if (cid == -1L) {
            return
        }
        loadDanmaku(cid)
        loadVideoUrl(cid)
        loadSnapshot(avid, bvid, cid)
    }

    fun changePage(page: PageX) {
        viewModelScope.launch(Dispatchers.Default) {
            if (page.cid != _cidState.value) {
                _titleSate.emit(page.part)
                _cidState.emit(page.cid)
            }
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
                _videoUrlState.emit(Resource.Error("加载视频失败:${ex.message}", ex))
            }
        }
    }

    fun init(avid: String?, bvid: String?) {
        this.avid = avid
        this.bvid = bvid
        viewModelScope.launch(Dispatchers.IO) {
            bilibiliApi.getVideoDetail(bvid, avid).data?.view?.let { detail ->
                videoPages = detail.pages
                if (videoPages.size > 1) {
                    bilibiliApi.getLastPlayInfo(avid, bvid, detail.cid).data?.let { lastPlay ->
                        if (lastPlay.lastPlayCid > 0) {
                            _cidState.emit(lastPlay.lastPlayCid)
                            lastPlayTime = lastPlay.lastPlayTime
                            _titleSate.emit(videoPages.find { it.cid == lastPlay.lastPlayCid }!!.part)
                        } else {
                            _cidState.emit(videoPages[0].cid)
                            _titleSate.emit(videoPages[0].part)
                        }
                    }
                } else {
                    _cidState.emit(videoPages[0].cid)
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
                    DanmakuProto.DmWebViewReply.parseFrom(
                        bilibiliApi.getVideoDanmaku(
                            cid = cid,
                            avid = avid
                        ).bytes()
                    )
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
                _danmakuData.emit(Resource.Error(exception = e))
            }
        }
    }

    /**
     * 上传视频播放进度
     * @param progress 视频播放进度,毫秒
     */
    private suspend fun uploadVideoProgress(progress: Long) {
        if (avid == null || _cidState.value == -1L) {
            return
        }
        bilibiliApi.updateVideoHistory(
            aid = avid!!,
            cid = _cidState.value,
            csrf = GlobalState.csrfToken,
            progress = progress / 1000
        )
    }

    fun loadSnapshot(avid: String?, bvid: String?, cid: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _snapshotResponse.emit(Resource.Loading())
            try {
                bilibiliApi.getVideoSnapshot(aid = avid, bvid = bvid, cid = cid).data?.let { data ->
                    _snapshotResponse.emit(Resource.Success(data))
                }
            } catch (e: Exception) {
                _snapshotResponse.emit(Resource.Error(exception = e))
                Log.e(TAG, "loadSnapshot: load snapshot error", e)
            }
        }
    }

    fun changeQn(newQn: Int) {
        if (_currentQnState.value == newQn) {
            return
        }
        viewModelScope.launch {
            _currentQnState.emit(newQn)
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

data class VideoUrlAndQuality(
    val qn: Int,
    val videoUrl: String,
    val audioUrl: String?
)