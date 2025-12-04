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

        // ⭐ 优先用后端返回的 nextCursor；如果还没初始化，就退回到最老的 publishTime
        val cursor: Long = current.nextCursor
            ?: current.mixedItems.minOfOrNull { it.publishTime }
            ?: return

        viewModelScope.launch {
            try {
                _state.value = current.copy(isLoadingMore = true)

                // ⭐ loadMoreFeedUseCase 现在返回的是 FeedData
                val feedData = loadMoreFeedUseCase(cursor)

                // 后端确认：如果没有更多数据
                if (feedData.items.isEmpty()) {
                    _state.value = current.copy(
                        isLoadingMore = false,
                        hasMore = false,
                        nextCursor = null
                    )
                    return@launch
                }

                // ⭐ 新旧数据合并 + 卡片类型渲染
                val newRendered = renderCardTypeUseCase.execute(feedData.items)
                val oldRendered = renderCardTypeUseCase.execute(
                    current.officialItems + current.mixedItems
                )

                val processed = processFeedItemsUseCase.execute(
                    oldRendered + newRendered
                )

                // ⭐ 更新 UI 状态（包括 nextCursor / hasMore / latestPublishTime）
                _state.value = current.copy(
                    officialItems = processed.officialList,
                    mixedItems = processed.mixedList,
                    isLoadingMore = false,

                    hasMore = feedData.hasMore,
                    nextCursor = feedData.nextCursor,
                    latestPublishTime = processed.mixedList.minOfOrNull { it.publishTime }
                )

            } catch (e: Exception) {
                // 不清空列表，只停止 loading
                _state.value = current.copy(isLoadingMore = false)
            }
        }
    }


}
