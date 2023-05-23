package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


data class LiveRoomDetail(
    @SerializedName("allow_change_area_time")
    val allowChangeAreaTime: Long,
    @SerializedName("allow_upload_cover_time")
    val allowUploadCoverTime: Long,
    @SerializedName("area_id")
    val areaId: Long,
    @SerializedName("area_name")
    val areaName: String,
    @SerializedName("area_pendants")
    val areaPendants: String,
    @SerializedName("attention")
    val attention: Long,
    @SerializedName("background")
    val background: String,
    @SerializedName("battle_id")
    val battleId: Long,
    @SerializedName("description")
    val description: String,
    @SerializedName("hot_words_status")
    val hotWordsStatus: Long,
    @SerializedName("is_anchor")
    val isAnchor: Long,
    @SerializedName("is_portrait")
    val isPortrait: Boolean,
    @SerializedName("is_strict_room")
    val isStrictRoom: Boolean,
    @SerializedName("keyframe")
    val keyframe: String,
    @SerializedName("live_status")
    val liveStatus: Long,
    @SerializedName("live_time")
    val liveTime: String,
    @SerializedName("old_area_id")
    val oldAreaId: Long,
    @SerializedName("online")
    val online: Long,
    @SerializedName("parent_area_id")
    val parentAreaId: Long,
    @SerializedName("parent_area_name")
    val parentAreaName: String,
    @SerializedName("pendants")
    val pendants: String,
    @SerializedName("pk_id")
    val pkId: Long,
    @SerializedName("pk_status")
    val pkStatus: Long,
    @SerializedName("room_id")
    val roomId: Long,
    @SerializedName("room_silent_level")
    val roomSilentLevel: Long,
    @SerializedName("room_silent_second")
    val roomSilentSecond: Long,
    @SerializedName("room_silent_type")
    val roomSilentType: String,
    @SerializedName("short_id")
    val shortId: Long,
    @SerializedName("tags")
    val tags: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("uid")
    val uid: Long,
    @SerializedName("up_session")
    val upSession: String,
    @SerializedName("user_cover")
    val userCover: String
)