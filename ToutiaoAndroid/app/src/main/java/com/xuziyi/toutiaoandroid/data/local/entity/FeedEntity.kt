package com.xuziyi.toutiaoandroid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用于本地缓存 feed 列表的实体
 * 可以只缓存最近一屏内容
 */
@Entity(tableName = "feed")
data class FeedEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val summary: String?,
    val coverImage: String?,
    val type: String,
    val publishTime: String
)
