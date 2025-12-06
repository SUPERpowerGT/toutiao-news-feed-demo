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
                // 1) 进入刷新状态
                _state.value = current.copy(
                    isRefreshing = true
                )

                val latest = System.currentTimeMillis() / 1000

                // 2) 请求新数据（根据 latest 刷新）
                val raw = refreshFeedUseCase(latest)

                // 3) 渲染新数据（不再拼接 oldRendered）
                val newRendered = renderCardTypeUseCase.execute(raw)

                // 4) 业务加工（Top5 + Normal15 混排）
                val processed = processFeedItemsUseCase.execute(newRendered)

                val newCount = raw.size

                // 5) 刷新完成 → 只更新“新数据”
                _state.value = current.copy(
                    officialItems = processed.officialList,
                    mixedItems = processed.mixedList,   // ⭐ 只保留新的列表
                    isRefreshing = false,
                    latestPublishTime = latest,
                    newCount = newCount
                )

                // 6) 回弹动画
                delay(300)

                // 7) 清掉下拉进度
                val after = _state.value
                if (after is FeedUiState.Success) {
                    _state.value = after.copy(
                        pullProgress = 0f
                    )
                }

                // 8) “xx 条更新”提示停留 1 秒
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
        if (current.isLoadingMore || !current.hasMore) return

        val cursor = current.nextCursor ?: current.mixedItems.minOf { it.publishTime }

        viewModelScope.launch {

            // ① 必须立即更新 UI
            _state.value = current.copy(isLoadingMore = true)

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
            val existingRendered = renderCardTypeUseCase.execute(afterLoading.officialItems + afterLoading.mixedItems)

            val processed = processFeedItemsUseCase.execute(existingRendered + newRendered)

            // ② 注意这里用 afterLoading，而不是 old current，否则会覆盖 isLoadingMore
            _state.value = afterLoading.copy(
                isLoadingMore = false,
                officialItems = processed.officialList,
                mixedItems = processed.mixedList,
                hasMore = feedData.hasMore,
                nextCursor = feedData.nextCursor
            )
        }
    }


}
