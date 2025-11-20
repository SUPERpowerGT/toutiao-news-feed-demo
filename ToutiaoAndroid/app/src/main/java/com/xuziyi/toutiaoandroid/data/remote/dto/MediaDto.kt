package com.xuziyi.toutiaoandroid.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MediaDto(
    @SerializedName("media_type")
    val mediaType: String, // image / video

    @SerializedName("url")
    val url: String? = null,

    @SerializedName("cover_url")
    val coverUrl: String? = null,

    @SerializedName("duration")
    val duration: Int? = null,

    @SerializedName("width")
    val width: Int? = null,

    @SerializedName("height")
    val height: Int? = null
)
