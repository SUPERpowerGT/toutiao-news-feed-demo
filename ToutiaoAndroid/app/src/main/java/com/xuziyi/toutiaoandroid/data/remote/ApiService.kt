package com.xuziyi.toutiaoandroid.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/api/v1/feed")
    suspend fun getFeed(
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int = 10
    ): FeedResponse
}
