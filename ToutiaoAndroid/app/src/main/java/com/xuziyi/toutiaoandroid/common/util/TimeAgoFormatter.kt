package com.xuziyi.toutiaoandroid.common.util

import kotlin.math.abs

object TimeAgoFormatter {
    fun format(timestampSec: Long): String {
        val nowSec = System.currentTimeMillis() / 1000
        val diff = abs(nowSec - timestampSec)

        val minute = 60
        val hour = 60 * minute
        val day = 24 * hour

        return when {
            diff < minute -> "刚刚"
            diff < hour -> "${diff / minute}分钟前"
            diff < day -> "${diff / hour}小时前"
            diff < 2 * day -> "昨天"
            diff < 7 * day -> "${diff / day}天前"
            else -> "较早"
        }
    }
}
