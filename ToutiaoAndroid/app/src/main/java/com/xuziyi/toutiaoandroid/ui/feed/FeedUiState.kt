package com.xuziyi.toutiaoandroid.ui.feed

import com.xuziyi.toutiaoandroid.domain.model.FeedItem

//这里要更新用feeduistate和sealed来管理
sealed interface FeedUiState {

    // 首次加载
    object Loading : FeedUiState

    // 加载失败（首次加载失败）
    data class Error(val message: String) : FeedUiState

    // 加载成功且可展示列表
    data class Success(
        val officialItems: List<FeedItem>,
        val mixedItems: List<FeedItem>,
        val isRefreshing: Boolean = false,
        val isLoadingMore: Boolean = false,
        val hasMore: Boolean = true,
        val latestPublishTime: Long? = null
    ) : FeedUiState
}
