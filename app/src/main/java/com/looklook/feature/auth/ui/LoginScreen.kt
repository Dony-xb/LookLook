package com.looklook.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.looklook.core.repository.AuthRepository
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {
    fun requestCode(phone: String) {
        viewModelScope.launch { repo.requestCode(phone) }
    }
    fun verify(phone: String, code: String, onSuccess: () -> Unit) {
        viewModelScope.launch { if (repo.verify(phone, code)) onSuccess() }
    }
}

@Composable
fun LoginScreen(onBack: () -> Unit, vm: LoginViewModel = hiltViewModel()) {
    val phone = remember { mutableStateOf("") }
    val code = remember { mutableStateOf("") }
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(value = phone.value, onValueChange = { phone.value = it }, modifier = Modifier.fillMaxWidth(), label = { Text("手机号") })
        Button(onClick = { /* 仅展示UI，后端待接入 */ }, modifier = Modifier.fillMaxWidth()) { Text("获取验证码") }
        OutlinedTextField(value = code.value, onValueChange = { code.value = it }, modifier = Modifier.fillMaxWidth(), label = { Text("验证码") })
        Button(onClick = { /* 仅展示UI，后端待接入 */ }, modifier = Modifier.fillMaxWidth()) { Text("登录") }
    }
}

