package com.looklook.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_videos")
data class CachedVideo(
    @PrimaryKey val id: String,
    val title: String,
    val coverUrl: String
)

