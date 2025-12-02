package com.looklook.core.model

data class Video(
    val id: String,
    val title: String,
    val description: String? = null,
    val tags: List<String>,
    val coverUrl: String,
    val streamUrl: String,
    val authorName: String,
    val authorAvatar: String
)

