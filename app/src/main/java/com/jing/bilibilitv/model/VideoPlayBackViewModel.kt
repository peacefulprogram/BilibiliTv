package com.jing.bilibilitv.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jing.bilibilitv.http.api.BilibiliApi
import com.jing.bilibilitv.http.cookie.BilibiliCookieName
import com.jing.bilibilitv.http.data.VideoSnapshotResponse
import com.jing.bilibilitv.http.data.VideoUrlResponse
import com.jing.bilibilitv.resource.Resource
import com.jing.bilibilitv.room.dao.BlCookieDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class VideoPlayBackViewModel @Inject constructor(
    private val bilibiliApi: BilibiliApi,
    private val blCookieDao: BlCookieDao
) : ViewModel() {

    private val TAG = VideoPlayBackViewModel::class.java.simpleName

    init {
        Log.d(TAG, "view model created")
    }

    private var avid: String? = null

    private var cid: Long? = null

    @Volatile
    private var csrfToken: String? = null


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
        viewModelScope.launch(Dispatchers.IO) {
            blCookieDao.findByCookieName(BilibiliCookieName.BILI_JCT.cookieName)?.let {
                csrfToken = it.cookieValue
            }
        }
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