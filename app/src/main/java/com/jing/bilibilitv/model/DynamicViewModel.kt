package com.jing.bilibilitv.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.jing.bilibilitv.http.api.BilibiliApi
import com.jing.bilibilitv.http.data.DynamicItem
import com.jing.bilibilitv.http.data.DynamicResponse
import com.jing.bilibilitv.http.data.DynamicType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DynamicViewModel @Inject constructor(
    private val bilibiliApi: BilibiliApi
) : ViewModel() {

    private var nextOffset: String? = null

    val pager = createPager()
        .flow
        .cachedIn(viewModelScope)

    private fun createPager() = Pager(
        config = PagingConfig(pageSize = 15, prefetchDistance = 20),
    ) {
        DynamicPagingSource(this)
    }


    private suspend fun loadPage(page: Int): DynamicResponse {
        if (page == 1) {
            nextOffset = null
        }
        val resp =
            bilibiliApi.getDynamicList(page, nextOffset)
        val data = resp.data ?: throw RuntimeException("数据为空")
        nextOffset = data.offset
        return data
    }

    private class DynamicPagingSource(
        private val viewModel: DynamicViewModel
    ) :
        PagingSource<Int, DynamicItem>() {

        override fun getRefreshKey(state: PagingState<Int, DynamicItem>): Int? {
            return null
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DynamicItem> {
            val page = params.key ?: 1
            return try {
                val data = viewModel.loadPage(page)
                LoadResult.Page(
                    data.items,
                    prevKey = null,
                    nextKey = if (data.hasMore) page + 1 else null
                )
            } catch (e: Exception) {
                return LoadResult.Error(e)
            }
        }


    }

}