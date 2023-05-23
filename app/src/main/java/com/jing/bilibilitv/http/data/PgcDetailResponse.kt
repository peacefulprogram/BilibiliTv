package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


data class PgcDetailResponse(
    @SerializedName("code")
    val code: Long,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: PgcDetailResult?
)

data class PgcDetailResult(
    @SerializedName("activity")
    val activity: PgcDetailActivity,
    @SerializedName("alias")
    val alias: String,
    @SerializedName("areas")
    val areas: List<PgcDetailArea>,
    @SerializedName("bkg_cover")
    val bkgCover: String,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("episodes")
    val episodes: List<PgcDetailEpisode>,
    @SerializedName("evaluate")
    val evaluate: String,
    @SerializedName("freya")
    val freya: PgcDetailFreya,
    @SerializedName("jp_title")
    val jpTitle: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("media_id")
    val mediaId: Long,
    @SerializedName("mode")
    val mode: Long,
    @SerializedName("new_ep")
    val newEp: PgcDetailNewEp,
    @SerializedName("payment")
    val payment: PgcDetailPayment,
    @SerializedName("positive")
    val positive: PgcDetailPositive,
    @SerializedName("publish")
    val publish: PgcDetailPublish,
    @SerializedName("rating")
    val rating: PgcDetailRating,
    @SerializedName("record")
    val record: String,
    @SerializedName("rights")
    val rights: PgcDetailRightsX,
    @SerializedName("season_id")
    val seasonId: Long,
    @SerializedName("season_title")
    val seasonTitle: String,
    @SerializedName("seasons")
    val seasons: List<PgcDetailSeason>,
    @SerializedName("section")
    val section: List<PgcDetailSection>,
    @SerializedName("series")
    val series: PgcDetailSeries,
    @SerializedName("share_copy")
    val shareCopy: String,
    @SerializedName("share_sub_title")
    val shareSubTitle: String,
    @SerializedName("share_url")
    val shareUrl: String,
    @SerializedName("show")
    val show: PgcDetailShow,
    @SerializedName("show_season_type")
    val showSeasonType: Long,
    @SerializedName("square_cover")
    val squareCover: String,
    @SerializedName("stat")
    val stat: PgcDetailStatXX,
    @SerializedName("status")
    val status: Long,
    @SerializedName("subtitle")
    val subtitle: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("total")
    val total: Long,
    @SerializedName("type")
    val type: Long,
    @SerializedName("up_info")
    val upInfo: PgcDetailUpInfo,
    @SerializedName("user_status")
    val userStatus: PgcDetailUserStatus
)

data class PgcDetailActivity(
    @SerializedName("head_bg_url")
    val headBgUrl: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String
)

data class PgcDetailArea(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String
)

data class PgcDetailEpisode(
    @SerializedName("aid")
    val aid: Long,
    @SerializedName("badge")
    val badge: String,
    @SerializedName("badge_info")
    val badgeInfo: PgcDetailBadgeInfo,
    @SerializedName("badge_type")
    val badgeType: Long,
    @SerializedName("bvid")
    val bvid: String,
    @SerializedName("cid")
    val cid: Long,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("dimension")
    val dimension: PgcDetailDimension,
    @SerializedName("duration")
    val duration: Long,
    @SerializedName("from")
    val from: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("is_view_hide")
    val isViewHide: Boolean,
    @SerializedName("link")
    val link: String,
    @SerializedName("long_title")
    val longTitle: String,
    @SerializedName("pub_time")
    val pubTime: Long,
    @SerializedName("pv")
    val pv: Long,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("rights")
    val rights: PgcDetailRights,
    @SerializedName("share_copy")
    val shareCopy: String,
    @SerializedName("share_url")
    val shareUrl: String,
    @SerializedName("short_link")
    val shortLink: String,
    @SerializedName("skip")
    val skip: PgcDetailSkip,
    @SerializedName("status")
    val status: Long,
    @SerializedName("subtitle")
    val subtitle: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("vid")
    val vid: String
)

