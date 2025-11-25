package com.xuziyi.toutiaoandroid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xuziyi.toutiaoandroid.ui.feed.FeedViewModel
import com.xuziyi.toutiaoandroid.ui.feed.FeedViewModelFactory
import com.xuziyi.toutiaoandroid.ui.detail.NewsDetailViewModel
import com.xuziyi.toutiaoandroid.data.datasource.RemoteDataSource
import com.xuziyi.toutiaoandroid.data.remote.api.FakeFeedApiService
import com.xuziyi.toutiaoandroid.data.repository.FeedRepository
import com.xuziyi.toutiaoandroid.domain.usecase.LoadInitialFeedUseCase
import com.xuziyi.toutiaoandroid.domain.usecase.RefreshFeedUseCase
import com.xuziyi.toutiaoandroid.domain.usecase.LoadMoreFeedUseCase
import com.xuziyi.toutiaoandroid.ui.feed.FeedScreen
import com.xuziyi.toutiaoandroid.ui.splash.SplashScreen


sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Main : Screen("main")  //必须有这个
    object Feed : Screen("feed")
    object Detail : Screen("detail/{newsId}") {
        fun createRoute(id: Long) = "detail/$id"
    }
}

//全局导航入口，控制splash main detail页面
@Composable
fun AppNavigator() {

    val navController = rememberNavController()

    // ---- ViewModel 初始化保持不变 ----
    val api = FakeFeedApiService()
    val remoteDataSource = RemoteDataSource(api)
    val repository = FeedRepository(remoteDataSource)

    val loadInitial = LoadInitialFeedUseCase(repository)
    val refreshFeed = RefreshFeedUseCase(repository)
    val loadMore = LoadMoreFeedUseCase(repository)

    val feedViewModel: FeedViewModel = viewModel(
        factory = FeedViewModelFactory(
            loadInitial = loadInitial,
            refreshFeed = refreshFeed,
            loadMore = loadMore
        )
    )

    val detailViewModel = viewModel<NewsDetailViewModel>()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // ====== 冷启动页 ======
        composable(Screen.Splash.route) {
            SplashScreen(
                onFinish = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // ====== 底部导航入口 ======
        composable(Screen.Main.route) {
            MainNavigator(
                feedScreen = {
                    FeedScreen(
                        viewModel = feedViewModel,
                        onOpenDetail = { id ->
                            navController.navigate(Screen.Detail.createRoute(id))
                        }
                    )
                }
            )
        }

        // ====== 新闻详情（以后再补） ======
        composable(Screen.Detail.route) { entry ->
            // TODO 以后你加 NewsDetailScreen
        }
    }
}
