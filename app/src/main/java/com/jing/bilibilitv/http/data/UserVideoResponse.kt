package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


data class UserVideoResponse(
    @SerializedName("episodic_button")
    val episodicButton: UserVideoEpisodicButton,
    @SerializedName("gaia_res_type")
    val gaiaResType: Long,
    @SerializedName("is_risk")
    val isRisk: Boolean,
    @SerializedName("list")
    val list: UserVideoList,
    @SerializedName("page")
    val page: UserVideoPage
)

data class UserVideoEpisodicButton(
    @SerializedName("text")
    val text: String,
    @SerializedName("uri")
    val uri: String
)

data class UserVideoList(
    @SerializedName("tlist")
    val tlist: Map<Long, UserVideoTagCount>,
    @SerializedName("vlist")
    val vlist: List<UserVideoVlist>
)

data class UserVideoPage(
    @SerializedName("count")
    val count: Long,
    @SerializedName("pn")
    val pn: Long,
    @SerializedName("ps")
    val ps: Long
)


data class UserVideoVlist(
    @SerializedName("aid")
    val aid: Long,
    @SerializedName("attribute")
    val attribute: Long,
    @SerializedName("author")
    val author: String,
    @SerializedName("bvid")
    val bvid: String,
    @SerializedName("comment")
    val comment: Long,
    @SerializedName("copyright")
    val copyright: String,
    @SerializedName("created")
    val created: Long,
    @SerializedName("description")
    val description: String,
    @SerializedName("hide_click")
    val hideClick: Boolean,
    @SerializedName("is_avoided")
    val isAvoided: Long,
    @SerializedName("is_live_playback")
    val isLivePlayback: Long,
    @SerializedName("is_pay")
    val isPay: Long,
    @SerializedName("is_steins_gate")
    val isSteinsGate: Long,
    @SerializedName("is_union_video")
    val isUnionVideo: Long,
    @SerializedName("length")
    val length: String,
    @SerializedName("meta")
    val meta: UserVideoMeta?,
    @SerializedName("mid")
    val mid: Long,
    @SerializedName("pic")
    val pic: String,
    @SerializedName("play")
    val play: Long,
    @SerializedName("review")
    val review: Long,
    @SerializedName("subtitle")
    val subtitle: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("typeid")
    val typeid: Long,
    @SerializedName("video_review")
    val videoReview: Long
)

data class UserVideoTagCount(
    @SerializedName("count")
    val count: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("tid")
    val tid: Long
)

data class UserVideoMeta(
    @SerializedName("attribute")
    val attribute: Long,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("ep_count")
    val epCount: Long,
    @SerializedName("ep_num")
    val epNum: Long,
    @SerializedName("first_aid")
    val firstAid: Long,
    @SerializedName("id")
    val id: Long,
    @SerializedName("intro")
    val intro: String,
    @SerializedName("mid")
    val mid: Long,
    @SerializedName("ptime")
    val ptime: Long,
    @SerializedName("sign_state")
    val signState: Long,
    @SerializedName("stat")
    val stat: UserVideoStat,
    @SerializedName("title")
    val title: String
)

data class UserVideoStat(
    @SerializedName("coin")
    val coin: Long,
    @SerializedName("danmaku")
    val danmaku: Long,
    @SerializedName("favorite")
    val favorite: Long,
    @SerializedName("like")
    val like: Long,
    @SerializedName("reply")
    val reply: Long,
    @SerializedName("season_id")
    val seasonId: Long,
    @SerializedName("share")
    val share: Long,
    @SerializedName("view")
    val view: Long
)