package com.xuziyi.toutiaoandroid.domain.usecase

import com.xuziyi.toutiaoandroid.domain.model.*
import com.xuziyi.toutiaoandroid.domain.usecase.model.ProcessedFeedResult

class ProcessFeedItemUseCase {

    fun execute(rendered: List<FeedItem>): ProcessedFeedResult {

        // 1. 挑出官方位（UI 渲染策略中定义为 OfficialTop）
        val officials = rendered
            .filter { it.isTopOfficial }
            .take(5)


        // 2. 不足 5 条则 mock 补齐
        val filledOfficials = if (officials.size < 5) {
            officials + mockOfficialItems().take(5 - officials.size)
        } else officials

        // 3. 其余进入普通混合流
        val mixed = rendered.filterNot { it in filledOfficials }

        // 4. 官方视频卡标题增强
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
                stats = FeedStatsItem(0, 0, 0, 0),
                publishTime = System.currentTimeMillis(),

                category = "official",
                subCategory = null,
                tags = null,
                city = null,

                isOfficialMedia = true,
                isTopOfficial = true,
                source = "新华社",

                contentType = FeedContentType(FeedContentType.TEXT),
                weight = 100
            )
        }
    }
}
