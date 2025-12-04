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
        val isRefreshing: Boolean = false,
        val pullProgress: Float = 0f,
        val newCount: Int = 0,

        // ===== 加载更多控制 =====
        val isLoadingMore: Boolean = false,
        val hasMore: Boolean = true,

        // ===== 游标管理（新增）=====
        val latestPublishTime: Long? = null,   // 刷新用
        val nextCursor: Long? = null           // 加载更多用 ← 必须新增！
    ) : FeedUiState

}
