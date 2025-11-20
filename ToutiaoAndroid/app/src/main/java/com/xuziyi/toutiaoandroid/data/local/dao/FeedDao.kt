package com.xuziyi.toutiaoandroid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xuziyi.toutiaoandroid.data.local.entity.FeedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {

    @Query("SELECT * FROM feed ORDER BY publishTime DESC")
    fun observeFeed(): Flow<List<FeedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FeedEntity>)

    @Query("DELETE FROM feed")
    suspend fun clearAll()
}
