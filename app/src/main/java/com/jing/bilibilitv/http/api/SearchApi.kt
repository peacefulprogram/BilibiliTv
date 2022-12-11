package com.jing.bilibilitv.http.api

import com.jing.bilibilitv.http.data.HotSearchResponse
import com.jing.bilibilitv.http.data.KeywordSuggestResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    /**
     * 查询搜索建议
     * @param 关键词
     *
     * @return key为索引
     */
    @GET("/main/suggest")
    suspend fun getKeywordSuggest(
        @Query("term") term: String
    ): Map<Int, KeywordSuggestResponse>

    @GET("/main/hotword")
    suspend fun getHotSearchKeyword(): HotSearchResponse


    companion object {
        const val BASE_URL = "https://s.search.bilibili.com"
    }
}