package com.jing.bilibilitv.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jing.bilibilitv.http.api.BilibiliApi
import com.jing.bilibilitv.http.cookie.BilibiliCookieName
import com.jing.bilibilitv.http.data.VideoUrlResponse
import com.jing.bilibilitv.resource.Resource
import com.jing.bilibilitv.room.dao.BlCookieDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class VideoPlayBackViewModel @Inject constructor(
    private val bilibiliApi: BilibiliApi,
    private val blCookieDao: BlCookieDao
) : ViewModel() {

    private var avid: String? = null

    private var cid: Long? = null

    private var csrfToken: String? = null


    private val _videoUrlState: MutableStateFlow<Resource<VideoUrlResponse>> =
        MutableStateFlow(Resource.Loading())

    private val _currentQnState: MutableStateFlow<Int> = MutableStateFlow(-1)

    val playerDelegate = VideoPlayerDelegate(viewModelScope, this::uploadVideoProgress)

    private val currentQnState: StateFlow<Int>
        get() = _currentQnState

    val viewUrlState: StateFlow<Resource<VideoUrlResponse>>
        get() = _videoUrlState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            blCookieDao.findByCookieName(BilibiliCookieName.BILI_JCT.cookieName)?.let {
                csrfToken = it.cookieValue
            }
        }
    }

    suspend fun changeCurrentQn(qn: Int) {
        _currentQnState.emit(qn)
    }

    fun loadVideoUrl(avid: String?, bvid: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val detailData = bilibiliApi.getVideoDetail(bvid, avid).data
                detailData?.view?.let { detail ->
                    this@VideoPlayBackViewModel.avid = detail.aid.toString()
                    this@VideoPlayBackViewModel.cid = detail.cid
                    bilibiliApi.getPlayUrl(
                        detail.aid.toString(),
                        detail.bvid,
                        detail.cid
                    ).data?.let {
                        _videoUrlState.emit(Resource.Success(it))
                    }
                }
            } catch (e: Exception) {
                _videoUrlState.emit(Resource.Error("加载链接失败:" + e.message))
            }
        }
    }

    /**
     * 上传视频播放进度
     * @param progress 视频播放进度,毫秒
     */
    private suspend fun uploadVideoProgress(progress: Long) {
        if (avid == null || cid == null || csrfToken == null) {
            return
        }
        bilibiliApi.updateVideoHistory(
            aid = avid!!,
            cid = cid!!,
            csrf = csrfToken!!,
            progress = progress / 1000
        )
    }


}

class VideoPlayerDelegate(
    private val scope: CoroutineScope,
    private val uploadProgress: suspend (Long) -> Unit = {}
) {

    var progress: Long = 0

    private var uploadProgressJob: Job? = null

    var isPlaying = false

    private fun runInScope(
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        func: suspend () -> Unit
    ) {
        scope.launch(dispatcher) {
            func()
        }
    }

    fun updateProgress(progress: Long) {
        this.progress = progress
    }

    fun play() {
        isPlaying = true
        startListenVideoProgress()
    }

    fun pause() {
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