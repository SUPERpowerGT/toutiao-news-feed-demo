package com.xuziyi.toutiaoandroid.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuziyi.toutiaoandroid.domain.usecase.LoadInitialFeedUseCase
import com.xuziyi.toutiaoandroid.domain.usecase.LoadMoreFeedUseCase
import com.xuziyi.toutiaoandroid.domain.usecase.RefreshFeedUseCase
import com.xuziyi.toutiaoandroid.domain.usecase.ProcessFeedItemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeedViewModel(
    private val loadInitialFeedUseCase: LoadInitialFeedUseCase,
    private val refreshFeedUseCase: RefreshFeedUseCase,
    private val loadMoreFeedUseCase: LoadMoreFeedUseCase,
    private val processFeedItemsUseCase: ProcessFeedItemsUseCase = ProcessFeedItemsUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val state: StateFlow<FeedUiState> = _state

    init {
        loadInitial()
    }

    // ======================
    // 1. 首次加载
    // ======================
    private fun loadInitial() {
        viewModelScope.launch {
            try {
                _state.value = FeedUiState.Loading

                val raw = loadInitialFeedUseCase()
                val processed = processFeedItemsUseCase.execute(raw)

                _state.value = FeedUiState.Success(
                    officialItems = processed.officialList,
                    mixedItems = processed.mixedList,
                    hasMore = true,
                    isRefreshing = false,
                    isLoadingMore = false
                )

            } catch (e: Exception) {
                _state.value = FeedUiState.Error("加载失败：${e.message}")
            }
        }
    }

    // ======================
    // 2. 下拉刷新
    // ======================
    fun refresh() {
        val current = state.value
        if (current !is FeedUiState.Success) return

        viewModelScope.launch {
            try {
                _state.value = current.copy(isRefreshing = true)

                val latest = System.currentTimeMillis() / 1000
                val raw = refreshFeedUseCase(latest)

                val processed = processFeedItemsUseCase.execute(
                    raw + current.mixedItems
                )

                _state.value = current.copy(
                    officialItems = processed.officialList,
                    mixedItems = processed.mixedList,
                    isRefreshing = false,
                    latestPublishTime = latest
                )
            } catch (e: Exception) {
                _state.value = FeedUiState.Error("刷新失败：${e.message}")
            }
        }
    }

    // ======================
    // 3. 加载更多
    // ======================
    fun loadMore() {
        val current = state.value
        if (current !is FeedUiState.Success) return

        // 未来分页会用，现在先假值
        val cursor = current.latestPublishTime?.toString() ?: "0"

        viewModelScope.launch {
            try {
                _state.value = current.copy(isLoadingMore = true)

                val raw = loadMoreFeedUseCase(cursor)

                if (raw.isEmpty()) {
                    _state.value = current.copy(
                        isLoadingMore = false,
                        hasMore = false
                    )
                    return@launch
                }

                val processed = processFeedItemsUseCase.execute(
                    current.officialItems +
                            current.mixedItems +
                            raw
                )

                _state.value = current.copy(
                    officialItems = processed.officialList,
                    mixedItems = processed.mixedList,
                    isLoadingMore = false,
                    hasMore = true
                )

            } catch (e: Exception) {
                _state.value = FeedUiState.Error("加载更多失败：${e.message}")
            }
        }
    }
}
