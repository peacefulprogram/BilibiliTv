package com.jing.bilibilitv.live

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jing.bilibilitv.http.api.LiveApi
import com.jing.bilibilitv.http.data.LiveRoomOfArea

class LiveRoomOfAreaViewModel(
    private val parentAreaId: String,
    private val areaId: String,
    private val liveApi: LiveApi
) : ViewModel() {


    private val TAG = LiveRoomOfAreaViewModel::class.java.simpleName

    val pager = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 2
        )
    ) {
        LiveRoomPagingSource()
    }.flow


    private inner class LiveRoomPagingSource : PagingSource<Int, LiveRoomOfArea>() {
        override fun getRefreshKey(state: PagingState<Int, LiveRoomOfArea>): Int? {
            return null
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveRoomOfArea> {
            val page = params.key ?: 1
            return try {
                val resp = liveApi.queryLiveRoomByArea(
                    parentAreaId = parentAreaId,
                    areaId = areaId,
                    sortType = "online",
                    page = page,
                ).data!!
                return LoadResult.Page(
                    data = resp.list ?: emptyList(),
                    prevKey = if (page > 1) page - 1 else null,
                    nextKey = if (resp.hasMore == 1) page + 1 else null
                )
            } catch (e: Exception) {
                Log.e(TAG, "load: ", e)
                LoadResult.Error(e)
            }
        }

    }

    companion object {
        fun createViewModelFactory(
            parentAreaId: String,
            areaId: String,
            liveApi: LiveApi
        ): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer { LiveRoomOfAreaViewModel(parentAreaId, areaId, liveApi) }
            }
        }
    }

}