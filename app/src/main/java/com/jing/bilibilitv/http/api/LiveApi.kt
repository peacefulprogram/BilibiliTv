package com.jing.bilibilitv.http.api

import com.jing.bilibilitv.http.data.*
import retrofit2.http.GET
import retrofit2.http.Query

interface LiveApi {

    /**
     * 根据直播分区查询直播间
     * @param sortType online-按人气,livetime-最新开播
     */
    @GET("/xlive/web-interface/v1/second/getList")
    suspend fun queryLiveRoomByArea(
        @Query("parent_area_id") parentAreaId: String,
        @Query("area_id") areaId: String,
        @Query("sort_type") sortType: String,
        @Query("page") page: Int,
        @Query("platform") platform: String = "web"
    ): CommonDataResponse<LiveRoomOfAreaResponse>

    /**
     * 查询直播分区
     */
    @GET("/room/v1/Area/getList")
    suspend fun liveRoomArea(): CommonDataResponse<List<LiveRoomArea>>

    /**
     * 查询关注到直播间
     */
    @GET("/xlive/web-ucenter/v1/xfetter/GetWebList")
    suspend fun queryFollowedLiveRoom(
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
    ): CommonDataResponse<FollowedLiveRoomResponse>

    /**
     * 查询直播间视频流
     * @param platform web(http-flv) 或 h5(hls)
     */
    @GET("/room/v1/Room/playUrl")
    suspend fun queryLiveStreamUrl(
        @Query("cid") roomId: Long,
        @Query("platform") platform: String = "h5",
        @Query("quality") quality: Int = 4
    ): CommonDataResponse<LiveStreamUrlResponse>

    @GET("/xlive/web-room/v1/index/getDanmuInfo")
    suspend fun queryLiveRoomWsServer(
        @Query("id") roomId: Long
    ): CommonDataResponse<LiveRoomWsResponse>

    @GET("/room/v1/Room/get_info")
    suspend fun queryLiveRoomDetail(@Query("room_id") roomId: Long): CommonDataResponse<LiveRoomDetail>

    companion object {
        const val BASE_URL = "https://api.live.bilibili.com"
    }
}