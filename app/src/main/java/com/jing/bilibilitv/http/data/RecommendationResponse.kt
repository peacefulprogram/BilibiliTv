package com.jing.bilibilitv.http.data

data class RecommendationResponse(
    val mid: Long,
    val item: List<VideoInfo>
)