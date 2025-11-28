package com.xuziyi.toutiaoandroid.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuziyi.toutiaoandroid.domain.usecase.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

    // ======================
    // 1. 首次加载
    // ======================
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

    // ======================
    // 2. 手势下拉：只更新动画进度
    // ======================
    fun updatePullProgress(progress: Float) {
        val current = state.value
        if (current !is FeedUiState.Success) return

        _state.value = current.copy(
            pullProgress = progress.coerceIn(0f, 1f)
        )
    }

    // ======================
    // 3. 触发刷新
    // ======================
    fun refresh() {
        val current = state.value
        if (current !is FeedUiState.Success) return

        viewModelScope.launch {
            try {
                // 1) 松手 → 启动刷新中动画（不重置 pullProgress）
                _state.value = current.copy(
                    isRefreshing = true
                )

                val latest = System.currentTimeMillis() / 1000

                // 2) 请求新数据
                val raw = refreshFeedUseCase(latest)

                val newRendered = renderCardTypeUseCase.execute(raw)
                val oldRendered = renderCardTypeUseCase.execute(current.mixedItems)

                val processed = processFeedItemsUseCase.execute(
                    newRendered + oldRendered
                )

                val newCount = raw.size

                // 3) 刷新完成 → 停止循环（isRefreshing=false）
                //    但不要立刻把 pullProgress 清零！
                _state.value = current.copy(
                    officialItems = processed.officialList,
                    mixedItems = processed.mixedList,
                    isRefreshing = false,
                    latestPublishTime = latest,
                    newCount = newCount
                )

                // 4) 给刷新头一个回弹时间（与头条一致）
                delay(300)

                // 5) 再归零进度
                val after = _state.value
                if (after is FeedUiState.Success) {
                    _state.value = after.copy(
                        pullProgress = 0f
                    )
                }

                // 6) 顶部 "xx 条更新" 显示 1 秒
                delay(1000)
                hideUpdateHint()

            } catch (e: Exception) {
                _state.value = FeedUiState.Error("刷新失败：${e.message}")
            }
        }
    }


    // 隐藏 “xx 条更新” 提示条
    private fun hideUpdateHint() {
        val current = _state.value
        if (current is FeedUiState.Success) {
            _state.value = current.copy(newCount = 0)
        }
    }

    // ======================
    // 4. 加载更多
    // ======================
    fun loadMore() {
        val current = state.value
        if (current !is FeedUiState.Success) return

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

                val newRendered = renderCardTypeUseCase.execute(raw)
                val oldRendered = renderCardTypeUseCase.execute(
                    current.officialItems + current.mixedItems
                )

                val processed = processFeedItemsUseCase.execute(
                    oldRendered + newRendered
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
