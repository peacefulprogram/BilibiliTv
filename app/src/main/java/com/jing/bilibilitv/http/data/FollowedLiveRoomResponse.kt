package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


data class FollowedLiveRoomResponse(
    val count: Int,
    val rooms: List<FollowedLiveRoom>?
)

data class FollowedLiveRoom(
    @SerializedName("area")
    val area: Int,
    @SerializedName("area_name")
    val areaName: String,
    @SerializedName("area_v2_id")
    val areaV2Id: Int,
    @SerializedName("area_v2_name")
    val areaV2Name: String,
    @SerializedName("area_v2_parent_id")
    val areaV2ParentId: Int,
    @SerializedName("area_v2_parent_name")
    val areaV2ParentName: String,
    @SerializedName("broadcast_type")
    val broadcastType: Int,
    @SerializedName("cover_from_user")
    val coverFromUser: String,
    @SerializedName("face")
    val face: String,
    @SerializedName("hidden_till")
    val hiddenTill: String,
    @SerializedName("is_encrypt")
    val isEncrypt: Boolean,
    @SerializedName("keyframe")
    val keyframe: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("live_status")
    val liveStatus: Int,
    @SerializedName("lock_till")
    val lockTill: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("online")
    val online: Int,
    @SerializedName("room_id")
    val roomId: Int,
    @SerializedName("roomid")
    val roomid: Int,
    @SerializedName("roomname")
    val roomname: String,
    @SerializedName("short_id")
    val shortId: Int,
    @SerializedName("tag_name")
    val tagName: String,
    @SerializedName("tags")
    val tags: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("uid")
    val uid: Int,
    @SerializedName("uname")
    val uname: String
)