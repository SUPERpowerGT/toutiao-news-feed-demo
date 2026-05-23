package com.xuziyi.toutiaoandroid.data.repository

import com.xuziyi.toutiaoandroid.data.datasource.RemoteDataSource
import com.xuziyi.toutiaoandroid.data.local.LocalDataSource
import com.xuziyi.toutiaoandroid.data.remote.mapper.toDomain
import com.xuziyi.toutiaoandroid.domain.model.FeedData
import com.xuziyi.toutiaoandroid.domain.repository.FeedRepositoryContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : FeedRepositoryContract {

    override suspend fun loadInitialFeed(scene: String): FeedData = withContext(Dispatchers.IO) {
        val response = remoteDataSource.loadInitialFeed(scene)
        return@withContext response.toDomain()
    }

    override suspend fun refreshFeed(scene: String, latestPublishTime: Long): FeedData =
        withContext(Dispatchers.IO) {
            val response = remoteDataSource.refreshFeed(scene, latestPublishTime)
            return@withContext response.toDomain()
        }

    override suspend fun loadMore(scene: String, cursor: Long): FeedData = withContext(Dispatchers.IO) {
        val response = remoteDataSource.loadMore(scene, cursor)
        return@withContext response.toDomain()
    }
}
