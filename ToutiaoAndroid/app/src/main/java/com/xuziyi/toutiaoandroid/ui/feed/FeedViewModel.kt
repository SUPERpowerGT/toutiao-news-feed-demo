package com.xuziyi.toutiaoandroid.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuziyi.toutiaoandroid.domain.usecase.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class FeedViewModel(
    private val loadInitialFeedUseCase: LoadInitialFeedUseCase,
    private val refreshFeedUseCase: RefreshFeedUseCase,
    private val loadMoreFeedUseCase: LoadMoreFeedUseCase,
    private val renderCardTypeUseCase: RenderCardTypeUseCase = RenderCardTypeUseCase(),
    private val processFeedItemsUseCase: ProcessFeedItemUseCase = ProcessFeedItemUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val state: StateFlow<FeedUiState> = _state

    init {
        loadInitial()
    }

    private fun loadInitial() {
        viewModelScope.launch {
            try {
                _state.value = FeedUiState.Loading

                val raw = loadInitialFeedUseCase()
                val rendered = renderCardTypeUseCase.execute(raw)
                val processed = processFeedItemsUseCase.execute(rendered)

                _state.value = FeedUiState.Success(
                    officialItems = processed.officialList,
                    mixedItems = processed.mixedList
                )

            } catch (e: Exception) {
                _state.value = FeedUiState.Error("加载失败：${e.message}")
            }
        }
    }

    fun updatePullProgress(progress: Float) {
        val current = state.value as? FeedUiState.Success ?: return

        _state.value = current.copy(
            pullProgress = progress.coerceIn(0f, 1f)
        )
    }

    fun refresh() {
        val current = state.value as? FeedUiState.Success ?: return

        viewModelScope.launch {
            try {
                _state.value = current.copy(
                    isRefreshing = true,
                    isHoldingRefreshHeader = true,
                    showRefreshAnimation = true,
                    showUpdateBanner = false,
                    newCount = 0
                )

                val latest = System.currentTimeMillis() / 1000

                val startTime = System.currentTimeMillis()
                val minDisplay = 1200L
                val maxTimeout = 5000L

                val raw = withTimeout(maxTimeout) { refreshFeedUseCase(latest) }

                val rendered = renderCardTypeUseCase.execute(raw)
                val processed = processFeedItemsUseCase.execute(rendered)

                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed < minDisplay) delay(minDisplay - elapsed)

                _state.value = (_state.value as FeedUiState.Success).copy(
                    officialItems = processed.officialList,
                    mixedItems = processed.mixedList,
                    isRefreshing = false,
                    showRefreshAnimation = false,
                    isHoldingRefreshHeader = true,
                    newCount = raw.size,
                    showUpdateBanner = true
                )

                delay(1000)

                _state.value = (_state.value as FeedUiState.Success).copy(
                    showUpdateBanner = false
                )

                delay(16)
                _state.value = (_state.value as FeedUiState.Success).copy(
                    isHoldingRefreshHeader = false
                )

                delay(160)
                _state.value = (_state.value as FeedUiState.Success).copy(
                    pullProgress = 0f,
                    newCount = 0,
                    showRefreshAnimation = false
                )

            } catch (e: Exception) {
                val fallback = state.value as? FeedUiState.Success ?: current
                _state.value = fallback.copy(
                    isRefreshing = false,
                    showRefreshAnimation = false,
                    isHoldingRefreshHeader = false,
                    showUpdateBanner = false
                )
            }
        }
    }

    private fun hideUpdateHint() {
        val current = _state.value as? FeedUiState.Success ?: return
        _state.value = current.copy(newCount = 0)
    }

    //触底加载更多 —— 新增分页错误处理
    fun loadMore() {
        val current = state.value as? FeedUiState.Success ?: return

        if (current.isLoadingMore || !current.hasMore) return

        val cursor = current.nextCursor ?: current.mixedItems.minOf { it.publishTime }

        viewModelScope.launch {

            _state.value = current.copy(
                isLoadingMore = true,
                loadMoreError = false,
                loadMoreErrorMessage = null
            )

            try {
                val feedData = loadMoreFeedUseCase(cursor)

                val afterLoading = _state.value as FeedUiState.Success

                if (feedData.items.isEmpty()) {
                    _state.value = afterLoading.copy(
                        isLoadingMore = false,
                        hasMore = false
                    )
                    return@launch
                }

                val newRendered = renderCardTypeUseCase.execute(feedData.items)
                val existingRendered =
                    renderCardTypeUseCase.execute(afterLoading.officialItems + afterLoading.mixedItems)

                val processed = processFeedItemsUseCase.execute(existingRendered + newRendered)

                _state.value = afterLoading.copy(
                    isLoadingMore = false,
                    officialItems = processed.officialList,
                    mixedItems = processed.mixedList,
                    hasMore = feedData.hasMore,
                    nextCursor = feedData.nextCursor
                )

            } catch (e: Exception) {

                val afterLoading = _state.value as FeedUiState.Success

                _state.value = afterLoading.copy(
                    isLoadingMore = false,
                    loadMoreError = true,                              // ← 告诉 UI 失败了
                    loadMoreErrorMessage = e.message ?: "加载更多失败"     // ← 显示错误信息
                )
            }
        }
    }
}
