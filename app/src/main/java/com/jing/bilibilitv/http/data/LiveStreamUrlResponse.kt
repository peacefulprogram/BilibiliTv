package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


data class LiveStreamUrlResponse(
    @SerializedName("accept_quality")
    val acceptQuality: List<String>,
    @SerializedName("current_qn")
    val currentQn: Long,
    @SerializedName("current_quality")
    val currentQuality: Long,
    @SerializedName("durl")
    val durl: List<LiveStreamUrlDurl>,
    @SerializedName("quality_description")
    val qualityDescription: List<LiveStreamUrlQualityDescription>
)

data class LiveStreamUrlDurl(
    @SerializedName("length")
    val length: Long,
    @SerializedName("order")
    val order: Long,
    @SerializedName("p2p_type")
    val p2pType: Long,
    @SerializedName("stream_type")
    val streamType: Long,
    @SerializedName("url")
    val url: String
)

data class LiveStreamUrlQualityDescription(
    @SerializedName("desc")
    val desc: String,
    @SerializedName("qn")
    val qn: Long
)