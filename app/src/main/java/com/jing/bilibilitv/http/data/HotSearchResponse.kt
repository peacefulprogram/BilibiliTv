package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


data class HotSearchResponse(
    @SerializedName("code")
    val code: Long,
    @SerializedName("cost")
    val cost: HotSearchCost,
    @SerializedName("exp_str")
    val expStr: String,
    @SerializedName("list")
    val list: List<HotSearchKeyword>,
    @SerializedName("message")
    val message: String,
    @SerializedName("seid")
    val seid: String,
    @SerializedName("timestamp")
    val timestamp: Long
)

data class HotSearchCost(
    @SerializedName("deserialize_response")
    val deserializeResponse: String,
    @SerializedName("hotword_request")
    val hotwordRequest: String,
    @SerializedName("hotword_request_format")
    val hotwordRequestFormat: String,
    @SerializedName("hotword_response_format")
    val hotwordResponseFormat: String,
    @SerializedName("main_handler")
    val mainHandler: String,
    @SerializedName("params_check")
    val paramsCheck: String,
    @SerializedName("total")
    val total: String
)

data class HotSearchKeyword(
    @SerializedName("call_reason")
    val callReason: Long,
    @SerializedName("goto_type")
    val gotoType: Long,
    @SerializedName("goto_value")
    val gotoValue: String,
    @SerializedName("hot_id")
    val hotId: Long,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("id")
    val id: Long,
    @SerializedName("keyword")
    val keyword: String,
    @SerializedName("name_type")
    val nameType: String,
    @SerializedName("pos")
    val pos: Long,
    @SerializedName("resource_id")
    val resourceId: Long,
    @SerializedName("score")
    val score: Double,
    @SerializedName("show_name")
    val showName: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("word_type")
    val wordType: Long
)