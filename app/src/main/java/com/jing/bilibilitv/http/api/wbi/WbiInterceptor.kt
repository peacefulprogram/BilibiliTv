package com.jing.bilibilitv.http.api.wbi

import cn.hutool.crypto.digest.MD5
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.net.URLEncoder
import java.util.StringJoiner

const val WBI_HEADER = "need-wbi-sign"

private var wbiAndSubKey: Triple<String, String, Long>? = null

private val wbiClient = OkHttpClient()

private fun getWbiKey(): Pair<String, String> {
    val extractKey: (String) -> String = {
        it.substring(it.lastIndexOf('/') + 1, it.lastIndexOf('.'))
    }
    if (wbiAndSubKey == null) {
        synchronized(Unit) {
            if (wbiAndSubKey == null || wbiAndSubKey!!.third - System.currentTimeMillis() > 2 * 60 * 60 * 1000) {
                val pair = requestWbiKey()
                wbiAndSubKey =
                    Triple(
                        extractKey(pair.first),
                        extractKey(pair.second),
                        System.currentTimeMillis()
                    )
            }
        }
    }
    return Pair(wbiAndSubKey!!.first, wbiAndSubKey!!.second)
}

private fun requestWbiKey(): Pair<String, String> {
    val resp = wbiClient.newCall(
        Request.Builder()
            .header("referer", "https://www.bilibili.com")
            .header(
                "user-agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36"
            )
            .url("https://api.bilibili.com/x/web-interface/nav")
            .get()
            .build()
    ).execute()
    val map = Gson().fromJson<Map<String, Any>>(resp.body!!.string(), Map::class.java)
    val wbiImage = (map["data"] as Map<*, *>)["wbi_img"] as Map<*, *>
    return Pair(wbiImage["img_url"] as String, wbiImage["sub_url"] as String)
}

class WbiInterceptor : Interceptor {
    companion object {
        val encodeTab = arrayOf(
            46, 47, 18, 2,
            53, 8, 23, 32,
            15, 50, 10, 31,
            58, 3, 45, 35,
            27, 43, 5, 49,
            33, 9, 42, 19,
            29, 28, 14, 39,
            12, 38, 41, 13
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url
        if (chain.request().header(WBI_HEADER)
                ?.isNotEmpty() != true || url.queryParameterNames.isEmpty()
        ) {
            return chain.proceed(chain.request())
        }
        val queryMap = mutableMapOf<String, String>()

        url.queryParameterNames.forEach {
            val value = url.queryParameter(it)
            if (value?.isNotEmpty() == true) {
                queryMap[it] = value
            }
        }
        val queryJoiner = StringJoiner("&")
        queryMap.keys.toList().sorted().forEach {
            queryJoiner.add("${it.encodeUrl()}=${queryMap[it]!!.encodeUrl()}")
        }
        val (wbiKey, subKey) = getWbiKey()
        val concat = wbiKey + subKey
        val wts = (System.currentTimeMillis() / 1000).toString()
        val mixinKey = encodeTab.asSequence().map {
            concat[it]
        }.joinToString(separator = "")
        val wtsQuery = "$queryJoiner&wts=$wts"
        val sign = MD5.create().digestHex(wtsQuery + mixinKey)
        val req = chain.request().newBuilder()
            .url(url.newBuilder().apply {
                encodedQuery("$wtsQuery&w_rid=$sign")
            }.build())
            .removeHeader(WBI_HEADER)
            .build()
        return chain.proceed(req)
    }

    private fun String.encodeUrl() = URLEncoder.encode(this, "UTF-8")

}
