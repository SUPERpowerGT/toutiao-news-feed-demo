package com.xuziyi.toutiaoandroid.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xuziyi.toutiaoandroid.ui.feed.components.FeedList
import com.xuziyi.toutiaoandroid.ui.feed.components.FeedTabBar
import com.xuziyi.toutiaoandroid.ui.feed.components.FeedTabItem
import com.xuziyi.toutiaoandroid.ui.feed.components.FeedTopBar
import com.xuziyi.toutiaoandroid.ui.feed.refresh.ToutiaoPullRefresh
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalDensity

@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onOpenDetail: (Long) -> Unit = {}
) {
    val state = viewModel.state.collectAsState().value

    var selectedIndex by remember { mutableIntStateOf(1) }
    var hasNewFollowingContent by remember { mutableStateOf(true) }

    val feedListState = rememberLazyListState()

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

    var showChannelManager by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        FeedTopBar()

        FeedTabBar(
            tabs = tabs,
            selectedIndex = selectedIndex,
            hasNewFollowing = hasNewFollowingContent,
            showChannelManager = showChannelManager,
            onTabSelected = { index ->
                selectedIndex = index
                showChannelManager = true
                if (index == 0) hasNewFollowingContent = false
            },
            onListenClick = { println("点击听新闻按钮") }
        )

        // ========================
        // ⭐ 内容区域
        // ========================
        Box(modifier = Modifier.weight(1f)) {

            when (state) {

                is FeedUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is FeedUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "加载失败：${state.message}")
                    }
                }

                is FeedUiState.Success -> {

                    // ⭐ 加载更多监听逻辑（官方推荐写法）
                    LaunchedEffect(feedListState, state) {
                        snapshotFlow {
                            val info = feedListState.layoutInfo
                            val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0
                            val total = info.totalItemsCount
                            android.util.Log.d("FeedScroll", "🔥 snapshotFlow: last=$last total=$total")
                            last to total
                        }.collect { (last, total) ->
                            android.util.Log.d("FeedScroll", "⚡ collect: last=$last total=$total")

                            val s = state as? FeedUiState.Success ?: return@collect

                            val shouldLoadMore =
                                s.hasMore &&
                                        !s.isLoadingMore &&
                                        last >= total - 5
                            android.util.Log.d("FeedScroll", "📌 shouldLoadMore=$shouldLoadMore")
                            if (shouldLoadMore) {
                                android.util.Log.d("FeedScroll", "🚀 loadMore() 即将触发")
                                viewModel.loadMore()
                            }
                        }
                    }

                    // ⭐ 仅推荐页启用下拉刷新
                    if (selectedIndex == 1) {

                        ToutiaoPullRefresh(
                            listState = feedListState,
                            isRefreshing = state.isRefreshing,
                            pullProgress = state.pullProgress,
                            onPull = { viewModel.updatePullProgress(it) },
                            onRefreshTriggered = { viewModel.refresh() }
                        ) { paddingTop ->

                            FeedList(
                                officialItems = state.officialItems,
                                mixedItems = state.mixedItems,
                                onItemClick = onOpenDetail,
                                listState = feedListState,

                                // ⭐ 加载更多状态传入
                                isLoadingMore = state.isLoadingMore,
                                hasMore = state.hasMore,

                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = with(LocalDensity.current) { paddingTop.toDp() })
                            )
                        }

                    } else {
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
