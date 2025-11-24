package com.xuziyi.toutiaoandroid.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuziyi.toutiaoandroid.domain.usecase.LoadInitialFeedUseCase
import com.xuziyi.toutiaoandroid.domain.usecase.RefreshFeedUseCase
import com.xuziyi.toutiaoandroid.domain.usecase.LoadMoreFeedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FeedViewModel(
    private val loadInitialFeedUseCase: LoadInitialFeedUseCase,
    private val refreshFeedUseCase: RefreshFeedUseCase,
    private val loadMoreFeedUseCase: LoadMoreFeedUseCase
) : ViewModel() {

    //_state是内部可以修改的
    //state是暴露给UI使用的订阅功能
    private val _state = MutableStateFlow(FeedUiState())
    val state: StateFlow<FeedUiState> = _state

    init {
        loadInitial()
    }

    // ===================
    // 首次加载
    // ===================
    private fun loadInitial() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            // 现在 result 是 List<FeedItem>
            val result = loadInitialFeedUseCase()

            _state.value = _state.value.copy(
                isLoading = false,
                items = result,
                // 目前先不做 cursor / refreshTime，保持为 null
                nextCursor = null,
                latestPublishTime = null
            )
        }
    }

    // ===================
    // 下拉刷新
    // ===================
    fun refresh() {
        // 先简单用当前时间刷新，或者直接 return（后面再接上真正的 latestPublishTime）
        val latest = System.currentTimeMillis() / 1000

        viewModelScope.launch {
            val result = refreshFeedUseCase(latest)

            _state.value = _state.value.copy(
                // 新内容加在前面
                items = result + _state.value.items,
                latestPublishTime = latest
            )
        }
    }

    // ===================
    // 加载更多
    // ===================
    fun loadMore() {
        // 现在还没真正 cursor，就先用一个假值或者直接 return
        val cursor = _state.value.nextCursor ?: "0"

        viewModelScope.launch {
            val result = loadMoreFeedUseCase(cursor)

            _state.value = _state.value.copy(
                // 旧内容在前，新内容接在后面
                items = _state.value.items + result,
                nextCursor = null   // 先不做真正分页
            )
        }
    }
}
