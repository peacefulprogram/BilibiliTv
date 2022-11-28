package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


data class LastPlayResponse(
    @SerializedName("aid")
    val aid: Long,
    @SerializedName("allow_bp")
    val allowBp: Boolean,
    @SerializedName("answer_status")
    val answerStatus: Long,
    @SerializedName("bgm_info")
    val bgmInfo: Any?,
    @SerializedName("block_time")
    val blockTime: Long,
    @SerializedName("bvid")
    val bvid: String,
    @SerializedName("cid")
    val cid: Long,
    @SerializedName("fawkes")
    val fawkes: LastPlayFawkes,
    @SerializedName("guide_attention")
    val guideAttention: List<LastPlayGuideAttention>,
    @SerializedName("has_next")
    val hasNext: Boolean,
    @SerializedName("ip_info")
    val ipInfo: LastPlayIpInfo,
    @SerializedName("is_owner")
    val isOwner: Boolean,
    @SerializedName("is_ugc_pay_preview")
    val isUgcPayPreview: Boolean,
    @SerializedName("jump_card")
    val jumpCard: List<Any>,
    /**
     * 上次播放的cid
     */
    @SerializedName("last_play_cid")
    val lastPlayCid: Long,
    /**
     * 上次播放的时间
     */
    @SerializedName("last_play_time")
    val lastPlayTime: Long,
    @SerializedName("level_info")
    val levelInfo: LastPlayLevelInfo,
    @SerializedName("login_mid")
    val loginMid: Long,
    @SerializedName("login_mid_hash")
    val loginMidHash: String,
    @SerializedName("max_limit")
    val maxLimit: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("no_share")
    val noShare: Boolean,
    @SerializedName("now_time")
    val nowTime: Long,
    @SerializedName("online_count")
    val onlineCount: Long,
    @SerializedName("online_switch")
    val onlineSwitch: LastPlayOnlineSwitch,
    @SerializedName("operation_card")
    val operationCard: List<Any>,
    @SerializedName("options")
    val options: LastPlayOptions,
    @SerializedName("page_no")
    val pageNo: Long,
    @SerializedName("pcdn_loader")
    val pcdnLoader: LastPlayPcdnLoader,
    @SerializedName("permission")
    val permission: String,
    @SerializedName("preview_toast")
    val previewToast: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("show_switch")
    val showSwitch: LastPlayShowSwitch,
    @SerializedName("subtitle")
    val subtitle: LastPlaySubtitle,
    @SerializedName("toast_block")
    val toastBlock: Boolean,
    @SerializedName("view_poLongs")
    val viewPoLongs: List<Any>,
    @SerializedName("vip")
    val vip: LastPlayVip
)

data class LastPlayFawkes(
    @SerializedName("config_version")
    val configVersion: Long,
    @SerializedName("ff_version")
    val ffVersion: Long
)

data class LastPlayGuideAttention(
    @SerializedName("from")
    val from: Long,
    @SerializedName("pos_x")
    val posX: Long,
    @SerializedName("pos_y")
    val posY: Long,
    @SerializedName("to")
    val to: Long,
    @SerializedName("type")
    val type: Long
)

data class LastPlayIpInfo(
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("ip")
    val ip: String,
    @SerializedName("province")
    val province: String,
    @SerializedName("zone_id")
    val zoneId: Long,
    @SerializedName("zone_ip")
    val zoneIp: String
)

data class LastPlayLevelInfo(
    @SerializedName("current_exp")
    val currentExp: Long,
    @SerializedName("current_level")
    val currentLevel: Long,
    @SerializedName("current_min")
    val currentMin: Long,
    @SerializedName("level_up")
    val levelUp: Long,
    @SerializedName("next_exp")
    val nextExp: Long
)

data class LastPlayOnlineSwitch(
    @SerializedName("enable_gray_dash_playback")
    val enableGrayDashPlayback: String,
    @SerializedName("new_broadcast")
    val newBroadcast: String,
    @SerializedName("realtime_dm")
    val realtimeDm: String,
    @SerializedName("subtitle_submit_switch")
    val subtitleSubmitSwitch: String
)

data class LastPlayOptions(
    @SerializedName("is_360")
    val is360: Boolean,
    @SerializedName("without_vip")
    val withoutVip: Boolean
)

data class LastPlayPcdnLoader(
    @SerializedName("dash")
    val dash: LastPlayDash,
    @SerializedName("flv")
    val flv: LastPlayFlv
)

data class LastPlayShowSwitch(
    @SerializedName("long_progress")
    val longProgress: Boolean
)

data class LastPlaySubtitle(
    @SerializedName("allow_submit")
    val allowSubmit: Boolean,
    @SerializedName("lan")
    val lan: String,
    @SerializedName("lan_doc")
    val lanDoc: String
)

data class LastPlayVip(
    @SerializedName("avatar_subscript")
    val avatarSubscript: Long,
    @SerializedName("avatar_subscript_url")
    val avatarSubscriptUrl: String,
    @SerializedName("due_date")
    val dueDate: Long,
    @SerializedName("label")
    val label: LastPlayLabel,
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
    val vipPayType: Long
)

data class LastPlayDash(
    @SerializedName("labels")
    val labels: LastPlayLabels
)

data class LastPlayFlv(
    @SerializedName("labels")
    val labels: LastPlayLabels
)

data class LastPlayLabels(
    @SerializedName("pcdn_group")
    val pcdnGroup: String,
    @SerializedName("pcdn_stage")
    val pcdnStage: String,
    @SerializedName("pcdn_vendor")
    val pcdnVendor: String,
    @SerializedName("pcdn_version")
    val pcdnVersion: String,
    @SerializedName("pcdn_video_type")
    val pcdnVideoType: String
)

data class LastPlayLabel(
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