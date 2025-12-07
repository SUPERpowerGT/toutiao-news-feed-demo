package com.xuziyi.toutiaoandroid.ui.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import com.xuziyi.toutiaoandroid.ui.feed.cards.FeedCardFactory
import com.xuziyi.toutiaoandroid.ui.feed.cards.OfficialTopCard
/**
 * 今日头条风格推荐流列表：
 * —— 顶部固定官方 Top5
 * —— 中间为混合推荐内容（图文 / 多图 / 视频）
 * —— 底部展示加载更多或“没有更多”状态
 * 使用 item.id 作为唯一 key 避免 LazyColumn 重组冲突。
 * 启用轻量级渲染优化：仅渲染可视范围附近的内容，其余以占位符代替，
 * 以提升长列表滑动性能（字节系常见做法）。
 * 本组件仅负责 UI 渲染，不包含业务逻辑（刷新/分页由 ViewModel 控制）。
 */

@Composable
fun FeedList(
    officialItems: List<FeedItem>,
    mixedItems: List<FeedItem>,
    onItemClick: (Long) -> Unit,
    listState: LazyListState,

    // 分页状态
    isLoadingMore: Boolean,
    hasMore: Boolean,

    modifier: Modifier = Modifier
) {

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {
        // 官方 Top5
        itemsIndexed(
            items = officialItems,
            key = { _, item -> item.id }
        ) { _, item ->
            OfficialTopCard(
                item = item,
                modifier = Modifier.clickable { onItemClick(item.id) }
            )
        }

        // 分割条
        item("divider") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(Color(0xFFF5F5F5))
            )
        }

        // 主推荐流（带懒加载渲染优化）
        itemsIndexed(
            items = mixedItems,
            key = { _, item -> item.id }
        ) { index, item ->

            // 🌟 今日头条式可视窗口渲染优化
            val firstVisible = listState.firstVisibleItemIndex
            val shouldRender = index <= firstVisible + 10

            if (shouldRender) {
                // → 正常渲染卡片
                FeedCardFactory(
                    item = item,
                    modifier = Modifier.clickable { onItemClick(item.id) }
                )
            } else {
                // → 不渲染内容，只占位防止跳动
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)  // 你可以按卡片平均高度调整
                )
            }
        }

        // ============================
        // Footer 加载更多区块
        // ============================
        item {
            FooterLoadingState(
                isLoadingMore = isLoadingMore,
                hasMore = hasMore
            )
        }

        // 额外 padding（防止滑动到尾部抖动）
        item("footer-padding") {
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun FooterLoadingState(
    isLoadingMore: Boolean,
    hasMore: Boolean
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {

        when {

            // 加载更多中
            isLoadingMore -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 1.5.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("正在加载…", color = Color.Gray)
                }
            }

            // 没有更多内容了
            !hasMore -> {
                Text(
                    text = "— 没有更多内容了 —",
                    color = Color.Gray
                )
            }

            // 默认状态，不显示任何 UI
        }
    }
}
