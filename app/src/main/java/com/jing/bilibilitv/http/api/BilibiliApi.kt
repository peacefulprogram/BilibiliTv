package com.jing.bilibilitv.http.api

import com.jing.bilibilitv.http.data.*
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BilibiliApi {

    @GET("/x/web-interface/nav")
    suspend fun getLoginUserInfo(): CommonDataResponse<UserInfo>

    @GET("/x/polymer/web-dynamic/v1/feed/all")
    suspend fun getDynamicList(
        @Query("page") page: Int,
        @Query("offset") offset: String? = null,
        @Query("type") type: String = "video",
        @Query("timezone_offset") timezoneOffset: Int = -480
    ): CommonDataResponse<DynamicResponse>

    /**
     * 获取视频播放地址
     * @param bvid
     * @param cid
     * @param qn 视频清晰度
     * @param fnval 流格式,0-flv,1-mp4,16-dash,64-杜比&qn=125,128-4k&qn=120
     */
    @GET("/x/player/playurl")
    suspend fun getVideoPlayerUrl(
        @Query("bvid") bvid: String,
        @Query("cid") cid: String,
        @Query("qn") qn: Int,
        @Query("fnval") fnval: Int
    )

    /**
     * 获取推荐
     * @param refreshType 相关性,值越大推荐内容越相关
     * @param version 0-旧web端, 1-新web端
     * @param pageSize 数量
     */
    @GET("/x/web-interface/index/top/rcmd")
    suspend fun getRecommendation(
        @Query("refresh_type") refreshType: Int = 3,
        @Query("version") version: Int = 1,
        @Query("ps") pageSize: Int = 12,
        @Query("refresh_idx") refreshIdx: Int = 1,
        @Query("fresh_idx_1h") refreshIdx1h: Int = 1
    ): CommonDataResponse<RecommendationResponse>

    /**
     * 获取视频详情
     */
    @GET("/x/web-interface/view/detail")
    suspend fun getVideoDetail(
        @Query("bvid") bvid: String?,
        @Query("aid") aid: String? = null
    ): CommonDataResponse<VideoDetailResponse>

    /**
     * 获取视频的相关推荐视频
     * aid和bvid任意传一个
     */
    @GET("/x/web-interface/archive/related")
    suspend fun getRecommendationOfVideo(
        @Query("aid") aid: Long?,
        @Query("bvid") bvid: String?
    ): CommonDataResponse<Any>

    @GET("/x/player/playurl")
    suspend fun getPlayUrl(
        @Query("avid") aid: String?,
        @Query("bvid") bvid: String?,
        @Query("cid") cid: Long,
        @Query("qn") qn: Int = 112,
        @Query("fnval") fnval: Int = 80,
        @Query("fnver") fnver: Int = 0,
        @Query("fourk") fourk: Int = 1
    ): CommonDataResponse<VideoUrlResponse>

    /**
     * 上传视频播放纪录
     */
    @POST("/x/v2/history/report")
    @FormUrlEncoded
    suspend fun updateVideoHistory(
        @Field("aid") aid: String,
        @Field("cid") cid: Long,
        @Field("csrf") csrf: String,
        @Field("progress") progress: Long,
        @Field("platform") platform: String = "android"
    ): CommonDataResponse<Unit>

    companion object {
        const val BASE_URL = "https://api.bilibili.com"
    }
}