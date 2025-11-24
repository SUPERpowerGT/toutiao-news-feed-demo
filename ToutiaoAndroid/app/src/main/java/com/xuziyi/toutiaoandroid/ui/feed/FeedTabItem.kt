package com.xuziyi.toutiaoandroid.ui.feed

// 最终模型：列表数据只需包含标题和是否显示红点
data class FeedTabItem(
    val title: String,
    // 用于“推荐”等 Tab 默认显示的红点
    val showDot: Boolean = false
)