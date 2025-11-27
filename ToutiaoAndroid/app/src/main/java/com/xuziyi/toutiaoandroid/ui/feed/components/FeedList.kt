package com.xuziyi.toutiaoandroid.ui.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    modifier: Modifier = Modifier
) {

    LazyColumn(modifier = modifier.fillMaxSize()) {

        // 官方媒体区域
        items(officialItems) { item ->
            OfficialTopCard(
                item = item,
                modifier = Modifier.clickable { onItemClick(item.id) }
            )
        }

        // 分割线
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFF5F5F5))
            )
        }

        // 其他内容
        items(mixedItems) { item ->
            FeedCardFactory(
                item = item,
                modifier = Modifier.clickable { onItemClick(item.id) }
            )
        }
    }
}
