package com.looklook.core.oss

import com.looklook.core.model.Video
import com.looklook.core.oss.api.OssVideoApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OssRepository @Inject constructor(
    private val api: OssVideoApi
) {
    fun list(pageCursor: String?, pageSize: Int): Flow<List<Video>> = flow {
        val resp = api.listVideos(pageCursor, pageSize)
        emit(resp.items)
    }
}

