package com.looklook.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.looklook.core.database.entity.CachedVideo

@Dao
interface CachedVideoDao {
    @Query("SELECT * FROM cached_videos")
    suspend fun getAll(): List<CachedVideo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CachedVideo>)
}

