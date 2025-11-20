package com.xuziyi.toutiaoandroid.domain.usecase

import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import com.xuziyi.toutiaoandroid.domain.repository.FeedRepositoryContract

class RefreshFeedUseCase(
    private val repo: FeedRepositoryContract
) {
    /**
     * latestPublishTime：当前列表中最新的一条 feed 的发布时间
     */
    suspend operator fun invoke(latestPublishTime: Long): List<FeedItem> {
        return repo.refreshFeed(latestPublishTime)
    }
}
