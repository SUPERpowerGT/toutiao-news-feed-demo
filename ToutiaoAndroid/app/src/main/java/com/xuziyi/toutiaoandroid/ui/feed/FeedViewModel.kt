package com.xuziyi.toutiaoandroid.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuziyi.toutiaoandroid.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class FeedViewModel(
    private val loadInitialFeedUseCase: LoadInitialFeedUseCase,
    private val refreshFeedUseCase: RefreshFeedUseCase,
    private val loadMoreFeedUseCase: LoadMoreFeedUseCase,
    private val renderCardTypeUseCase: RenderCardTypeUseCase = RenderCardTypeUseCase(),
    private val processFeedItemsUseCase: ProcessFeedItemUseCase = ProcessFeedItemUseCase()
) : ViewModel() {

    //是 ViewModel 里用来保存和更新 UI 状态的“可观察变量”，
    //它既有当前值，又能在变化时自动通知 UI
    private val _state = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val state: StateFlow<FeedUiState> = _state
    // 语义代（generation）
    // 每次 refresh 发生，都会 +1
    private var requestVersion = 0

    init {
        loadInitial()
    }

    //把 state 设为 Loading → 拉 raw 数据 → 计算卡片类型 → 拆成官方区 + 混排区 → 成功态塞进去
    private fun loadInitial() {
        viewModelScope.launch {
            try {
                _state.value = FeedUiState.Loading

                val raw = loadInitialFeedUseCase()
                //val rendered = renderCardTypeUseCase.execute(raw)
                //val processed = processFeedItemsUseCase.execute(rendered)

                // [PERF-THREAD] Feed 渲染计算从 Main 下沉到 Default dispatcher
                // [PERF-TIME] 可统计渲染 + 分组耗时，验证不阻塞主线程
                val startCompute = System.currentTimeMillis()

                //切换到CPU线程池来运行
                val processed = withContext(Dispatchers.Default) {
                    val rendered = renderCardTypeUseCase.execute(raw)
                    processFeedItemsUseCase.execute(rendered)
                }

                val computeCost = System.currentTimeMillis() - startCompute
                // [PERF-TIME] Feed 初始渲染计算耗时（ms）
                android.util.Log.d("FeedPerf", "loadInitial compute cost = $computeCost ms")

                _state.value = FeedUiState.Success(
                    officialItems = processed.officialList,
                    mixedItems = processed.mixedList
                )

            } catch (e: Exception) {
                _state.value = FeedUiState.Error("加载失败：${e.message}")
            }
        }
    }

    //VM 接收“下拉进度”，更新 Success 态字段
    fun updatePullProgress(progress: Float) {
        //as?判断类型，==是判断值的
        val current = _state.value as? FeedUiState.Success ?: return

        _state.value = current.copy(
            pullProgress = progress.coerceIn(0f, 1f)
        )
    }


    fun refresh() {
        val current = state.value as? FeedUiState.Success ?: return
        //refresh 发生，整体语义前进
        requestVersion++
        val version = requestVersion

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

                //这里需要优化，不要再main线程做计算处理，我们选择把这两个分配给default来计算，用线程池（协程方式）
                //val rendered = renderCardTypeUseCase.execute(raw)
                //val processed = processFeedItemsUseCase.execute(rendered)

                // [PERF-THREAD] refresh 阶段 CPU 计算下沉至 Default
                val startCompute = System.currentTimeMillis()

                //别在主线程算，交给 CPU 线程池去算
                val processed = withContext(Dispatchers.Default) {
                    val rendered = renderCardTypeUseCase.execute(raw)
                    processFeedItemsUseCase.execute(rendered)
                }

                val computeCost = System.currentTimeMillis() - startCompute
                android.util.Log.d("FeedPerf", "refresh compute cost = $computeCost ms")

                // [PERF-GUARD] 并发保护：
                // refresh 发生后，丢弃旧语义结果，避免无效计算回写 UI
                if (version != requestVersion) {
                    android.util.Log.d("FeedPerf", "refresh result dropped (stale version)")
                    return@launch
                }

                //保证刷新动画至少播放1.2s保证刷新体验
                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed < minDisplay) delay(minDisplay - elapsed)

                //刷新成功更新状态（一定要做）
                _state.value = (_state.value as FeedUiState.Success).copy(
                    officialItems = processed.officialList,
                    mixedItems = processed.mixedList,
                    isRefreshing = false,
                    showRefreshAnimation = false,
                    isHoldingRefreshHeader = true,
                    newCount = raw.size,
                    showUpdateBanner = true
                )

                //banner显示1秒自动消失
                delay(1000)
                _state.value = (_state.value as FeedUiState.Success).copy(
                    showUpdateBanner = false
                )

                //1帧是16ms（60fps）后续可能根据不同设备，不同刷新帧率要调整
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

            } catch (_: Exception) {
                //UI兜底，后续做异常处理,因为这个异常不需要进入 UI 语义，只要把刷新流程收尾、避免界面卡死即可
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

//    private fun hideUpdateHint() {
//        val current = _state.value as? FeedUiState.Success ?: return
//        _state.value = current.copy(newCount = 0)
//    }

    //触底加载更多 —— 新增分页错误处理
    fun loadMore() {
        val current = state.value as? FeedUiState.Success ?: return

        //绑定当前语义代
        val version = requestVersion

        if (current.isLoadingMore || !current.hasMore) return

        //兜底策略，优先使用后端的cursor，否则使用publishTime
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
                    //返回到launch这一层
                    return@launch
                }

                //这里也要修改不在主线程运行
                //去重
                //val newRendered = renderCardTypeUseCase.execute(feedData.items)
                //排序
                //val existingRendered =
                //   renderCardTypeUseCase.execute(afterLoading.officialItems + afterLoading.mixedItems)
                //分组
                //val processed = processFeedItemsUseCase.execute(existingRendered + newRendered)

                // [PERF-THREAD] loadMore 场景数据量递增，计算更重，必须下沉 Default
                val startCompute = System.currentTimeMillis()

                val processed = withContext(Dispatchers.Default){
                    //新数据
                    val newRendered =
                        renderCardTypeUseCase.execute(feedData.items)

                    //已有数据
                    val existingRendered =
                        renderCardTypeUseCase.execute(
                            afterLoading.officialItems + afterLoading.mixedItems
                        )

                    processFeedItemsUseCase.execute(
                        existingRendered + newRendered
                    )
                }

                val computeCost = System.currentTimeMillis() - startCompute
                android.util.Log.d("FeedPerf", "loadMore compute cost = $computeCost ms")

                // [PERF-GUARD] refresh 期间 loadMore 结果直接丢弃
                // 避免：
                // 1. UI 无效刷新
                // 2. 重复列表合并
                // 3. Recycler/Compose 重组抖动
                if (version != requestVersion) {
                    android.util.Log.d("FeedPerf", "loadMore result dropped (stale version)")
                    return@launch
                }


                _state.value = afterLoading.copy(
                    isLoadingMore = false,
                    officialItems = processed.officialList,
                    mixedItems = processed.mixedList,
                    hasMore = feedData.hasMore,
                    nextCursor = feedData.nextCursor
                )

            } catch (e: Exception) {

                //不要在default直接读写_state StateFlow 的读写，语义上应该留在 Main
                val afterLoading = _state.value as FeedUiState.Success

                _state.value = afterLoading.copy(
                    isLoadingMore = false,
                    loadMoreError = true,                              //告诉 UI 失败了
                    loadMoreErrorMessage = e.message ?: "加载更多失败"     //显示错误信息
                )
            }
        }
    }
}
