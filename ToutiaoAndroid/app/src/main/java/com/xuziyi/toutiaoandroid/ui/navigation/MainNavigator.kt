package com.xuziyi.toutiaoandroid.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.xuziyi.toutiaoandroid.ui.components.BottomNavBar
import com.xuziyi.toutiaoandroid.ui.video.VideoScreen
import com.xuziyi.toutiaoandroid.ui.shop.ShopScreen
import com.xuziyi.toutiaoandroid.ui.profile.ProfileScreen

/**
 * MainNavigator：底部导航体系的“局部导航容器”。
 *
 * 作用：
 *  - 单独维护一个 NavController（与 AppNavigator 解耦）
 *  - 控制 home / video / shop / profile 四个 tab 的路由
 *  - 根据当前路由自动高亮 BottomNavBar 的对应 tab
 *
 * 注意：
 *  - FeedScreen 的内容由 AppNavigator 注入，保证首页 ViewModel 生命周期稳定
 *  - 这里只负责页面切换，不负责业务逻辑
 */
@Composable
fun MainNavigator(
    // 首页 tab 的内容，由 AppNavigator 传入
    feedScreen: @Composable () -> Unit
) {
    // 为底部 tab 创建独立的 NavController
    val navController = rememberNavController()

    // 当前处于哪个 tab（监听路由变化）
    val entry by navController.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: "home"

    // 根据当前路由决定 BottomNavBar 的选中状态
    val selectedIndex = when (route) {
        "home" -> 0
        "video" -> 1
        "shop" -> 2
        "profile" -> 3
        else -> 0
    }


    // 整体布局：上方是内容区域，下方是 BottomNavBar
    Column {

        /**
         * 这里是“底部导航内部”的局部 NavHost。
         * 只关心 4 个 tab 的切换，不影响全局路由结构。
         */
        NavHost(
            navController = navController,
            startDestination = "home",   // 首页默认显示 Feed 页面
            modifier = Modifier.weight(1f) // 占满除底部导航外的全部空间
        ) {

            // 首页：使用外部传入的 FeedScreen
            composable("home") { feedScreen() }

            // 视频 tab
            composable("video") { VideoScreen() }

            // 商城 tab
            composable("shop") { ShopScreen() }

            // 我的 tab
            composable("profile") { ProfileScreen() }
        }

        /**
         * 底部导航栏
         * - 显示当前选中的 tab
         * - 点击后通知 navController 切换路由
         */
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
