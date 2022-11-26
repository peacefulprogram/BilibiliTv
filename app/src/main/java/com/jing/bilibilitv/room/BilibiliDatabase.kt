package com.jing.bilibilitv.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jing.bilibilitv.room.dao.BlCookieDao
import com.jing.bilibilitv.room.entity.BLCookieEntity

@Database(entities = [BLCookieEntity::class], version = 1, exportSchema = false)
abstract class BilibiliDatabase : RoomDatabase() {
    abstract fun blCookieDao(): BlCookieDao
}