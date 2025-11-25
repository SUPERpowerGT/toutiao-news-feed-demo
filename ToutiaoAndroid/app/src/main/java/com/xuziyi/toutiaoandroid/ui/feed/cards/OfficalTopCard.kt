package com.xuziyi.toutiaoandroid.ui.feed.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xuziyi.toutiaoandroid.domain.model.FeedItem

@Composable
fun OfficialTopCard(
    item: FeedItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {

        // 标题（统一紧凑）
        Text(
            text = item.title,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222),
            lineHeight = 22.sp,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(2.dp))

        // 来源 + 评论数（无头像）
        Text(
            text = "${item.author.name}  ${item.stats.commentCount}评论",
            fontSize = 13.sp,
            color = Color(0xFF999999)
        )
    }
}
