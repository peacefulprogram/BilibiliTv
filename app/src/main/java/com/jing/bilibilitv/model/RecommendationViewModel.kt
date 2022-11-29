package com.jing.bilibilitv.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jing.bilibilitv.http.api.BilibiliApi
import com.jing.bilibilitv.http.data.VideoInfo
import com.jing.bilibilitv.resource.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val bilibiliApi: BilibiliApi
) : ViewModel() {

    private val _recommendationState =
        MutableStateFlow<Resource<List<VideoInfo>>>(Resource.Loading())

    val recommendationState
        get() = _recommendationState

    init {
        loadRecommendation()
    }

    fun loadRecommendation(pageSize: Int = 12) {
        viewModelScope.launch(Dispatchers.IO) {
            _recommendationState.emit(Resource.Loading())
            try {
                val videoList =
                    bilibiliApi.getRecommendation(pageSize = pageSize).data?.item?.takeIf { it.isNotEmpty() }
                        ?: throw RuntimeException("未查到数据")
                _recommendationState.emit(Resource.Success(videoList))
            } catch (e: Exception) {
                _recommendationState.emit(Resource.Error(exception = e))
            }
        }
    }

}