package com.jing.bilibilitv.http.cookie

import android.util.Log
import com.jing.bilibilitv.BuildConfig
import com.jing.bilibilitv.room.dao.BlCookieDao
import com.jing.bilibilitv.room.entity.BLCookieEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantReadWriteLock

class BilibiliCookieJar(
    private val cookieDao: BlCookieDao
) : CookieJar {

    private val TAG = "CookieJar"


    val readWriteLock = ReentrantReadWriteLock()

    val cookieStore = mutableListOf<Cookie>()

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        if (cookieStore.isEmpty()) {
            return emptyList()
        }
        return aquireLockAndProcess(readWriteLock.readLock()) {
            val now = System.currentTimeMillis()
            cookieStore.filter {
                it.matches(url) && it.expiresAt > now
            }
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isEmpty()) {
            return
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "saveFromResponse: " + cookies.joinToString { it.name })
        }
        val pathSet = cookies.mapTo(mutableSetOf()) { it.path + '|' + it.domain }
        aquireLockAndProcess(readWriteLock.writeLock()) {
            cookieStore.removeIf { pathSet.contains(it.path + '|' + it.domain) }
            cookieStore.addAll(cookies)
        }
        GlobalScope.launch(Dispatchers.IO) {
            val saveCookieArray = cookies.map { cookie ->
                BLCookieEntity(
                    cookieName = cookie.name,
                    cookieValue = cookie.value,
                    expiresAt = cookie.expiresAt,
                    domain = cookie.domain,
                    path = cookie.path,
                    secure = cookie.secure,
                    httpOnly = cookie.httpOnly,
                    hostOnly = cookie.hostOnly
                )
            }
                .toTypedArray()
            cookieDao.insertCookies(*saveCookieArray)
        }
    }

    fun <T> aquireLockAndProcess(lock: Lock, func: () -> T): T {
        lock.lock()
        try {
            return func()
        } finally {
            lock.unlock()
        }
    }


    fun loadCookieFromDatabase() {
        val initialCookies = cookieDao.findAllCookie().map { dbCookie ->
            Cookie.Builder()
                .name(dbCookie.cookieName)
                .value(dbCookie.cookieValue)
                .expiresAt(dbCookie.expiresAt)
                .domain(dbCookie.domain)
                .path(dbCookie.path)
                .apply {
                    if (dbCookie.secure) {
                        secure()
                    }
                    if (dbCookie.httpOnly) {
                        httpOnly()
                    }
                    if (dbCookie.hostOnly) {
                        hostOnlyDomain(dbCookie.domain)
                    }
                }
                .build()
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "loadCookieFromDatabase: " + initialCookies.joinToString { it.name })
        }
        cookieStore.addAll(initialCookies)
    }
}