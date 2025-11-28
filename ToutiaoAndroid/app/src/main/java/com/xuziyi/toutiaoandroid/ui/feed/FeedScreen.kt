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

@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onOpenDetail: (Long) -> Unit = {}
) {
    val state = viewModel.state.collectAsState().value

    // 1. Tab 选中状态（推荐）
    var selectedIndex by remember { mutableIntStateOf(1) }

    // 2. 关注红点状态
    var hasNewFollowingContent by remember { mutableStateOf(true) }

    // 3. 保留上滑丝滑体验
    val lazyListState = rememberLazyListState()

    // Tabs
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

    // 管理频道弹窗开关
    var showChannelManager by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        when (state) {

            // ===============================
            // 1) 首次加载
            // ===============================
            is FeedUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ===============================
            // 2) 加载失败
            // ===============================
            is FeedUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "加载失败：${state.message}")
                }
            }

            // ===============================
            // 3) 加载成功（推荐流）
            // ===============================
            is FeedUiState.Success -> {

                Column(modifier = Modifier.fillMaxSize()) {

                    // 顶栏
                    FeedTopBar()

                    // TabBar
                    FeedTabBar(
                        tabs = tabs,
                        selectedIndex = selectedIndex,
                        lazyListState = lazyListState,
                        hasNewFollowing = hasNewFollowingContent,
                        showChannelManager = showChannelManager,
                        onTabSelected = { index ->
                            selectedIndex = index
                            showChannelManager = true
                            if (index == 0) hasNewFollowingContent = false
                        },
                        onListenClick = {
                            println("点击听新闻按钮")
                        }
                    )

                    // 内容区
                    Box(modifier = Modifier.weight(1f)) {

                        when (selectedIndex) {

                            1 -> { // 推荐 tab

                                if (state is FeedUiState.Success) {

                                    ToutiaoPullRefresh(
                                        isRefreshing = state.isRefreshing,
                                        pullProgress = state.pullProgress,          // ⭐ 把进度传给刷新头
                                        onPull = { progress ->
                                            viewModel.updatePullProgress(progress)  // 手势 -> VM
                                        },
                                        onRefreshTriggered = {
                                            viewModel.refresh()
                                        }
                                    ) { paddingTop ->

                                        FeedList(
                                            officialItems = state.officialItems,
                                            mixedItems = state.mixedItems,
                                            onItemClick = onOpenDetail,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                // 这里你现在先保持 0.dp，因为整体下移已经在 ToutiaoPullRefresh 里做了
                                                .padding(top = 0.dp)
                                        )
                                    }


                                } else if (state is FeedUiState.Loading) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                } else if (state is FeedUiState.Error) {
                                    Text(
                                        text = "加载失败：${state.message}",
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }

                            else -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val currentTitle = tabs[selectedIndex].title
                                    Text(text = "$currentTitle 内容建设中…")
                                }
                            }
                        }
                    }



                }
            }
        }
    }
}

