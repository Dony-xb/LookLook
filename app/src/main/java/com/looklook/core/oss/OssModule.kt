package com.looklook.core.oss

import com.looklook.core.oss.api.OssVideoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OssModule {
    @Provides
    @Singleton
    fun provideOssApi(retrofit: Retrofit): OssVideoApi = retrofit.create(OssVideoApi::class.java)
}

