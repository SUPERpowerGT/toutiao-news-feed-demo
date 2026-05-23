package com.xuziyi.toutiaoandroid.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FeedResponseDto(
    @SerializedName("scene")
    val scene: String = "recommend",

    @SerializedName("top_items")
    val topItems: List<FeedItemDto> = emptyList(),

    @SerializedName("items")
    val items: List<FeedItemDto> = emptyList(),

    @SerializedName("next_cursor")
    val nextCursor: Long? = null,

    @SerializedName("has_more")
    val hasMore: Boolean = false,

    @SerializedName("latest_publish_time")
    val latestPublishTime: Long = 0L
)
