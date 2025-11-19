package com.xuziyi.toutiaoandroid.domain.model

data class FeedItem(
    val id: Long,
    val news_id: Long,
    val display_type: String,
    val weight: Double,
    val scene: String,
    val model_id: String,
    val publish_time: String,
    val seq_id: Long,
    val created_at: String
)
