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

@Composable
fun RegisterScreen(onBack: () -> Unit) {
    val phone = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val code = remember { mutableStateOf("") }
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(value = phone.value, onValueChange = { phone.value = it }, modifier = Modifier.fillMaxWidth(), label = { Text("手机号") })
        OutlinedTextField(value = name.value, onValueChange = { name.value = it }, modifier = Modifier.fillMaxWidth(), label = { Text("昵称") })
        OutlinedTextField(value = code.value, onValueChange = { code.value = it }, modifier = Modifier.fillMaxWidth(), label = { Text("验证码") })
        Button(onClick = { /* 后端暂缓 */ }, modifier = Modifier.fillMaxWidth()) { Text("注册") }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("返回") }
    }
}

