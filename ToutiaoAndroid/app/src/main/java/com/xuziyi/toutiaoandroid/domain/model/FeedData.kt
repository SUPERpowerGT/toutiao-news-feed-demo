package com.xuziyi.toutiaoandroid.domain.model

data class FeedData(
    val scene: String = "recommend",
    val topItems: List<FeedItem> = emptyList(),
    val items: List<FeedItem>,
    val nextCursor: Long? = null,
    val hasMore: Boolean = false,
    val latestPublishTime: Long? = null
)
