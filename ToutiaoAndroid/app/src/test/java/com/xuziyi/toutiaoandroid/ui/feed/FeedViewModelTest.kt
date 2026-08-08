package com.xuziyi.toutiaoandroid.ui.feed

import com.xuziyi.toutiaoandroid.domain.model.FeedData
import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import com.xuziyi.toutiaoandroid.domain.repository.FeedRepositoryContract
import com.xuziyi.toutiaoandroid.domain.usecase.LoadInitialFeedUseCase
import com.xuziyi.toutiaoandroid.domain.usecase.LoadMoreFeedUseCase
import com.xuziyi.toutiaoandroid.domain.usecase.RefreshFeedUseCase
import com.xuziyi.toutiaoandroid.fakes.FakeModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    @Test
    fun initialLoadPublishesGroupedSuccessState() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val official = FakeModelFactory.fakeFeedItem(id = 1, isOfficial = true, isTop = true)
            val normal = FakeModelFactory.fakeFeedItem(id = 2)
            val repository = FakeFeedRepository(
                initialResult = FeedData(
                    topItems = listOf(official),
                    items = listOf(normal),
                    nextCursor = 90,
                    hasMore = true,
                    latestPublishTime = 100
                )
            )

            val viewModel = createViewModel(repository, dispatcher)
            advanceUntilIdle()

            val state = viewModel.state.value as FeedUiState.Success
            assertEquals(listOf(1L), state.officialItems.map(FeedItem::id))
            assertEquals(listOf(2L), state.mixedItems.map(FeedItem::id))
            assertEquals(90L, state.nextCursor)
            assertTrue(state.hasMore)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun initialLoadFailurePublishesErrorState() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = FakeFeedRepository(initialError = IllegalStateException("offline"))
            val viewModel = createViewModel(repository, dispatcher)

            advanceUntilIdle()

            val state = viewModel.state.value as FeedUiState.Error
            assertTrue(state.message.contains("offline"))
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun sceneSwitchReloadsRequestedSceneWithoutOfficialSection() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = FakeFeedRepository(
                initialResult = FeedData(items = listOf(FakeModelFactory.fakeFeedItem(id = 1)))
            )
            val viewModel = createViewModel(repository, dispatcher)
            advanceUntilIdle()

            repository.initialResult = FeedData(
                scene = "video",
                items = listOf(FakeModelFactory.fakeFeedItem(id = 3, isOfficial = true, isTop = true))
            )
            viewModel.selectScene("video")
            advanceUntilIdle()

            val state = viewModel.state.value as FeedUiState.Success
            assertEquals("video", repository.initialScenes.last())
            assertTrue(state.officialItems.isEmpty())
            assertEquals(listOf(3L), state.mixedItems.map(FeedItem::id))
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun loadMoreMergesUniqueItemsAndUpdatesCursor() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = FakeFeedRepository(
                initialResult = FeedData(
                    items = listOf(FakeModelFactory.fakeFeedItem(id = 1)),
                    nextCursor = 90,
                    hasMore = true
                ),
                loadMoreResult = FeedData(
                    items = listOf(
                        FakeModelFactory.fakeFeedItem(id = 1),
                        FakeModelFactory.fakeFeedItem(id = 2)
                    ),
                    nextCursor = 80,
                    hasMore = false
                )
            )
            val viewModel = createViewModel(repository, dispatcher)
            advanceUntilIdle()

            viewModel.loadMore()
            advanceUntilIdle()

            val state = viewModel.state.value as FeedUiState.Success
            assertEquals(listOf(1L, 2L), state.mixedItems.map(FeedItem::id).sorted())
            assertEquals(80L, state.nextCursor)
            assertFalse(state.hasMore)
            assertFalse(state.isLoadingMore)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun refreshMergesNewItemsAndFinishesRefreshState() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val repository = FakeFeedRepository(
                initialResult = FeedData(
                    items = listOf(FakeModelFactory.fakeFeedItem(id = 1, publishTime = 100)),
                    hasMore = true,
                    latestPublishTime = 100
                ),
                refreshResult = FeedData(
                    items = listOf(FakeModelFactory.fakeFeedItem(id = 2, publishTime = 200)),
                    latestPublishTime = 200
                )
            )
            val viewModel = createViewModel(repository, dispatcher)
            advanceUntilIdle()

            viewModel.refresh()
            advanceUntilIdle()

            val state = viewModel.state.value as FeedUiState.Success
            assertEquals(listOf(1L, 2L), state.mixedItems.map(FeedItem::id).sorted())
            assertEquals(200L, state.latestPublishTime)
            assertFalse(state.isRefreshing)
            assertFalse(state.showUpdateBanner)
            assertEquals(listOf(100L), repository.refreshTimes)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun createViewModel(
        repository: FeedRepositoryContract,
        dispatcher: CoroutineDispatcher
    ) = FeedViewModel(
        loadInitialFeedUseCase = LoadInitialFeedUseCase(repository),
        refreshFeedUseCase = RefreshFeedUseCase(repository),
        loadMoreFeedUseCase = LoadMoreFeedUseCase(repository),
        computationDispatcher = dispatcher
    )
}

private class FakeFeedRepository(
    var initialResult: FeedData = FeedData(items = emptyList()),
    var refreshResult: FeedData = FeedData(items = emptyList()),
    var loadMoreResult: FeedData = FeedData(items = emptyList()),
    private val initialError: Throwable? = null
) : FeedRepositoryContract {
    val initialScenes = mutableListOf<String>()
    val refreshTimes = mutableListOf<Long>()

    override suspend fun loadInitialFeed(scene: String): FeedData {
        initialScenes += scene
        initialError?.let { throw it }
        return initialResult
    }

    override suspend fun refreshFeed(scene: String, latestPublishTime: Long): FeedData {
        refreshTimes += latestPublishTime
        return refreshResult
    }

    override suspend fun loadMore(scene: String, cursor: Long): FeedData = loadMoreResult
}
