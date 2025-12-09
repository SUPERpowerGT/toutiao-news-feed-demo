package com.xuziyi.toutiaoandroid.fakes

import com.xuziyi.toutiaoandroid.domain.model.*

/**
 * Factory 用于快速构建 FeedItem 测试数据
 * 避免测试里手写一堆复杂参数
 */
object FakeModelFactory {

    fun fakeFeedItem(
        id: Long = 1L,
        title: String = "Title $id",
        summary: String = "Summary $id",
        publishTime: Long = id * 1000L,
        cardType: FeedCardType = FeedCardType.Text,
        contentType: FeedContentType = FeedContentType(FeedContentType.TEXT),
        isOfficial: Boolean = false,
        isTop: Boolean = false
    ): FeedItem {

        return FeedItem(
            id = id,
            title = title,
            summary = summary,
            cardType = cardType,

            media = listOf(fakeMediaItem()),
            author = fakeAuthor(),
            stats = fakeStats(),

            publishTime = publishTime,

            category = "news",
            subCategory = "general",
            tags = listOf("TAG"),
            city = "shenzhen",

            isOfficialMedia = isOfficial,
            isTopOfficial = isTop,
            source = "新华社",

            contentType = contentType,

            weight = 0.5f
        )
    }

    fun fakeMediaItem() = FeedMediaItem(
        mediaType = "image",
        url = "https://example.com/img.jpg",
        coverUrl = null,
        duration = null,
        width = 800,
        height = 600
    )

    fun fakeAuthor() = FeedAuthorItem(
        id = 99,
        name = "Fake Author",
        avatarUrl = null,
        certification = null
    )

    fun fakeStats() = FeedStatsItem(
        likeCount = 10,
        commentCount = 3,
        favoriteCount = 1,
        shareCount = 0
    )
}
