package com.xuziyi.toutiaoandroid.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xuziyi.toutiaoandroid.data.local.entity.*

@Database(
    entities = [
        FeedItemEntity::class,
        AuthorEntity::class,
        StatsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FeedDatabase : RoomDatabase() {

    abstract fun feedItemDao(): FeedItemDao
    abstract fun authorDao(): AuthorDao
    abstract fun statsDao(): StatsDao
}
