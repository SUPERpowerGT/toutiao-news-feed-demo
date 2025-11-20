package com.xuziyi.toutiaoandroid.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CommentDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("user_name")
    val userName: String,

    @SerializedName("user_avatar")
    val userAvatar: String? = null,

    @SerializedName("content")
    val content: String,

    @SerializedName("publish_time")
    val publishTime: Long,

    @SerializedName("like_count")
    val likeCount: Int = 0
)
