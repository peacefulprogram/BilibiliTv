package com.jing.bilibilitv.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bl_cookie")
data class BLCookieEntity(
    @PrimaryKey val cookieName: String,
    val cookieValue: String,
    val expiresAt: Long,
    val domain: String,
    val path: String,
    val secure: Boolean,
    val httpOnly: Boolean,
    val hostOnly: Boolean
)