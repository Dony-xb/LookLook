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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.looklook.core.model.Video
import com.looklook.core.repository.VideoRepository
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: VideoRepository
) : ViewModel() {
    val videos: StateFlow<List<Video>> = repo.getStaticVideos()
        .map { it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}

@Composable
fun HomeScreen(
    onOpenVideo: (Int) -> Unit,
    onOpenProfile: () -> Unit,
    onOpenLogin: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val list by vm.videos.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().statusBarsPadding(), contentAlignment = Alignment.Center) {
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
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(list, key = { _, it -> it.id }) { index, item ->
                Card(
                    modifier = Modifier.clickable { onOpenVideo(index) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box {
                        Image(
                            painter = rememberAsyncImagePainter(item.coverUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(4f/5f)
                        )
                        Text(
                            text = "推荐",
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .padding(8.dp)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Image(painter = rememberAsyncImagePainter(item.authorAvatar), contentDescription = null, modifier = Modifier.size(18.dp), contentScale = ContentScale.Crop)
                        Text(text = item.authorName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}
