package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


interface BilibiliSearchResult {
    val type: String
}


data class SearchResultWrapper<T : BilibiliSearchResult>(
    @SerializedName("in_black_key")
    val inBlackKey: Long,
    @SerializedName("in_white_key")
    val inWhiteKey: Long,
    @SerializedName("numPages")
    val numPages: Int,
    @SerializedName("numResults")
    val numResults: Long,
    @SerializedName("page")
    val page: Int,
    @SerializedName("pagesize")
    val pageSize: Int,
    @SerializedName("rqt_type")
    val rqtType: String,
    @SerializedName("seid")
    val seid: String,
    @SerializedName("show_column")
    val showColumn: Long,
    @SerializedName("suggest_keyword")
    val suggestKeyword: String,
    @SerializedName("result")
    val result: List<T>?
)


data class SearchVideoResult(
    @SerializedName("aid")
    val aid: Long,
    @SerializedName("arcrank")
    val arcrank: String,
    @SerializedName("arcurl")
    val arcurl: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("badgepay")
    val badgepay: Boolean,
    @SerializedName("bvid")
    val bvid: String,
    @SerializedName("corner")
    val corner: String,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("danmaku")
    val danmaku: Long,
    @SerializedName("desc")
    val desc: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("favorites")
    val favorites: Long,
    @SerializedName("hit_columns")
    val hitColumns: List<String>,
    @SerializedName("id")
    val id: Long,
    @SerializedName("is_pay")
    val isPay: Long,
    @SerializedName("is_union_video")
    val isUnionVideo: Long,
    @SerializedName("like")
    val like: Long,
    @SerializedName("mid")
    val mid: Long,
    @SerializedName("pic")
    val pic: String,
    @SerializedName("play")
    val play: Long,
    @SerializedName("pubdate")
    val pubdate: Long,
    @SerializedName("rank_score")
    val rankScore: Long,
    @SerializedName("rec_reason")
    val recReason: String,
    @SerializedName("review")
    val review: Long,
    @SerializedName("senddate")
    val senddate: Long,
    @SerializedName("tag")
    val tag: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("type")
    override val type: String,
    @SerializedName("typeid")
    val typeid: String,
    @SerializedName("typename")
    val typename: String,
    @SerializedName("upic")
    val upic: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("video_review")
    val videoReview: Long,
    @SerializedName("view_type")
    val viewType: String
) : BilibiliSearchResult

data class SearchLiveRoomResult(
    @SerializedName("area")
    val area: Long,
    @SerializedName("attentions")
    val attentions: Long,
    @SerializedName("cate_name")
    val cateName: String,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("is_live_room_inline")
    val isLiveRoomInline: Long,
    @SerializedName("live_status")
    val liveStatus: Long,
    @SerializedName("live_time")
    val liveTime: String,
    @SerializedName("online")
    val online: Long,
    @SerializedName("rank_index")
    val rankIndex: Long,
    @SerializedName("rank_offset")
    val rankOffset: Long,
    @SerializedName("rank_score")
    val rankScore: Long,
    @SerializedName("roomid")
    val roomid: Long,
    @SerializedName("short_id")
    val shortId: Long,
    @SerializedName("tags")
    val tags: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("type")
    override val type: String,
    @SerializedName("uface")
    val uface: String,
    @SerializedName("uid")
    val uid: Long,
    @SerializedName("uname")
    val uname: String,
    @SerializedName("user_cover")
    val userCover: String
) : BilibiliSearchResult

data class SearchUserResult(
    @SerializedName("face_nft")
    val faceNft: Long,
    @SerializedName("face_nft_type")
    val faceNftType: Long,
    @SerializedName("fans")
    val fans: Long,
    @SerializedName("gender")
    val gender: Long,
    @SerializedName("is_live")
    val isLive: Long,
    @SerializedName("is_senior_member")
    val isSeniorMember: Long,
    @SerializedName("is_upuser")
    val isUpuser: Long,
    @SerializedName("level")
    val level: Long,
    @SerializedName("mid")
    val mid: Long,
    @SerializedName("official_verify")
    val officialVerify: SearchUserOfficialVerify,
    @SerializedName("res")
    val res: List<SearchUserRecentVideo>,
    @SerializedName("room_id")
    val roomId: Long,
    @SerializedName("type")
    override val type: String,
    @SerializedName("uname")
    val uname: String,
    @SerializedName("upic")
    val upic: String,
    @SerializedName("usign")
    val usign: String,
    @SerializedName("verify_info")
    val verifyInfo: String,
    @SerializedName("videos")
    val videos: Long
) : BilibiliSearchResult

data class SearchUserOfficialVerify(
    @SerializedName("desc")
    val desc: String,
    @SerializedName("type")
    val type: Long
)

data class SearchUserRecentVideo(
    @SerializedName("aid")
    val aid: Long,
    @SerializedName("arcurl")
    val arcurl: String,
    @SerializedName("bvid")
    val bvid: String,
    @SerializedName("coin")
    val coin: Long,
    @SerializedName("desc")
    val desc: String,
    @SerializedName("dm")
    val dm: Long,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("fav")
    val fav: Long,
    @SerializedName("is_pay")
    val isPay: Long,
    @SerializedName("is_union_video")
    val isUnionVideo: Long,
    @SerializedName("pic")
    val pic: String,
    @SerializedName("play")
    val play: String,
    @SerializedName("pubdate")
    val pubdate: Long,
    @SerializedName("title")
    val title: String
)