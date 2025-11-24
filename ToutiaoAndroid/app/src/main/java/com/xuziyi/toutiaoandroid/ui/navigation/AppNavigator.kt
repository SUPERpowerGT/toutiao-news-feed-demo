package com.xuziyi.toutiaoandroid.ui.navigation

import android.window.SplashScreen
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

//定义路由，定义所有的App页面
/*
这里用到sealed单例来保证没有人可以在外面随便继承screen类，
用object声明feed保证唯一性（单例），
object功能类比java的class+insatance+singleton
*/
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Feed : Screen("feed")
    object Detail : Screen("detail/{newsId}") {
        fun createRoute(id: Long) = "detail/$id"
    }
}

@Composable
fun AppNavigator() {

    val navController = rememberNavController()

    // 1.Fake API → RemoteDataSource → Repository
    // 这里用的fake api后续要替换
    val api = FakeFeedApiService()
    val remoteDataSource = RemoteDataSource(api)
    val repository = FeedRepository(remoteDataSource)

    // 2.UseCases
    val loadInitial = LoadInitialFeedUseCase(repository)
    val refreshFeed = RefreshFeedUseCase(repository)
    val loadMore = LoadMoreFeedUseCase(repository)

    // 3.Use ViewModelFactory to create FeedViewModel
    val feedViewModel: FeedViewModel = viewModel(
        factory = FeedViewModelFactory(
            loadInitial = loadInitial,
            refreshFeed = refreshFeed,
            loadMore = loadMore
        )
    )

    // 4.DetailViewModel（暂时不用后端）
    val detailViewModel = viewModel<NewsDetailViewModel>()

    // 5.Navigation Host 声明路由方便后面调用
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash页 冷启动
        composable(Screen.Splash.route){
            SplashScreen(
                onFinish = {
                    navController.navigate(Screen.Feed.route){
                        popUpTo(Screen.Splash.route){ inclusive = true}
                    }
                }
            )
        }

        // 首页 Feed
        composable(Screen.Feed.route) {
            FeedScreen(
                viewModel = feedViewModel,
                onOpenDetail = { id ->
                    navController.navigate(Screen.Detail.createRoute(id))
                }
            )
        }

//        // 新闻详情
//        composable(
//            route = Screen.Detail.route,
//            arguments = listOf(navArgument("newsId") { type = NavType.LongType })
//        ) { entry ->
//            val newsId = entry.arguments?.getLong("newsId") ?: 0L
//            NewsDetailScreen(
//                newsId = newsId,
//                viewModel = detailViewModel
//            )
//        }
    }
}