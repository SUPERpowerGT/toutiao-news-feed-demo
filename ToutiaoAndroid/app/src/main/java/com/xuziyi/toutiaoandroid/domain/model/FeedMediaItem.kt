package com.xuziyi.toutiaoandroid.domain.model

data class FeedMediaItem(
    val mediaType: String,
    val url: String?,
    val coverUrl: String?,
    val duration: Int?,
    val width: Int?,
    val height: Int?
)
