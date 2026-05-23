package com.xuziyi.toutiaoandroid.ui.feed

import com.xuziyi.toutiaoandroid.domain.model.FeedItem

/*
这种 UI State 的设计方式，把页面可能出现的所有状态在类型层面一次性限定住，
通过不可变的数据快照驱动 UI 渲染，使非法状态在编译期就被排除，
从而让客户端逻辑更加安全、可预测、也更容易维护和演进。
 */


sealed interface FeedUiState {

    object Loading : FeedUiState

    data class Error(val message: String) : FeedUiState

    data class Success(

        // 官方/置顶内容列表（如官方账号、Top 内容）
        // Success 状态的核心数据之一，用于渲染固定优先级内容
        val officialItems: List<FeedItem>,

        // 普通推荐流内容列表（信息流主体）
        // Success 状态下页面主要展示的数据集合
        val mixedItems: List<FeedItem>,

        // 是否处于下拉刷新中的标志位
        // 用于控制刷新动画、Header 状态，与 Loading 状态区分
        val isRefreshing: Boolean = false,

        // 当前下拉刷新的手势进度（0f ~ 1f）
        // 用于驱动自定义 Pull-to-Refresh 的视觉反馈
        val pullProgress: Float = 0f,

        // 本次刷新后新增的数据条数
        // 用于展示“已更新 X 条内容”等轻提示
        val newCount: Int = 0,

        // 是否正在加载更多（分页请求中）
        // 用于控制列表底部 loading indicator
        val isLoadingMore: Boolean = false,

        // 是否还有更多数据可加载
        // false 时禁用触底加载逻辑，避免无效请求
        val hasMore: Boolean = true,

        // 当前列表中最新一条内容的发布时间
        // 用于下拉刷新时判断是否有更新
        val latestPublishTime: Long? = null,

        // 下一页分页游标（cursor-based pagination）
        // 用于后端分页请求，null 表示无后续分页
        val nextCursor: Long? = null,

        // 是否保持刷新 Header 的可见状态
        // 用于实现“刷新完成后短暂停留”的交互效果
        val isHoldingRefreshHeader: Boolean = false,

        // 是否展示刷新完成的动画效果
        // 与 isRefreshing 区分，用于动画生命周期控制
        val showRefreshAnimation: Boolean = false,

        // 是否显示“内容已更新”的提示 Banner
        // 通常在刷新完成后短暂展示
        val showUpdateBanner: Boolean = false,

        // 刷新提示 Banner 展示的文案
        // 用于区分“X 条更新”和“当前已是最新内容”
        val updateBannerText: String? = null,

        // 是否处于“无数据”状态
        // 表示请求成功但列表为空，用于展示 Empty View
        val isEmpty: Boolean = false,

        // 加载更多是否失败
        // 用于区分分页失败与整体 Error 状态
        val loadMoreError: Boolean = false,

        //架构预留
        // 加载更多失败时的错误提示信息
        // 仅在 loadMoreError = true 时生效
        val loadMoreErrorMessage: String? = null

    ) : FeedUiState

}
