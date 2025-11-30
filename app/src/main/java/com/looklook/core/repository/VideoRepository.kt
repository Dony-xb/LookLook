package com.looklook.core.repository

import com.looklook.core.model.Video
import com.looklook.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VideoRepository @Inject constructor() {
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
    fun getStaticVideos(): Flow<List<Video>> = flow {
        val local = getLocalDebugVideoList()
        if (local.isNotEmpty()) {
            emit(local)
        } else {
            emit(
            listOf(
                Video(
                    id = "1",
                    title = "夏日街头纪实",
                    tags = listOf("街拍", "夏日"),
                    coverUrl = "https://picsum.photos/seed/1/600/800",
                    streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    authorName = "Luna",
                    authorAvatar = "https://picsum.photos/seed/a1/100/100"
                ),
                Video(
                    id = "2",
                    title = "城市夜景延时",
                    tags = listOf("延时", "夜景"),
                    coverUrl = "https://picsum.photos/seed/2/600/800",
                    streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                    authorName = "Noah",
                    authorAvatar = "https://picsum.photos/seed/a2/100/100"
                ),
                Video(
                    id = "3",
                    title = "晨跑vlog",
                    tags = listOf("运动", "健康"),
                    coverUrl = "https://picsum.photos/seed/3/600/800",
                    streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                    authorName = "Ivy",
                    authorAvatar = "https://picsum.photos/seed/a3/100/100"
                )
            )
        )
        }
    }
}

