package com.xuziyi.toutiaoandroid.data.repository

import com.xuziyi.toutiaoandroid.data.datasource.RemoteDataSource
import com.xuziyi.toutiaoandroid.data.local.LocalDataSource
import com.xuziyi.toutiaoandroid.data.remote.dto.AuthorDto
import com.xuziyi.toutiaoandroid.data.remote.dto.FeedItemDto
import com.xuziyi.toutiaoandroid.data.remote.dto.FeedResponseDto
import com.xuziyi.toutiaoandroid.data.remote.dto.StatsDto
import com.xuziyi.toutiaoandroid.domain.model.FeedData
import com.xuziyi.toutiaoandroid.fakes.FakeModelFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedRepositoryTest {
    private val remote = mockk<RemoteDataSource>()
    private val local = mockk<LocalDataSource>()

    @Test
    fun successfulInitialLoadWritesRemoteItemsToCache() = runTest {
        coEvery { remote.loadInitialFeed("recommend") } returns remoteFeed()
        coEvery { local.getFeed("recommend") } returns FeedData(items = emptyList())
        coEvery { local.saveFeedItems(any()) } returns Unit

        val result = FeedRepository(remote, local).loadInitialFeed("recommend")

        assertEquals(listOf(7L), result.items.map { it.id })
        coVerify(exactly = 1) { local.saveFeedItems(match { it.map { item -> item.id } == listOf(7L) }) }
    }

    @Test
    fun cachedInitialLoadReturnsImmediatelyAndRefreshesCacheInBackground() = runTest {
        val cached = FeedData(scene = "tech", items = listOf(FakeModelFactory.fakeFeedItem(id = 9)))
        coEvery { remote.loadInitialFeed("tech") } returns remoteFeed()
        coEvery { local.getFeed("tech") } returns cached
        coEvery { local.saveFeedItems(any()) } returns Unit

        val result = FeedRepository(
            remote,
            local,
            cacheRefreshScope = this
        ).loadInitialFeed("tech")

        assertEquals(listOf(9L), result.items.map { it.id })
        coVerify(exactly = 1) { local.getFeed("tech") }
        coVerify(exactly = 1) { remote.loadInitialFeed("tech") }
        coVerify(exactly = 1) { local.saveFeedItems(any()) }
    }

    @Test
    fun cachedInitialLoadSurvivesOfflineBackgroundRefresh() = runTest(UnconfinedTestDispatcher()) {
        val cached = FeedData(scene = "tech", items = listOf(FakeModelFactory.fakeFeedItem(id = 9)))
        coEvery { remote.loadInitialFeed("tech") } throws IllegalStateException("offline")
        coEvery { local.getFeed("tech") } returns cached

        val result = FeedRepository(remote, local, this).loadInitialFeed("tech")

        assertEquals(listOf(9L), result.items.map { it.id })
    }

    private fun remoteFeed() = FeedResponseDto(
        items = listOf(
            FeedItemDto(
                id = 7,
                title = "cached story",
                contentType = "text",
                author = AuthorDto(1, "author"),
                stats = StatsDto(),
                publishTime = 100
            )
        ),
        latestPublishTime = 100
    )
}
