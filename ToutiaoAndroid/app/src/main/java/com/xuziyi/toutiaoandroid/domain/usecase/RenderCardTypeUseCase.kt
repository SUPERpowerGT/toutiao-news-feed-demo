package com.xuziyi.toutiaoandroid.domain.usecase

import com.xuziyi.toutiaoandroid.domain.model.ContentType
import com.xuziyi.toutiaoandroid.domain.model.FeedCardType
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
        // ======== UI 渲染策略在这里统一定义 =========
        return when {
            item.isTopOfficial -> FeedCardType.OfficialTop

            item.contentType == ContentType.VIDEO ->
                FeedCardType.Video

            item.contentType == ContentType.IMAGE ->
                FeedCardType.Image

            //item.media.size >= 3 ->
                //FeedCardType.ThreeImage

            //item.media.size == 1 ->
                //FeedCardType.SingleImage

            else -> FeedCardType.Text
        }
    }
}
