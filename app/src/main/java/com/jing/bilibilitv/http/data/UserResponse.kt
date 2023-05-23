package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName

data class UserInfo(
    val isLogin: Boolean,
    val face: String,
    @SerializedName("level_info")
    val levelInfo: UserLevelInfo,
    val mid: Long,
    /**
     * 是否验证手机号
     * 0：未验证
     * 1：已验证
     */
    @SerializedName("mobile_verified")
    val mobileVerified: Long,
    val money: Double,
    val uname: String,
    /**
     * 会员到期时间	毫秒 时间戳
     */
    val vipDueDate: Long,
    /**
     * 会员开通状态
     * 0：无
     * 1：有
     */
    val vipStatus: Long,
    /**
     * 会员类型
     * 0：无
     * 1：月度大会员
     * 2：年度及以上大会员
     */
    val vipType: Long
)

data class UserLevelInfo(
    @SerializedName("current_level")
    val currentLevel: Long,
    @SerializedName("current_min")
    val currentMin: Long,
    @SerializedName("current_exp")
    val currentExp: Long,
)