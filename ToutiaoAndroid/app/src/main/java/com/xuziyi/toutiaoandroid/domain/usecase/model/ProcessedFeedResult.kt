package com.xuziyi.toutiaoandroid.domain.usecase.model

import com.xuziyi.toutiaoandroid.domain.model.FeedItem

data class ProcessedFeedResult(
    val officialList: List<FeedItem>,
    val mixedList: List<FeedItem>
)
