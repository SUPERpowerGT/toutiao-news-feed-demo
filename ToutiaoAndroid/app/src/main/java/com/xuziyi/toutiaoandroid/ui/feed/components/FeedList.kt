package com.xuziyi.toutiaoandroid.ui.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
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
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {

        // ===== 1. 官方媒体 Top 区 =====
        itemsIndexed(officialItems, key = { _, item -> item.id }) { _, item ->
            OfficialTopCard(
                item = item,
                modifier = Modifier.clickable { onItemClick(item.id) }
            )
        }

        // ===== 2. 分割线 =====
        item(key = "official-divider") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(Color(0xFFF5F5F5))
            )
        }

        // ===== 3. 推荐流（今日头条式延迟加载）=====
        itemsIndexed(mixedItems, key = { index, item -> item.id }) { index, item ->

            // ⭐ 今日头条同款 ：首屏只加载前 6 条
            val shouldRender =
                index < 6 ||
                        listState.firstVisibleItemIndex >= index - 1

            if (!shouldRender) {
                // 渲染一个轻量占位区，避免 UI 卡顿
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp))
                return@itemsIndexed
            }

            // ===== 正式卡片 ==========
            FeedCardFactory(
                item = item,
                modifier = Modifier.clickable { onItemClick(item.id) }
            )
        }
    }
}
