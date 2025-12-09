package com.xuziyi.toutiaoandroid.ui.feed.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 分页加载错误尾部（不影响主列表）
 */
@Composable
fun LoadMoreErrorFooter(
    errorMessage: String?,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
            .clickable { onRetry() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorMessage ?: "加载失败，点击重试",
            fontSize = 14.sp,
            color = Color(0xFFB00020),
            fontWeight = FontWeight.Medium
        )
    }
}
