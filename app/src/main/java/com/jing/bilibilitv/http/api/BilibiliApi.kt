package com.jing.bilibilitv.http.api

import com.jing.bilibilitv.http.data.*
import okhttp3.ResponseBody
import retrofit2.http.*

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
     * 获取上次播放的信息
     */
    @GET("x/player/v2")
    suspend fun getLastPlayInfo(
        @Query("aid") aid: String?,
        @Query("bvid") bvid: String?,
        @Query("cid") cid: Long
    ): CommonDataResponse<LastPlayResponse>

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
     * 上传视频播放记录
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

    /**
     * 获取播放历史记录
     *
     * @param max 第一页为0
     * @param viewAt 第一页为0
     * @param business archive:视频,live:直播
     * @param type 与business保持一致
     * @param pageSize 分页大小,默认20,最大30
     */
    @GET("/x/web-interface/history/cursor")
    suspend fun getHistory(
        @Query("max") max: Long,
        @Query("view_at") viewAt: Long,
        @Query("business") business: String,
        @Query("type") type: String? = null,
        @Query("ps") pageSize: Int = 20
    ): CommonDataResponse<HistoryResponse>

    /**
     * 获取视频分段快照
     */
    @GET("/x/player/videoshot")
    suspend fun getVideoSnapshot(
        @Query("aid") aid: String?,
        @Query("bvid") bvid: String?,
        @Query("cid") cid: Long? = null,
        @Query("index") index: Int = 1,
    ): CommonDataResponse<VideoSnapshotResponse>

    /**
     * 判断视频是否已点赞
     * @return 0-未点赞,1-已点赞
     */
    @GET("/x/web-interface/archive/has/like")
    suspend fun isVideoHasLike(
        @Query("aid") aid: String?,
        @Query("bvid") bvid: String?
    ): CommonDataResponse<Int>


    /**
     * 点赞或者取消点赞
     * @param like 1-点赞,2-取消赞
     */
    @FormUrlEncoded
    @POST("/x/web-interface/archive/like")
    suspend fun likeVideo(
        @Field("aid") aid: String?,
        @Field("bvid") bvid: String?,
        @Field("like") like: Int,
        @Field("csrf") csrf: String
    ): CommonDataResponse<Unit>

    /**
     * 判断视频是否已投币
     * @return 0-未点赞,1-已点赞
     */
    @GET("/x/web-interface/archive/coins")
    suspend fun isVideoHasCoin(
        @Query("aid") aid: String?,
        @Query("bvid") bvid: String?
    ): CommonDataResponse<HasCoinResponse>

    /**
     * 投币
     * @param like 1-点赞,2-取消赞
     */
    @FormUrlEncoded
    @POST("/x/web-interface/coin/add")
    suspend fun giveCoin(
        @Field("aid") aid: String?,
        @Field("bvid") bvid: String?,
        @Field("csrf") csrf: String,
        @Field("multiply") multiply: Int = 2,
        @Field("select_like") selectLike: Int = 1,
    ): CommonDataResponse<GiveCoinResponse>


    @GET("/x/v2/dm/web/view")
    suspend fun getVideoDanmaku(
        @Query("oid") cid: Long,
        @Query("pid") avid: String?,
        @Query("type") type: Int = 1,
    ): ResponseBody

    @GET("/x/v2/dm/web/seg.so")
    suspend fun getVideoDanmakuSegment(
        @Query("oid") cid: Long,
        @Query("segment_index") segmentIndex: Long,
        @Query("type") type: Int = 1,
    ): ResponseBody


    /**
     * 查询用户发布的视频
     * @param mid 用户id
     * @param order 排序方式,最新发布：pubdate,最多播放：click,最多收藏：stow
     * @param tid 分区id
     * @param keyword 关键词
     */
    @GET("/x/space/arc/search")
    suspend fun getAuthorVideo(
        @Query("mid") mid: Long,
        @Query("pn") pageNumber: Int,
        @Query("ps") pageSize: Int,
        @Query("order") order: String = "pubdate",
        @Query("tid") tid: Long? = null,
        @Query("keyworkd") keyword: String? = null
    ): CommonDataResponse<AuthorVideoResponse>


    /**
     * 查询番剧详情
     *
     */
    @GET("/pgc/view/web/season")
    suspend fun getPgcDetail(
        @Query("ep_id") epid: Long?,
        @Query("season_id") seasonId: Long?
    ): PgcDetailResponse

    /**
     * 搜索视频
     */
    @GET("/x/web-interface/search/type")
    suspend fun searchVideo(
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("order") order: String? = null,
        @Query("search_type") searchType: String = "video"
    ): CommonDataResponse<SearchResultWrapper<SearchVideoResult>>


    /**
     * 搜索直播间
     */
    @GET("/x/web-interface/search/type")
    suspend fun searchLiveRoom(
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("order") order: String? = null,
        @Query("search_type") searchType: String = "live_room"
    ): CommonDataResponse<SearchResultWrapper<SearchLiveRoomResult>>


    /**
     * 搜索用户
     */
    @GET("/x/web-interface/search/type")
    suspend fun searchUser(
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("order") order: String? = null,
        @Query("search_type") searchType: String = "bili_user"
    ): CommonDataResponse<SearchResultWrapper<SearchUserResult>>

    companion object {
        const val BASE_URL = "https://api.bilibili.com"
    }
}