package com.xuziyi.toutiaoandroid.data.remote

data class FeedResponse(
    val code: Int,
    val message: String,
    val data: FeedData
)

data class FeedData(
    val items: List<FeedItemDto>,
    val next_cursor: String?
)

data class FeedItemDto(
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

