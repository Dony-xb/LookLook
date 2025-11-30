package com.looklook.core.network.api

import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("auth/request_code")
    suspend fun requestSmsCode(@Query("phone") phone: String)

    @POST("auth/verify")
    suspend fun verifyCode(
        @Query("phone") phone: String,
        @Query("code") code: String
    ): String
}

