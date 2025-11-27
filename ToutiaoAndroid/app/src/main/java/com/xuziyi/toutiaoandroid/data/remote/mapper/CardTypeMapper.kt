package com.xuziyi.toutiaoandroid.data.mapper

import com.xuziyi.toutiaoandroid.domain.model.FeedCardType

object CardTypeMapper {

    fun from(raw: String?): FeedCardType {
        return when (raw?.lowercase()) {
            "text" -> FeedCardType.Text
            "image" -> FeedCardType.Image
            "video" -> FeedCardType.Video
            //"official" -> FeedCardType.Official
            "official_top" -> FeedCardType.OfficialTop
            //"gallery" -> FeedCardType.Gallery
            //"ad" -> FeedCardType.Ad
            else -> FeedCardType.Text // 默认兜底
        }
    }
}
