package com.xuziyi.toutiaoandroid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xuziyi.toutiaoandroid.data.local.dao.FeedDao
import com.xuziyi.toutiaoandroid.data.local.entity.FeedEntity

@Database(
    entities = [FeedEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao
}
