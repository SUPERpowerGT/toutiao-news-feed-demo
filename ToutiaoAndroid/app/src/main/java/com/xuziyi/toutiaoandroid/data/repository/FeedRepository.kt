package com.xuziyi.toutiaoandroid.data.repository

import com.xuziyi.toutiaoandroid.data.datasource.RemoteDataSource
import com.xuziyi.toutiaoandroid.data.remote.mapper.toDomain
import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import com.xuziyi.toutiaoandroid.domain.repository.FeedRepositoryContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedRepository(
    private val remoteDataSource: RemoteDataSource
) : FeedRepositoryContract {

    override suspend fun loadInitialFeed(): List<FeedItem> {
        val response = remoteDataSource.loadInitialFeed()
        // 🔥 把 map 转换放到 Default 线程（CPU 密集型，避免卡 UI）
        return withContext(Dispatchers.Default) {
            response.items.map { it.toDomain() }
        }
    }

    override suspend fun refreshFeed(latestPublishTime: Long): List<FeedItem> {
        val response = remoteDataSource.refreshFeed(latestPublishTime)

        return withContext(Dispatchers.Default) {
            response.items.map { it.toDomain() }
        }
    }

    override suspend fun loadMore(cursor: String): List<FeedItem> {
        val response = remoteDataSource.loadMore(cursor)

        return withContext(Dispatchers.Default) {
            response.items.map { it.toDomain() }
        }
    }
}
