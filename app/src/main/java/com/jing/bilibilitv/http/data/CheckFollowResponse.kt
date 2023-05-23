package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName

data class CheckFollowResponse(
    @SerializedName("attribute")
    val attribute: Long,
    @SerializedName("mid")
    val mid: Long,
    @SerializedName("mtime")
    val mtime: Long,
    @SerializedName("special")
    val special: Long
) {
    fun isFollowing(): Boolean = attribute == 2L || attribute == 6L
}

