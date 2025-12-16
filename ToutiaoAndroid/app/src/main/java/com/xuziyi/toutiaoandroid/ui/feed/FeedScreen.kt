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
import com.xuziyi.toutiaoandroid.ui.feed.skeleton.SkeletonFirstScreen
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

                    is FeedUiState.Loading -> SkeletonFirstScreen()

                    is FeedUiState.Error -> ErrorScreen(
                        message = state.message ?: "网络异常，请稍后重试",
                        onRetry = { viewModel.refresh() }
                    )

                    is FeedUiState.Success -> {

                        // 自动加载更多这里负面影响是effect频繁重启
                        LaunchedEffect(feedListState, state) {
                            snapshotFlow {
                                val info = feedListState.layoutInfo
                                val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0
                                val total = info.totalItemsCount
                                last to total
                            }.collect { (last, total) ->
                                val s = state as? FeedUiState.Success ?: return@collect
                                if (s.hasMore && !s.isLoadingMore && last >= total - 5) {
                                    viewModel.loadMore()
                                }
                            }
                        }

                        // 下拉刷新逻辑保留
                        /*
                        该页面通过自定义 PullRefresh 容器解耦刷新交互与列表渲染，
                        所有刷新、分页、动画与错误状态均由 ViewModel 的 UiState 统一驱动，
                        UI 仅负责事件上报与状态渲染，形成清晰的单向数据流。
                         */
                        ToutiaoPullRefresh(
                            listState = feedListState,
                            isRefreshing = state.isRefreshing,
                            isHoldingRefreshHeader = state.isHoldingRefreshHeader,

                            //新增两个核心状态（ViewModel）
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

                                //新增三行（让分页错误 UI 生效）
                                loadMoreError = state.loadMoreError,
                                loadMoreErrorMessage = state.loadMoreErrorMessage,
                                onLoadMoreRetry = { viewModel.loadMore() },

                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = with(LocalDensity.current) { paddingTop.toDp() })
                            )

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