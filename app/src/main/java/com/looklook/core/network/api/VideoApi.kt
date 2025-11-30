package com.looklook.core.network.api

import com.looklook.core.model.Video
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoApi {
    @GET("videos")
    suspend fun listVideos(@Query("page") page: Int, @Query("size") size: Int): List<Video>
}

