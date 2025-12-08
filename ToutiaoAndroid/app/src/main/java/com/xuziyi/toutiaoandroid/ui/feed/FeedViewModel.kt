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


    //首次加载（进入首页时调用）
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

    //下拉手势中：实时更新下拉进度（用于动画）
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
                //松手：开始刷新 → 播放动画 + 吸顶
                _state.value = current.copy(
                    isRefreshing = true,
                    isHoldingRefreshHeader = true,
                    showRefreshAnimation = true,
                    showUpdateBanner = false,
                    newCount = 0
                )

                val latest = System.currentTimeMillis() / 1000

                // 最小动画播放时长
                val startTime = System.currentTimeMillis()
                val minDisplay = 1200L
                val maxTimeout = 5000L

                //拉取新数据
                val raw = withTimeout(maxTimeout) { refreshFeedUseCase(latest) }

                val rendered = renderCardTypeUseCase.execute(raw)
                val processed = processFeedItemsUseCase.execute(rendered)

                // 保证动画最少播放 1200ms
                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed < minDisplay) delay(minDisplay - elapsed)

                //数据准备好 → 停止动画但继续吸顶（非常关键）
                _state.value = (_state.value as FeedUiState.Success).copy(
                    officialItems = processed.officialList,
                    mixedItems = processed.mixedList,
                    isRefreshing = false,          // 停止动画
                    showRefreshAnimation = false,  // 动画不再绘制
                    isHoldingRefreshHeader = true, // 继续吸顶锁住高度
                    newCount = raw.size,
                    showUpdateBanner = true        // 显示“X 条更新”
                )

                //Banner 停留 1 秒
                delay(1000)

                //隐藏 Banner（此时仍保持吸顶，避免闪动）
                _state.value = (_state.value as FeedUiState.Success).copy(
                    showUpdateBanner = false
                )

                //等一帧再解除吸顶（解决 UI 抖动、空白条问题）
                delay(16)
                _state.value = (_state.value as FeedUiState.Success).copy(
                    isHoldingRefreshHeader = false
                )

                //回弹动画：拉回到顶部
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





    // 隐藏“xx 条更新”提示条
    private fun hideUpdateHint() {
        val current = _state.value as? FeedUiState.Success ?: return
        _state.value = current.copy(newCount = 0)
    }

    //触底加载更多
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
