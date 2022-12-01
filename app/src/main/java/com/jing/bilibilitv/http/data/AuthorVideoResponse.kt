package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


data class AuthorVideoResponse(
    @SerializedName("episodic_button")
    val episodicButton: AuthorVideoEpisodicButton,
    @SerializedName("gaia_data")
    val gaiaData: Any?,
    @SerializedName("gaia_res_type")
    val gaiaResType: Long,
    @SerializedName("is_risk")
    val isRisk: Boolean,
    @SerializedName("list")
    val list: AuthorVideoList,
    @SerializedName("page")
    val page: AuthorVideoPage
)

data class AuthorVideoEpisodicButton(
    @SerializedName("text")
    val text: String,
    @SerializedName("uri")
    val uri: String
)

data class AuthorVideoList(
    @SerializedName("tlist")
    val tlist: Map<Long, AuthorVideoTag>,
    @SerializedName("vlist")
    val vlist: List<AuthorVideoVlist>
)

data class AuthorVideoPage(
    @SerializedName("count")
    val count: Long,
    @SerializedName("pn")
    val pn: Long,
    @SerializedName("ps")
    val ps: Long
)


data class AuthorVideoVlist(
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

data class AuthorVideoTag(
    @SerializedName("count")
    val count: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("tid")
    val tid: Long
)