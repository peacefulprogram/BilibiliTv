package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName

data class DynamicResponse(
    @SerializedName("has_more")
    val hasMore: Boolean,
    @SerializedName("items")
    val items: List<DynamicItem>,
    @SerializedName("offset")
    val offset: String,
    @SerializedName("update_baseline")
    val updateBaseline: String,
    @SerializedName("update_num")
    val updateNum: Int
)

data class DynamicItem(
    @SerializedName("basic")
    val basic: DynamicBasic,
    @SerializedName("id_str")
    val idStr: String,
    @SerializedName("modules")
    val modules: DynamicModules,
    @SerializedName("orig")
    val orig: DynamicOrig?,
    @SerializedName("type")
    val type: String,
    @SerializedName("visible")
    val visible: Boolean
)

data class DynamicBasic(
    @SerializedName("comment_id_str")
    val commentIdStr: String,
    @SerializedName("comment_type")
    val commentType: Int,
    @SerializedName("like_icon")
    val likeIcon: DynamicLikeIcon,
    @SerializedName("rid_str")
    val ridStr: String
)

data class DynamicModules(
    @SerializedName("module_author")
    val moduleAuthor: DynamicModuleAuthor,
    @SerializedName("module_dynamic")
    val moduleDynamic: DynamicModuleDynamic,
//    @SerializedName("module_interaction")
//    val moduleInteraction: DynamicModuleInteraction?,
//    @SerializedName("module_more")
//    val moduleMore: DynamicModuleMore,
    @SerializedName("module_stat")
    val moduleStat: DynamicModuleStat
)

data class DynamicOrig(
    @SerializedName("basic")
    val basic: DynamicBasicX,
    @SerializedName("id_str")
    val idStr: String,
    @SerializedName("modules")
    val modules: DynamicModulesX,
    @SerializedName("type")
    val type: String,
    @SerializedName("visible")
    val visible: Boolean
)

data class DynamicLikeIcon(
    @SerializedName("action_url")
    val actionUrl: String,
    @SerializedName("end_url")
    val endUrl: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("start_url")
    val startUrl: String
)

data class DynamicModuleAuthor(
    @SerializedName("decorate")
    val decorate: DynamicDecorate?,
    @SerializedName("face")
    val face: String,
    @SerializedName("face_nft")
    val faceNft: Boolean,
    @SerializedName("following")
    val following: Boolean,
    @SerializedName("jump_url")
    val jumpUrl: String,
    @SerializedName("label")
    val label: String,
    @SerializedName("mid")
    val mid: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("official_verify")
    val officialVerify: DynamicOfficialVerify?,
    @SerializedName("pendant")
    val pendant: DynamicPendant?,
    @SerializedName("pub_action")
    val pubAction: String,
    @SerializedName("pub_location_text")
    val pubLocationText: String?,
    @SerializedName("pub_time")
    val pubTime: String,
    @SerializedName("pub_ts")
    val pubTs: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("vip")
    val vip: DynamicVip?
)

data class DynamicModuleDynamic(
    @SerializedName("additional")
    val additional: DynamicAdditional?,
    @SerializedName("desc")
    val desc: DynamicDesc?,
    @SerializedName("major")
    val major: DynamicMajor?,
    @SerializedName("topic")
    val topic: DynamicTopic?
)

data class DynamicModuleInteraction(
    @SerializedName("items")
    val items: List<DynamicItemXX>
)

data class DynamicModuleMore(
    @SerializedName("three_point_items")
    val threePointItems: List<DynamicThreePointItem>
)

data class DynamicModuleStat(
    @SerializedName("comment")
    val comment: DynamicComment,
    @SerializedName("forward")
    val forward: DynamicForward,
    @SerializedName("like")
    val like: DynamicLike
)

data class DynamicDecorate(
    @SerializedName("card_url")
    val cardUrl: String,
    @SerializedName("fan")
    val fan: DynamicFan,
    @SerializedName("id")
    val id: Int,
    @SerializedName("jump_url")
    val jumpUrl: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: Int
)

data class DynamicOfficialVerify(
    @SerializedName("desc")
    val desc: String,
    @SerializedName("type")
    val type: Int
)

data class DynamicPendant(
    @SerializedName("expire")
    val expire: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("image_enhance")
    val imageEnhance: String,
    @SerializedName("image_enhance_frame")
    val imageEnhanceFrame: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("pid")
    val pid: Int
)

data class DynamicVip(
    @SerializedName("avatar_subscript")
    val avatarSubscript: Int,
    @SerializedName("avatar_subscript_url")
    val avatarSubscriptUrl: String,
    @SerializedName("due_date")
    val dueDate: Long,
    @SerializedName("label")
    val label: DynamicLabel,
    @SerializedName("nickname_color")
    val nicknameColor: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("theme_type")
    val themeType: Int,
    @SerializedName("type")
    val type: Int
)

