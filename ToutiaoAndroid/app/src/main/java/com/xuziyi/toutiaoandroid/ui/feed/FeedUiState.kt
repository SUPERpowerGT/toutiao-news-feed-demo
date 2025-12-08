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
        val officialItems: List<FeedItem>,
        val mixedItems: List<FeedItem>,

        val isRefreshing: Boolean = false,
        val pullProgress: Float = 0f,
        val newCount: Int = 0,

        val isLoadingMore: Boolean = false,
        val hasMore: Boolean = true,

        val latestPublishTime: Long? = null,
        val nextCursor: Long? = null,

        //刷新头是否吸顶固定
        val isHoldingRefreshHeader: Boolean = false,

        //是否显示刷新动画
        val showRefreshAnimation: Boolean = false,

        // 是否显示“X 条已更新”提示条
        val showUpdateBanner: Boolean = false
    ) : FeedUiState



}
