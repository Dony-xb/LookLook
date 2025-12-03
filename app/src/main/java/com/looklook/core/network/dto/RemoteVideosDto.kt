package com.looklook.core.network.dto

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class VideosResponse(
    val videos: List<VideoDto>
)

@JsonClass(generateAdapter = true)
data class VideoDto(
    val id: String,
    val title: String,
    val description: String?,
    val videoUrl: String,
    val coverUrl: String,
    val user: UserDto,
    val tags: List<String> = emptyList(),
    val stats: StatsDto? = null,
    val createdAt: String?,
    @Json(name = "hometag") val homeTag: String? = null
)

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: String,
    val username: String,
    val avatarUrl: String
)

@JsonClass(generateAdapter = true)
data class StatsDto(
    val likesCount: Int?,
    val commentsCount: Int?,
    val sharesCount: Int?
)
