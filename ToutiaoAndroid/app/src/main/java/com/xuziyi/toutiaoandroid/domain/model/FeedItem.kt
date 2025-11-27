package com.xuziyi.toutiaoandroid.domain.model


data class FeedItem(
    val id: Long,
    val title: String,
    val summary: String?,
    val cardType: FeedCardType,         // 构成 UI 的主类型（图文 / 视频 / 官方卡）
    val media: List<FeedMediaItem>,
    val author: FeedAuthorItem,
    val stats: FeedStatsItem,
    val publishTime: Long,

    // ========= 新增关键属性 =========

    val category: String?,              // sports / finance / shenzhen / video
    val subCategory: String? = null,    // nba / cba / tech / ai
    val tags: List<String>? = null,     // ["热点", "NBA", "深圳"]
    val city: String? = null,           // 地域频道用：shenzhen / beijing

    val isOfficialMedia: Boolean,
    val isTopOfficial: Boolean,
    val source: String? = null,         // 来源：新华社 / 人民日报 / etc.

    val contentType: ContentType,       // text / image / video / gallery

    val weight: Int = 0                 // 用于推荐排序 & 热榜
)

