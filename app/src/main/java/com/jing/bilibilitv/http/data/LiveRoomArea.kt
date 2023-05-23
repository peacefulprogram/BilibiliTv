package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


data class LiveRoomArea(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("list")
    val list: List<LiveRoomSubArea>
)

data class LiveRoomSubArea(
    @SerializedName("act_id")
    val actId: String,
    @SerializedName("area_type")
    val areaType: Long,
    @SerializedName("complex_area_name")
    val complexAreaName: String,
    @SerializedName("hot_status")
    val hotStatus: Long,
    @SerializedName("id")
    val id: String,
    @SerializedName("lock_status")
    val lockStatus: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("old_area_id")
    val oldAreaId: String,
    @SerializedName("parent_id")
    val parentId: String,
    @SerializedName("parent_name")
    val parentName: String,
    @SerializedName("pic")
    val pic: String,
    @SerializedName("pk_status")
    val pkStatus: String
)