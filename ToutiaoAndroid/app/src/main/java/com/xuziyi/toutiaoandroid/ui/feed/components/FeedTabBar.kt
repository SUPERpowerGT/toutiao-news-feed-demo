package com.xuziyi.toutiaoandroid.ui.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xuziyi.toutiaoandroid.R
import com.xuziyi.toutiaoandroid.ui.feed.components.FeedTabItem
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch

@Composable
fun FeedTabBar(
    tabs: List<FeedTabItem>,
    selectedIndex: Int,
    lazyListState: LazyListState,
    hasNewFollowing: Boolean = false,
    showChannelManager: Boolean = true, // 默认开启 Menu 和渐变遮罩
    onTabSelected: (Int) -> Unit,
    onListenClick: () -> Unit = {}
) {
    val activeColor = Color(0xFFFF4D4F)
    val inactiveColor = Color.Black
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedIndex) {
        coroutineScope.launch {

            val layoutInfo = lazyListState.layoutInfo
            val itemInfo = layoutInfo.visibleItemsInfo.find { it.index == selectedIndex }

            // 目标：让这个 item 的中心 == LazyRow 视口中心
            val viewportCenter = layoutInfo.viewportEndOffset / 2

            if (itemInfo == null) {
                // ❗ 完全不可见时：立即大范围滚动，使 item 至少滚进来
                lazyListState.animateScrollToItem(selectedIndex)
            }
            awaitFrame()

            // 重新获取位置（确保在可见区域）
            val newInfo = lazyListState.layoutInfo.visibleItemsInfo.find { it.index == selectedIndex }
                ?: return@launch

            val itemCenter = newInfo.offset + newInfo.size / 2
            val diff = itemCenter - viewportCenter     // 距离中心的偏移量（越精确越好）

            // 🎯 使用动画，丝滑居中，无跳动
            lazyListState.animateScrollBy(diff.toFloat())
        }
    }



    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ==========================================
        // 1. 左侧：滑动区域 (LazyRow) + 渐变遮罩 (Box 叠加)
        // ==========================================
        Box(modifier = Modifier.weight(1f)) {

            // 1.1. LazyRow (Tab内容)
            LazyRow(
                state = lazyListState,
                // 为了让最左边的“关注”紧贴左边，这里只使用 12.dp 的左内边距
                contentPadding = PaddingValues(start = 12.dp, end = 0.dp),
                // 缩短 Tab 间距，以便容纳更多 Tab
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(
                    tabs,
                ) { index, tab ->
                    val isSelected = (index == selectedIndex)

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onTabSelected(index) }//优化点击行为（禁止灰色ripple）
                            // 如果是最后一个 Tab，增加右边距，防止被渐变完全覆盖
                            .padding(end = if (index == tabs.lastIndex) 30.dp else 0.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        // 堆叠布局：Text 和红点 (位置在右上角)
                        Box(contentAlignment = Alignment.Center) {

                            // 1. Tab 文字
                            Text(
                                text = tab.title,
                                fontSize = if (isSelected) 19.sp else 19.sp,//避免卡顿不改变字号
                                color = if (isSelected) activeColor else inactiveColor,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Bold,//避免卡顿还是都用bold先
                                modifier = Modifier.padding(end = 4.dp)
                            )

                            // 2. 红点逻辑
                            val shouldShowDot = (tab.title == "关注" && hasNewFollowing) || tab.showDot

                            if (shouldShowDot) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 2.dp, y = (-2).dp)
                                        .size(8.dp)
                                        .background(activeColor, CircleShape)
                                )
                            }
                        }

                        // 选中下划线
                        Spacer(modifier = Modifier.height(4.dp))
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .width(24.dp) // 下划线长度
                                    .height(2.dp)
                                    .background(activeColor, CircleShape)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }
                }
            }

            // 1.2. 🔥 渐变遮罩 (悬浮在 LazyRow 上方，实现若隐若现)
            if (showChannelManager) {
                Box(
                    modifier = Modifier
                        // 宽度覆盖 LazyRow 右侧边缘
                        .width(30.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .background(
                            brush = Brush.horizontalGradient(
                                colorStops = arrayOf(
                                    0.0f to Color.Transparent, // 左侧完全透明
                                    1.0f to Color.White        // 右侧完全不透明 (白色背景)
                                )
                            )
                        )
                )
            }
        }

        // ==========================================
        // 2. 频道管理图标 (Menu Icon)
        // ==========================================
        if (showChannelManager) {
            Icon(
                painter = painterResource(id = R.drawable.ic_menu),
                contentDescription = "频道管理",
                tint = Color.Black,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(24.dp)
                    .clickable { /* TODO: 打开频道管理页面 */ }
            )
        }

        // ==========================================
        // 3. 右侧：固定的耳机图标
        // ==========================================
        // 注意：根据你的截图，耳机图标和 Menu 图标是分开的
        Box(
            modifier = Modifier
                .padding(start = 6.dp, end = 12.dp)
                .clickable(onClick = onListenClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_listen),
                contentDescription = "听新闻",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}