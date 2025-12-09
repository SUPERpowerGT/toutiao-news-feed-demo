package com.xuziyi.toutiaoandroid.ui.feed

import com.xuziyi.toutiaoandroid.domain.model.FeedItem

sealed interface FeedUiState {

    object Loading : FeedUiState

    data class Error(val message: String) : FeedUiState

    data class Success(
        val officialItems: List<FeedItem>,
        val mixedItems: List<FeedItem>,

        val isRefreshing: Boolean = false,
        val pullProgress: Float = 0f,
        val newCount: Int = 0,

        val isLoadingMore: Boolean = false,
        val hasMore: Boolean = true,

        val latestPublishTime: Long? = null,
        val nextCursor: Long? = null,

        val isHoldingRefreshHeader: Boolean = false,
        val showRefreshAnimation: Boolean = false,
        val showUpdateBanner: Boolean = false,

        // ---------- 新增：无数据状态 ----------
        val isEmpty: Boolean = false,

        // ---------- 新增：加载更多失败 ----------
        val loadMoreError: Boolean = false,
        val loadMoreErrorMessage: String? = null
    ) : FeedUiState
}
