package com.xuziyi.toutiaoandroid.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xuziyi.toutiaoandroid.data.local.entity.StatsEntity

@Dao
interface StatsDao {

    @Query("SELECT * FROM stats WHERE id = :id LIMIT 1")
    suspend fun getStatsById(id: Long): StatsEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: StatsEntity)

    @Query("DELETE FROM stats")
    suspend fun clearAll()
}
