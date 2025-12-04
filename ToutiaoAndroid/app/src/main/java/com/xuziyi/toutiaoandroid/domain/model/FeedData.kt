package com.xuziyi.toutiaoandroid.domain.model

data class FeedData(
    val items: List<FeedItem>,
    val nextCursor: Long? = null,
    val hasMore: Boolean = false,
    val latestPublishTime: Long? = null
)
