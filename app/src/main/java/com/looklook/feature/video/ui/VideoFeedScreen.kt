package com.looklook.feature.video.ui

import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.looklook.core.model.Video
import com.looklook.core.repository.VideoRepository
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VideoFeedViewModel @Inject constructor(
    private val repo: VideoRepository
) : ViewModel() {
    val videos: StateFlow<List<Video>> = repo.getStaticVideos()
        .map { it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}

@Composable
fun VideoFeedScreen(startIndex: Int, onBack: () -> Unit, vm: VideoFeedViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    val pagerState = rememberPagerState(initialPage = startIndex) { vm.videos.value.size }
    var ready by remember { mutableStateOf(false) }
    var liked by remember { mutableStateOf(false) }
    var collected by remember { mutableStateOf(false) }
    LaunchedEffect(pagerState.currentPage, vm.videos.value) {
        val list = vm.videos.value
        if (list.isNotEmpty()) {
            val url = list[pagerState.currentPage.coerceIn(list.indices)].streamUrl
            exoPlayer.setMediaItem(MediaItem.fromUri(url))
            exoPlayer.prepare()
            ready = false
            exoPlayer.playWhenReady = true
        }
    }
    LaunchedEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                ready = playbackState == Player.STATE_READY
            }
            override fun onPlayerError(error: PlaybackException) {}
        }
        exoPlayer.addListener(listener)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        VerticalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val item = vm.videos.value.getOrNull(page)
            if (item != null) {
                Box(modifier = Modifier.fillMaxSize().pointerInput(exoPlayer) {
                    detectTapGestures(
                        onTap = {
                            if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                            exoPlayer.playbackParameters = PlaybackParameters(1f)
                        },
                        onLongPress = {
                            exoPlayer.playbackParameters = PlaybackParameters(2f)
                        }
                    )
                }) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                useController = false
                                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                                player = exoPlayer
                            }
                        },
                        update = { it.player = exoPlayer }
                    )
                    if (!ready) {
                        AsyncImage(model = item.coverUrl, contentDescription = null, modifier = Modifier.fillMaxSize())
                    }
                    Column(modifier = Modifier.align(Alignment.CenterEnd).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(model = item.authorAvatar, contentDescription = null, modifier = Modifier.size(48.dp))
                        IconButton(onClick = { liked = !liked }) { Icon(imageVector = Icons.Filled.Favorite, contentDescription = null, tint = if (liked) androidx.compose.ui.graphics.Color.Red else androidx.compose.ui.graphics.Color.White) }
                        IconButton(onClick = { /* TODO: comment */ }) { Icon(imageVector = Icons.Filled.Favorite, contentDescription = null) }
                        IconButton(onClick = { collected = !collected }) { Icon(imageVector = Icons.Filled.Bookmark, contentDescription = null, tint = if (collected) androidx.compose.ui.graphics.Color.Yellow else androidx.compose.ui.graphics.Color.White) }
                        IconButton(onClick = { /* TODO: share */ }) { Icon(imageVector = Icons.Filled.Share, contentDescription = null) }
                    }
                    AsyncImage(model = item.authorAvatar, contentDescription = null, modifier = Modifier.align(Alignment.CenterEnd).padding(16.dp))
                    Text(text = item.authorName, modifier = Modifier.align(Alignment.BottomStart).padding(16.dp), fontWeight = FontWeight.Bold)
                    Text(text = item.title, modifier = Modifier.align(Alignment.BottomStart).padding(start = 16.dp, bottom = 40.dp))
                }
            }
        }
    }
}
