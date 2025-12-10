package com.xuziyi.toutiaoandroid.ui.feed

import com.xuziyi.toutiaoandroid.domain.model.FeedData
import com.xuziyi.toutiaoandroid.domain.usecase.*
import com.xuziyi.toutiaoandroid.fakes.FakeModelFactory.fakeFeedItem
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private lateinit var viewModel: FeedViewModel

    private val loadInitialFeedUseCase: LoadInitialFeedUseCase = mockk()
    private val refreshFeedUseCase: RefreshFeedUseCase = mockk()
    private val loadMoreFeedUseCase: LoadMoreFeedUseCase = mockk()

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    // 工具：创建 ViewModel 并执行 init{} 完整流程
    private fun buildViewModel() {
        viewModel = FeedViewModel(
            loadInitialFeedUseCase,
            refreshFeedUseCase,
            loadMoreFeedUseCase,
            renderCardTypeUseCase = RenderCardTypeUseCase(),
            processFeedItemsUseCase = ProcessFeedItemUseCase()
        )
        // 让 init{} 的 loadInitial() 执行完成
        advanceUntilIdle()
    }

    private fun advanceUntilIdle() {
        dispatcher.scheduler.advanceUntilIdle()
    }

    // ================================================================
    // loadInitial() - 成功
    // ================================================================
    @Test
    fun `loadInitial should update state to Success`() = runTest {
        coEvery { loadInitialFeedUseCase() } returns listOf(fakeFeedItem(id = 1))

        buildViewModel()

        val state = viewModel.state.value
        assertTrue(state is FeedUiState.Success)
        val s = state as FeedUiState.Success
        assertEquals(1, s.mixedItems.size)
    }

    // ================================================================
    // loadInitial() - 失败
    // ================================================================
    @Test
    fun `loadInitial should emit Error when exception thrown`() = runTest {
        coEvery { loadInitialFeedUseCase() } throws RuntimeException("Network error")

        buildViewModel()

        assertTrue(viewModel.state.value is FeedUiState.Error)
    }


    // ================================================================
    // refresh() - 异常 fallback
    // ================================================================
    @Test
    fun `refresh should fallback to stable state when exception thrown`() = runTest {
        coEvery { loadInitialFeedUseCase() } returns listOf(fakeFeedItem(id = 1))
        buildViewModel()

        coEvery { refreshFeedUseCase(any()) } throws RuntimeException()

        viewModel.refresh()
        advanceUntilIdle()

        val s = viewModel.state.value as FeedUiState.Success
        assertFalse(s.isRefreshing)
        assertFalse(s.showRefreshAnimation)
        assertFalse(s.isHoldingRefreshHeader)
        assertFalse(s.showUpdateBanner)
    }

    // ================================================================
    // loadMore() - 成功追加
    // ================================================================
    @Test
    fun `loadMore should append new items`() = runTest {

        coEvery { loadInitialFeedUseCase() } returns listOf(
            fakeFeedItem(id = 1, publishTime = 1000)
        )

        buildViewModel()

        coEvery { loadMoreFeedUseCase(any()) } returns FeedData(
            items = listOf(fakeFeedItem(id = 2, publishTime = 900)),
            hasMore = true,
            nextCursor = 500L
        )

        viewModel.loadMore()
        advanceUntilIdle()

        val s = viewModel.state.value as FeedUiState.Success

        assertEquals(2, s.mixedItems.size)
        assertTrue(s.mixedItems.any { it.id == 1L })
        assertTrue(s.mixedItems.any { it.id == 2L })
        assertTrue(s.hasMore)
        assertEquals(500L, s.nextCursor)
    }


    // ================================================================
    // loadMore() - items.isEmpty → No more content
    // ================================================================
    @Test
    fun `loadMore should stop when no more data`() = runTest {
        coEvery { loadInitialFeedUseCase() } returns listOf(fakeFeedItem(id = 1))
        buildViewModel()

        coEvery { loadMoreFeedUseCase(any()) } returns FeedData(
            items = emptyList(),
            hasMore = false
        )

        viewModel.loadMore()
        advanceUntilIdle()

        val s = viewModel.state.value as FeedUiState.Success
        assertFalse(s.hasMore)
        assertFalse(s.isLoadingMore)
    }

    // ================================================================
    // loadMore() - 异常
    // ================================================================
    @Test
    fun `loadMore should set loadMoreError when exception occurs`() = runTest {
        coEvery { loadInitialFeedUseCase() } returns listOf(fakeFeedItem(id = 1))
        buildViewModel()

        coEvery { loadMoreFeedUseCase(any()) } throws RuntimeException("LoadMore failed")

        viewModel.loadMore()
        advanceUntilIdle()

        val s = viewModel.state.value as FeedUiState.Success
        assertTrue(s.loadMoreError)
        assertEquals("LoadMore failed", s.loadMoreErrorMessage)
    }

    // ================================================================
    // updatePullProgress()
    // ================================================================
    @Test
    fun `updatePullProgress should clamp value between 0 and 1`() = runTest {
        coEvery { loadInitialFeedUseCase() } returns listOf(fakeFeedItem(id = 1))
        buildViewModel()

        viewModel.updatePullProgress(5f)
        advanceUntilIdle()

        val s = viewModel.state.value as FeedUiState.Success
        assertEquals(1f, s.pullProgress, 0.01f)
    }
}
