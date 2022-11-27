package com.jing.bilibilitv.http.data

data class CommonDataResponse<T>(
    val code: Int,
    val message: String,
    val ttl: Int
) {
    var data: T? = null
        get() {
            if (!isRequestSuccess()) {
                throw RuntimeException(ResponseErrorCode.resolveErrorMessage(code))
            }
            return field
        }

    fun isRequestSuccess(): Boolean {
        return code == 0
    }
}
