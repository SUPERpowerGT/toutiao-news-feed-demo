package com.xuziyi.toutiaoandroid.ui.feed.cards.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun CardTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF222222),
        lineHeight = 20.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}
