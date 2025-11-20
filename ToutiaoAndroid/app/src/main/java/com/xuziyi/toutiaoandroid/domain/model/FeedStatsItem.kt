package com.xuziyi.toutiaoandroid.domain.model

data class FeedStatsItem(
    val likeCount: Int,
    val commentCount: Int,
    val favoriteCount: Int?,
    val shareCount: Int?
)
