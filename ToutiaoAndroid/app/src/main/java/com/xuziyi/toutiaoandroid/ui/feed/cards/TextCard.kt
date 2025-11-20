package com.xuziyi.toutiaoandroid.ui.feed.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xuziyi.toutiaoandroid.domain.model.FeedItem

@Composable
fun TextCard(
    item: FeedItem,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {

            // 标题
            Text(text = item.title)

            // 摘要
            item.summary?.let { Text(text = it) }

            // 作者
            Text(text = "作者：${item.author.name}")

            // 时间
            Text(text = "发布时间：${item.publishTime}")
        }
    }
}
