package com.xuziyi.toutiaoandroid.ui.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
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
    listState: LazyListState,              // ⭐ 文章列表的 state
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,                 // ⭐ 必须绑定
        modifier = modifier.fillMaxSize()
    ) {

        // ⭐ 官方媒体列表区域（固定在顶部）
        items(officialItems, key = { it.id }) { item ->
            OfficialTopCard(
                item = item,
                modifier = Modifier.clickable { onItemClick(item.id) }
            )
        }

        // ⭐ 分割线（头条同款）
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(Color(0xFFF5F5F5))
            )
        }

        // ⭐ 推荐流内容
        items(mixedItems, key = { it.id }) { item ->
            FeedCardFactory(
                item = item,
                modifier = Modifier.clickable { onItemClick(item.id) }
            )
        }
    }
}
