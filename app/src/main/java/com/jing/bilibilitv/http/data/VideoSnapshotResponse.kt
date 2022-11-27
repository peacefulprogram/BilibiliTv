package com.jing.bilibilitv.http.data
import com.google.gson.annotations.SerializedName


data class VideoSnapshotResponse(
    @SerializedName("image")
    val image: List<String>,
    @SerializedName("img_x_len")
    val imgXLen: Int,
    @SerializedName("img_x_size")
    val imgXSize: Int,
    @SerializedName("img_y_len")
    val imgYLen: Int,
    @SerializedName("img_y_size")
    val imgYSize: Int,
    @SerializedName("index")
    val index: List<Long>,
    @SerializedName("pvdata")
    val pvdata: String
)