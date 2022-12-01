package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


data class PgcDetailResponse(
    @SerializedName("code")
    val code: Int,
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
    val mediaId: Int,
    @SerializedName("mode")
    val mode: Int,
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
    val seasonId: Int,
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
    val showSeasonType: Int,
    @SerializedName("square_cover")
    val squareCover: String,
    @SerializedName("stat")
    val stat: PgcDetailStatXX,
    @SerializedName("status")
    val status: Int,
    @SerializedName("subtitle")
    val subtitle: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("total")
    val total: Int,
    @SerializedName("type")
    val type: Int,
    @SerializedName("up_info")
    val upInfo: PgcDetailUpInfo,
    @SerializedName("user_status")
    val userStatus: PgcDetailUserStatus
)

data class PgcDetailActivity(
    @SerializedName("head_bg_url")
    val headBgUrl: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String
)

data class PgcDetailArea(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class PgcDetailEpisode(
    @SerializedName("aid")
    val aid: Int,
    @SerializedName("badge")
    val badge: String,
    @SerializedName("badge_info")
    val badgeInfo: PgcDetailBadgeInfo,
    @SerializedName("badge_type")
    val badgeType: Int,
    @SerializedName("bvid")
    val bvid: String,
    @SerializedName("cid")
    val cid: Int,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("dimension")
    val dimension: PgcDetailDimension,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("from")
    val from: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_view_hide")
    val isViewHide: Boolean,
    @SerializedName("link")
    val link: String,
    @SerializedName("long_title")
    val longTitle: String,
    @SerializedName("pub_time")
    val pubTime: Int,
    @SerializedName("pv")
    val pv: Int,
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
    val status: Int,
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
    val bubbleShowCnt: Int,
    @SerializedName("icon_show")
    val iconShow: Int
)

data class PgcDetailNewEp(
    @SerializedName("desc")
    val desc: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_new")
    val isNew: Int,
    @SerializedName("title")
    val title: String
)

data class PgcDetailPayment(
    @SerializedName("discount")
    val discount: Int,
    @SerializedName("pay_type")
    val payType: PgcDetailPayType,
    @SerializedName("price")
    val price: String,
    @SerializedName("promotion")
    val promotion: String,
    @SerializedName("tip")
    val tip: String,
    @SerializedName("view_start_time")
    val viewStartTime: Int,
    @SerializedName("vip_discount")
    val vipDiscount: Int,
    @SerializedName("vip_first_promotion")
    val vipFirstPromotion: String,
    @SerializedName("vip_promotion")
    val vipPromotion: String
)

data class PgcDetailPositive(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String
)

data class PgcDetailPublish(
    @SerializedName("is_finish")
    val isFinish: Int,
    @SerializedName("is_started")
    val isStarted: Int,
    @SerializedName("pub_time")
    val pubTime: String,
    @SerializedName("pub_time_show")
    val pubTimeShow: String,
    @SerializedName("unknow_pub_date")
    val unknowPubDate: Int,
    @SerializedName("weekday")
    val weekday: Int
)

data class PgcDetailRating(
    @SerializedName("count")
    val count: Int,
    @SerializedName("score")
    val score: Double
)

data class PgcDetailRightsX(
    @SerializedName("allow_bp")
    val allowBp: Int,
    @SerializedName("allow_bp_rank")
    val allowBpRank: Int,
    @SerializedName("allow_download")
    val allowDownload: Int,
    @SerializedName("allow_review")
    val allowReview: Int,
    @SerializedName("area_limit")
    val areaLimit: Int,
    @SerializedName("ban_area_show")
    val banAreaShow: Int,
    @SerializedName("can_watch")
    val canWatch: Int,
    @SerializedName("copyright")
    val copyright: String,
    @SerializedName("forbid_pre")
    val forbidPre: Int,
    @SerializedName("freya_white")
    val freyaWhite: Int,
    @SerializedName("is_cover_show")
    val isCoverShow: Int,
    @SerializedName("is_preview")
    val isPreview: Int,
    @SerializedName("only_vip_download")
    val onlyVipDownload: Int,
    @SerializedName("resource")
    val resource: String,
    @SerializedName("watch_platform")
    val watchPlatform: Int
)

data class PgcDetailSeason(
    @SerializedName("badge")
    val badge: String,
    @SerializedName("badge_info")
    val badgeInfo: PgcDetailBadgeInfo,
    @SerializedName("badge_type")
    val badgeType: Int,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("horizontal_cover_1610")
    val horizontalCover1610: String,
    @SerializedName("horizontal_cover_169")
    val horizontalCover169: String,
    @SerializedName("media_id")
    val mediaId: Int,
    @SerializedName("new_ep")
    val newEp: PgcDetailNewEp,
    @SerializedName("season_id")
    val seasonId: Int,
    @SerializedName("season_title")
    val seasonTitle: String,
    @SerializedName("season_type")
    val seasonType: Int,
    @SerializedName("stat")
    val stat: PgcDetailStat
)

data class PgcDetailSection(
    @SerializedName("attr")
    val attr: Int,
    @SerializedName("episode_id")
    val episodeId: Int,
    @SerializedName("episode_ids")
    val episodeIds: List<Any>,
    @SerializedName("episodes")
    val episodes: List<PgcDetailEpisode>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: Int
)

data class PgcDetailSeries(
    @SerializedName("display_type")
    val displayType: Int,
    @SerializedName("series_id")
    val seriesId: Int,
    @SerializedName("series_title")
    val seriesTitle: String
)

data class PgcDetailShow(
    @SerializedName("wide_screen")
    val wideScreen: Int
)

data class PgcDetailStatXX(
    @SerializedName("coins")
    val coins: Int,
    @SerializedName("danmakus")
    val danmakus: Int,
    @SerializedName("favorite")
    val favorite: Int,
    @SerializedName("favorites")
    val favorites: Int,
    @SerializedName("likes")
    val likes: Int,
    @SerializedName("reply")
    val reply: Int,
    @SerializedName("share")
    val share: Int,
    @SerializedName("views")
    val views: Int
)

data class PgcDetailUpInfo(
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("avatar_subscript_url")
    val avatarSubscriptUrl: String,
    @SerializedName("follower")
    val follower: Int,
    @SerializedName("is_follow")
    val isFollow: Int,
    @SerializedName("mid")
    val mid: Int,
    @SerializedName("nickname_color")
    val nicknameColor: String,
    @SerializedName("pendant")
    val pendant: PgcDetailPendant,
    @SerializedName("theme_type")
    val themeType: Int,
    @SerializedName("uname")
    val uname: String,
    @SerializedName("verify_type")
    val verifyType: Int,
    @SerializedName("vip_label")
    val vipLabel: PgcDetailVipLabel,
    @SerializedName("vip_status")
    val vipStatus: Int,
    @SerializedName("vip_type")
    val vipType: Int
)

data class PgcDetailUserStatus(
    @SerializedName("area_limit")
    val areaLimit: Int,
    @SerializedName("ban_area_show")
    val banAreaShow: Int,
    @SerializedName("follow")
    val follow: Int,
    @SerializedName("follow_status")
    val followStatus: Int,
    @SerializedName("login")
    val login: Int,
    @SerializedName("pay")
    val pay: Int,
    @SerializedName("pay_pack_paid")
    val payPackPaid: Int,
    @SerializedName("sponsor")
    val sponsor: Int
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
    val allowDemand: Int,
    @SerializedName("allow_dm")
    val allowDm: Int,
    @SerializedName("allow_download")
    val allowDownload: Int,
    @SerializedName("area_limit")
    val areaLimit: Int
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
    val end: Int,
    @SerializedName("start")
    val start: Int
)

data class PgcDetailPayType(
    @SerializedName("allow_discount")
    val allowDiscount: Int,
    @SerializedName("allow_pack")
    val allowPack: Int,
    @SerializedName("allow_ticket")
    val allowTicket: Int,
    @SerializedName("allow_time_limit")
    val allowTimeLimit: Int,
    @SerializedName("allow_vip_discount")
    val allowVipDiscount: Int,
    @SerializedName("forbid_bb")
    val forbidBb: Int
)


data class PgcDetailStat(
    @SerializedName("favorites")
    val favorites: Int,
    @SerializedName("series_follow")
    val seriesFollow: Int,
    @SerializedName("views")
    val views: Int
)


data class PgcDetailPendant(
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("pid")
    val pid: Int
)

data class PgcDetailVipLabel(
    @SerializedName("bg_color")
    val bgColor: String,
    @SerializedName("bg_style")
    val bgStyle: Int,
    @SerializedName("border_color")
    val borderColor: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("text_color")
    val textColor: String
)