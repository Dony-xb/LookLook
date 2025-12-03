package com.looklook.core.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.looklook.core.model.Video
import com.looklook.core.network.api.RemoteVideoApi

class VideoPagingSource(
    private val api: RemoteVideoApi,
    private val pageSize: Int = 10
) : PagingSource<Int, Video>() {
    private var cached: List<Video>? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {
        return try {
            val page = params.key ?: 0
            val all = cached ?: api.fetch().videos.map { it.toDomain() }.also { cached = it }
            val from = page * pageSize
            val to = minOf(from + pageSize, all.size)
            val data = if (from < to) all.subList(from, to) else emptyList()
            val nextKey = if (to < all.size) page + 1 else null
            val prevKey = if (page > 0) page - 1 else null
            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Video>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor)
        return page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
    }
}

// Visible for repository mapping reuse
private fun com.looklook.core.network.dto.VideoDto.toDomain(): Video = Video(
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

private fun sanitizeUrl(u: String?): String = (u ?: "").trim().trim('`').replace(" ", "")
