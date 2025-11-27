package com.xuziyi.toutiaoandroid.data.remote.mapper

import com.xuziyi.toutiaoandroid.data.mapper.CardTypeMapper
import com.xuziyi.toutiaoandroid.data.remote.dto.AuthorDto
import com.xuziyi.toutiaoandroid.data.remote.dto.FeedItemDto
import com.xuziyi.toutiaoandroid.data.remote.dto.MediaDto
import com.xuziyi.toutiaoandroid.data.remote.dto.StatsDto
import com.xuziyi.toutiaoandroid.domain.model.*


fun FeedItemDto.toDomain(): FeedItem {
    return FeedItem(
        id = id,
        title = title,
        summary = summary,
        cardType = CardTypeMapper.from(cardType),
        media = media.map { it.toDomain() },
        author = author.toDomain(),
        stats = stats.toDomain(),
        publishTime = publishTime
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
