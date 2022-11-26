package com.jing.bilibilitv.http.data
import com.google.gson.annotations.SerializedName


data class VideoUrlResponse(
    @SerializedName("accept_description")
    val acceptDescription: List<String>,
    @SerializedName("accept_format")
    val acceptFormat: String,
    @SerializedName("accept_quality")
    val acceptQuality: List<Int>,
    @SerializedName("dash")
    val dash: VideoUrlDash?,
    @SerializedName("format")
    val format: String,
    @SerializedName("from")
    val from: String,
    @SerializedName("high_format")
    val highFormat: Any?,
    @SerializedName("last_play_cid")
    val lastPlayCid: Long,
    @SerializedName("last_play_time")
    val lastPlayTime: Long,
    @SerializedName("message")
    val message: String,
    @SerializedName("quality")
    val quality: Int,
    @SerializedName("result")
    val result: String,
    @SerializedName("seek_param")
    val seekParam: String,
    @SerializedName("seek_type")
    val seekType: String,
    @SerializedName("support_formats")
    val supportFormats: List<VideoUrlSupportFormat>,
    @SerializedName("timelength")
    val timelength: Int,
    @SerializedName("video_codecid")
    val videoCodecid: Int
)

data class VideoUrlDash(
    @SerializedName("audio")
    val audio: List<VideoUrlAudio>,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("minBufferTime")
    val minBufferTime: Double,
    @SerializedName("video")
    val video: List<VideoUrlVideo>
)

data class VideoUrlSupportFormat(
    @SerializedName("codecs")
    val codecs: List<String>,
    @SerializedName("display_desc")
    val displayDesc: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("new_description")
    val newDescription: String,
    @SerializedName("quality")
    val quality: Int,
    @SerializedName("superscript")
    val superscript: String
)

data class VideoUrlAudio(
    @SerializedName("backup_url")
    val backupUrl: List<String>,
    @SerializedName("bandwidth")
    val bandwidth: Int,
    @SerializedName("base_url")
    val baseUrl: String,
    @SerializedName("codecid")
    val codecid: Int,
    @SerializedName("codecs")
    val codecs: String,
    @SerializedName("frame_rate")
    val frameRate: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("mimeType")
    val mimeType: String,
    @SerializedName("sar")
    val sar: String,
    @SerializedName("segment_base")
    val segmentBase: VideoUrlSegmentBaseX,
    @SerializedName("start_with_sap")
    val startWithSap: Int,
    @SerializedName("width")
    val width: Int
)

data class VideoUrlVideo(
    @SerializedName("backup_url")
    val backupUrl: List<String>,
    @SerializedName("bandwidth")
    val bandwidth: Int,
    @SerializedName("base_url")
    val baseUrl: String,
    @SerializedName("codecid")
    val codecid: Int,
    @SerializedName("codecs")
    val codecs: String,
    @SerializedName("frame_rate")
    val frameRate: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("mime_type")
    val mimeType: String,
    @SerializedName("sar")
    val sar: String,
    @SerializedName("segment_base")
    val segmentBase: VideoUrlSegmentBase,
    @SerializedName("start_with_sap")
    val startWithSap: Int,
    @SerializedName("width")
    val width: Int
)

data class VideoUrlSegmentBase(
    @SerializedName("indexRange")
    val indexRange: String,
    @SerializedName("Initialization")
    val initialization: String
)

data class VideoUrlSegmentBaseX(
    @SerializedName("index_range")
    val indexRange: String,
    @SerializedName("initialization")
    val initialization: String
)