package com.jing.bilibilitv.http.api

import com.jing.bilibilitv.http.data.ApplyQrCodeResponse
import com.jing.bilibilitv.http.data.CommonDataResponse
import com.jing.bilibilitv.http.data.QrCodeStatusResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PassportApi {

    @GET("/x/passport-login/web/qrcode/generate")
    suspend fun applyQrCode(): CommonDataResponse<ApplyQrCodeResponse>

    @GET("/x/passport-login/web/qrcode/poll")
    suspend fun pollQrCodeStatus(@Query("qrcode_key") qrCodeKey: String): CommonDataResponse<QrCodeStatusResponse>

    companion object {
        const val BASE_URL = "https://passport.bilibili.com"
    }

}