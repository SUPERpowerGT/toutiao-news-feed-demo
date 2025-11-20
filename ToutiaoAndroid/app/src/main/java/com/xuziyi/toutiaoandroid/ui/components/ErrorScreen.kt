package com.xuziyi.toutiaoandroid.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "出错了：$message")
        Button(onClick = onRetry) {
            Text("重试")
        }

    }
}
