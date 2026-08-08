package com.xuziyi.toutiaoandroid.data.repository

import com.xuziyi.toutiaoandroid.data.datasource.RemoteDataSource
import com.xuziyi.toutiaoandroid.data.local.LocalDataSource
import com.xuziyi.toutiaoandroid.data.remote.mapper.toDomain
import com.xuziyi.toutiaoandroid.domain.model.FeedData
import com.xuziyi.toutiaoandroid.domain.repository.FeedRepositoryContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val cacheRefreshScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : FeedRepositoryContract {

    override suspend fun loadInitialFeed(scene: String): FeedData = withContext(Dispatchers.IO) {
        val cached = localDataSource.getFeed(scene)
        if (cached.topItems.isNotEmpty() || cached.items.isNotEmpty()) {
            cacheRefreshScope.launch {
                runCatching { remoteDataSource.loadInitialFeed(scene).toDomain() }
                    .onSuccess { cache(it) }
            }
            return@withContext cached
        }

        val data = remoteDataSource.loadInitialFeed(scene).toDomain()
        cache(data)
        data
    }

    override suspend fun refreshFeed(scene: String, latestPublishTime: Long): FeedData =
        withContext(Dispatchers.IO) {
            val data = remoteDataSource.refreshFeed(scene, latestPublishTime).toDomain()
            cache(data)
            data
        }

    override suspend fun loadMore(scene: String, cursor: Long): FeedData = withContext(Dispatchers.IO) {
        val data = remoteDataSource.loadMore(scene, cursor).toDomain()
        cache(data)
        data
    }

    private suspend fun cache(data: FeedData) {
        localDataSource.saveFeedItems(data.topItems + data.items)
    }
}
