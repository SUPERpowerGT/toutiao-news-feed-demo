package com.xuziyi.toutiaoandroid.domain.usecase

import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import com.xuziyi.toutiaoandroid.domain.repository.FeedRepositoryContract

class LoadInitialFeedUseCase(
    private val repo: FeedRepositoryContract
) {
    suspend operator fun invoke(): List<FeedItem> {
        return repo.loadInitialFeed()
    }
}
