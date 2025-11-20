package com.xuziyi.toutiaoandroid.data.datasource

import com.xuziyi.toutiaoandroid.data.remote.api.FakeFeedApiService
import com.xuziyi.toutiaoandroid.data.remote.dto.FeedResponseDto

/**
 * RemoteDataSource
 * 负责发起实际的网络请求（或者 mock 请求）
 */
class RemoteDataSource(
    private val api: FakeFeedApiService   // 你现在是用假的 API
) {

    suspend fun loadInitialFeed(): FeedResponseDto {
        return api.getFeed(cursor = null, refreshTime = null)
    }

    suspend fun refreshFeed(latestPublishTime: Long): FeedResponseDto {
        return api.getFeed(cursor = null, refreshTime = latestPublishTime)
    }

    suspend fun loadMore(cursor: String): FeedResponseDto {
        return api.getFeed(cursor = cursor, refreshTime = null)
    }
}
