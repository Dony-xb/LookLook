package com.looklook.core.network.api

import com.looklook.core.model.UserProfile
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApi {
    @GET("profiles/{id}")
    suspend fun getProfile(@Path("id") id: String): UserProfile
}

