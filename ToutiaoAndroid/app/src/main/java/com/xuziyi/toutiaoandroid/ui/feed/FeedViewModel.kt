package com.xuziyi.toutiaoandroid.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuziyi.toutiaoandroid.domain.usecase.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * 首页推荐流 ViewModel
 *
 * 负责：
 *  - 首次加载
 *  - 下拉刷新
 *  - 上拉加载更多
 *  - 列表业务加工（卡片类型 / 混排）
 *
 * 状态通过 MutableStateFlow 暴露给 UI，Compose 会自动重组。
 */
class FeedViewModel(
    private val loadInitialFeedUseCase: LoadInitialFeedUseCase,
    private val refreshFeedUseCase: RefreshFeedUseCase,
    private val loadMoreFeedUseCase: LoadMoreFeedUseCase,

    // 卡片类型渲染、业务列表加工（头条 Top5 + Normal15）
    private val renderCardTypeUseCase: RenderCardTypeUseCase = RenderCardTypeUseCase(),
    private val processFeedItemsUseCase: ProcessFeedItemUseCase = ProcessFeedItemUseCase()
) : ViewModel() {

    // UI 状态流（Compose 订阅后会自动刷新 UI）
    private val _state = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val state: StateFlow<FeedUiState> = _state

    init {
        loadInitial()
    }


    // 1. 首次加载（进入首页时调用）
    private fun loadInitial() {
        viewModelScope.launch {
            try {
                _state.value = FeedUiState.Loading

                // 拉取数据 → 渲染卡片类型 → 业务混排（Top5 + Normal15）
                val raw = loadInitialFeedUseCase()
                val rendered = renderCardTypeUseCase.execute(raw)
                val processed = processFeedItemsUseCase.execute(rendered)

                // 完成首次加载
                _state.value = FeedUiState.Success(
                    officialItems = processed.officialList,
                    mixedItems = processed.mixedList
                )

            } catch (e: Exception) {
                _state.value = FeedUiState.Error("加载失败：${e.message}")
            }
        }
    }

    // 2. 下拉手势中：实时更新下拉进度（用于动画）
    fun updatePullProgress(progress: Float) {
        val current = state.value as? FeedUiState.Success ?: return

        _state.value = current.copy(
            pullProgress = progress.coerceIn(0f, 1f)
        )
    }

    // 3. 下拉松手 → 触发刷新
    fun refresh() {
        val current = state.value as? FeedUiState.Success ?: return

        viewModelScope.launch {
            try {
                // ⭐ 切换为刷新状态（显示吸顶动画）
                _state.value = current.copy(
                    isRefreshing = true,
                    isHoldingRefreshHeader = true
                )

                val latest = System.currentTimeMillis() / 1000

                // ⭐ 记录开始时间，用于最小展示时长判断
                val startTime = System.currentTimeMillis()
                val minDisplay = 700L     // 今日头条体验：600–800ms
                val maxTimeout = 5000L    // 最大等待时间，防止卡死

                // ⭐ 刷新 API（带最大超时保护）
                val raw = withTimeout(maxTimeout) {
                    refreshFeedUseCase(latest)
                }

                // 渲染业务数据
                val newRendered = renderCardTypeUseCase.execute(raw)
                val processed = processFeedItemsUseCase.execute(newRendered)

                // ⭐ 保证刷新动画至少展示 minDisplay
                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed < minDisplay) {
                    delay(minDisplay - elapsed)
                }

                // ⭐ 刷新成功：关闭 holding，准备回弹
                _state.value = current.copy(
                    officialItems = processed.officialList,
                    mixedItems = processed.mixedList,
                    isRefreshing = false,
                    isHoldingRefreshHeader = false,
                    latestPublishTime = latest,
                    newCount = raw.size
                )

                // 下拉进度归 0 → PullRefresh 会自动回弹
                delay(300)
                _state.value = (_state.value as FeedUiState.Success).copy(
                    pullProgress = 0f
                )

                // “xx 条更新”停留 1 秒
                delay(1000)
                hideUpdateHint()

            } catch (e: Exception) {
                // 异常也必须释放刷新状态，否则会卡吸顶
                val fallback = state.value as? FeedUiState.Success ?: current
                _state.value = fallback.copy(
                    isRefreshing = false,
                    isHoldingRefreshHeader = false
                )
                _state.value = FeedUiState.Error("刷新失败：${e.message}")
            }
        }
    }



    // 隐藏“xx 条更新”提示条
    private fun hideUpdateHint() {
        val current = _state.value as? FeedUiState.Success ?: return
        _state.value = current.copy(newCount = 0)
    }

    // 4. 触底加载更多
    fun loadMore() {
        val current = state.value as? FeedUiState.Success ?: return

        // 防止重复触发
        if (current.isLoadingMore || !current.hasMore) return

        // 使用 nextCursor，否则使用最小时间戳兜底
        val cursor = current.nextCursor ?: current.mixedItems.minOf { it.publishTime }

        viewModelScope.launch {

            // 立即更新 UI 状态：显示 LoadingMore
            _state.value = current.copy(isLoadingMore = true)

            val feedData = loadMoreFeedUseCase(cursor)

            // 状态可能已经更新，因此重新取一次 Success
            val afterLoading = _state.value as FeedUiState.Success

            // 没更多数据
            if (feedData.items.isEmpty()) {
                _state.value = afterLoading.copy(
                    isLoadingMore = false,
                    hasMore = false
                )
                return@launch
            }

            // 渲染新卡片 & 合并旧列表
            val newRendered = renderCardTypeUseCase.execute(feedData.items)
            val existingRendered =
                renderCardTypeUseCase.execute(afterLoading.officialItems + afterLoading.mixedItems)

            val processed = processFeedItemsUseCase.execute(existingRendered + newRendered)

            // 使用 afterLoading 避免覆盖 isLoadingMore
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
