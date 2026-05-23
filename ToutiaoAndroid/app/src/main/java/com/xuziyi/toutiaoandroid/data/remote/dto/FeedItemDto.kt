package com.xuziyi.toutiaoandroid.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FeedItemDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("summary")
    val summary: String? = null,

    // 内容本质类型（后端给）
    @SerializedName("content_type")
    val contentType: String,

    // 媒体列表
    @SerializedName("media")
    val media: List<MediaDto> = emptyList(),

    // 作者信息（一个或者多个）
    @SerializedName("author")
    val author: AuthorDto,

    // 点赞、评论、阅读统计
    @SerializedName("stats")
    val stats: StatsDto,

    @SerializedName("publish_time")
    val publishTime: Long,

    // ===== 新增：内容语义属性 =====

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("sub_category")
    val subCategory: String? = null,

    @SerializedName("tags")
    val tags: List<String>? = null,

    @SerializedName("city")
    val city: String? = null,

    // 是否是官方媒体内容（作者级的）
    @SerializedName("is_official_media")
    val isOfficialMedia: Boolean = false,

    // “权威发布/官方 Top5” 业务
    @SerializedName("is_top_official")
    val isTopOfficial: Boolean = false,

    //发布账号
    @SerializedName("source")
    val source: String? = null,

    // 推荐排序权重
    @SerializedName("weight")
    val weight: Float = 0f,

    @SerializedName("recommend_score")
    val recommendScore: Float = 0f,

    @SerializedName("reason")
    val reason: String? = null
)
