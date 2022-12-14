package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


data class LiveRoomWsResponse(
    @SerializedName("business_id")
    val businessId: Int,
    @SerializedName("group")
    val group: String,
    @SerializedName("host_list")
    val hostList: List<LiveRoomWsHost>,
    @SerializedName("max_delay")
    val maxDelay: Int,
    @SerializedName("refresh_rate")
    val refreshRate: Int,
    @SerializedName("refresh_row_factor")
    val refreshRowFactor: Double,
    @SerializedName("token")
    val token: String
)

data class LiveRoomWsHost(
    @SerializedName("host")
    val host: String,
    @SerializedName("port")
    val port: Int,
    @SerializedName("ws_port")
    val wsPort: Int,
    @SerializedName("wss_port")
    val wssPort: Int
)