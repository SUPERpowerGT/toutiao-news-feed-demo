package com.xuziyi.toutiaoandroid.domain.usecase

import com.xuziyi.toutiaoandroid.domain.model.FeedCardType
import com.xuziyi.toutiaoandroid.domain.model.FeedContentType
import com.xuziyi.toutiaoandroid.domain.model.FeedItem

class RenderCardTypeUseCase {

    fun execute(items: List<FeedItem>): List<FeedItem> {
        return items.map { item ->
            item.copy(
                cardType = decideCardType(item)
            )
        }
    }


    private fun decideCardType(item: FeedItem): FeedCardType {
        return when {
            item.isTopOfficial -> FeedCardType.OfficialTop

            item.contentType.value == FeedContentType.VIDEO ->
                FeedCardType.Video

            item.contentType.value == FeedContentType.IMAGE ->
                FeedCardType.Image

            else -> FeedCardType.Text
        }
    }
}
