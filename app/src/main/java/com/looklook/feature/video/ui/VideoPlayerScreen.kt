package com.looklook.feature.video.ui

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.Player
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun VideoPlayerScreen(id: String, url: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    DisposableEffect(url) {
        val item = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(item)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        val listener = object : Player.Listener {
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                errorMsg = error.message ?: "播放失败"
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = true
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    player = exoPlayer
                }
            },
            update = { it.player = exoPlayer }
        )
        if (errorMsg != null) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                content = { androidx.compose.material3.Text(text = errorMsg!!) }
            )
        }
    }
}

