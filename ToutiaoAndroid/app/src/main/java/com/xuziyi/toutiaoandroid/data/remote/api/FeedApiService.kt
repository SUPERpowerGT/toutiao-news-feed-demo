package com.xuziyi.toutiaoandroid.data.remote.api

import com.xuziyi.toutiaoandroid.data.remote.dto.ApiResponse
import com.xuziyi.toutiaoandroid.data.remote.dto.CommentDto
import com.xuziyi.toutiaoandroid.data.remote.dto.FeedResponseDto
import com.xuziyi.toutiaoandroid.data.remote.dto.NewsDetailDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 今日头条首页 Feed 接口
 * 只负责定义 HTTP 形状，不负责业务逻辑
 */
interface FeedApiService {

    @GET("api/v1/feed")
    suspend fun getFeed(
        @Query("cursor") cursor: String? = null,
        @Query("refresh_time") refreshTime: Long? = null
    ): ApiResponse<FeedResponseDto>

    @GET("api/v1/news/{id}")
    suspend fun getNewsDetail(
        @Path("id") id: Long
    ): NewsDetailDto

    @GET("api/v1/news/{id}/comments")
    suspend fun getComments(
        @Path("id") id: Long,
        @Query("cursor") cursor: String? = null
    ): List<CommentDto>
}
