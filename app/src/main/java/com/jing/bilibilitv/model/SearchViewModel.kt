package com.jing.bilibilitv.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jing.bilibilitv.http.api.BilibiliApi
import com.jing.bilibilitv.http.api.SearchApi
import com.jing.bilibilitv.http.data.BilibiliSearchResult
import com.jing.bilibilitv.http.data.HotSearchKeyword
import com.jing.bilibilitv.http.data.KeywordSuggestResponse
import com.jing.bilibilitv.resource.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchApi: SearchApi,
    private val bilibiliApi: BilibiliApi,
    private val okHttpClient: OkHttpClient
) : ViewModel() {

    private val _currentSearchType = MutableStateFlow("video")

    private val _keyword = MutableStateFlow("")

    init {
        // 获取cookie
        viewModelScope.launch(Dispatchers.IO) {
            okHttpClient.newCall(
                Request.Builder()
                    .url("https://www.bilibili.com")
                    .get()
                    .build()
            )
        }
    }

    private val _keywordSuggest =
        MutableStateFlow<Resource<List<KeywordSuggestResponse>>>(Resource.Loading())
    val keywordSuggest: StateFlow<Resource<List<KeywordSuggestResponse>>>
        get() = _keywordSuggest

    private val _hotSearchKeyword =
        MutableStateFlow<Resource<List<HotSearchKeyword>>>(Resource.Loading())
    val hotSearchKeyword: MutableStateFlow<Resource<List<HotSearchKeyword>>>
        get() = _hotSearchKeyword

    val pager = Pager(
        PagingConfig(pageSize = 20)
    ) {
        BilibiliSearchPagingSource()
    }.flow


    fun keywordInputChange(input: String) {
        if (input.isEmpty()) {
            viewModelScope.launch {
                _keywordSuggest.emit(Resource.Success(emptyList()))
            }
        } else {
            loadSuggestKeyword(input)
        }
    }

    private fun loadSuggestKeyword(input: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _keywordSuggest.emit(Resource.Loading())
            try {
                val suggests = searchApi.getKeywordSuggest(input).let { map ->
                    map.keys.toList().sorted().map { map[it]!! }
                }
                _keywordSuggest.emit(Resource.Success(suggests))
            } catch (e: Exception) {
                _keywordSuggest.emit(Resource.Error("加载搜索建议失败:${e.message}", e))
            }
        }

    }

    fun loadHotSearchKeywords() {
        viewModelScope.launch(Dispatchers.IO) {
            _hotSearchKeyword.emit(Resource.Loading())
            try {
                _hotSearchKeyword.emit(Resource.Success(searchApi.getHotSearchKeyword().list))
            } catch (ex: Exception) {
                _hotSearchKeyword.emit(Resource.Error("加载搜索推荐失败:${ex.message}", ex))
            }
        }

    }

    fun setSearchType(searchTypeVideo: String): Boolean {
        if (searchTypeVideo == _currentSearchType.value) {
            return false
        }
        viewModelScope.launch {
            _currentSearchType.emit(searchTypeVideo)
        }
        return true
    }

    fun changeKeyword(keyword: String) {
        viewModelScope.launch {
            _keyword.emit(keyword)
        }
    }

    inner class BilibiliSearchPagingSource : PagingSource<Int, BilibiliSearchResult>() {
        override fun getRefreshKey(state: PagingState<Int, BilibiliSearchResult>): Int? {
            return null
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BilibiliSearchResult> {
            val page = params.key ?: 1
            val currentType = _currentSearchType.value
            val keyword = _keyword.value
            if (keyword.isEmpty()) {
                return LoadResult.Page(
                    data = emptyList(),
                    nextKey = null,
                    prevKey = null
                )
            }
            return try {
                val searchResult = when (currentType) {
                    "video" -> bilibiliApi.searchVideo(keyword, page).data?.apply {
                        result.forEach { row ->
                            row.title = row.title.replace(Regex("</?.+?/?>"), "")
                        }
                    }
                    "bili_user" -> bilibiliApi.searchUser(keyword, page).data
                    "live_room" -> bilibiliApi.searchLiveRoom(keyword, page).data?.apply {
                        result.forEach { row ->
                            row.title = row.title.replace(Regex("</?.+?/?>"), "")
                        }
                    }
                    else -> throw IllegalStateException("不支持到搜索类型$currentType")
                } ?: throw RuntimeException("请求数据为空")
                LoadResult.Page(
                    data = searchResult.result,
                    prevKey = null,
                    nextKey = if (page < searchResult.numPages) page + 1 else null
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }


    }
}