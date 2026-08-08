package com.xuziyi.toutiaoandroid.di

import android.content.Context
import androidx.room.Room
import com.xuziyi.toutiaoandroid.data.datasource.RemoteDataSource
import com.xuziyi.toutiaoandroid.data.local.LocalDataSource
import com.xuziyi.toutiaoandroid.data.local.db.AppDatabase
import com.xuziyi.toutiaoandroid.data.remote.RetrofitClient
import com.xuziyi.toutiaoandroid.data.repository.FeedRepository

object DatabaseModule {

    fun provideDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "feed_cache.db"
        ).fallbackToDestructiveMigration().build()

    fun provideLocalDataSource(db: AppDatabase): LocalDataSource =
        LocalDataSource(
            feedItemDao = db.feedItemDao(),
            authorDao = db.authorDao(),
            statsDao = db.statsDao()
        )

    fun provideRemoteDataSource(): RemoteDataSource =
        RemoteDataSource(api = RetrofitClient.feedApi)

    fun provideFeedRepository(context: Context): FeedRepository {
        val db = provideDatabase(context)
        val local = provideLocalDataSource(db)
        val remote = provideRemoteDataSource()
        return FeedRepository(remote, local)
    }
}
