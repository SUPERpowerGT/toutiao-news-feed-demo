package com.xuziyi.toutiaoandroid.data.repository

import com.xuziyi.toutiaoandroid.data.datasource.RemoteDataSource
import com.xuziyi.toutiaoandroid.data.remote.mapper.toDomain
import com.xuziyi.toutiaoandroid.domain.model.FeedData
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
        //val response = remoteDataSource.refreshFeed(latestPublishTime)
        //fake一个时间来模拟有新的新闻插入（给一个旧的时间）
        val fakeRefreshTime = 1730419200L
        val response = remoteDataSource.refreshFeed(fakeRefreshTime)
        return withContext(Dispatchers.Default) {
            response.items.map { it.toDomain() }
        }
    }

    override suspend fun loadMore(cursor: Long): FeedData {
        val response = remoteDataSource.loadMore(cursor)
        return withContext(Dispatchers.Default) {
            response.toDomain()   // ⭐ 同样使用 mapper
        }
    }
}