data class PgcDetailFreya(
    @SerializedName("bubble_desc")
    val bubbleDesc: String,
    @SerializedName("bubble_show_cnt")
    val bubbleShowCnt: Long,
    @SerializedName("icon_show")
    val iconShow: Long
)

data class PgcDetailNewEp(
    @SerializedName("desc")
    val desc: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("is_new")
    val isNew: Long,
    @SerializedName("title")
    val title: String
)

data class PgcDetailPayment(
    @SerializedName("discount")
    val discount: Long,
    @SerializedName("pay_type")
    val payType: PgcDetailPayType,
    @SerializedName("price")
    val price: String,
    @SerializedName("promotion")
    val promotion: String,
    @SerializedName("tip")
    val tip: String,
    @SerializedName("view_start_time")
    val viewStartTime: Long,
    @SerializedName("vip_discount")
    val vipDiscount: Long,
    @SerializedName("vip_first_promotion")
    val vipFirstPromotion: String,
    @SerializedName("vip_promotion")
    val vipPromotion: String
)

data class PgcDetailPositive(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String
)

data class PgcDetailPublish(
    @SerializedName("is_finish")
    val isFinish: Long,
    @SerializedName("is_started")
    val isStarted: Long,
    @SerializedName("pub_time")
    val pubTime: String,
    @SerializedName("pub_time_show")
    val pubTimeShow: String,
    @SerializedName("unknow_pub_date")
    val unknowPubDate: Long,
    @SerializedName("weekday")
    val weekday: Long
)

data class PgcDetailRating(
    @SerializedName("count")
    val count: Long,
    @SerializedName("score")
    val score: Double
)

data class PgcDetailRightsX(
    @SerializedName("allow_bp")
    val allowBp: Long,
    @SerializedName("allow_bp_rank")
    val allowBpRank: Long,
    @SerializedName("allow_download")
    val allowDownload: Long,
    @SerializedName("allow_review")
    val allowReview: Long,
    @SerializedName("area_limit")
    val areaLimit: Long,
    @SerializedName("ban_area_show")
    val banAreaShow: Long,
    @SerializedName("can_watch")
    val canWatch: Long,
    @SerializedName("copyright")
    val copyright: String,
    @SerializedName("forbid_pre")
    val forbidPre: Long,
    @SerializedName("freya_white")
    val freyaWhite: Long,
    @SerializedName("is_cover_show")
    val isCoverShow: Long,
    @SerializedName("is_preview")
    val isPreview: Long,
    @SerializedName("only_vip_download")
    val onlyVipDownload: Long,
    @SerializedName("resource")
    val resource: String,
    @SerializedName("watch_platform")
    val watchPlatform: Long
)

data class PgcDetailSeason(
    @SerializedName("badge")
    val badge: String,
    @SerializedName("badge_info")
    val badgeInfo: PgcDetailBadgeInfo,
    @SerializedName("badge_type")
    val badgeType: Long,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("horizontal_cover_1610")
    val horizontalCover1610: String,
    @SerializedName("horizontal_cover_169")
    val horizontalCover169: String,
    @SerializedName("media_id")
    val mediaId: Long,
    @SerializedName("new_ep")
    val newEp: PgcDetailNewEp,
    @SerializedName("season_id")
    val seasonId: Long,
    @SerializedName("season_title")
    val seasonTitle: String,
    @SerializedName("season_type")
    val seasonType: Long,
    @SerializedName("stat")
    val stat: PgcDetailStat
)

data class PgcDetailSection(
    @SerializedName("attr")
    val attr: Long,
    @SerializedName("episode_id")
    val episodeId: Long,
    @SerializedName("episode_ids")
    val episodeIds: List<Any>,
    @SerializedName("episodes")
    val episodes: List<PgcDetailEpisode>,
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: Long
)

data class PgcDetailSeries(
    @SerializedName("display_type")
    val displayType: Long,
    @SerializedName("series_id")
    val seriesId: Long,
    @SerializedName("series_title")
    val seriesTitle: String
)

