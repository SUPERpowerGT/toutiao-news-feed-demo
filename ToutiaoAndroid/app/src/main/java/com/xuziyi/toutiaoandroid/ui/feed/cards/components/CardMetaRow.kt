package com.xuziyi.toutiaoandroid.ui.feed.cards.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CardMetaRow(
    source: String,
    commentCount: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = source,
            fontSize = 14.sp,
            color = Color(0xFF999999)
        )

        Spacer(Modifier.width(5.dp))

        Text(
            text = "${commentCount}评论",
            fontSize = 14.sp,
            color = Color(0xFF999999)
        )
    }
}
