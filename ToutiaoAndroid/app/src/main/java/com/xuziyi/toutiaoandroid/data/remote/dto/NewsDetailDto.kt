package com.xuziyi.toutiaoandroid.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NewsDetailDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("content_html")
    val contentHtml: String? = null,

    @SerializedName("content_json")
    val contentJson: String? = null,

    @SerializedName("news_type")
    val newsType: String,

    @SerializedName("media")
    val media: List<MediaDto> = emptyList(),

    @SerializedName("author")
    val author: AuthorDto,

    @SerializedName("stats")
    val stats: StatsDto,

    @SerializedName("publish_time")
    val publishTime: Long
)
