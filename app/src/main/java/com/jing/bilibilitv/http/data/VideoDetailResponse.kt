package com.jing.bilibilitv.http.data
import com.google.gson.annotations.SerializedName

data class VideoDetailResponse(
    @SerializedName("Card")
    val card: Card,
    @SerializedName("Related")
    val related: List<Related>,
    @SerializedName("Reply")
    val reply: Reply,
    @SerializedName("Tags")
    val tags: List<Tag>,
    @SerializedName("View")
    val view: View
)

data class Card(
    @SerializedName("archive_count")
    val archiveCount: Long,
    @SerializedName("article_count")
    val articleCount: Long,
    @SerializedName("card")
    val card: CardX,
    @SerializedName("follower")
    val follower: Long,
    @SerializedName("following")
    val following: Boolean,
    @SerializedName("like_num")
    val likeNum: Long,
    @SerializedName("space")
    val space: Space
)

data class HotShare(
    @SerializedName("list")
    val list: List<Any>,
    @SerializedName("show")
    val show: Boolean
)

data class Related(
    @SerializedName("aid")
    val aid: Long,
    @SerializedName("bvid")
    val bvid: String,
    @SerializedName("cid")
    val cid: Long,
    @SerializedName("copyright")
    val copyright: Long,
    @SerializedName("ctime")
    val ctime: Long,
    @SerializedName("desc")
    val desc: String,
    @SerializedName("dimension")
    val dimension: Dimension,
    @SerializedName("duration")
    val duration: Long,
    @SerializedName("dynamic")
    val `dynamic`: String,
    @SerializedName("first_frame")
    val firstFrame: String?,
    @SerializedName("is_ogv")
    val isOgv: Boolean,
    @SerializedName("mission_id")
    val missionId: Long?,
    @SerializedName("ogv_info")
    val ogvInfo: Any?,
    @SerializedName("owner")
    val owner: Owner,
    @SerializedName("pic")
    val pic: String,
    @SerializedName("pub_location")
    val pubLocation: String?,
    @SerializedName("pubdate")
    val pubdate: Long,
    @SerializedName("rcmd_reason")
    val rcmdReason: String,
    @SerializedName("rights")
    val rights: Rights,
    @SerializedName("season_id")
    val seasonId: Long?,
    @SerializedName("season_type")
    val seasonType: Long,
    @SerializedName("short_link")
    val shortLink: String,
    @SerializedName("short_link_v2")
    val shortLinkV2: String,
    @SerializedName("stat")
    val stat: Stat,
    @SerializedName("state")
    val state: Long,
    @SerializedName("tid")
    val tid: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("tname")
    val tname: String,
    @SerializedName("up_from_v2")
    val upFromV2: Long?,
    @SerializedName("videos")
    val videos: Long
)

data class Reply(
    @SerializedName("page")
    val page: Page,
    @SerializedName("replies")
    val replies: List<ReplyX>
)

data class Tag(
    @SerializedName("alpha")
    val alpha: Long,
    @SerializedName("archive_count")
    val archiveCount: String,
    @SerializedName("attribute")
    val attribute: Long,
    @SerializedName("color")
    val color: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("count")
    val count: Count,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("ctime")
    val ctime: Long,
    @SerializedName("extra_attr")
    val extraAttr: Long,
    @SerializedName("featured_count")
    val featuredCount: Long,
    @SerializedName("hated")
    val hated: Long,
    @SerializedName("hates")
    val hates: Long,
    @SerializedName("head_cover")
    val headCover: String,
    @SerializedName("is_activity")
    val isActivity: Boolean,
    @SerializedName("is_atten")
    val isAtten: Long,
    @SerializedName("is_season")
    val isSeason: Boolean,
    @SerializedName("jump_url")
    val jumpUrl: String,
    @SerializedName("liked")
    val liked: Long,
    @SerializedName("likes")
    val likes: Long,
    @SerializedName("music_id")
    val musicId: String,
    @SerializedName("short_content")
    val shortContent: String,
    @SerializedName("state")
    val state: Long,
    @SerializedName("subscribed_count")
    val subscribedCount: Long,
    @SerializedName("tag_id")
    val tagId: Long,
    @SerializedName("tag_name")
    val tagName: String,
    @SerializedName("tag_type")
    val tagType: String,
    @SerializedName("type")
    val type: Long
)

