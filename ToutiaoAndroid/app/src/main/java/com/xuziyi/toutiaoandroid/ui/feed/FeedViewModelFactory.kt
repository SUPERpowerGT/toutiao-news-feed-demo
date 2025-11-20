package com.xuziyi.toutiaoandroid.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xuziyi.toutiaoandroid.domain.usecase.LoadInitialFeedUseCase
import com.xuziyi.toutiaoandroid.domain.usecase.RefreshFeedUseCase
import com.xuziyi.toutiaoandroid.domain.usecase.LoadMoreFeedUseCase

class FeedViewModelFactory(
    private val loadInitial: LoadInitialFeedUseCase,
    private val refreshFeed: RefreshFeedUseCase,
    private val loadMore: LoadMoreFeedUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FeedViewModel(
                loadInitialFeedUseCase = loadInitial,
                refreshFeedUseCase = refreshFeed,
                loadMoreFeedUseCase = loadMore
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
