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
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.PlayArrow
import androidx.activity.compose.BackHandler
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import com.looklook.core.model.Video
import com.looklook.core.repository.VideoRepository
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.res.colorResource
import androidx.compose.foundation.border
import com.looklook.R
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class VideoFeedViewModel @Inject constructor(
    private val repo: VideoRepository
) : ViewModel() {
    val videos: StateFlow<List<Video>> = repo.getRemoteVideos()
        .map { it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}

@Composable
fun VideoFeedScreen(startIndex: Int, onBack: () -> Unit, vm: VideoFeedViewModel = hiltViewModel()) {
    val playerVm: PlayerViewModel = hiltViewModel()
    val exoPlayer = playerVm.player
    val videosState = vm.videos.collectAsState()
    val videos = videosState.value
    val pagerState = rememberPagerState(initialPage = startIndex.coerceIn(0, maxOf(0, videos.size - 1))) { maxOf(1, videos.size) }
    var ready by remember { mutableStateOf(false) }
    var liked by remember { mutableStateOf(false) }
    var collected by remember { mutableStateOf(false) }
    var isDoubleSpeed by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var exiting by remember { mutableStateOf(false) }
    var switching by remember { mutableStateOf(false) }
    var isScrolling by remember { mutableStateOf(false) }
    LaunchedEffect(pagerState.currentPage, videos, isScrolling) {
        val list = videos
        val item = list.getOrNull(pagerState.currentPage)
        if (item != null) {
            val targetUri = android.net.Uri.parse(item.streamUrl.trim())
            val existingUri = exoPlayer.currentMediaItem?.localConfiguration?.uri
            val needSwitch = existingUri == null || existingUri.toString() != targetUri.toString()
            if (needSwitch) {
                switching = true
                val currentItem = MediaItem.fromUri(targetUri)
                val nextItem = list.getOrNull(pagerState.currentPage + 1)?.let {
                    MediaItem.fromUri(android.net.Uri.parse(it.streamUrl.trim()))
                }
                val items = mutableListOf(currentItem)
                if (nextItem != null) items.add(nextItem)
                exoPlayer.setMediaItems(items, 0, androidx.media3.common.C.TIME_UNSET)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = false
                if (nextItem != null) {
                    playerVm.prefetch(list[pagerState.currentPage + 1].streamUrl)
                }
            }
        } else {
            exoPlayer.playWhenReady = false
        }
    }
    LaunchedEffect(videos) {
        if (videos.isNotEmpty()) {
            val target = startIndex.coerceIn(0, videos.size - 1)
            pagerState.scrollToPage(target)
        }
    }
    LaunchedEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                ready = playbackState == Player.STATE_READY
                if (playbackState == Player.STATE_READY) switching = false
            }
            override fun onRenderedFirstFrame() {
                switching = false
            }
            override fun onPlayerError(error: PlaybackException) {}
        }
        exoPlayer.addListener(listener)
    }
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.isScrollInProgress }
                .collect { scr ->
                    if (scr) exoPlayer.playWhenReady = false
                }
        }
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.isScrollInProgress }
                .collect { scr ->
                    isScrolling = scr
                    if (scr) exoPlayer.playWhenReady = false
                }
        }
        BackHandler(enabled = true) {
            exiting = true
            try {
                exoPlayer.playWhenReady = false
                exoPlayer.stop()
            } catch (_: Throwable) {}
            onBack()
        }
        VerticalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val item = videos.getOrNull(page)
            if (item != null) {
                Box(modifier = Modifier.fillMaxSize().pointerInput(exoPlayer) {
                    detectTapGestures(
                        onTap = {
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause(); isPaused = true
                            } else {
                                exoPlayer.play(); isPaused = false
                            }
                            if (!isDoubleSpeed) exoPlayer.playbackParameters = PlaybackParameters(1f)
                        },
                        onLongPress = {
                            if (isDoubleSpeed) {
                                exoPlayer.playbackParameters = PlaybackParameters(1f)
                                isDoubleSpeed = false
                            } else {
                                exoPlayer.playbackParameters = PlaybackParameters(2f)
                                isDoubleSpeed = true
                            }
                        }
                    )
                }) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                useController = false
                                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                                setKeepContentOnPlayerReset(true)
                                setUseArtwork(false)
                                setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                                setResizeMode(androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM)
                            }
                        },
                        update = { view ->
                            if (exiting) {
                                view.setKeepContentOnPlayerReset(false)
                                view.player = null
                            } else {
                                view.setKeepContentOnPlayerReset(!switching)
                                val resizeMode = if (isPortrait) androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM else androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                                view.setResizeMode(resizeMode)
                                view.player = if (page == pagerState.currentPage) exoPlayer else null
                            }
                        }
                    )

                    val isCurrent = page == pagerState.currentPage
                    if (!isCurrent || !ready) {
                        AsyncImage(
                            model = item.coverUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    
                    if (isPaused) {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.align(Alignment.Center).size(64.dp)
                        )
                    }
                    if (isDoubleSpeed) {
                        val pos = integerResource(R.integer.video_speed_indicator_position)
                        val mStart = dimensionResource(R.dimen.video_speed_indicator_margin_start)
                        val mEnd = dimensionResource(R.dimen.video_speed_indicator_margin_end)
                        val mTop = dimensionResource(R.dimen.video_speed_indicator_margin_top)
                        val mBottom = dimensionResource(R.dimen.video_speed_indicator_margin_bottom)
                        val align = when (pos) {
                            0 -> Alignment.TopStart
                            1 -> Alignment.TopCenter
                            2 -> Alignment.TopEnd
                            3 -> Alignment.Center
                            4 -> Alignment.BottomStart
                            5 -> Alignment.BottomCenter
                            else -> Alignment.BottomEnd
                        }
                        Text(
                            text = "2x",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = integerResource(R.integer.video_speed_indicator_text_size_sp).sp,
                            modifier = Modifier.align(align).padding(start = mStart, end = mEnd, top = mTop, bottom = mBottom)
                        )
                    }
                    run {
                        val marginEnd = dimensionResource(R.dimen.video_actions_margin_end)
                        val marginBottom = dimensionResource(R.dimen.video_actions_margin_bottom)
                        val spacing = dimensionResource(R.dimen.video_actions_spacing)
                        val avatarSize = dimensionResource(R.dimen.video_author_avatar_size)
                        val iconSize = dimensionResource(R.dimen.video_action_icon_size)
                        val countSize = integerResource(R.integer.video_action_count_text_size_sp).sp

                        Column(
                            modifier = Modifier.align(Alignment.BottomEnd).padding(end = marginEnd, bottom = marginBottom),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(spacing)
                        ) {
                            AsyncImage(model = item.authorAvatar, contentDescription = null, modifier = Modifier.size(avatarSize).clip(androidx.compose.foundation.shape.CircleShape).border(dimensionResource(R.dimen.video_author_avatar_border_width), Color.White, androidx.compose.foundation.shape.CircleShape))
                            IconButton(onClick = { liked = !liked }) { Icon(imageVector = Icons.Filled.Favorite, contentDescription = null, tint = if (liked) androidx.compose.ui.graphics.Color.Red else androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(iconSize)) }
                            Text(text = (item.likesCount ?: 0).toString(), color = Color.White, fontSize = countSize)
                            IconButton(onClick = { /* TODO: comment */ }) { Icon(imageVector = Icons.Filled.ChatBubble, contentDescription = null, tint = Color.White, modifier = Modifier.size(iconSize)) }
                            Text(text = (item.commentsCount ?: 0).toString(), color = Color.White, fontSize = countSize)
                            IconButton(onClick = { collected = !collected }) { Icon(imageVector = Icons.Filled.Bookmark, contentDescription = null, tint = if (collected) androidx.compose.ui.graphics.Color.Yellow else androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(iconSize)) }
                            Text(text = "0", color = Color.White, fontSize = countSize)
                            IconButton(onClick = { /* TODO: share */ }) { androidx.compose.material3.Icon(painter = androidx.compose.ui.res.painterResource(R.drawable.ic_share_douyin), contentDescription = null, tint = Color.White, modifier = Modifier.size(iconSize)) }
                            Text(text = (item.sharesCount ?: 0).toString(), color = Color.White, fontSize = countSize)
                        }
                    }
                    val usernameSize = integerResource(R.integer.video_username_text_size_sp).sp
                    val descSize = integerResource(R.integer.video_description_text_size_sp).sp
                    val tagsText = if (item.tags.isNotEmpty()) item.tags.joinToString(" ") { "#" + it } else ""
                    val descWithTags = listOfNotNull(item.description, if (tagsText.isNotEmpty()) tagsText else null).joinToString(" ")
                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(start = 16.dp, bottom = 40.dp)) {
                        Text(text = "@" + item.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = usernameSize)
                        Text(text = descWithTags, color = Color.White, fontSize = descSize)
                    }
                    val dateText = item.createdAt
                    if (!dateText.isNullOrBlank()) {
                        Text(
                            text = dateText,
                            color = colorResource(R.color.video_date_text_color),
                            fontSize = integerResource(R.integer.video_date_text_size_sp).sp,
                            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 8.dp)
                        )
                    }
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = progress,
                        color = colorResource(R.color.video_progress_color),
                        trackColor = colorResource(R.color.video_progress_track_color),
                        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(dimensionResource(R.dimen.video_progress_height))
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose { exoPlayer.pause() }
    }
    LaunchedEffect(exoPlayer) {
        while (true) {
            val d = exoPlayer.duration
            val p = exoPlayer.currentPosition
            progress = if (d > 0) (p.toFloat() / d.toFloat()).coerceIn(0f, 1f) else 0f
            kotlinx.coroutines.delay(100)
        }
    }
    LaunchedEffect(ready, pagerState.currentPage) {
        if (ready) {
            exoPlayer.playWhenReady = true
        }
    }
    LaunchedEffect(isPortrait) {
        val mode = if (isPortrait) androidx.media3.common.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING else androidx.media3.common.C.VIDEO_SCALING_MODE_SCALE_TO_FIT
        exoPlayer.setVideoScalingMode(mode)
    }
}
