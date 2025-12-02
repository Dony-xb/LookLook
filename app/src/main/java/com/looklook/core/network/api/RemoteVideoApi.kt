package com.looklook.core.network.api

import com.looklook.core.network.dto.VideosResponse
import retrofit2.http.GET

interface RemoteVideoApi {
    @GET("Videos.json")
    suspend fun fetch(): VideosResponse
}

