package com.xuziyi.toutiaoandroid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xuziyi.toutiaoandroid.ui.feed.FeedViewModel
import com.xuziyi.toutiaoandroid.ui.feed.FeedViewModelFactory
import com.xuziyi.toutiaoandroid.data.datasource.RemoteDataSource
import com.xuziyi.toutiaoandroid.data.remote.RetrofitClient
import com.xuziyi.toutiaoandroid.data.repository.FeedRepository
import com.xuziyi.toutiaoandroid.di.DatabaseModule
import com.xuziyi.toutiaoandroid.domain.usecase.LoadInitialFeedUseCase
import com.xuziyi.toutiaoandroid.domain.usecase.RefreshFeedUseCase
import com.xuziyi.toutiaoandroid.domain.usecase.LoadMoreFeedUseCase
import com.xuziyi.toutiaoandroid.ui.feed.FeedScreen
import com.xuziyi.toutiaoandroid.ui.splash.SplashScreen


/**
 * 定义 App 中有哪些“页面路由”，每个页面对应一个唯一的字符串 route。
 */
sealed class Screen(val route: String) {

    // App 打开后的第一屏（冷启动动画）
    object Splash : Screen("splash")

    // 主业务页面（包含底部导航：推荐 / 视频 / 商城 / 我的）
    object Main : Screen("main")

    // 新闻详情页（需要携带新闻 id）
    object Detail : Screen("detail/{newsId}") {
        fun createRoute(id: Long) = "detail/$id"
    }
}


/**
 * AppNavigator：应用的全局导航中心。
 *
 * - 负责声明整个应用的导航结构（Splash、Main、Detail）
 * - 负责注入 FeedScreen 所需的所有业务依赖（ViewModel / UseCase / Repository）
 * - 负责应用冷启动流程
 *
 * MainNavigator 是它的子导航，用于管理底部 Tab 的内部逻辑。
 */
@Composable
fun AppNavigator() {

    val navController = rememberNavController()

    // 1) 获取 Context（构建数据库需要）
    val context = LocalContext.current

    // 2) 从 DI 模块拿仓库
    val repository = remember {
        DatabaseModule.provideFeedRepository(context)
    }

    // 3) UseCases
    val loadInitial = LoadInitialFeedUseCase(repository)
    val refreshFeed = RefreshFeedUseCase(repository)
    val loadMore = LoadMoreFeedUseCase(repository)

    // 4) 创建 ViewModel（注入 UseCase）
    val feedViewModel: FeedViewModel = viewModel(
        factory = FeedViewModelFactory(
            loadInitial = loadInitial,
            refreshFeed = refreshFeed,
            loadMore = loadMore
        )
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onFinish = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

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
    }
}