data class View(
    @SerializedName("aid")
    val aid: Long,
    @SerializedName("bvid")
    val bvid: String,
    @SerializedName("cid")
    val cid: Long,
    @SerializedName("copyright")
    val copyright: Long,
    @SerializedName("ctime")
    val ctime: Long,
    @SerializedName("desc")
    val desc: String,
    @SerializedName("desc_v2")
    val descV2: List<DescV2>,
    @SerializedName("dimension")
    val dimension: Dimension,
    @SerializedName("duration")
    val duration: Long,
    @SerializedName("dynamic")
    val `dynamic`: String,
    @SerializedName("honor_reply")
    val honorReply: HonorReply,
    @SerializedName("is_chargeable_season")
    val isChargeableSeason: Boolean,
    @SerializedName("is_season_display")
    val isSeasonDisplay: Boolean,
    @SerializedName("is_story")
    val isStory: Boolean,
    @SerializedName("like_icon")
    val likeIcon: String,
    @SerializedName("no_cache")
    val noCache: Boolean,
    @SerializedName("owner")
    val owner: Owner,
    @SerializedName("pages")
    val pages: List<PageX>,
    @SerializedName("pic")
    val pic: String,
    @SerializedName("premiere")
    val premiere: Any?,
    @SerializedName("pubdate")
    val pubdate: Long,
    @SerializedName("rights")
    val rights: RightsX,
    @SerializedName("stat")
    val stat: StatX,
    @SerializedName("state")
    val state: Long,
    @SerializedName("subtitle")
    val subtitle: Subtitle,
    @SerializedName("teenage_mode")
    val teenageMode: Long,
    @SerializedName("tid")
    val tid: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("tname")
    val tname: String,
    @SerializedName("user_garb")
    val userGarb: UserGarb,
    @SerializedName("videos")
    val videos: Long
)


data class CardX(
    @SerializedName("approve")
    val approve: Boolean,
    @SerializedName("article")
    val article: Long,
    @SerializedName("attention")
    val attention: Long,
    @SerializedName("attentions")
    val attentions: List<Any>,
    @SerializedName("birthday")
    val birthday: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("DisplayRank")
    val displayRank: String,
    @SerializedName("face")
    val face: String,
    @SerializedName("face_nft")
    val faceNft: Long,
    @SerializedName("face_nft_type")
    val faceNftType: Long,
    @SerializedName("fans")
    val fans: Long,
    @SerializedName("friend")
    val friend: Long,
    @SerializedName("is_senior_member")
    val isSeniorMember: Long,
    @SerializedName("level_info")
    val levelInfo: LevelInfo,
    @SerializedName("mid")
    val mid: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("nameplate")
    val nameplate: Nameplate,
    @SerializedName("Official")
    val official: Official,
    @SerializedName("official_verify")
    val officialVerify: OfficialVerify,
    @SerializedName("pendant")
    val pendant: Pendant,
    @SerializedName("place")
    val place: String,
    @SerializedName("rank")
    val rank: String,
    @SerializedName("regtime")
    val regtime: Long,
    @SerializedName("sex")
    val sex: String,
    @SerializedName("sign")
    val sign: String,
    @SerializedName("spacesta")
    val spacesta: Long,
    @SerializedName("vip")
    val vip: Vip
)

data class Space(
    @SerializedName("l_img")
    val lImg: String,
    @SerializedName("s_img")
    val sImg: String
)

data class LevelInfo(
    @SerializedName("current_exp")
    val currentExp: Long,
    @SerializedName("current_level")
    val currentLevel: Long,
    @SerializedName("current_min")
    val currentMin: Long,
    @SerializedName("next_exp")
    val nextExp: Long
)

data class Nameplate(
    @SerializedName("condition")
    val condition: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("image_small")
    val imageSmall: String,
    @SerializedName("level")
    val level: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("nid")
    val nid: Long
)

data class Official(
    @SerializedName("desc")
    val desc: String,
    @SerializedName("role")
    val role: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: Long
)

data class OfficialVerify(
    @SerializedName("desc")
    val desc: String,
    @SerializedName("type")
    val type: Long
)

