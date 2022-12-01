package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName


data class KeywordSuggestResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("ref")
    val ref: Int,
    @SerializedName("spid")
    val spid: Int,
    @SerializedName("term")
    val term: String,
    @SerializedName("value")
    val value: String
)