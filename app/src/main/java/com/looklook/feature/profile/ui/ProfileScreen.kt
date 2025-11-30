package com.looklook.feature.profile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.looklook.core.repository.ProfileRepository
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: ProfileRepository
) : ViewModel() {
    val profile: StateFlow<com.looklook.core.model.UserProfile> =
        repo.getMyProfile().stateIn(viewModelScope, SharingStarted.Eagerly, com.looklook.core.model.UserProfile("", "", "", ""))
}

@Composable
fun ProfileScreen(onBack: () -> Unit, vm: ProfileViewModel = hiltViewModel()) {
    val p by vm.profile.collectAsState()
    Column(modifier = Modifier.padding(16.dp)) {
        Image(painter = rememberAsyncImagePainter(p.avatarUrl), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth())
        Text(text = p.name, modifier = Modifier.padding(top = 8.dp), fontWeight = FontWeight.Bold)
        Text(text = p.bio)
        Spacer(modifier = Modifier.padding(8.dp))
        Button(onClick = onBack, modifier = Modifier.padding(top = 12.dp)) { Text("返回") }
    }
}

