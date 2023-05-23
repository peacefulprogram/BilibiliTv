package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


data class FollowedLiveRoomResponse(
    val count: Long,
    val rooms: List<FollowedLiveRoom>?
)

data class FollowedLiveRoom(
    @SerializedName("area")
    val area: Long,
    @SerializedName("area_name")
    val areaName: String,
    @SerializedName("area_v2_id")
    val areaV2Id: Long,
    @SerializedName("area_v2_name")
    val areaV2Name: String,
    @SerializedName("area_v2_parent_id")
    val areaV2ParentId: Long,
    @SerializedName("area_v2_parent_name")
    val areaV2ParentName: String,
    @SerializedName("broadcast_type")
    val broadcastType: Long,
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
    val liveStatus: Long,
    @SerializedName("lock_till")
    val lockTill: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("online")
    val online: Long,
    @SerializedName("room_id")
    val roomId: Long,
    @SerializedName("roomname")
    val roomName: String,
    @SerializedName("short_id")
    val shortId: Long,
    @SerializedName("tag_name")
    val tagName: String,
    @SerializedName("tags")
    val tags: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("uid")
    val uid: Long,
    @SerializedName("uname")
    val uname: String
)