package com.xuziyi.toutiaoandroid.ui.feed

// 🔥 修正：添加一个唯一的 long/int 类型 ID
data class FeedTabItem(
    val id: Long, // <--- 新增：用于 LazyRow 的稳定 Key
    val title: String,
    val showDot: Boolean = false
)