package com.xuziyi.toutiaoandroid.data.local.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xuziyi.toutiaoandroid.domain.model.FeedMediaItem

class Converters {

    private val gson = Gson()

    // List<String> ↔ JSON
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(json: String?): List<String>? {
        if (json.isNullOrEmpty()) return null
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    // List<FeedMediaItem> ↔ JSON
    @TypeConverter
    fun fromMediaList(list: List<FeedMediaItem>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toMediaList(json: String): List<FeedMediaItem> {
        val type = object : TypeToken<List<FeedMediaItem>>() {}.type
        return gson.fromJson(json, type)
    }
}
