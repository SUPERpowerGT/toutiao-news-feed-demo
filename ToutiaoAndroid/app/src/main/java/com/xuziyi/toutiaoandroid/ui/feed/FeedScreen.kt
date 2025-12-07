package com.xuziyi.toutiaoandroid.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xuziyi.toutiaoandroid.ui.feed.components.FeedList
import com.xuziyi.toutiaoandroid.ui.feed.components.FeedTabBar
import com.xuziyi.toutiaoandroid.ui.feed.components.FeedTabItem
import com.xuziyi.toutiaoandroid.ui.feed.components.FeedTopBar
import com.xuziyi.toutiaoandroid.ui.feed.refresh.ToutiaoPullRefresh
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalDensity

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
 */
@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onOpenDetail: (Long) -> Unit = {}
) {
    // 订阅 ViewModel 的状态流（推荐流数据、加载状态等）
    val state = viewModel.state.collectAsState().value

    // Tab 栏当前选中项（默认“推荐”，即 index=1）
    var selectedIndex by remember { mutableIntStateOf(1) }

    // 是否有关注 Tab 的“新内容提示”
    var hasNewFollowingContent by remember { mutableStateOf(true) }

    // 列表的滚动状态（用于监听是否触底）
    val feedListState = rememberLazyListState()

    // 顶部 Tab 栏的数据源
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

    // “频道管理”是否展示（需要在配置变更后继续保持，使用 rememberSaveable）
    var showChannelManager by rememberSaveable { mutableStateOf(false) }

    // 页面整体布局
    Column(modifier = Modifier.fillMaxSize()) {

        // 今日头条顶部栏（天气、搜索等）
        FeedTopBar()

        // Tab 栏（关注/推荐/热榜/...）
        FeedTabBar(
            tabs = tabs,
            selectedIndex = selectedIndex,
            hasNewFollowing = hasNewFollowingContent,
            showChannelManager = showChannelManager,
            onTabSelected = { index ->
                selectedIndex = index
                showChannelManager = true
                // 点击关注 tab → 清除“小红点”
                if (index == 0) hasNewFollowingContent = false
            },
            onListenClick = { println("点击听新闻按钮") }
        )

        // 内容区域（列表、加载中、错误）
        Box(modifier = Modifier.weight(1f)) {

            when (state) {

                /**
                 * 正在加载初始数据
                 */
                is FeedUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                /**
                 * 加载失败
                 */
                is FeedUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "加载失败：${state.message}")
                    }
                }

                /**
                 * 加载成功
                 */
                is FeedUiState.Success -> {

                    /**
                     * 自动加载更多（官方推荐 snapshotFlow 监听列表滚动）
                     *
                     * snapshotFlow 会在以下情况触发：
                     *  - 列表滚动
                     *  - 列表结构发生变化（添加新项目）
                     *
                     * 我们从 layoutInfo 中取：
                     *   - 当前可见的最后一项 index
                     *   - 列表总数
                     * 如果 last >= total - 5 → 触底 → 加载更多
                     */
                    LaunchedEffect(feedListState, state) {
                        snapshotFlow {
                            val info = feedListState.layoutInfo
                            val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0
                            val total = info.totalItemsCount
                            last to total
                        }.collect { (last, total) ->

                            // state 可能在过程中变成 Error/Loading → 需做类型检查
                            val s = state as? FeedUiState.Success ?: return@collect

                            // 触底条件：还有更多 && 未在加载 && 接近底部
                            val shouldLoadMore =
                                s.hasMore &&
                                        !s.isLoadingMore &&
                                        last >= total - 5

                            if (shouldLoadMore) {
                                viewModel.loadMore()
                            }
                        }
                    }


                    /**
                     * 推荐 tab 支持下拉刷新；其他 tab 显示文案
                     */
                    if (selectedIndex == 1) {

                        // 自定义下拉刷新组件（支持动画、绳性、回弹）
                        ToutiaoPullRefresh(
                            listState = feedListState,
                            isRefreshing = state.isRefreshing,
                            pullProgress = state.pullProgress,
                            onPull = { viewModel.updatePullProgress(it) },
                            onRefreshTriggered = { viewModel.refresh() }
                        ) { paddingTop ->

                            // 推荐列表渲染
                            FeedList(
                                officialItems = state.officialItems,
                                mixedItems = state.mixedItems,
                                onItemClick = onOpenDetail,
                                listState = feedListState,

                                // 加载更多的控制参数
                                isLoadingMore = state.isLoadingMore,
                                hasMore = state.hasMore,

                                // 下拉头部的 padding（跟随下拉距离）
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = with(LocalDensity.current) { paddingTop.toDp() })
                            )
                        }

                    } else {

                        /**
                         * 非推荐 tab：仅展示“内容建设中”
                         */
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "${tabs[selectedIndex].title} 内容建设中…")
                        }
                    }
                }
            }
        }
    }
}
