package com.xuziyi.toutiaoandroid.data.local.db

import androidx.room.*
import com.xuziyi.toutiaoandroid.data.local.entity.FeedItemEntity

@Dao
interface FeedItemDao {

    //查询所有 FeedItem，按发布时间排序（用于首页秒开）
    @Query("SELECT * FROM feed_items ORDER BY publishTime DESC")
    suspend fun getAllFeedItems(): List<FeedItemEntity>

    //插入或更新
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedItems(items: List<FeedItemEntity>)

    //清空（用于全量刷新策略）
    @Query("DELETE FROM feed_items")
    suspend fun clearAll()
}
