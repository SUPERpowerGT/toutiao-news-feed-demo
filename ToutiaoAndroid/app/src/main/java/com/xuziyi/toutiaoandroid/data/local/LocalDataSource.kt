package com.xuziyi.toutiaoandroid.data.local

import com.xuziyi.toutiaoandroid.data.local.db.FeedItemDao
import com.xuziyi.toutiaoandroid.data.local.db.AuthorDao
import com.xuziyi.toutiaoandroid.data.local.db.StatsDao
import com.xuziyi.toutiaoandroid.data.local.entity.AuthorEntity
import com.xuziyi.toutiaoandroid.data.local.entity.StatsEntity
import com.xuziyi.toutiaoandroid.data.local.mapper.FeedLocalMapper
import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import com.xuziyi.toutiaoandroid.domain.model.FeedData

class LocalDataSource(
    private val feedItemDao: FeedItemDao,
    private val authorDao: AuthorDao,
    private val statsDao: StatsDao
) {

    //1. 首页秒开：读取所有本地 FeedItem
    suspend fun getAllFeedItems(): List<FeedItem> {
        val itemEntities = feedItemDao.getAllFeedItems()

        return itemEntities.map { item ->
            val author = authorDao.getAuthorById(item.authorId)
            val stats = statsDao.getStatsById(item.statsId)
            FeedLocalMapper.toDomain(item, author, stats)
        }
    }

    suspend fun getFeed(scene: String): FeedData {
        val filtered = getAllFeedItems().filter { item ->
            when (scene) {
                "following" -> item.category == "关注"
                "hot" -> item.category == "热榜"
                "video" -> item.contentType.value == "video"
                "shenzhen" -> item.city == "深圳"
                "featured" -> item.category == "精选"
                "image" -> item.contentType.value == "image"
                "war" -> item.category == "抗战"
                "sports" -> item.category == "体育"
                "finance" -> item.category == "财经"
                "tech" -> item.category == "科技"
                else -> true
            }
        }
        val topItems = if (scene == "recommend") filtered.filter { it.isTopOfficial }.take(5) else emptyList()
        val normalItems = filtered.filterNot { it.id in topItems.map(FeedItem::id).toSet() }
        return FeedData(
            scene = scene,
            topItems = topItems,
            items = normalItems,
            nextCursor = null,
            hasMore = false,
            latestPublishTime = filtered.maxOfOrNull(FeedItem::publishTime)
        )
    }

    //2. 写入（网络成功后同步本地缓存）
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

        //正确：一次性转换成 FeedItemEntity 列表
        val feedEntities = items.map { feed ->
            FeedLocalMapper.fromDomain(
                item = feed,
                authorId = feed.author.id,
                statsId = feed.id
            )
        }

        //正确：一次性写入，不要在 forEach 里重复写
        feedItemDao.insertFeedItems(feedEntities)
    }


    //3. 清空缓存（用于全量刷新）
    suspend fun clearAll() {
        feedItemDao.clearAll()
        authorDao.clearAll()
        statsDao.clearAll()
    }
}
