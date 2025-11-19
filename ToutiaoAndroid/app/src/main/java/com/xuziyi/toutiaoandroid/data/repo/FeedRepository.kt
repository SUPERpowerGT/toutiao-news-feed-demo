package com.xuziyi.toutiaoandroid.data.repo

import com.xuziyi.toutiaoandroid.data.remote.RetrofitClient
import com.xuziyi.toutiaoandroid.domain.model.FeedItem

class FeedRepository {

    suspend fun loadFeedList(cursor: String? = null): List<FeedItem> {

        val resp = RetrofitClient.api.getFeed(cursor)

        return resp.data.items.map {
            FeedItem(
                id = it.id,
                news_id = it.news_id,
                display_type = it.display_type,
                weight = it.weight,
                scene = it.scene,
                model_id = it.model_id,
                publish_time = it.publish_time,
                seq_id = it.seq_id,
                created_at = it.created_at
            )
        }
    }
}

