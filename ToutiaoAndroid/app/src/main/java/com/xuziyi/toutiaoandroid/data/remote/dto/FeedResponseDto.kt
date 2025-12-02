package com.xuziyi.toutiaoandroid.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FeedResponseDto(
    @SerializedName("items")
    val items: List<FeedItemDto> = emptyList(),

    // ⚠️ 修正：next_cursor 从 String? 更改为 Long? (因为后端返回的是数字)
    @SerializedName("next_cursor")
    val nextCursor: Long? = null,

    @SerializedName("has_more")
    val hasMore: Boolean = false,

    @SerializedName("latest_publish_time")
    val latestPublishTime: Long = 0L
)