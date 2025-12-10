package com.xuziyi.toutiaoandroid.data.local.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xuziyi.toutiaoandroid.data.local.entity.AuthorEntity
import com.xuziyi.toutiaoandroid.data.local.entity.FeedItemEntity
import com.xuziyi.toutiaoandroid.data.local.entity.StatsEntity
import com.xuziyi.toutiaoandroid.domain.model.*

object FeedLocalMapper {

    private val gson = Gson()

    // FeedItemEntity → Domain FeedItem
    fun toDomain(
        item: FeedItemEntity,
        author: AuthorEntity,
        stats: StatsEntity
    ): FeedItem {

        val mediaList = gson.fromJson<List<FeedMediaItem>>(
            item.mediaJson,
            object : TypeToken<List<FeedMediaItem>>() {}.type
        )

        val tagsList = item.tagsJson?.let {
            gson.fromJson<List<String>>(it, object : TypeToken<List<String>>() {}.type)
        }

        return FeedItem(
            id = item.id,
            title = item.title,
            summary = item.summary,

            // Domain 中 FeedCardType 是 UI 层决定的
            cardType = FeedCardType.Text,

            media = mediaList,
            author = author.toDomain(),
            stats = stats.toDomain(),
            publishTime = item.publishTime,

            category = item.category,
            subCategory = item.subCategory,
            tags = tagsList,
            city = item.city,

            isOfficialMedia = item.isOfficialMedia,
            isTopOfficial = item.isTopOfficial,
            source = item.source,

            contentType = FeedContentType(item.contentType),
            weight = item.weight
        )
    }

    // Domain FeedItem → FeedItemEntity（用于写入本地）
    fun fromDomain(
        item: FeedItem,
        authorId: Long,
        statsId: Long
    ): FeedItemEntity {

        val tagsJson = item.tags?.let { gson.toJson(it) }
        val mediaJson = gson.toJson(item.media)

        return FeedItemEntity(
            id = item.id,
            title = item.title,
            summary = item.summary,

            publishTime = item.publishTime,

            category = item.category,
            subCategory = item.subCategory,
            tagsJson = tagsJson,
            city = item.city,

            isOfficialMedia = item.isOfficialMedia,
            isTopOfficial = item.isTopOfficial,
            source = item.source,

            contentType = item.contentType.value,
            weight = item.weight,

            authorId = authorId,
            statsId = statsId,

            mediaJson = mediaJson
        )
    }

    // AuthorEntity → Domain
    private fun AuthorEntity.toDomain() = FeedAuthorItem(
        id = id,
        name = name,
        avatarUrl = avatarUrl,
        certification = certification
    )

    // StatsEntity → Domain
    private fun StatsEntity.toDomain() = FeedStatsItem(
        likeCount = likeCount,
        commentCount = commentCount,
        favoriteCount = favoriteCount,
        shareCount = shareCount
    )
}
