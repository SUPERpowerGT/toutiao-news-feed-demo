package com.xuziyi.toutiaoandroid.ui.feed.cards

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xuziyi.toutiaoandroid.domain.model.FeedCardType
import com.xuziyi.toutiaoandroid.domain.model.FeedItem

@Composable
fun FeedCardFactory(
    item: FeedItem,
    modifier: Modifier = Modifier
) {
    when (item.cardType) {
        is FeedCardType.Text -> TextCard(item, modifier)
        is FeedCardType.Image -> ImageCard(item, modifier)
        is FeedCardType.Video -> VideoCard(item, modifier)
        is FeedCardType.OfficialTop -> OfficialTopCard(item, modifier)
        //is FeedCardType.Gallery -> GalleryCard(item, modifier)
        //is FeedCardType.Ad -> AdCard(item, modifier)
    }
}

