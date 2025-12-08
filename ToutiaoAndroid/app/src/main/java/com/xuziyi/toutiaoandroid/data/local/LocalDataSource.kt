package com.xuziyi.toutiaoandroid.data.local

import com.xuziyi.toutiaoandroid.data.local.db.FeedItemDao
import com.xuziyi.toutiaoandroid.data.local.db.AuthorDao
import com.xuziyi.toutiaoandroid.data.local.db.StatsDao
import com.xuziyi.toutiaoandroid.data.local.entity.AuthorEntity
import com.xuziyi.toutiaoandroid.data.local.entity.StatsEntity
import com.xuziyi.toutiaoandroid.data.local.mapper.FeedLocalMapper
import com.xuziyi.toutiaoandroid.domain.model.FeedItem

class LocalDataSource(
    private val feedItemDao: FeedItemDao,
    private val authorDao: AuthorDao,
    private val statsDao: StatsDao
) {

    // =========================================
    // ⭐ 1. 首页秒开：读取所有本地 FeedItem
    // =========================================
    suspend fun getAllFeedItems(): List<FeedItem> {
        val itemEntities = feedItemDao.getAllFeedItems()

        return itemEntities.map { item ->
            val author = authorDao.getAuthorById(item.authorId)
            val stats = statsDao.getStatsById(item.statsId)
            FeedLocalMapper.toDomain(item, author, stats)
        }
    }

    // =========================================
    // ⭐ 2. 写入（网络成功后同步本地缓存）
    // =========================================
    suspend fun saveFeedItems(items: List<FeedItem>) {

        // 1. 写入 authors / stats（拆表结构）
        items.forEach { feed ->
            authorDao.insertAuthor(
                AuthorEntity(
                    id = feed.author.id,
                    name = feed.author.name,
                    avatarUrl = feed.author.avatarUrl,
                    certification = feed.author.certification
                )
            )

            statsDao.insertStats(
                StatsEntity(
                    id = feed.id,   // 统计用 feedId
                    likeCount = feed.stats.likeCount,
                    commentCount = feed.stats.commentCount,
                    favoriteCount = feed.stats.favoriteCount ?: 0,
                    shareCount = feed.stats.shareCount ?: 0
                )
            )
        }

        // ⭐ 正确：一次性转换成 FeedItemEntity 列表
        val feedEntities = items.map { feed ->
            FeedLocalMapper.fromDomain(
                item = feed,
                authorId = feed.author.id,
                statsId = feed.id
            )
        }

        // ⭐ 正确：一次性写入，不要在 forEach 里重复写
        feedItemDao.insertFeedItems(feedEntities)
    }


    // =========================================
    // ⭐ 3. 清空缓存（用于全量刷新）
    // =========================================
    suspend fun clearAll() {
        feedItemDao.clearAll()
        authorDao.clearAll()
        statsDao.clearAll()
    }
}
