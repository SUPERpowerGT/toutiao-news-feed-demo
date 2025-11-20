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


sealed class Screen(val route: String) {
    object Feed : Screen("feed")
    object Detail : Screen("detail/{newsId}") {
        fun createRoute(id: Long) = "detail/$id"
    }
}

@Composable
fun AppNavigator() {

    val navController = rememberNavController()

    // -----------------------------
    // 1) Fake API → RemoteDataSource → Repository
    // -----------------------------
    val api = FakeFeedApiService()
    val remoteDataSource = RemoteDataSource(api)
    val repository = FeedRepository(remoteDataSource)

    // -----------------------------
    // 2) UseCases
    // -----------------------------
    val loadInitial = LoadInitialFeedUseCase(repository)
    val refreshFeed = RefreshFeedUseCase(repository)
    val loadMore = LoadMoreFeedUseCase(repository)

    // -----------------------------
    // 3) Use ViewModelFactory to create FeedViewModel
    // -----------------------------
    val feedViewModel: FeedViewModel = viewModel(
        factory = FeedViewModelFactory(
            loadInitial = loadInitial,
            refreshFeed = refreshFeed,
            loadMore = loadMore
        )
    )

    // -----------------------------
    // 4) DetailViewModel（暂时不用后端）
    // -----------------------------
    val detailViewModel = viewModel<NewsDetailViewModel>()

    // -----------------------------
    // 5) Navigation Host
    // -----------------------------
    NavHost(
        navController = navController,
        startDestination = Screen.Feed.route
    ) {

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