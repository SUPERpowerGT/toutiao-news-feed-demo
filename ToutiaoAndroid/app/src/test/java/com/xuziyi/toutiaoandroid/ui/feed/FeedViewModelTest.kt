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

        viewModel = FeedViewModel(
            loadInitialFeedUseCase,
            refreshFeedUseCase,
            loadMoreFeedUseCase,
            renderCardTypeUseCase = RenderCardTypeUseCase(),
            processFeedItemsUseCase = ProcessFeedItemUseCase()
        )
    }

    // ================================================================
    // loadInitial() - 成功
    // ================================================================
    @Test
    fun `loadInitial should update state to Success`() = runTest {

        coEvery { loadInitialFeedUseCase() } returns listOf(fakeFeedItem(id = 1))

        dispatcher.scheduler.advanceUntilIdle()

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

        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.state.value is FeedUiState.Error)
    }

    // ================================================================
    // refresh() - 成功
    // ================================================================
    @Test
    fun `refresh should update items and newCount`() = runTest {

        // 先让 loadInitial 进入 Success 状态
        coEvery { loadInitialFeedUseCase() } returns listOf(fakeFeedItem(id = 1))
        dispatcher.scheduler.advanceUntilIdle()

        // 刷新数据
        coEvery { refreshFeedUseCase(any()) } returns listOf(fakeFeedItem(id = 2))

        viewModel.refresh()
        dispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value as FeedUiState.Success

        assertEquals(1, state.mixedItems.size)
        assertEquals(1, state.newCount)
        assertFalse(state.isRefreshing)
        assertTrue(state.isHoldingRefreshHeader)  // 动画逻辑正确
    }

    // ================================================================
    // refresh() - 异常 fallback
    // ================================================================
    @Test
    fun `refresh should fallback to stable state when exception thrown`() = runTest {

        coEvery { loadInitialFeedUseCase() } returns listOf(fakeFeedItem(id = 1))
        dispatcher.scheduler.advanceUntilIdle()

        // 刷新抛异常
        coEvery { refreshFeedUseCase(any()) } throws RuntimeException()

        viewModel.refresh()
        dispatcher.scheduler.advanceUntilIdle()

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
        dispatcher.scheduler.advanceUntilIdle()

        coEvery { loadMoreFeedUseCase(any()) } returns FeedData(
            items = listOf(fakeFeedItem(id = 2)),
            hasMore = true,
            nextCursor = 500
        )

        viewModel.loadMore()
        dispatcher.scheduler.advanceUntilIdle()

        val s = viewModel.state.value as FeedUiState.Success

        assertEquals(2, s.mixedItems.size)
        assertTrue(s.hasMore)
        assertEquals(500, s.nextCursor)
    }

    // ================================================================
    // loadMore() - items.isEmpty → 没有更多内容
    // ================================================================
    @Test
    fun `loadMore should stop when no more data`() = runTest {

        coEvery { loadInitialFeedUseCase() } returns listOf(fakeFeedItem(id = 1))
        dispatcher.scheduler.advanceUntilIdle()

        coEvery { loadMoreFeedUseCase(any()) } returns FeedData(
            items = emptyList(),
            hasMore = false
        )

        viewModel.loadMore()
        dispatcher.scheduler.advanceUntilIdle()

        val s = viewModel.state.value as FeedUiState.Success
        assertFalse(s.hasMore)
        assertFalse(s.isLoadingMore)
    }

    // ================================================================
    // loadMore() - 异常处理
    // ================================================================
    @Test
    fun `loadMore should set loadMoreError when exception occurs`() = runTest {

        coEvery { loadInitialFeedUseCase() } returns listOf(fakeFeedItem(id = 1))
        dispatcher.scheduler.advanceUntilIdle()

        coEvery { loadMoreFeedUseCase(any()) } throws RuntimeException("LoadMore failed")

        viewModel.loadMore()
        dispatcher.scheduler.advanceUntilIdle()

        val s = viewModel.state.value as FeedUiState.Success

        assertTrue(s.loadMoreError)
        assertEquals("LoadMore failed", s.loadMoreErrorMessage)
    }

    // ================================================================
    // updatePullProgress() - clamp 测试
    // ================================================================
    @Test
    fun `updatePullProgress should clamp value between 0 and 1`() = runTest {

        coEvery { loadInitialFeedUseCase() } returns listOf(fakeFeedItem(id = 1))
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.updatePullProgress(5f)
        dispatcher.scheduler.advanceUntilIdle()

        val s = viewModel.state.value as FeedUiState.Success
        assertEquals(1f, s.pullProgress, 0.01f)
    }
}
