package com.xuziyi.toutiaoandroid.ui.feed.cards

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xuziyi.toutiaoandroid.domain.model.FeedItem

@Composable
fun FeedCardFactory(
    item: FeedItem,
    modifier: Modifier = Modifier
) {
    when (item.newsType) {
        "text" -> TextCard(item, modifier)
        "image" -> ImageCard(item, modifier)
        "video" -> VideoCard(item, modifier)
        else -> TextCard(item, modifier)
    }
}
