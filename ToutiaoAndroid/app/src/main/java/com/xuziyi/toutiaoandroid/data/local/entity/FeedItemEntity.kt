package com.xuziyi.toutiaoandroid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed_items")
data class FeedItemEntity(

    @PrimaryKey val id: Long,

    val title: String,
    val summary: String?,

    val publishTime: Long,

    // ===== 分类与业务属性 =====
    val category: String?,
    val subCategory: String?,
    val tagsJson: String?,      // List<String> → JSON
    val city: String?,

    // ===== 官方属性 =====
    val isOfficialMedia: Boolean,
    val isTopOfficial: Boolean,
    val source: String?,

    // ===== 内容类型（用于卡片渲染决策）=====
    val contentType: String,     // FeedContentType.value
    val weight: Float,
    val recommendScore: Float,
    val reason: String?,

    // ===== 外键引用 =====
    val authorId: Long,          // FK → authors.id
    val statsId: Long,           // FK → stats.id

    // ===== 媒体(JSON) =====
    val mediaJson: String        // List<FeedMediaItem> → JSON
)
