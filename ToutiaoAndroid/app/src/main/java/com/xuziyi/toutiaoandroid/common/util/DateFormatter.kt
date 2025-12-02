/*
package com.xuziyi.toutiaoandroid.common.util

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object DateFormatter {

    private val inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    private val outputFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")

    fun format(raw: String?): String {
        return try {
            raw?.let {
                OffsetDateTime.parse(it, inputFormatter).format(outputFormatter)
            } ?: ""
        } catch (e: Exception) {
            raw ?: ""
        }
    }
}
*/
