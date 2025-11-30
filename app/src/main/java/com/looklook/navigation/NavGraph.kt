package com.looklook.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import android.net.Uri
import com.looklook.feature.auth.ui.LoginScreen
import com.looklook.feature.auth.ui.RegisterScreen
import com.looklook.feature.home.ui.HomeScreen
import com.looklook.feature.profile.ui.ProfileScreen
import com.looklook.feature.video.ui.VideoPlayerScreen
import com.looklook.feature.video.ui.VideoFeedScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable(
            route = "home",
            enterTransition = { fadeIn(tween(180)) },
            exitTransition = { fadeOut(tween(180)) }
        ) {
            HomeScreen(
                onOpenVideo = { index ->
                    navController.navigate("feed?start=$index")
                },
                onOpenProfile = { navController.navigate("profile") },
                onOpenLogin = { navController.navigate("login") }
            )
        }
        composable(
            route = "video?id={id}&url={url}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("url") { type = NavType.StringType }
            ),
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) }
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val url = Uri.decode(backStackEntry.arguments?.getString("url") ?: "")
            VideoPlayerScreen(id = id, url = url, onBack = { navController.popBackStack() })
        }
        composable(
            route = "feed?start={start}",
            arguments = listOf(navArgument("start") { type = NavType.IntType })
        ) { backStackEntry ->
            val start = backStackEntry.arguments?.getInt("start") ?: 0
            VideoFeedScreen(startIndex = start, onBack = { navController.popBackStack() })
        }
        composable(
            route = "login",
            enterTransition = { fadeIn(tween(180)) },
            exitTransition = { fadeOut(tween(180)) }
        ) { LoginScreen(onBack = { navController.popBackStack() }) }
        composable(
            route = "register",
            enterTransition = { fadeIn(tween(180)) },
            exitTransition = { fadeOut(tween(180)) }
        ) { RegisterScreen(onBack = { navController.popBackStack() }) }
        composable(
            route = "profile",
            enterTransition = { fadeIn(tween(180)) },
            exitTransition = { fadeOut(tween(180)) }
        ) { ProfileScreen(onBack = { navController.popBackStack() }) }
    }
}
