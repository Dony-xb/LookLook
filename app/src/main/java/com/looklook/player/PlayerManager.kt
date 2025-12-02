package com.looklook.player

import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerManager @Inject constructor(
    @ApplicationContext context: Context,
    cache: SimpleCache,
    okHttpClient: OkHttpClient
) {
    val player: ExoPlayer

    init {
        val upstream: DataSource.Factory = OkHttpDataSource.Factory(okHttpClient)
            .setUserAgent("LookLook/1.0")
        val cacheFactory: DataSource.Factory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstream)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        val mediaSourceFactory = DefaultMediaSourceFactory(cacheFactory)

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(15_000, 30_000, 800, 1_500)
            .build()

        player = ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                addListener(object : Player.Listener {
                    override fun onRenderedFirstFrame() {
                        Timber.d("First frame rendered")
                    }
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        Timber.d("Playback state: %d", playbackState)
                    }
                })
            }
    }

    fun setItems(current: androidx.media3.common.MediaItem, next: androidx.media3.common.MediaItem?) {
        val items = mutableListOf(current)
        if (next != null) items.add(next)
        player.setMediaItems(items, /*startIndex*/0, /*startPositionMs*/C.TIME_UNSET)
        player.prepare()
        player.playWhenReady = true
    }
}