data class Pendant(
    @SerializedName("expire")
    val expire: Long,
    @SerializedName("image")
    val image: String,
    @SerializedName("image_enhance")
    val imageEnhance: String,
    @SerializedName("image_enhance_frame")
    val imageEnhanceFrame: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("pid")
    val pid: Long
)

data class Vip(
    @SerializedName("avatar_subscript")
    val avatarSubscript: Long,
    @SerializedName("avatar_subscript_url")
    val avatarSubscriptUrl: String,
    @SerializedName("due_date")
    val dueDate: Long,
    @SerializedName("label")
    val label: Label,
    @SerializedName("nickname_color")
    val nicknameColor: String,
    @SerializedName("role")
    val role: Long,
    @SerializedName("status")
    val status: Long,
    @SerializedName("theme_type")
    val themeType: Long,
    @SerializedName("tv_vip_pay_type")
    val tvVipPayType: Long,
    @SerializedName("tv_vip_status")
    val tvVipStatus: Long,
    @SerializedName("type")
    val type: Long,
    @SerializedName("vip_pay_type")
    val vipPayType: Long,
    @SerializedName("vipStatus")
    val vipStatus: Long,
    @SerializedName("vipType")
    val vipType: Long
)

data class Label(
    @SerializedName("bg_color")
    val bgColor: String,
    @SerializedName("bg_style")
    val bgStyle: Long,
    @SerializedName("border_color")
    val borderColor: String,
    @SerializedName("img_label_uri_hans")
    val imgLabelUriHans: String,
    @SerializedName("img_label_uri_hans_static")
    val imgLabelUriHansStatic: String,
    @SerializedName("img_label_uri_hant")
    val imgLabelUriHant: String,
    @SerializedName("img_label_uri_hant_static")
    val imgLabelUriHantStatic: String,
    @SerializedName("label_theme")
    val labelTheme: String,
    @SerializedName("path")
    val path: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("text_color")
    val textColor: String,
    @SerializedName("use_img_label")
    val useImgLabel: Boolean
)

data class Dimension(
    @SerializedName("height")
    val height: Long,
    @SerializedName("rotate")
    val rotate: Long,
    @SerializedName("width")
    val width: Long
)

data class Owner(
    @SerializedName("face")
    val face: String,
    @SerializedName("mid")
    val mid: Long,
    @SerializedName("name")
    val name: String
)

data class Rights(
    @SerializedName("arc_pay")
    val arcPay: Long,
    @SerializedName("autoplay")
    val autoplay: Long,
    @SerializedName("bp")
    val bp: Long,
    @SerializedName("download")
    val download: Long,
    @SerializedName("elec")
    val elec: Long,
    @SerializedName("hd5")
    val hd5: Long,
    @SerializedName("is_cooperation")
    val isCooperation: Long,
    @SerializedName("movie")
    val movie: Long,
    @SerializedName("no_background")
    val noBackground: Long,
    @SerializedName("no_reprLong")
    val noReprLong: Long,
    @SerializedName("pay")
    val pay: Long,
    @SerializedName("pay_free_watch")
    val payFreeWatch: Long,
    @SerializedName("ugc_pay")
    val ugcPay: Long,
    @SerializedName("ugc_pay_preview")
    val ugcPayPreview: Long
)

data class Stat(
    @SerializedName("aid")
    val aid: Long,
    @SerializedName("coin")
    val coin: Long,
    @SerializedName("danmaku")
    val danmaku: Long,
    @SerializedName("dislike")
    val dislike: Long,
    @SerializedName("favorite")
    val favorite: Long,
    @SerializedName("his_rank")
    val hisRank: Long,
    @SerializedName("like")
    val like: Long,
    @SerializedName("now_rank")
    val nowRank: Long,
    @SerializedName("reply")
    val reply: Long,
    @SerializedName("share")
    val share: Long,
    @SerializedName("view")
    val view: Long
)

data class Page(
    @SerializedName("acount")
    val acount: Long,
    @SerializedName("count")
    val count: Long,
    @SerializedName("num")
    val num: Long,
    @SerializedName("size")
    val size: Long
)

