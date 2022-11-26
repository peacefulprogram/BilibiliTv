package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName

data class ApplyQrCodeResponse(
    val url: String,
    @SerializedName("qrcode_key")
    val qrCodeKey: String
)

data class QrCodeStatusResponse(
    val url: String,

    @SerializedName("refresh_token")
    val refreshToken: String,
    val timestamp: Long,
    /**
     * @see QrCodeStatus
     */
    val code: Int,
    val message: String
)

enum class QrCodeStatus(val code: Int) {
    /**
     * 未扫码
     */
    NOT_SCAN(86101),

    /**
     * 已过期
     */
    EXPIRED(86038),

    /**
     * 已扫描,未确认登录
     */
    SCANNED(86090),

    /**
     * 登录成功
     */
    LOGIN_SUCCESS(0);

    companion object {
        fun fromCodeValue(codeValue: Int) = values().find { it.code == codeValue }
            ?: throw IllegalStateException("无法识别的状态:$codeValue")
    }

}