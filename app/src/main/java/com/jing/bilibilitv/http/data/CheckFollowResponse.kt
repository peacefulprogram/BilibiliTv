package com.jing.bilibilitv.http.data

import com.google.gson.annotations.SerializedName

data class CheckFollowResponse(
    @SerializedName("attribute")
    val attribute: Int,
    @SerializedName("mid")
    val mid: Int,
    @SerializedName("mtime")
    val mtime: Int,
    @SerializedName("special")
    val special: Int
) {
    fun isFollowing(): Boolean = attribute == 2 || attribute == 6
}

