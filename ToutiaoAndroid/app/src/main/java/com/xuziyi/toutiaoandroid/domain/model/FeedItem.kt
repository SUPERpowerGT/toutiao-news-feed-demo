package com.xuziyi.toutiaoandroid.domain.model

/**
 * Domain 层的核心数据模型：代表一条推荐流内容（Feed 卡片内容）
 *
 * - 来自后端 FeedItemDto（经过 Mapper 转换）
 * - 经过 UseCase 渲染策略（RenderCardType / ProcessFeedItem）决定最终 cardType
 * - 最终由 FeedCardFactory 根据 cardType 决定 UI 卡片样式（Text/Image/Video/TopOfficial）
 */
data class FeedItem(

    // ===== 基础信息 =====
    val id: Long,                       // 内容唯一 ID（用于跳详情页）
    val title: String,                  // 标题（用于官方区可能会被增强，如“视频｜xxx”）
    val summary: String?,               // 摘要（部分文字卡使用）

    // ===== UI 渲染核心字段 =====
    val cardType: FeedCardType,         // 最终用于 UI 渲染的卡片类型（前端计算，不直接来自后端）

    val media: List<FeedMediaItem>,     // 媒体内容（图片/视频，含封面、时长等）
    val author: FeedAuthorItem,         // 作者/发布账号信息
    val stats: FeedStatsItem,           // 点赞、评论、收藏等统计
    val publishTime: Long,              // 发布时间（Unix Timestamp）

    // ===== 业务属性（来源于后端）=====
    val category: String?,              // 一级分类，如：sports / finance / tech / video
    val subCategory: String? = null,    // 二级分类，如：nba / ai / cba
    val tags: List<String>? = null,     // 语义标签，如 ["热点", "深圳", "社会"]
    val city: String? = null,           // 地域频道，如：beijing / shenzhen

    // ===== 官方属性（推荐流策略关键字段）=====
    val isOfficialMedia: Boolean,       // 是否是官方媒体账号（新华社、人民日报等）
    val isTopOfficial: Boolean,         // 是否进入“官方位”（前 5 条顶置区域）
    val source: String? = null,         // 来源名称（用于展示，如 “新华社”）

    // ===== 内容本质类型（后端决定，用于前端渲染决策）=====
    val contentType: FeedContentType,   // 内容本质类型：text / image / video / gallery

    // ===== 推荐排序相关 =====
    val weight: Float = 0f,                // 排序权重（后端用于推荐/热榜打分）
    val recommendScore: Float = 0f,
    val reason: String? = null
)