data class ReplyX(
    @SerializedName("action")
    val action: Long,
    @SerializedName("assist")
    val assist: Long,
    @SerializedName("attr")
    val attr: Long,
    @SerializedName("content")
    val content: Content,
    @SerializedName("count")
    val count: Long,
    @SerializedName("ctime")
    val ctime: Long,
    @SerializedName("dialog")
    val dialog: Long,
    @SerializedName("fansgrade")
    val fansgrade: Long,
    @SerializedName("floor")
    val floor: Long,
    @SerializedName("like")
    val like: Long,
    @SerializedName("mid")
    val mid: Long,
    @SerializedName("oid")
    val oid: Long,
    @SerializedName("parent")
    val parent: Long,
    @SerializedName("rcount")
    val rcount: Long,
    @SerializedName("replies")
    val replies: Any?,
    @SerializedName("root")
    val root: Long,
    @SerializedName("rpid")
    val rpid: Long,
    @SerializedName("show_follow")
    val showFollow: Boolean,
    @SerializedName("state")
    val state: Long,
    @SerializedName("type")
    val type: Long
)

data class Content(
    @SerializedName("device")
    val device: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("plat")
    val plat: Long
)

data class Count(
    @SerializedName("atten")
    val atten: Long,
    @SerializedName("use")
    val use: Long,
    @SerializedName("view")
    val view: Long
)

data class DescV2(
    @SerializedName("biz_id")
    val bizId: Long,
    @SerializedName("raw_text")
    val rawText: String,
    @SerializedName("type")
    val type: Long
)

class HonorReply

data class PageX(
    @SerializedName("cid")
    val cid: Long,
    @SerializedName("dimension")
    val dimension: Dimension,
    @SerializedName("duration")
    val duration: Long,
    @SerializedName("first_frame")
    val firstFrame: String?,
    @SerializedName("from")
    val from: String,
    @SerializedName("page")
    val page: Long,
    @SerializedName("part")
    val part: String,
    @SerializedName("vid")
    val vid: String,
    @SerializedName("weblink")
    val weblink: String
)

data class RightsX(
    @SerializedName("arc_pay")
    val arcPay: Long,
    @SerializedName("autoplay")
    val autoplay: Long,
    @SerializedName("bp")
    val bp: Long,
    @SerializedName("clean_mode")
    val cleanMode: Long,
    @SerializedName("download")
    val download: Long,
    @SerializedName("elec")
    val elec: Long,
    @SerializedName("free_watch")
    val freeWatch: Long,
    @SerializedName("hd5")
    val hd5: Long,
    @SerializedName("is_360")
    val is360: Long,
    @SerializedName("is_cooperation")
    val isCooperation: Long,
    @SerializedName("is_stein_gate")
    val isSteinGate: Long,
    @SerializedName("movie")
    val movie: Long,
    @SerializedName("no_background")
    val noBackground: Long,
    @SerializedName("no_reprLong")
    val noReprLong: Long,
    @SerializedName("no_share")
    val noShare: Long,
    @SerializedName("pay")
    val pay: Long,
    @SerializedName("ugc_pay")
    val ugcPay: Long,
    @SerializedName("ugc_pay_preview")
    val ugcPayPreview: Long
)

data class StatX(
    @SerializedName("aid")
    val aid: Long,
    @SerializedName("argue_msg")
    val argueMsg: String,
    @SerializedName("coin")
    val coin: Long,
    @SerializedName("danmaku")
    val danmaku: Long,
    @SerializedName("dislike")
    val dislike: Long,
    @SerializedName("evaluation")
    val evaluation: String,
    @SerializedName("favorite")
    val favorite: Long,
    @SerializedName("his_rank")
    val hisRank: Long,
    @SerializedName("like")
    val like: Long,
    @SerializedName("now_rank")
    val nowRank: Long,
    @SerializedName("reply")
    val reply: Long,
    @SerializedName("share")
    val share: Long,
    @SerializedName("view")
    val view: Long
)

data class Subtitle(
    @SerializedName("allow_submit")
    val allowSubmit: Boolean,
    @SerializedName("list")
    val list: List<Any>
)

data class UserGarb(
    @SerializedName("url_image_ani_cut")
    val urlImageAniCut: String
)