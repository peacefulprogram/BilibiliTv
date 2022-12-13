package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


data class UserDetailResponse(
    @SerializedName("coins")
    val coins: Int,
    @SerializedName("face")
    val face: String,
    @SerializedName("face_nft")
    val faceNft: Int,
    @SerializedName("face_nft_type")
    val faceNftType: Int,
    @SerializedName("fans_badge")
    val fansBadge: Boolean,
    @SerializedName("is_followed")
    val isFollowed: Boolean,
    @SerializedName("is_risk")
    val isRisk: Boolean,
    @SerializedName("is_senior_member")
    val isSeniorMember: Int,
    @SerializedName("jointime")
    val jointime: Int,
    @SerializedName("level")
    val level: Int,
    @SerializedName("live_room")
    val liveRoom: UserDetailLiveRoom,
    @SerializedName("mid")
    val mid: Int,
    @SerializedName("moral")
    val moral: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("official")
    val official: UserDetailOfficial,
    @SerializedName("rank")
    val rank: Int,
    @SerializedName("sex")
    val sex: String,
    @SerializedName("sign")
    val sign: String,
    @SerializedName("silence")
    val silence: Int,
    @SerializedName("top_photo")
    val topPhoto: String
)

data class UserDetailLiveRoom(
    @SerializedName("broadcast_type")
    val broadcastType: Int,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("liveStatus")
    val liveStatus: Int,
    @SerializedName("roomStatus")
    val roomStatus: Int,
    @SerializedName("roomid")
    val roomid: Int,
    @SerializedName("roundStatus")
    val roundStatus: Int,
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
    val role: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: Int
)

data class UserDetailWatchedShow(
    @SerializedName("icon")
    val icon: String,
    @SerializedName("icon_location")
    val iconLocation: String,
    @SerializedName("icon_web")
    val iconWeb: String,
    @SerializedName("num")
    val num: Int,
    @SerializedName("switch")
    val switch: Boolean,
    @SerializedName("text_large")
    val textLarge: String,
    @SerializedName("text_small")
    val textSmall: String
)