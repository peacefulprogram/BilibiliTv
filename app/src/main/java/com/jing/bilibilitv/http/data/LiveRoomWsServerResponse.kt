package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


data class LiveRoomWsResponse(
    @SerializedName("business_id")
    val businessId: Long,
    @SerializedName("group")
    val group: String,
    @SerializedName("host_list")
    val hostList: List<LiveRoomWsHost>,
    @SerializedName("max_delay")
    val maxDelay: Long,
    @SerializedName("refresh_rate")
    val refreshRate: Long,
    @SerializedName("refresh_row_factor")
    val refreshRowFactor: Double,
    @SerializedName("token")
    val token: String
)

data class LiveRoomWsHost(
    @SerializedName("host")
    val host: String,
    @SerializedName("port")
    val port: Long,
    @SerializedName("ws_port")
    val wsPort: Long,
    @SerializedName("wss_port")
    val wssPort: Long
)