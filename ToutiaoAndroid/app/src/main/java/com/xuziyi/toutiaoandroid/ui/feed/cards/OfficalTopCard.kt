package com.xuziyi.toutiaoandroid.ui.feed.cards

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import com.xuziyi.toutiaoandroid.ui.feed.cards.components.CardTitle
import com.xuziyi.toutiaoandroid.ui.feed.cards.components.CardMetaRow
import com.xuziyi.toutiaoandroid.ui.feed.cards.components.RecommendationReasonChip

@Composable
fun OfficialTopCard(
    item: FeedItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
    ) {

        //统一标题组件
        CardTitle(
            title = item.title
        )

        if (!item.reason.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            RecommendationReasonChip(item.reason)
        }

        Spacer(modifier = Modifier.height(4.dp))

        //官方来源 & 评论数
        CardMetaRow(
            source = item.author.name,
            commentCount = item.stats.commentCount
        )

        Spacer(modifier = Modifier.height(0.dp))

    }
}
