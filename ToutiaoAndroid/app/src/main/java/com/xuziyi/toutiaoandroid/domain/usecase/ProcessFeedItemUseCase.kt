package com.xuziyi.toutiaoandroid.domain.usecase

import com.xuziyi.toutiaoandroid.domain.model.*

class ProcessFeedItemsUseCase {

    fun execute(raw: List<FeedItem>): ProcessedFeedResult {

        // 1. 挑出官方位（由 cardType 决定）
        val officials = raw.filter { it.cardType is FeedCardType.OfficialTop }
            .take(5)

        // 2. 不足 5 条则 mock 补齐
        val filledOfficials = if (officials.size < 5) {
            officials + mockOfficialItems().take(5 - officials.size)
        } else officials

        // 3. 剩余的进入普通混合流
        val mixed = raw.filterNot { it in filledOfficials }

        // 4. 对官方条目进行标题修饰（视频加 “视频｜”）
        val finalOfficials = filledOfficials.map { item ->
            if (item.cardType is FeedCardType.Video) {
                item.copy(title = "视频｜${item.title}")
            } else item
        }

        return ProcessedFeedResult(
            officialList = finalOfficials,
            mixedList = mixed
        )
    }

    // 官方 mock
    private fun mockOfficialItems(): List<FeedItem> {
        return List(5) { index ->
            FeedItem(
                id = 10000L + index,
                title = "【权威发布】今日最新重要时政要闻 $index",
                summary = null,
                cardType = FeedCardType.OfficialTop,
                media = emptyList(),
                author = FeedAuthorItem(
                    id = 999,
                    name = "新华社",
                    avatarUrl = null,
                    certification = "official"
                ),
                stats = FeedStatsItem(
                    likeCount = 0,
                    commentCount = 0,
                    favoriteCount = 0,
                    shareCount = 0
                ),
                publishTime = System.currentTimeMillis()
            )
        }
    }
}

data class ProcessedFeedResult(
    val officialList: List<FeedItem>,
    val mixedList: List<FeedItem>
)
