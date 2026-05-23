package com.xuziyi.toutiaoandroid.domain.usecase

import com.xuziyi.toutiaoandroid.domain.model.FeedData
import com.xuziyi.toutiaoandroid.domain.repository.FeedRepositoryContract

class LoadMoreFeedUseCase(
    private val repo: FeedRepositoryContract
) {
    suspend operator fun invoke(scene: String, cursor: Long): FeedData {
        return repo.loadMore(scene, cursor)   //返回 FeedData（items + nextCursor + hasMore）
    }
}
