package com.looklook.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.looklook.ui.theme.LookLookTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.looklook.R

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LookLookTheme {
                Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.White), contentAlignment = Alignment.Center) {
                    Image(painter = painterResource(id = R.drawable.app_logo), contentDescription = null)
                }
            }
        }
        lifecycleScope.launch {
            delay(1200)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}

