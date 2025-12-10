package com.xuziyi.toutiaoandroid.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff


@Composable
fun ErrorScreen(
    message: String = "网络异常，请检查后重试",
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable { onRetry() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Icons.Filled.CloudOff,
            contentDescription = null,
            tint = Color(0xFFB0B0B0),
            modifier = Modifier.size(60.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = message,
            fontSize = 16.sp,
            color = Color(0xFF666666)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "点击重试",
            fontSize = 15.sp,
            color = Color(0xFF1A73E8)
        )
    }
}
