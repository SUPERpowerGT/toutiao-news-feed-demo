package com.xuziyi.toutiaoandroid.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xuziyi.toutiaoandroid.ui.feed.components.FeedList
import com.xuziyi.toutiaoandroid.ui.feed.components.FeedTabBar
import com.xuziyi.toutiaoandroid.ui.feed.components.FeedTabItem
import com.xuziyi.toutiaoandroid.ui.feed.components.FeedTopBar
import com.xuziyi.toutiaoandroid.ui.feed.refresh.ToutiaoPullRefresh
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalDensity
import com.xuziyi.toutiaoandroid.ui.components.ErrorScreen
import com.xuziyi.toutiaoandroid.ui.feed.skeleton.FeedLoadingPlaceholder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * FeedScreen：今日头条首页推荐流的主界面
 *
 * 负责：
 *  - 顶部栏 + Tab 栏
 *  - 列表展示
 *  - 下拉刷新
 *  - 上拉加载更多
 *  - 多 Tab 切换
 *
 * ViewModel 是 AppNavigator 注入的（在 MainNavigator 外层），
 * 首页的 ViewModel 生命周期稳定，不会随着 Tab 切换而重建。
 * 从而避免：
 *   - 滚动位置丢失
 *   - 刷新状态丢失
 *   - 数据重复加载
 *   - 卡顿闪烁
 */@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onOpenDetail: (Long) -> Unit = {}
) {

    //从viewmodel订阅uistate，监听uistate状态切换，和stateflow配合使用
    val state = viewModel.state.collectAsState().value
    //第一次创建后续重组仍存在
    val feedListState = rememberLazyListState()
    //同理，重组保证状态
    var selectedIndex by remember { mutableIntStateOf(1) }
    //同理，这里是频道是否有新内容
    var hasNewFollowingContent by remember { mutableStateOf(true) }

    val tabs = remember {
        listOf(
            FeedTabItem(1L, "关注"),
            FeedTabItem(2L, "推荐"),
            FeedTabItem(3L, "热榜"),
            FeedTabItem(4L, "深圳"),
            FeedTabItem(5L, "视频"),
            FeedTabItem(6L, "精选"),
            FeedTabItem(7L, "图片"),
            FeedTabItem(8L, "抗战"),
            FeedTabItem(9L, "体育"),
            FeedTabItem(10L, "财经"),
            FeedTabItem(11L, "科技")
        )
    }

    //避免重组失忆page切换状态
    val pagerState = rememberPagerState(
        initialPage = selectedIndex,
        pageCount = { tabs.size }
    )

    //创建协程scope和当前composable生命周期绑定
    val scope = rememberCoroutineScope()

    //点击 tab → 滚动 pager
    fun onTabClick(index: Int) {
        selectedIndex = index
        //animateScrollToPage是supend函数，需要在协程作用域使用
        scope.launch { pagerState.animateScrollToPage(index) }
        //处理是否有最新内容，有的话则红点消失
        if (index == 0) hasNewFollowingContent = false
    }

    //滑动 pager → 更新 tab 选中项
    LaunchedEffect(pagerState) {
        //监听
        snapshotFlow { pagerState.currentPage }.collect { page ->
            selectedIndex = page
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        FeedTopBar()

        FeedTabBar(
            tabs = tabs,
            selectedIndex = selectedIndex,
            hasNewFollowing = hasNewFollowingContent,
            onTabSelected = { onTabClick(it) }
        )

        //横向 pager 区域（左右滑切换页面）
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->

            val tab = tabs[page]

            when (page) {

                // page == 1 → 推荐流
                1 -> when (state) {

                    is FeedUiState.Loading -> FeedLoadingPlaceholder()

                    is FeedUiState.Error -> ErrorScreen(
                        message = state.message ?: "网络异常，请稍后重试",
                        onRetry = { viewModel.refresh() }
                    )

                    is FeedUiState.Success -> {

                        /**
                         * Success 内部首帧 Gate
                         *
                         * 控制：
                         * - Success 状态的第一阶段只展示 Skeleton
                         * - 至少展示一个“人眼可感知”的最短时间
                         * - 再进入真实 Feed UI
                         */
                        var showRealFeed by remember { mutableStateOf(false) }

                        /**
                         * 首帧 + 最短展示时间控制
                         *
                         * 为什么这样做？
                         * 1. withFrameNanos {}：保证切换发生在帧边界
                         * 2. minShowTime：保证 Skeleton 动画“看得见”
                         * 3. 不阻塞主线程，仅延迟 UI 构建
                         */
                        LaunchedEffect(Unit) {

                            val startNs = System.nanoTime()
                            val minShowTimeNs = 300_000_000L // 300ms：经验值，视觉友好

                            // 让出当前帧（Skeleton 消费首帧）
                            withFrameNanos { }

                            // 如果 Skeleton 展示时间还不够，继续等待
                            val elapsedNs = System.nanoTime() - startNs
                            if (elapsedNs < minShowTimeNs) {
                                delay((minShowTimeNs - elapsedNs) / 1_000_000)
                            }

                            // 再等一帧，保证 Feed 在新帧进入（防撕裂）
                            withFrameNanos { }

                            showRealFeed = true
                        }

                        /**
                         * Success 第一阶段：
                         * - 仍然展示 Skeleton
                         * - 不是 Loading 状态
                         * - 只是 Success 的首帧缓冲
                         */
                        if (!showRealFeed) {

                            FeedLoadingPlaceholder()

                        } else {

                            /**
                             * Success 第二阶段：
                             * 真正进入完整 Feed UI
                             */

                            /**
                             * 自动加载更多（分页监听）
                             *
                             * 使用 feedListState 作为 key：
                             * - 避免因 state.copy() 导致 effect 频繁重启
                             * - 滚动频率 ≪ 状态变化频率
                             */
                            LaunchedEffect(feedListState) {
                                snapshotFlow {
                                    val info = feedListState.layoutInfo
                                    val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0
                                    val total = info.totalItemsCount
                                    last to total
                                }.collect { (last, total) ->
                                    val s = viewModel.state.value as? FeedUiState.Success ?: return@collect
                                    if (s.hasMore && !s.isLoadingMore && last >= total - 5) {
                                        viewModel.loadMore()
                                    }
                                }
                            }

                            /**
                             * 下拉刷新 + Feed 列表主体
                             *
                             * 所有业务状态：
                             * - 刷新
                             * - Banner
                             * - 分页
                             * 都由 ViewModel 驱动
                             */
                            ToutiaoPullRefresh(
                                listState = feedListState,
                                isRefreshing = state.isRefreshing,
                                isHoldingRefreshHeader = state.isHoldingRefreshHeader,
                                showRefreshAnimation = state.showRefreshAnimation,
                                showUpdateBanner = state.showUpdateBanner,
                                newCount = state.newCount,
                                pullProgress = state.pullProgress,
                                onPull = { viewModel.updatePullProgress(it) },
                                onRefreshTriggered = { viewModel.refresh() }
                            ) { paddingTop ->

                                FeedList(
                                    officialItems = state.officialItems,
                                    mixedItems = state.mixedItems,
                                    onItemClick = onOpenDetail,
                                    listState = feedListState,

                                    isLoadingMore = state.isLoadingMore,
                                    hasMore = state.hasMore,

                                    loadMoreError = state.loadMoreError,
                                    loadMoreErrorMessage = state.loadMoreErrorMessage,
                                    onLoadMoreRetry = { viewModel.loadMore() },

                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = with(LocalDensity.current) {
                                            paddingTop.toDp()
                                        })
                                )
                            }
                        }
                    }



                }

                // 其他 tab
                else -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${tab.title} 内容建设中…")
                }
            }
        }
    }
}