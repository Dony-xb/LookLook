package com.looklook.core.repository

import com.looklook.core.model.Video
import com.looklook.BuildConfig
import com.looklook.core.network.api.RemoteVideoApi
import com.looklook.core.network.dto.VideoDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val remoteApi: RemoteVideoApi
) {
    fun getLocalDebugVideoList(): List<Video> = listOf(
        Video(
            id = "local-1",
            title = "本地示例视频",
            tags = listOf("本地"),
            coverUrl = "https://picsum.photos/seed/local/600/800",
            streamUrl = BuildConfig.DEBUG_LOCAL_VIDEO_PATH.ifEmpty { "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" },
            authorName = "本地作者",
            authorAvatar = "https://picsum.photos/seed/localav/100/100"
        )
    )
    fun getStaticVideos(): Flow<List<Video>> = flow { emit(emptyList()) }

    fun getRemoteVideos(): Flow<List<Video>> = flow {
        try {
            val resp = remoteApi.fetch()
            val mapped = resp.videos.map { it.toDomain() }
            emit(mapped)
        } catch (e: Exception) {
            emit(getLocalDebugVideoList())
        }
    }
}

private fun VideoDto.toDomain(): Video = Video(
    id = id,
    title = title,
    description = description,
    tags = tags,
    coverUrl = sanitizeUrl(coverUrl),
    streamUrl = sanitizeUrl(videoUrl),
    authorName = user.username,
    authorAvatar = sanitizeUrl(user.avatarUrl),
    likesCount = stats?.likesCount,
    commentsCount = stats?.commentsCount,
    sharesCount = stats?.sharesCount,
    createdAt = createdAt,
    homeTag = homeTag
)

private fun sanitizeUrl(u: String?): String =
    (u ?: "").trim().trim('`').replace(" ", "")
