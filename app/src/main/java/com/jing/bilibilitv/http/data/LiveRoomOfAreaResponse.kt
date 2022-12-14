package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName

data class LiveRoomOfAreaResponse(
    @SerializedName("list")
    val list: List<LiveRoomOfArea>?,
    @SerializedName("count")
    val count: Int,
    @SerializedName("has_more")
    val hasMore: Int
)


data class LiveRoomOfArea(
    @SerializedName("area_id")
    val areaId: Long,
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
    @SerializedName("click_callback")
    val clickCallback: String,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("face")
    val face: String,
    @SerializedName("flag")
    val flag: Long,
    @SerializedName("group_id")
    val groupId: Long,
    @SerializedName("head_box_type")
    val headBoxType: Long,
    @SerializedName("is_auto_play")
    val isAutoPlay: Long,
    @SerializedName("is_nft")
    val isNft: Long,
    @SerializedName("link")
    val link: String,
    @SerializedName("nft_dmark")
    val nftDmark: String,
    @SerializedName("online")
    val online: Long?,
    @SerializedName("parent_id")
    val parentId: Long,
    @SerializedName("parent_name")
    val parentName: String,
    @SerializedName("pk_id")
    val pkId: Long,
    @SerializedName("roomid")
    val roomId: Long,
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("show_callback")
    val showCallback: String,
    @SerializedName("show_cover")
    val showCover: String,
    @SerializedName("system_cover")
    val systemCover: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("uid")
    val uid: Long,
    @SerializedName("uname")
    val uname: String,
    @SerializedName("user_cover")
    val userCover: String,
    @SerializedName("user_cover_flag")
    val userCoverFlag: Long,
    @SerializedName("watched_show")
    val watchedShow: WatchedShow
)

data class WatchedShow(
    @SerializedName("icon")
    val icon: String,
    @SerializedName("icon_location")
    val iconLocation: Long,
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