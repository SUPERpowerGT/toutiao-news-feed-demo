package com.xuziyi.toutiaoandroid.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xuziyi.toutiaoandroid.data.local.entity.AuthorEntity
import com.xuziyi.toutiaoandroid.data.local.entity.FeedItemEntity
import com.xuziyi.toutiaoandroid.data.local.entity.StatsEntity

@Database(
    entities = [
        FeedItemEntity::class,
        AuthorEntity::class,
        StatsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun feedItemDao(): FeedItemDao
    abstract fun authorDao(): AuthorDao
    abstract fun statsDao(): StatsDao
}
