package com.xuziyi.toutiaoandroid.domain.repository

import com.xuziyi.toutiaoandroid.domain.model.FeedItem

/**
 * Feed 仓库接口（领域层）
 * 负责定义：推荐流相关的所有数据获取方法
 *
 * 现在我们只实现 3 个最核心功能：
 * - 首次加载
 * - 下拉刷新
 * - 加载更多
 */
interface FeedRepositoryContract {

    suspend fun loadInitialFeed(): List<FeedItem>

    suspend fun refreshFeed(latestPublishTime: Long): List<FeedItem>

    suspend fun loadMore(cursor: String): List<FeedItem>
}
