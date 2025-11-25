package com.xuziyi.toutiaoandroid.ui.feed


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.foundation.lazy.rememberLazyListState // 引入 rememberLazyListState
import androidx.compose.runtime.saveable.rememberSaveable


@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onOpenDetail: (Long) -> Unit = {}
) {
    val state = viewModel.state.collectAsState().value

    // 1. Tab 选中状态
    var selectedIndex by remember { mutableIntStateOf(1) }

    // 2. 关注红点状态：可变状态 (var)，用于动态显示/隐藏红点
    var hasNewFollowingContent by remember { mutableStateOf(true) }

    // 3. 丝滑滚动关键：创建 LazyListState 并提升状态
    val lazyListState = rememberLazyListState()

    // 定义 Tab 数据列表
    val tabs = remember {
        listOf(
            // 为每个 Tab 分配一个唯一的 ID，并使用具名参数
            FeedTabItem(id = 1L, title = "关注"),
            FeedTabItem(id = 2L, title = "推荐"),
            FeedTabItem(id = 3L, title = "热榜"),
            FeedTabItem(id = 4L, title = "深圳"),
            FeedTabItem(id = 5L, title = "视频"),
            FeedTabItem(id = 6L, title = "精选"),
            FeedTabItem(id = 7L, title = "图片"),
            FeedTabItem(id = 8L, title = "抗战"),
            FeedTabItem(id = 9L, title = "体育"),
            FeedTabItem(id = 10L, title = "财经"),
            FeedTabItem(id = 11L, title = "科技")
        )
    }
    // 用来设计menu显示逻辑开关
    var showChannelManager by rememberSaveable { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(modifier = Modifier.fillMaxSize()) {

                // 1. 顶部栏
                FeedTopBar()

                // 2. Tab 栏 (传入所有状态，包括 LazyListState)
                FeedTabBar(
                    tabs = tabs,
                    selectedIndex = selectedIndex,
                    lazyListState = lazyListState, //传递 LazyListState
                    hasNewFollowing = hasNewFollowingContent,
                    showChannelManager = showChannelManager,
                    onTabSelected = { index ->
                        selectedIndex = index
                        showChannelManager = true
                        //逻辑：如果用户点击了“关注”Tab (index == 0)，则清除红点
                        if (index == 0) {
                            hasNewFollowingContent = false
                        }
                    },
                    onListenClick = {
                        println("点击了听新闻/耳机图标")
                    }
                )

                // 3. 只有在“推荐”Tab (index == 1) 才显示的更新提示条
/*                if (selectedIndex == 1) {
                    UpdateHintBar()
                }*/

                // 4. 内容区域
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedIndex) {
                        1 -> { // 推荐
                            // 保留 FeedList 调用
                            FeedList(
                                items = state.items,
                                onItemClick = onOpenDetail,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        else -> {
                            // 其他 Tab 的占位内容
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                val currentTabTitle = tabs[selectedIndex].title
                                Text(text = "$currentTabTitle 内容建设中…")
                            }
                        }
                    }
                }
            }
        }
    }
}


