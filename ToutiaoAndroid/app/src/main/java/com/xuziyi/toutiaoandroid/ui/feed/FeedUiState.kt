package com.xuziyi.toutiaoandroid.ui.feed

import com.xuziyi.toutiaoandroid.domain.model.FeedItem

data class FeedUiState(
    val isLoading: Boolean = false,
    val items: List<FeedItem> = emptyList(),
    val nextCursor: String? = null,
    val latestPublishTime: Long? = null,
    val errorMessage: String? = null
)
