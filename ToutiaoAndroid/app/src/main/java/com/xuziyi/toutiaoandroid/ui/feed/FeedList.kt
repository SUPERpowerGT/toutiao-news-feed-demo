package com.xuziyi.toutiaoandroid.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xuziyi.toutiaoandroid.domain.model.FeedAuthorItem
import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import com.xuziyi.toutiaoandroid.domain.model.FeedStatsItem
import com.xuziyi.toutiaoandroid.ui.feed.cards.FeedCardFactory   // ← 正确路径！！
import com.xuziyi.toutiaoandroid.ui.feed.cards.OfficialTopCard
import com.xuziyi.toutiaoandroid.ui.feed.cards.TextCard
@Composable
fun FeedList(
    items: List<FeedItem>,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. 前五条筛选逻辑：官方媒体，不论类型（视频/图片/文字）
    val officialItems = items.filter { item ->
        isOfficialMedia(item.author.name)
    }.take(5)

    // 2. 如果不足 5 条 → mock 补齐
    val filledOfficialItems = if (officialItems.size < 5) {
        val need = 5 - officialItems.size
        officialItems + mockOfficialItems().take(need)
    } else officialItems

    // 3. 第二段：混合内容（剔除前五条）
    val mixedItems = items.filterNot { it in filledOfficialItems }

    LazyColumn(modifier = modifier.fillMaxSize()) {

        // 🔥 第一段：官方媒体新闻（统一用“文字版卡片”）
        items(filledOfficialItems) { item ->

            val displayItem = item.copy(
                title = convertToOfficialTitle(item)  // 视频加“视频｜”
            )

            OfficialTopCard(                        // ← 使用专门的官方卡片
                item = displayItem,
                modifier = Modifier.clickable { onItemClick(item.id) }
            )
        }

        // 分隔线
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFF5F5F5)) // 头条常用浅灰区块
            )
        }


        // 第二段：正常混合流（用 CardFactory）
        items(mixedItems) { item ->
            FeedCardFactory(
                item = item,
                modifier = Modifier.clickable { onItemClick(item.id) }
            )
        }
    }
}

fun convertToOfficialTitle(item: FeedItem): String {
    return when (item.newsType) {
        "video" -> "视频｜${item.title}"   // 只有视频需要加
        else -> item.title                 // 文字 & 图片 → 不变
    }
}



// ------------------------
// 判断是否为官方媒体
// ------------------------
fun isOfficialMedia(name: String): Boolean {
    val officials = listOf(
        "新华社", "人民日报", "央视新闻", "新华社客户端",
        "中国新闻网", "央视", "深圳卫视", "广东广播电视台",
        "人民日报海外版", "新华网","人民网","央广网"
    )
    return officials.any { name.contains(it) }
}

// ------------------------
// 兜底 mock 官方新闻（补足 5 条）
// ------------------------
fun mockOfficialItems(): List<FeedItem> {
    return List(5) { index ->
        FeedItem(
            id = 10_000L + index,
            title = "【权威发布】今日最新重要时政要闻 $index",
            summary = null,
            newsType = "text",
            media = emptyList(),
            author = FeedAuthorItem(
                id = 999,
                name = "新华社",
                avatarUrl = null,
                certification = "official"
            ),
            stats = FeedStatsItem(
                likeCount = 0,
                commentCount = 0,
                favoriteCount = 0,
                shareCount = 0
            ),
            publishTime = System.currentTimeMillis()
        )
    }
}
