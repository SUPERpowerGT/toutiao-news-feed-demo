package com.xuziyi.toutiaoandroid.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.xuziyi.toutiaoandroid.data.remote.api.FeedApiService
import com.xuziyi.toutiaoandroid.data.remote.dto.NewsDetailDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface NewsDetailUiState {
    data object Loading : NewsDetailUiState
    data class Success(val detail: NewsDetailDto) : NewsDetailUiState
    data class Error(val message: String) : NewsDetailUiState
}

class NewsDetailViewModel(
    private val newsId: Long,
    private val api: FeedApiService
) : ViewModel() {
    private val _state = MutableStateFlow<NewsDetailUiState>(NewsDetailUiState.Loading)
    val state: StateFlow<NewsDetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = NewsDetailUiState.Loading
            _state.value = try {
                NewsDetailUiState.Success(api.getNewsDetail(newsId))
            } catch (error: Exception) {
                NewsDetailUiState.Error(error.message ?: "详情加载失败")
            }
        }
    }
}

class NewsDetailViewModelFactory(
    private val newsId: Long,
    private val api: FeedApiService
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsDetailViewModel(newsId, api) as T
    }
}
