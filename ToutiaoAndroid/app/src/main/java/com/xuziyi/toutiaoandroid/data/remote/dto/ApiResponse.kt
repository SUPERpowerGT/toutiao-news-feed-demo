package com.xuziyi.toutiaoandroid.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * 通用 API 响应包装类
 * 对应后端 JSON 的顶层结构：{ "code": 0, "data": {...}, "message": "..." }
 * * @param T 是 data 字段内部的具体数据类型 (例如 FeedResponseDto)
 */
data class ApiResponse<T>(
    @SerializedName("code")
    val code: Int,

    @SerializedName("data")
    val data: T? = null, // 使用泛型 T 来包裹实际的业务数据

    @SerializedName("message")
    val message: String? = null
)