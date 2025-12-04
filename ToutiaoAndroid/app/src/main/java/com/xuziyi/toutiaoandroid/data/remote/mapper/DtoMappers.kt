package com.xuziyi.toutiaoandroid.data.remote.mapper

import com.xuziyi.toutiaoandroid.data.remote.dto.AuthorDto
import com.xuziyi.toutiaoandroid.data.remote.dto.FeedItemDto
import com.xuziyi.toutiaoandroid.data.remote.dto.FeedResponseDto
import com.xuziyi.toutiaoandroid.data.remote.dto.MediaDto
import com.xuziyi.toutiaoandroid.data.remote.dto.StatsDto
import com.xuziyi.toutiaoandroid.domain.model.*

fun FeedItemDto.toDomain(): FeedItem {
    return FeedItem(
        id = id,
        title = title,
        summary = summary,

        // ❗cardType 由前端策略 UseCase 决定
        cardType = FeedCardType.Text,

        // 媒体内容
        media = media.map { it.toDomain() },

        // 发布账号
        author = author.toDomain(),

        // 点赞评论等数据
        stats = stats.toDomain(),

        publishTime = publishTime,

        // ========= 业务语义字段 =========
        category = category,
        subCategory = subCategory,
        tags = tags,
        city = city,
        isOfficialMedia = isOfficialMedia,   // ← Correct
        isTopOfficial = isTopOfficial,       // ← NEW!
        source = source,
        weight = weight,

        // ⬇ 前端强类型：contentType（由字符串转换）
        contentType = FeedContentType(contentType.lowercase()),
    )
}
fun MediaDto.toDomain(): FeedMediaItem {
    return FeedMediaItem(
        mediaType = mediaType,
        url = url,
        coverUrl = coverUrl,
        duration = duration,
        width = width,
        height = height
    )
}

fun AuthorDto.toDomain(): FeedAuthorItem {
    return FeedAuthorItem(
        id = id,
        name = name,
        avatarUrl = avatarUrl,
        certification = certification
    )
}


fun StatsDto.toDomain(): FeedStatsItem {
    return FeedStatsItem(
        likeCount = likeCount,
        commentCount = commentCount,
        favoriteCount = favoriteCount,
        shareCount = shareCount
    )
}

fun FeedResponseDto.toDomain(): FeedData {
    return FeedData(
        items = items.map { it.toDomain() }, // ⭐ 复用你已有的 FeedItemDto.toDomain()
        nextCursor = nextCursor,
        hasMore = hasMore,
        latestPublishTime = latestPublishTime
    )
}