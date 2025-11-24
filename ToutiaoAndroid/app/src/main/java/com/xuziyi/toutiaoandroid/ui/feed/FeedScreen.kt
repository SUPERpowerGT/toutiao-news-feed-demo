package com.xuziyi.toutiaoandroid.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            FeedTabItem("关注"),
            FeedTabItem("推荐"),
            FeedTabItem("热榜"),
            FeedTabItem("深圳"),
            FeedTabItem("视频"),
            FeedTabItem("精选"),
            FeedTabItem("图片"),
            FeedTabItem("抗战"),
            FeedTabItem("体育"),
            FeedTabItem("财经"),
            FeedTabItem("科技")
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
                if (selectedIndex == 1) {
                    UpdateHintBar()
                }

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

                // 5. 底部导航
                BottomNavBar()
            }
        }
    }
}

// ------------------------------------
// 辅助 Composable (保持不变)
// ------------------------------------
@Composable
fun UpdateHintBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF4F5F6))
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "今日头条为你推荐了 12 条更新",
            color = Color(0xFFF04142),
            fontSize = 13.sp
        )
    }
}

@Composable
fun BottomNavBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text("首页", fontWeight = FontWeight.Bold, color = Color(0xFFFF4D4F))
        Text("视频")
        Text("放映厅")
        Text("我的")
    }
}