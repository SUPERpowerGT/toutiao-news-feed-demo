package com.xuziyi.toutiaoandroid.domain.usecase

import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import com.xuziyi.toutiaoandroid.domain.repository.FeedRepositoryContract

class LoadMoreFeedUseCase(
    private val repo: FeedRepositoryContract
) {
    /**
     * cursor：上一次列表最后一条的 seq_id
     */
    suspend operator fun invoke(cursor: String): List<FeedItem> {
        return repo.loadMore(cursor)
    }
}
