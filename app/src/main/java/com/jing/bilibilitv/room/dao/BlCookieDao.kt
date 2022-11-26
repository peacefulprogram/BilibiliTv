package com.jing.bilibilitv.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jing.bilibilitv.room.entity.BLCookieEntity

@Dao
interface BlCookieDao {

    @Query("select * from bl_cookie where cookieName=:name")
    fun findByCookieName(name: String): BLCookieEntity?

    @Query("select * from bl_cookie")
    fun findAllCookie(): List<BLCookieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCookies(vararg cookie: BLCookieEntity)

    @Delete
    fun deleteCookie(cookie: BLCookieEntity)

    @Query("delete from bl_cookie")
    fun deleteAllCookie()
}