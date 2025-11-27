package com.xuziyi.toutiaoandroid.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FeedItemDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("summary")
    val summary: String? = null,

    @SerializedName("news_type")
    val cardType: String, // text / image / multi_image / video

    @SerializedName("media")
    val media: List<MediaDto> = emptyList(),

    @SerializedName("author")
    val author: AuthorDto,

    @SerializedName("stats")
    val stats: StatsDto,

    @SerializedName("publish_time")
    val publishTime: Long
)
