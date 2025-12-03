package com.looklook.feature.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import coil.compose.rememberAsyncImagePainter
import com.looklook.core.model.Video
import com.looklook.core.repository.VideoRepository
import com.looklook.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.LazyPagingItems
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import coil.ImageLoader
import coil.request.ImageRequest
import androidx.compose.foundation.lazy.grid.rememberLazyGridState

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: com.looklook.core.network.api.RemoteVideoApi
) : ViewModel() {
    val pager = Pager(config = PagingConfig(pageSize = 10, prefetchDistance = 5)) {
        com.looklook.core.repository.VideoPagingSource(api, 10)
    }.flow
}

@Composable
fun HomeScreen(
    onOpenVideo: (Int) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenLogin: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val pagingItems: LazyPagingItems<Video> = vm.pager.collectAsLazyPagingItems()
    val gridState = rememberLazyGridState()
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val imageLoader = coil.compose.LocalImageLoader.current
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(vertical = dimensionResource(R.dimen.home_topbar_padding_v)), contentAlignment = Alignment.Center) {
            Text("推荐", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Box(modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 4.dp)
                .height(2.dp)
                .width(32.dp)
                .background(MaterialTheme.colorScheme.onSurface))
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(dimensionResource(R.dimen.home_grid_spacing)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.home_grid_spacing)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.home_grid_spacing))
        ) {
            items(pagingItems.itemCount, key = { index ->
                val it = pagingItems.peek(index)
                ((it?.id ?: "unknown") + "#" + index)
            }) { index ->
                val item = pagingItems[index]
                Card(
                    modifier = Modifier.clickable { onOpenVideo(index) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.home_card_elevation)),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.home_card_radius))
                ) {
                    Box {
                        val aspect = integerResource(R.integer.home_card_aspect_x).toFloat() / integerResource(R.integer.home_card_aspect_y).toFloat()
                        Image(
                            painter = rememberAsyncImagePainter(item?.coverUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(aspect)
                        )
                        val homeTag = item?.homeTag?.trim()
                        if (!homeTag.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .background(colorResource(R.color.home_tag_bg), shape = RoundedCornerShape(dimensionResource(R.dimen.home_tag_radius)))
                                    .defaultMinSize(
                                        minWidth = dimensionResource(R.dimen.home_tag_min_width),
                                        minHeight = dimensionResource(R.dimen.home_tag_height)
                                    )
                            ) {
                                Text(
                                    text = homeTag,
                                    color = colorResource(R.color.home_tag_text),
                                    modifier = Modifier
                                        .padding(
                                            horizontal = dimensionResource(R.dimen.home_tag_padding_h),
                                            vertical = dimensionResource(R.dimen.home_tag_padding_v)
                                        )
                                    ,
                                    fontSize = integerResource(R.integer.home_tag_text_size_sp).sp
                                )
                            }
                        }
                    }
                    Text(
                        text = item?.title ?: "",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.home_title_padding_h), vertical = dimensionResource(R.dimen.home_title_padding_v)),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.home_title_padding_h), vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Image(painter = rememberAsyncImagePainter(item?.authorAvatar), contentDescription = null, modifier = Modifier.size(dimensionResource(R.dimen.home_author_avatar_size)).clip(CircleShape), contentScale = ContentScale.Crop)
                        Text(text = item?.authorName ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                }
            }
        }

        LaunchedEffect(gridState) {
            snapshotFlow { gridState.layoutInfo.visibleItemsInfo }
                .collect { visible ->
                    val maxIndex = visible.maxOfOrNull { it.index } ?: 0
                    val prefetchRange = (maxIndex + 1)..minOf(maxIndex + 8, pagingItems.itemCount - 1)
                    prefetchRange.forEach { idx ->
                        val v = pagingItems.peek(idx)
                        val url = v?.coverUrl ?: return@forEach
                        imageLoader.enqueue(
                            ImageRequest.Builder(ctx).data(url).build()
                        )
                    }
                    if (pagingItems.itemCount - maxIndex < 6) {
                        pagingItems.retry()
                    }
                }
        }
    }
}
