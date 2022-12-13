package com.jing.bilibilitv.live

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jing.bilibilitv.http.api.LiveApi
import com.jing.bilibilitv.http.data.FollowedLiveRoom
import com.jing.bilibilitv.http.data.LiveRoomArea
import com.jing.bilibilitv.resource.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BilibiliLiveViewModel @Inject constructor(
    private val liveApi: LiveApi
) : ViewModel() {

    val followedLiveRoomPager = Pager(
        config = PagingConfig(pageSize = 10, prefetchDistance = 3),
    ) {
        FollowedLiveRoomPagingSource()
    }.flow

    private val _areaList: MutableStateFlow<Resource<List<LiveRoomArea>>> =
        MutableStateFlow(Resource.Loading())

    val areaList: StateFlow<Resource<List<LiveRoomArea>>>
        get() = _areaList

    init {
        loadLiveRoomArea()
    }

    private fun loadLiveRoomArea() {
        viewModelScope.launch(Dispatchers.IO) {
            _areaList.emit(Resource.Loading())
            try {
                val areas = liveApi.liveRoomArea().data ?: emptyList()
                _areaList.emit(Resource.Success(areas))
            } catch (e: Exception) {
                _areaList.emit(Resource.Error("加载直播分区失败:${e.message}", e))
            }
        }

    }

    private inner class FollowedLiveRoomPagingSource : PagingSource<Int, FollowedLiveRoom>() {

        private val pageSize = 10

        override fun getRefreshKey(state: PagingState<Int, FollowedLiveRoom>): Int? {
            return null
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FollowedLiveRoom> {
            val page = params.key ?: 1
            return try {
                val resp = liveApi.queryFollowedLiveRoom(page, pageSize).data!!
                val roomList = resp.rooms ?: emptyList()
                val hasNext = resp.count > page * pageSize
                LoadResult.Page(
                    data = roomList,
                    prevKey = if (page > 1) page - 1 else null,
                    nextKey = if (hasNext) page + 1 else null
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

    }
}