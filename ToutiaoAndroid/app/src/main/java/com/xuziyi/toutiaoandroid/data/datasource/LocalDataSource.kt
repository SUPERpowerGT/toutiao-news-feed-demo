package com.xuziyi.toutiaoandroid.data.datasource
import com.xuziyi.toutiaoandroid.domain.model.FeedItem


class LocalDataSource {

    private var cachedFeed: List<FeedItem> = emptyList()

    fun saveFeed(data: List<FeedItem>) {
        cachedFeed = data
    }

    fun loadFeed(): List<FeedItem> = cachedFeed
}

