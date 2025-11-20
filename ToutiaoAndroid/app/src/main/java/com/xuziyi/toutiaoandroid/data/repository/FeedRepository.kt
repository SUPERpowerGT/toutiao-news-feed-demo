package com.xuziyi.toutiaoandroid.data.repository

import com.xuziyi.toutiaoandroid.data.datasource.RemoteDataSource
import com.xuziyi.toutiaoandroid.data.remote.dto.toDomain
import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import com.xuziyi.toutiaoandroid.domain.repository.FeedRepositoryContract

/**
 * FeedRepository
 * 具体实现 FeedRepositoryContract
 */
class FeedRepository(
    private val remoteDataSource: RemoteDataSource
) : FeedRepositoryContract {

    override suspend fun loadInitialFeed(): List<FeedItem> {
        val response = remoteDataSource.loadInitialFeed()
        return response.items.map { it.toDomain() }
    }

    override suspend fun refreshFeed(latestPublishTime: Long): List<FeedItem> {
        val response = remoteDataSource.refreshFeed(latestPublishTime)
        return response.items.map { it.toDomain() }
    }

    override suspend fun loadMore(cursor: String): List<FeedItem> {
        val response = remoteDataSource.loadMore(cursor)
        return response.items.map { it.toDomain() }
    }
}
