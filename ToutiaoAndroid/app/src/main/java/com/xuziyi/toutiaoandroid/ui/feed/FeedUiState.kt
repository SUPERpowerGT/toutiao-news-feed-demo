package com.xuziyi.toutiaoandroid.ui.feed

import com.xuziyi.toutiaoandroid.domain.model.FeedItem

sealed interface FeedUiState {

    // ---------- 首次加载 ----------
    object Loading : FeedUiState

    // ---------- 首次加载失败 ----------
    data class Error(val message: String) : FeedUiState

    // ---------- 加载成功 ----------
    data class Success(
        val officialItems: List<FeedItem>,
        val mixedItems: List<FeedItem>,

        // ===== 下拉刷新控制 =====
        val isRefreshing: Boolean = false,     // 正在下拉刷新
        val pullProgress: Float = 0f,          // 下拉进度 0f~1f
        val newCount: Int = 0,                 // 刷新后 xx 条更新

        // ===== 加载更多控制 =====
        val isLoadingMore: Boolean = false,    // 正在加载更多
        val hasMore: Boolean = true,
        val latestPublishTime: Long? = null
    ) : FeedUiState
}
