package com.xuziyi.toutiaoandroid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xuziyi.toutiaoandroid.ui.feed.FeedViewModel
import com.xuziyi.toutiaoandroid.ui.feed.FeedViewModelFactory
import com.xuziyi.toutiaoandroid.data.datasource.RemoteDataSource
import com.xuziyi.toutiaoandroid.data.remote.RetrofitClient
import com.xuziyi.toutiaoandroid.data.repository.FeedRepository
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

    // 全局 NavController：用于页面跳转
    val navController = rememberNavController()


    /**
     * ---------------------------
     *  初始化 Feed 业务所需的依赖
     * ---------------------------
     *
     * AppNavigator 负责创建这些对象，是为了让 FeedViewModel 生命周期保持稳定，
     * 避免它随着 Tab 切换被反复销毁（提升首页推荐流的体验）。
     */

    val api = RetrofitClient.feedApi                           // Retrofit API 实例
    val remoteDataSource = RemoteDataSource(api)               // 网络数据源
    val repository = FeedRepository(remoteDataSource)          // 仓库（统一数据入口）

    // UseCases（业务动作）
    val loadInitial = LoadInitialFeedUseCase(repository)
    val refreshFeed = RefreshFeedUseCase(repository)
    val loadMore = LoadMoreFeedUseCase(repository)

    // 创建首页 Feed 的 ViewModel，并注入所有 UseCase
    val feedViewModel: FeedViewModel = viewModel(
        factory = FeedViewModelFactory(
            loadInitial = loadInitial,
            refreshFeed = refreshFeed,
            loadMore = loadMore
        )
    )

    // 未来详情页需要时再开启
    // val detailViewModel = viewModel<NewsDetailViewModel>()


    /**
     * NavHost：声明“每个路由页面”对应的 UI 页面。
     * startDestination = Splash
     * 表示 App 打开时先显示 SplashScreen。
     */
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        /**
         * 冷启动页面
         */
        composable(Screen.Splash.route) {
            SplashScreen(
                onFinish = {
                    // 冷启动动画结束 → 跳转到主页面
                    navController.navigate(Screen.Main.route) {

                        // 将 Splash 从返回栈中移除，避免按返回键回到它
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }


        /**
         * 主页面（包含底部导航）
         * MainNavigator 会负责：
         *  - tab 切换
         *  - tab 内路由
         *  - UI 排版
         */
        composable(Screen.Main.route) {
            MainNavigator(
                feedScreen = {

                    // 首页推荐流的真正页面
                    FeedScreen(
                        viewModel = feedViewModel,     // 注入 App 级别的 ViewModel
                        onOpenDetail = { id ->

                            // 点击新闻卡片 → 跳转到详情页
                            navController.navigate(Screen.Detail.createRoute(id))
                        }
                    )
                }
            )
        }


        /**
         * 新闻详情页（未来补充）
         */
        /*
        composable(Screen.Detail.route) { entry ->
            // TODO: 详情 UI 内容以后补充
        }
        */
    }
}
