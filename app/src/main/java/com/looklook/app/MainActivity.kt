package com.looklook.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.looklook.ui.theme.LookLookTheme
import com.looklook.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.looklook.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.res.dimensionResource

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LookLookTheme {
                Surface {
                    var showSplash by remember { mutableStateOf(true) }
                    LaunchedEffect(Unit) { kotlinx.coroutines.delay(1200); showSplash = false }
                    if (showSplash) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(painter = painterResource(R.drawable.applogo_trans), contentDescription = null, modifier = Modifier.fillMaxWidth(0.5f))
                                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.splash_text_margin_top)))
                                Text(text = stringResource(R.string.splash_tagline), fontWeight = FontWeight.Medium)
                            }
                        }
                    } else {
                        NavGraph()
                    }
                }
            }
        }
    }
}

