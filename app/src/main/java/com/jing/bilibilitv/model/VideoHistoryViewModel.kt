package com.jing.bilibilitv.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.jing.bilibilitv.http.api.BilibiliApi
import com.jing.bilibilitv.http.data.HistoryCursor
import com.jing.bilibilitv.http.data.HistoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoHistoryViewModel @Inject constructor(private val bilibiliApi: BilibiliApi) :
    ViewModel() {

    private var lastCursor: HistoryCursor? = null


    val pager = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 40)
    ) {
        createPagingSource()
    }
        .flow
        .cachedIn(viewModelScope)

    private fun createPagingSource(): PagingSource<Int, HistoryItem> =
        object : PagingSource<Int, HistoryItem>() {
            override fun getRefreshKey(state: PagingState<Int, HistoryItem>): Int? {
                return null
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HistoryItem> {
                val actualCursor = if (params.key == null) null else {
                    lastCursor
                }
                val page = params.key ?: 1
                try {
                    val history = bilibiliApi.getHistory(
                        max = actualCursor?.max ?: 0,
                        viewAt = actualCursor?.viewAt ?: 0,
                        business = "archive",
                        type = "archive"
                    ).data ?: throw RuntimeException("查询历史记录失败,数据为null")
                    lastCursor = history.cursor
                    return LoadResult.Page(
                        data = history.list,
                        prevKey = null,
                        nextKey = if (history.list.isEmpty()) null else page + 1
                    )
                } catch (e: Exception) {
                    return LoadResult.Error(e)
                }
            }

        }

}