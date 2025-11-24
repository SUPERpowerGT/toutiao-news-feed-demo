package com.xuziyi.toutiaoandroid.ui.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import com.xuziyi.toutiaoandroid.ui.feed.cards.FeedCardFactory   // ← 正确路径！！

@Composable
fun FeedList(
    items: List<FeedItem>,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items) { item ->
            FeedCardFactory(
                item = item,
                modifier = Modifier.clickable { onItemClick(item.id) }
            )
        }
    }
}

