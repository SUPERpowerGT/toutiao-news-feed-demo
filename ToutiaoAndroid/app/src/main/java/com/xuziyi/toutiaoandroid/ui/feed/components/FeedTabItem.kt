package com.xuziyi.toutiaoandroid.ui.feed.components

data class FeedTabItem(
    val id: Long, // <--- 新增：用于 LazyRow 的稳定 Key
    val title: String,
    val showDot: Boolean = false
)