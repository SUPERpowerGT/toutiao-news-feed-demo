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
 *
 * 本次增强：
 * —— 新增空数据页面 EmptyScreen
 * —— 新增分页失败 LoadMoreErrorFooter
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

    loadMoreError: Boolean = false,
    loadMoreErrorMessage: String? = null,
    onLoadMoreRetry: () -> Unit = {},

    modifier: Modifier = Modifier
) {

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {

        // 空数据页（Top5 + Mixed 都空）
        if (officialItems.isEmpty() && mixedItems.isEmpty()) {
            item("empty-screen") {
                EmptyScreen(modifier = Modifier.fillParentMaxSize())
            }
            return@LazyColumn
        }

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
        if (officialItems.isNotEmpty()) {
            item("divider") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .background(Color(0xFFF5F5F5))
                )
            }
        }

        // 主推荐流
        itemsIndexed(
            items = mixedItems,
            key = { _, item -> item.id }
        ) { index, item ->

            val firstVisible = listState.firstVisibleItemIndex
            val shouldRender = index <= firstVisible + 10

            if (shouldRender) {
                FeedCardFactory(
                    item = item,
                    modifier = Modifier.clickable { onItemClick(item.id) }
                )
            } else {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }
        }

        //分页错误 UI（点击重试）
        if (loadMoreError) {
            item("load-more-error") {
                LoadMoreErrorFooter(
                    errorMessage = loadMoreErrorMessage,
                    onRetry = onLoadMoreRetry
                )
            }
        }

        // Footer 加载更多状态
        item {
            FooterLoadingState(
                isLoadingMore = isLoadingMore,
                hasMore = hasMore
            )
        }

        // 底部 padding
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

            !hasMore -> {
                Text(
                    text = "— 没有更多内容了 —",
                    color = Color.Gray
                )
            }
        }
    }
}
