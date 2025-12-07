package com.xuziyi.toutiaoandroid.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xuziyi.toutiaoandroid.domain.usecase.LoadInitialFeedUseCase
import com.xuziyi.toutiaoandroid.domain.usecase.RefreshFeedUseCase
import com.xuziyi.toutiaoandroid.domain.usecase.LoadMoreFeedUseCase

/**
 * FeedViewModelFactory
 *
 * ViewModel 的构造函数带参数（UseCase），因此不能直接由系统实例化，
 * 必须通过自定义 Factory 来手动创建。
 *
 * 为什么要用 Factory？
 *  - 用于向 ViewModel 注入依赖（UseCase / Repository 等）
 *  - 避免 ViewModel 在 Compose 中重建时丢失依赖
 *  - 保证 AppNavigator 创建的 FeedViewModel 生命周期稳定
 */
class FeedViewModelFactory(
    private val loadInitial: LoadInitialFeedUseCase,
    private val refreshFeed: RefreshFeedUseCase,
    private val loadMore: LoadMoreFeedUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // 判断要创建的 ViewModel 是否为 FeedViewModel
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {

            // 手动构造并注入 UseCase
            return FeedViewModel(
                loadInitialFeedUseCase = loadInitial,
                refreshFeedUseCase = refreshFeed,
                loadMoreFeedUseCase = loadMore
            ) as T
        }

        // 如果类型不匹配 → 抛异常
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
