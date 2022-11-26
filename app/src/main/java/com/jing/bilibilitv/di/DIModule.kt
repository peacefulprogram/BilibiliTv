package com.jing.bilibilitv.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jing.bilibilitv.BuildConfig
import com.jing.bilibilitv.http.api.BilibiliApi
import com.jing.bilibilitv.http.api.PassportApi
import com.jing.bilibilitv.http.cookie.BilibiliCookieJar
import com.jing.bilibilitv.room.BilibiliDatabase
import com.jing.bilibilitv.room.dao.BlCookieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DIModule {


    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): BilibiliDatabase =
        Room.databaseBuilder(context, BilibiliDatabase::class.java, "bl_db")
            .build()

    @Provides
    @Singleton
    fun provideBlCookieDao(database: BilibiliDatabase): BlCookieDao = database.blCookieDao()


    @Provides
    @Singleton
    fun provideCookieJar(cookieDao: BlCookieDao) = BilibiliCookieJar(cookieDao)

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .create()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun provideOkHttpClient(cookieJar: BilibiliCookieJar): OkHttpClient {

        val builder = OkHttpClient
            .Builder()
            .cookieJar(cookieJar)
            .addInterceptor(Interceptor { chain ->
                chain.request().newBuilder()
                    .header(
                        "user-agent",
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36"
                    )
                    .header("referer", "https://www.bilibili.com")
                    .build()
                    .let {
                        chain.proceed(it)
                    }
            })
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(Interceptor { chain ->
                val request = chain.request()
                val requestLines = mutableListOf<String>()
                val prefix = "==== "
                requestLines.add("$prefix> url: ${request.url}")
                request.headers.forEach {
                    requestLines.add("$prefix> ${it.first}: ${it.second}")
                }
                val response = chain.proceed(request)
                requestLines.add("<$prefix status: ${response.code}")
                val resp = if (response.body == null) {
                    response
                } else {
                    if ((response.body?.contentLength() ?: Long.MAX_VALUE) < (100 * 1024)) {
                        val bytes = response.body!!.bytes()
                        requestLines.add("<$prefix body: ${String(bytes, Charsets.UTF_8)}")
                        response.newBuilder()
                            .body(bytes.toResponseBody(response.body!!.contentType()))
                            .build()

                    } else {
                        response
                    }
                }
                requestLines.forEach {
                    println("request: $it")
                }
                resp
            })
        }
        return builder.build()
    }


    @Provides
    @Singleton
    fun provideLoginApi(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): PassportApi = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(PassportApi.BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .build()
        .create(PassportApi::class.java)


    @Provides
    @Singleton
    fun provideBilibiliApi(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): BilibiliApi = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BilibiliApi.BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .build()
        .create(BilibiliApi::class.java)


}