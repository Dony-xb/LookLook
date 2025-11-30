package com.looklook.core.oss.api

import com.looklook.core.model.Video
import retrofit2.http.GET
import retrofit2.http.Query

interface OssVideoApi {
    @GET("videos")
    suspend fun listVideos(@Query("cursor") cursor: String?, @Query("size") size: Int): OssListResponse
}

data class OssListResponse(
    val items: List<Video>,
    val hasMore: Boolean,
    val nextCursor: String?
)

