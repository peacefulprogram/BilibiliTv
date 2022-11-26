package com.jing.bilibilitv.http.data

data class CommonDataResponse<T>(
    val code:Int,
    val message:String,
    val data:T?,
    val ttl:Int
)
