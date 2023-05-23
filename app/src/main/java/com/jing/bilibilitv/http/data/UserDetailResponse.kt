package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


data class UserDetailResponse(
    @SerializedName("coins")
    val coins: Long,
    @SerializedName("face")
    val face: String,
    @SerializedName("face_nft")
    val faceNft: Long,
    @SerializedName("face_nft_type")
    val faceNftType: Long,
    @SerializedName("fans_badge")
    val fansBadge: Boolean,
    @SerializedName("is_followed")
    val isFollowed: Boolean,
    @SerializedName("is_risk")
    val isRisk: Boolean,
    @SerializedName("is_senior_member")
    val isSeniorMember: Long,
    @SerializedName("jointime")
    val jointime: Long,
    @SerializedName("level")
    val level: Long,
    @SerializedName("live_room")
    val liveRoom: UserDetailLiveRoom,
    @SerializedName("mid")
    val mid: Long,
    @SerializedName("moral")
    val moral: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("official")
    val official: UserDetailOfficial,
    @SerializedName("rank")
    val rank: Long,
    @SerializedName("sex")
    val sex: String,
    @SerializedName("sign")
    val sign: String,
    @SerializedName("silence")
    val silence: Long,
    @SerializedName("top_photo")
    val topPhoto: String
)

data class UserDetailLiveRoom(
    @SerializedName("broadcast_type")
    val broadcastType: Long,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("liveStatus")
    val liveStatus: Long,
    @SerializedName("roomStatus")
    val roomStatus: Long,
    @SerializedName("roomid")
    val roomid: Long,
    @SerializedName("roundStatus")
    val roundStatus: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("watched_show")
    val watchedShow: UserDetailWatchedShow
)

data class UserDetailOfficial(
    @SerializedName("desc")
    val desc: String,
    @SerializedName("role")
    val role: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: Long
)

data class UserDetailWatchedShow(
    @SerializedName("icon")
    val icon: String,
    @SerializedName("icon_location")
    val iconLocation: String,
    @SerializedName("icon_web")
    val iconWeb: String,
    @SerializedName("num")
    val num: Long,
    @SerializedName("switch")
    val switch: Boolean,
    @SerializedName("text_large")
    val textLarge: String,
    @SerializedName("text_small")
    val textSmall: String
)