package com.xuziyi.toutiaoandroid.ui.feed.cards

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import com.xuziyi.toutiaoandroid.ui.feed.cards.components.*

@Composable
fun TextCard(
    item: FeedItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        // ===== 标题（统一组件） =====
        CardTitle(item.title)

        Spacer(modifier = Modifier.height(8.dp))

        // ===== 作者 + 时间 + 点赞 + 更多（统一组件） =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {

            CardAuthorInfo(item)

            Spacer(modifier = Modifier.weight(1f))

            CardActionRow(item)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ===== 底部分割线 =====
        CardDivider()
    }
}
