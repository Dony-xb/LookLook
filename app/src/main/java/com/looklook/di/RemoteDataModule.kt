package com.looklook.di

import com.looklook.BuildConfig
import com.looklook.core.network.api.RemoteVideoApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataModule {
    @Provides
    @Singleton
    @Named("videosRetrofit")
    fun provideVideosRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.VIDEOS_BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideRemoteVideoApi(@Named("videosRetrofit") retrofit: Retrofit): RemoteVideoApi =
        retrofit.create(RemoteVideoApi::class.java)
}

