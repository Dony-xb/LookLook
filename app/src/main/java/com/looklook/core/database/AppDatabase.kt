package com.looklook.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.looklook.core.database.dao.CachedVideoDao
import com.looklook.core.database.entity.CachedVideo

@Database(entities = [CachedVideo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cachedVideoDao(): CachedVideoDao
}

