package com.xuziyi.toutiaoandroid.ui.feed.cards

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import com.xuziyi.toutiaoandroid.ui.feed.cards.components.*

@Composable
fun ImageCard(
    item: FeedItem,
    modifier: Modifier = Modifier
) {
    val coverUrl = item.media.firstOrNull()?.url

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        //标题 + 封面图

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            //左：标题
            CardTitle(
                title = item.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            )

            //右：封面图
            CardCoverImage(
                url = coverUrl,
                width = 120.dp,
                height = 80.dp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        RecommendationReasonChip(item.reason)

        if (!item.reason.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
        }

        //作者信息 + 点赞 + 更多

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {

            CardAuthorInfo(item)

            Spacer(modifier = Modifier.weight(1f))

            CardActionRow(item)
        }

        Spacer(modifier = Modifier.height(12.dp))

        CardDivider()
    }
}