data class DynamicFan(
    @SerializedName("color")
    val color: String,
    @SerializedName("is_fan")
    val isFan: Boolean,
    @SerializedName("num_str")
    val numStr: String,
    @SerializedName("number")
    val number: Int
)

data class DynamicLabel(
    @SerializedName("bg_color")
    val bgColor: String,
    @SerializedName("bg_style")
    val bgStyle: Int,
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

data class DynamicAdditional(
    @SerializedName("type")
    val type: String,
    @SerializedName("vote")
    val vote: DynamicVote
)

data class DynamicDesc(
    @SerializedName("rich_text_nodes")
    val richTextNodes: List<DynamicRichTextNode>,
    @SerializedName("text")
    val text: String
)

data class DynamicMajor(
    @SerializedName("archive")
    val archive: DynamicArchive?,
    @SerializedName("draw")
    val draw: DynamicDraw?,
    @SerializedName("live_rcmd")
    val liveRcmd: DynamicLiveRcmd?,
    @SerializedName("pgc")
    val pgc: DynamicPgc?,
    @SerializedName("type")
    val type: String
)

data class DynamicTopic(
    @SerializedName("id")
    val id: Int,
    @SerializedName("jump_url")
    val jumpUrl: String,
    @SerializedName("name")
    val name: String
)

data class DynamicVote(
    @SerializedName("choice_cnt")
    val choiceCnt: Int,
    @SerializedName("default_share")
    val defaultShare: Int,
    @SerializedName("desc")
    val desc: String,
    @SerializedName("end_time")
    val endTime: Long,
    @SerializedName("join_num")
    val joinNum: Int,
    @SerializedName("status")
    val status: Int,
    @SerializedName("uid")
    val uid: Int,
    @SerializedName("vote_id")
    val voteId: Int
)

data class DynamicRichTextNode(
    @SerializedName("emoji")
    val emoji: DynamicEmoji?,
    @SerializedName("jump_url")
    val jumpUrl: String?,
    @SerializedName("orig_text")
    val origText: String,
    @SerializedName("rid")
    val rid: String?,
    @SerializedName("text")
    val text: String,
    @SerializedName("type")
    val type: String
)

data class DynamicEmoji(
    @SerializedName("icon_url")
    val iconUrl: String,
    @SerializedName("size")
    val size: Int,
    @SerializedName("text")
    val text: String,
    @SerializedName("type")
    val type: Int
)

data class DynamicArchive(
    @SerializedName("aid")
    val aid: String,
    @SerializedName("badge")
    val badge: DynamicBadge,
    @SerializedName("bvid")
    val bvid: String,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("desc")
    val desc: String,
    @SerializedName("disable_preview")
    val disablePreview: Int,
    @SerializedName("duration_text")
    val durationText: String,
    @SerializedName("jump_url")
    val jumpUrl: String,
    @SerializedName("stat")
    val stat: DynamicStat,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: Int
)

data class DynamicDraw(
    @SerializedName("id")
    val id: Int,
    @SerializedName("items")
    val items: List<DynamicItemX>
)

data class DynamicLiveRcmd(
    @SerializedName("content")
    val content: String,
    @SerializedName("reserve_type")
    val reserveType: Int
)

data class DynamicPgc(
    @SerializedName("badge")
    val badge: DynamicBadge,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("epid")
    val epid: Int,
    @SerializedName("jump_url")
    val jumpUrl: String,
    @SerializedName("season_id")
    val seasonId: Int,
    @SerializedName("stat")
    val stat: DynamicStat,
    @SerializedName("sub_type")
    val subType: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: Int
)

data class DynamicBadge(
    @SerializedName("bg_color")
    val bgColor: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("text")
    val text: String
)

data class DynamicStat(
    @SerializedName("danmaku")
    val danmaku: String,
    @SerializedName("play")
    val play: String
)

data class DynamicItemX(
    @SerializedName("height")
    val height: Int,
    @SerializedName("size")
    val size: Double,
    @SerializedName("src")
    val src: String,
    @SerializedName("width")
    val width: Int
)

data class DynamicItemXX(
    @SerializedName("desc")
    val desc: DynamicDescX,
    @SerializedName("type")
    val type: Int
)

data class DynamicDescX(
    @SerializedName("rich_text_nodes")
    val richTextNodes: List<DynamicRichTextNodeX>,
    @SerializedName("text")
    val text: String
)

data class DynamicRichTextNodeX(
    @SerializedName("emoji")
    val emoji: DynamicEmoji?,
    @SerializedName("orig_text")
    val origText: String,
    @SerializedName("rid")
    val rid: String?,
    @SerializedName("text")
    val text: String,
    @SerializedName("type")
    val type: String
)

data class DynamicThreePointItem(
    @SerializedName("label")
    val label: String,
    @SerializedName("type")
    val type: String
)

data class DynamicComment(
    @SerializedName("count")
    val count: Int,
    @SerializedName("forbidden")
    val forbidden: Boolean,
    @SerializedName("hidden")
    val hidden: Boolean?
)

data class DynamicForward(
    @SerializedName("count")
    val count: Int,
    @SerializedName("forbidden")
    val forbidden: Boolean
)

data class DynamicLike(
    @SerializedName("count")
    val count: Int,
    @SerializedName("forbidden")
    val forbidden: Boolean,
    @SerializedName("status")
    val status: Boolean
)

data class DynamicBasicX(
    @SerializedName("comment_id_str")
    val commentIdStr: String,
    @SerializedName("comment_type")
    val commentType: Int,
    @SerializedName("like_icon")
    val likeIcon: DynamicLikeIcon,
    @SerializedName("rid_str")
    val ridStr: String
)

data class DynamicModulesX(
    @SerializedName("module_author")
    val moduleAuthor: DynamicModuleAuthorX,
    @SerializedName("module_dynamic")
    val moduleDynamic: DynamicModuleDynamicX
)

data class DynamicModuleAuthorX(
    @SerializedName("decorate")
    val decorate: DynamicDecorateX?,
    @SerializedName("face")
    val face: String,
    @SerializedName("face_nft")
    val faceNft: Boolean,
    @SerializedName("following")
    val following: Boolean?,
    @SerializedName("jump_url")
    val jumpUrl: String,
    @SerializedName("label")
    val label: String,
    @SerializedName("mid")
    val mid: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("official_verify")
    val officialVerify: DynamicOfficialVerify,
    @SerializedName("pendant")
    val pendant: DynamicPendant,
    @SerializedName("pub_action")
    val pubAction: String,
    @SerializedName("pub_time")
    val pubTime: String,
    @SerializedName("pub_ts")
    val pubTs: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("vip")
    val vip: DynamicVipX
)

data class DynamicModuleDynamicX(
    @SerializedName("additional")
    val additional: Any?,
    @SerializedName("desc")
    val desc: DynamicDescXX,
    @SerializedName("major")
    val major: DynamicMajorX,
    @SerializedName("topic")
    val topic: Any?
)

data class DynamicDecorateX(
    @SerializedName("card_url")
    val cardUrl: String,
    @SerializedName("fan")
    val fan: DynamicFan,
    @SerializedName("id")
    val id: Int,
    @SerializedName("jump_url")
    val jumpUrl: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: Int
)

data class DynamicVipX(
    @SerializedName("avatar_subscript")
    val avatarSubscript: Int,
    @SerializedName("avatar_subscript_url")
    val avatarSubscriptUrl: String,
    @SerializedName("due_date")
    val dueDate: Long,
    @SerializedName("label")
    val label: DynamicLabel,
    @SerializedName("nickname_color")
    val nicknameColor: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("theme_type")
    val themeType: Int,
    @SerializedName("type")
    val type: Int
)

data class DynamicDescXX(
    @SerializedName("rich_text_nodes")
    val richTextNodes: List<DynamicRichTextNodeXX>,
    @SerializedName("text")
    val text: String
)

data class DynamicMajorX(
    @SerializedName("archive")
    val archive: DynamicArchiveX,
    @SerializedName("type")
    val type: String
)

data class DynamicRichTextNodeXX(
    @SerializedName("jump_url")
    val jumpUrl: String?,
    @SerializedName("orig_text")
    val origText: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("type")
    val type: String
)

data class DynamicArchiveX(
    @SerializedName("aid")
    val aid: String,
    @SerializedName("badge")
    val badge: DynamicBadge,
    @SerializedName("bvid")
    val bvid: String,
    @SerializedName("cover")
    val cover: String,
    @SerializedName("desc")
    val desc: String,
    @SerializedName("disable_preview")
    val disablePreview: Int,
    @SerializedName("duration_text")
    val durationText: String,
    @SerializedName("jump_url")
    val jumpUrl: String,
    @SerializedName("stat")
    val stat: DynamicStat,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: Int
)

enum class DynamicType(val value: String? = null) {
    /**
     * 转发动态
     */
    DYNAMIC_TYPE_FORWARD,

    /**
     * 投稿视频
     */
    DYNAMIC_TYPE_AV,

    DYNAMIC_TYPE_DRAW,

    /**
     * 直播
     */
    DYNAMIC_TYPE_LIVE_RCMD,

    /**
     * 全部类型
     */
    ALL("all");

    fun getKey(): String {
        return this.value ?: this.name
    }
}