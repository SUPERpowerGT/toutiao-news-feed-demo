package com.xuziyi.toutiaoandroid.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.xuziyi.toutiaoandroid.ui.components.BottomNavBar
import com.xuziyi.toutiaoandroid.ui.feed.FeedScreen
import com.xuziyi.toutiaoandroid.ui.video.VideoScreen
import com.xuziyi.toutiaoandroid.ui.shop.ShopScreen
import com.xuziyi.toutiaoandroid.ui.profile.ProfileScreen

@Composable
fun MainNavigator(
    feedScreen: @Composable () -> Unit
) {
    val navController = rememberNavController()

    val entry by navController.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: "home"

    val selectedIndex = when (route) {
        "home" -> 0
        "video" -> 1
        "shop" -> 2
        "profile" -> 3
        else -> 0
    }

    Column {

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.weight(1f)
        ) {

            composable("home") { feedScreen() }
            composable("video") { VideoScreen() }
            composable("shop") { ShopScreen() }
            composable("profile") { ProfileScreen() }
        }

        BottomNavBar(
            selectedIndex = selectedIndex,
            onTabSelected = {
                when (it) {
                    0 -> navController.navigate("home")
                    1 -> navController.navigate("video")
                    2 -> navController.navigate("shop")
                    3 -> navController.navigate("profile")
                }
            }
        )
    }
}
