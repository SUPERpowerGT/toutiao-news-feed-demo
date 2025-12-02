package com.xuziyi.toutiaoandroid.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StatsDto(
    // ✅ 优化：显式指定 JSON 字段名
    @SerializedName("like_count")
    val likeCount: Int = 0,

    @SerializedName("comment_count")
    val commentCount: Int = 0,

    @SerializedName("favorite_count")
    val favoriteCount: Int = 0,

    @SerializedName("share_count")
    val shareCount: Int = 0
)