package com.xuziyi.toutiaoandroid.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthorDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @SerializedName("certification")
    val certification: String? = null // red_v / yellow_v / null
)
