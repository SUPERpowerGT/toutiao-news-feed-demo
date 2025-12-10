package com.xuziyi.toutiaoandroid.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StatsDto(
    @SerializedName("like_count")
    val likeCount: Int = 0,

    @SerializedName("comment_count")
    val commentCount: Int = 0,

    @SerializedName("favorite_count")
    val favoriteCount: Int = 0,

    @SerializedName("share_count")
    val shareCount: Int = 0
)