data class PgcDetailShow(
    @SerializedName("wide_screen")
    val wideScreen: Long
)

data class PgcDetailStatXX(
    @SerializedName("coins")
    val coins: Long,
    @SerializedName("danmakus")
    val danmakus: Long,
    @SerializedName("favorite")
    val favorite: Long,
    @SerializedName("favorites")
    val favorites: Long,
    @SerializedName("likes")
    val likes: Long,
    @SerializedName("reply")
    val reply: Long,
    @SerializedName("share")
    val share: Long,
    @SerializedName("views")
    val views: Long
)

data class PgcDetailUpInfo(
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("avatar_subscript_url")
    val avatarSubscriptUrl: String,
    @SerializedName("follower")
    val follower: Long,
    @SerializedName("is_follow")
    val isFollow: Long,
    @SerializedName("mid")
    val mid: Long,
    @SerializedName("nickname_color")
    val nicknameColor: String,
    @SerializedName("pendant")
    val pendant: PgcDetailPendant,
    @SerializedName("theme_type")
    val themeType: Long,
    @SerializedName("uname")
    val uname: String,
    @SerializedName("verify_type")
    val verifyType: Long,
    @SerializedName("vip_label")
    val vipLabel: PgcDetailVipLabel,
    @SerializedName("vip_status")
    val vipStatus: Long,
    @SerializedName("vip_type")
    val vipType: Long
)

data class PgcDetailUserStatus(
    @SerializedName("area_limit")
    val areaLimit: Long,
    @SerializedName("ban_area_show")
    val banAreaShow: Long,
    @SerializedName("follow")
    val follow: Long,
    @SerializedName("follow_status")
    val followStatus: Long,
    @SerializedName("login")
    val login: Long,
    @SerializedName("pay")
    val pay: Long,
    @SerializedName("pay_pack_paid")
    val payPackPaid: Long,
    @SerializedName("sponsor")
    val sponsor: Long
)

data class PgcDetailBadgeInfo(
    @SerializedName("bg_color")
    val bgColor: String,
    @SerializedName("bg_color_night")
    val bgColorNight: String,
    @SerializedName("text")
    val text: String
)

data class PgcDetailDimension(
    @SerializedName("height")
    val height: Int,
    @SerializedName("rotate")
    val rotate: Int,
    @SerializedName("width")
    val width: Int
)

data class PgcDetailRights(
    @SerializedName("allow_demand")
    val allowDemand: Long,
    @SerializedName("allow_dm")
    val allowDm: Long,
    @SerializedName("allow_download")
    val allowDownload: Long,
    @SerializedName("area_limit")
    val areaLimit: Long
)

data class PgcDetailSkip(
    @SerializedName("ed")
    val ed: PgcDetailEd,
    @SerializedName("op")
    val op: PgcDetailOp
)

data class PgcDetailEd(
    @SerializedName("end")
    val end: Int,
    @SerializedName("start")
    val start: Int
)

data class PgcDetailOp(
    @SerializedName("end")
    val end: Long,
    @SerializedName("start")
    val start: Long
)

data class PgcDetailPayType(
    @SerializedName("allow_discount")
    val allowDiscount: Long,
    @SerializedName("allow_pack")
    val allowPack: Long,
    @SerializedName("allow_ticket")
    val allowTicket: Long,
    @SerializedName("allow_time_limit")
    val allowTimeLimit: Long,
    @SerializedName("allow_vip_discount")
    val allowVipDiscount: Long,
    @SerializedName("forbid_bb")
    val forbidBb: Long
)


data class PgcDetailStat(
    @SerializedName("favorites")
    val favorites: Long,
    @SerializedName("series_follow")
    val seriesFollow: Long,
    @SerializedName("views")
    val views: Long
)


data class PgcDetailPendant(
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("pid")
    val pid: Long
)

data class PgcDetailVipLabel(
    @SerializedName("bg_color")
    val bgColor: String,
    @SerializedName("bg_style")
    val bgStyle: Long,
    @SerializedName("border_color")
    val borderColor: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("text_color")
    val textColor: String
)