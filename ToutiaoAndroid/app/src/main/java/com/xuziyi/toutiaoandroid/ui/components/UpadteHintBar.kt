package com.xuziyi.toutiaoandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ------------------------------------
// 辅助 Composable (保持不变)
// ------------------------------------
@Composable
fun UpdateHintBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF4F5F6))
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "今日头条为你推荐了 12 条更新",
            color = Color(0xFFF04142),
            fontSize = 13.sp
        )
    }
}