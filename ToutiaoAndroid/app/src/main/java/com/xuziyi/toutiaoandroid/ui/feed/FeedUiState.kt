package com.xuziyi.toutiaoandroid.ui.feed

import com.xuziyi.toutiaoandroid.domain.model.FeedItem

//这里要更新用feeduistate和sealed来管理
data class FeedUiState(
    val isLoading: Boolean = false,
    val items: List<FeedItem> = emptyList(),
    val nextCursor: String? = null,
    val latestPublishTime: Long? = null,
    val errorMessage: String? = null
)
