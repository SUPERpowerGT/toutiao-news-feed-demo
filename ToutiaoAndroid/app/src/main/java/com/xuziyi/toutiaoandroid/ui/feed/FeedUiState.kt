package com.xuziyi.toutiaoandroid.ui.feed

import com.xuziyi.toutiaoandroid.domain.model.FeedItem

/**
 * 首页推荐流的 UI 状态模型。
 *
 *  - 强类型：明确区分「加载中 / 出错 / 成功」
 *  - Compose 响应式重组友好
 *  - when(state) 分支完整性可检查（编译器会提示遗漏状态）
 */
sealed interface FeedUiState {

    /** 首次进入页面时 → 显示加载动画 */
    object Loading : FeedUiState

    /** 加载失败 → 显示错误信息 */
    data class Error(val message: String) : FeedUiState

    /**
     * 加载成功状态：
     * 包含列表数据 + 刷新状态 + 加载更多状态
     */
    data class Success(
        val officialItems: List<FeedItem>,   // 头条官方 Top5
        val mixedItems: List<FeedItem>,      // 推荐流混排列表

        // ------ 下拉刷新 ------
        val isRefreshing: Boolean = false,   // 是否正在刷新
        val pullProgress: Float = 0f,        // 手势下拉进度（0–1）
        val newCount: Int = 0,               // “xx 条新内容”提示

        // ------ 加载更多 ------
        val isLoadingMore: Boolean = false,  // 是否正在加载更多
        val hasMore: Boolean = true,         // 是否还有更多内容

        // ------ 分页与刷新游标 ------
        val latestPublishTime: Long? = null, // 刷新基准（timestamp）
        val nextCursor: Long? = null,        // 加载更多游标

        // ------ 新增：刷新头保持状态 ------
        val isHoldingRefreshHeader: Boolean = false  // ⭐ 新增字段
    ) : FeedUiState
}
