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
@Composable
fun FeedList(
    officialItems: List<FeedItem>,
    mixedItems: List<FeedItem>,
    onItemClick: (Long) -> Unit,
    listState: LazyListState,

    isLoadingMore: Boolean,
    hasMore: Boolean,

    modifier: Modifier = Modifier
) {

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {

        // ===== 1. 官方 Top5 =====
        itemsIndexed(officialItems, key = { _, item -> item.id }) { _, item ->
            OfficialTopCard(
                item = item,
                modifier = Modifier.clickable { onItemClick(item.id) }
            )
        }

        // ===== 2. 分割线 =====
        item("divider") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(Color(0xFFF5F5F5))
            )
        }

        // ===== 3. 主内容流 =====
        itemsIndexed(mixedItems, key = { index, item -> item.id }) { index, item ->

            val shouldRender =
                index < 6 ||
                        listState.firstVisibleItemIndex >= index - 1

            if (!shouldRender) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
                return@itemsIndexed
            }

            FeedCardFactory(
                item = item,
                modifier = Modifier.clickable { onItemClick(item.id) }
            )
        }

        // ===== 4. 优化后的 Footer =====
        item("footer") {

            FooterLoadingState(
                isLoadingMore = isLoadingMore,
                hasMore = hasMore
            )
        }

        // ===== 5. 加一点额外底部安全间距（防跳动）======
        item("footer-padding") {
            Spacer(Modifier.height(32.dp))
        }
    }
}

/**
 * 分离 Footer 逻辑，结构更清晰
 */
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
