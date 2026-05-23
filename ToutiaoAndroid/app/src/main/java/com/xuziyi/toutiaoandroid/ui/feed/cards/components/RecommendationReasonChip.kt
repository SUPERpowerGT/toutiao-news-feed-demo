package com.xuziyi.toutiaoandroid.ui.feed.cards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecommendationReasonChip(
    reason: String?,
    modifier: Modifier = Modifier
) {
    if (reason.isNullOrBlank()) return

    Row(
        modifier = modifier
            .background(
                color = Color(0xFFFDF0E6),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = reason,
            color = Color(0xFFD96A1D),
            fontSize = 12.sp
        )
    }
}
