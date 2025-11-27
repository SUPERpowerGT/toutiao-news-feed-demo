package com.xuziyi.toutiaoandroid.domain.model

data class FeedItem(
    val id: Long,
    val title: String,
    val summary: String?,
    val cardType: FeedCardType,
    val media: List<FeedMediaItem>,   //必须是你自己的类型
    val author: FeedAuthorItem,
    val stats: FeedStatsItem,
    val publishTime: Long
)

