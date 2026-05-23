package com.xuziyi.toutiaoandroid.domain.usecase

import com.xuziyi.toutiaoandroid.domain.model.FeedData
import com.xuziyi.toutiaoandroid.domain.repository.FeedRepositoryContract

class RefreshFeedUseCase(
    private val repo: FeedRepositoryContract
) {
    /**
     * latestPublishTime：当前列表中最新的一条 feed 的发布时间
     */
    suspend operator fun invoke(scene: String, latestPublishTime: Long): FeedData {
        return repo.refreshFeed(scene, latestPublishTime)
    }
}
