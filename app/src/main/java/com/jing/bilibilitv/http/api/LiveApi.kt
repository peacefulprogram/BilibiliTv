package com.jing.bilibilitv.http.api

import com.jing.bilibilitv.http.data.CommonDataResponse
import com.jing.bilibilitv.http.data.FollowedLiveRoomResponse
import com.jing.bilibilitv.http.data.LiveRoomArea
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
    ): CommonDataResponse<Unit>

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

    companion object {
        const val BASE_URL = "https://api.live.bilibili.com"
    }
}