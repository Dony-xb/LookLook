package com.looklook.feature.video.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.datasource.DataSpec
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.C
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val cache: SimpleCache,
    private val okHttpClient: OkHttpClient
) : ViewModel() {
    val player: ExoPlayer
    private val upstream: DataSource.Factory
    private val cacheFactory: CacheDataSource.Factory
    private val prefetched = mutableSetOf<String>()

    init {
        upstream = OkHttpDataSource.Factory(okHttpClient)
            .setUserAgent("LookLook/1.0")
        cacheFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstream)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        val mediaSourceFactory = DefaultMediaSourceFactory(cacheFactory as DataSource.Factory)

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                15_000,
                30_000,
                800,
                1_500
            )
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

    fun prefetch(url: String) {
        val clean = url.trim()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ds = cacheFactory.createDataSource()
                val spec = DataSpec(Uri.parse(clean))
                ds.open(spec)
                val buffer = ByteArray(64 * 1024)
                var readTotal = 0L
                val maxPrefetch = 512 * 1024L
                while (readTotal < maxPrefetch) {
                    val r = ds.read(buffer, 0, buffer.size)
                    if (r == C.RESULT_END_OF_INPUT) break
                    if (r > 0) readTotal += r
                }
                ds.close()
                synchronized(prefetched) { prefetched.add(clean) }
            } catch (t: Throwable) {
                Timber.w(t)
            }
        }
    }

    fun isPrefetched(url: String?): Boolean = synchronized(prefetched) { url != null && prefetched.contains(url.trim()) }
}
