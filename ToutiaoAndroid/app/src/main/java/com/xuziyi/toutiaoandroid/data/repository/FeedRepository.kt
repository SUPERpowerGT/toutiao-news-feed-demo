package com.xuziyi.toutiaoandroid.data.repository

import com.xuziyi.toutiaoandroid.data.datasource.RemoteDataSource
import com.xuziyi.toutiaoandroid.data.local.LocalDataSource
import com.xuziyi.toutiaoandroid.data.remote.mapper.toDomain
import com.xuziyi.toutiaoandroid.domain.model.FeedData
import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import com.xuziyi.toutiaoandroid.domain.repository.FeedRepositoryContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : FeedRepositoryContract {

    override suspend fun loadInitialFeed(): List<FeedItem> = withContext(Dispatchers.IO) {
        val localItems = localDataSource.getAllFeedItems()
        if (localItems.isNotEmpty()) {
            refreshSilently()
            return@withContext localItems
        }

        val response = remoteDataSource.loadInitialFeed()
        val items = response.items.map { it.toDomain() }

        localDataSource.saveFeedItems(items)
        return@withContext items
    }

    override suspend fun refreshFeed(latestPublishTime: Long): List<FeedItem> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = remoteDataSource.refreshFeed(latestPublishTime)
                val items = response.items.map { it.toDomain() }

                localDataSource.saveFeedItems(items)
                items
            } catch (e: Exception) {
                localDataSource.getAllFeedItems()
            }
        }

    override suspend fun loadMore(cursor: Long): FeedData = withContext(Dispatchers.IO) {
        val response = remoteDataSource.loadMore(cursor)
        val domain = response.toDomain()

        localDataSource.saveFeedItems(domain.items)
        domain
    }

    private suspend fun refreshSilently() {
        try {
            val response = remoteDataSource.loadInitialFeed()
            val items = response.items.map { it.toDomain() }
            localDataSource.saveFeedItems(items)
        } catch (_: Exception) { }
    }
}
