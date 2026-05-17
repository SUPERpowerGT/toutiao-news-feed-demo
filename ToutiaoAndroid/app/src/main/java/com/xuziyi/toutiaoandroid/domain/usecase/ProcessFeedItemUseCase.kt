package com.xuziyi.toutiaoandroid.domain.usecase

import com.xuziyi.toutiaoandroid.domain.model.*
import com.xuziyi.toutiaoandroid.domain.usecase.model.ProcessedFeedResult

class ProcessFeedItemUseCase {

    fun execute(rendered: List<FeedItem>): ProcessedFeedResult {

        // 1. 挑出官方位（UI 渲染策略中定义为 OfficialTop）
        val officials = rendered
            .filter { it.isTopOfficial }
            .take(5)

        // 2. 其余进入普通混合流
        val mixed = rendered.filterNot { it in officials }

        // 3. 官方视频卡标题增强
        val finalOfficials = officials.map { item ->
            if (item.cardType is FeedCardType.Video) {
                item.copy(title = "视频｜${item.title}")
            } else item
        }

        return ProcessedFeedResult(
            officialList = finalOfficials,
            mixedList = mixed
        )
    }
}
