package com.xuziyi.toutiaoandroid.ui.feed.cards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VideoDurationLabel(
    sec: Int,
    modifier: Modifier = Modifier
) {
    val m = sec / 60
    val s = sec % 60
    val formatted = "%02d:%02d".format(m, s)

    Text(
        text = formatted,
        fontSize = 12.sp,
        color = Color.White,
        modifier = modifier
            .background(Color(0x80000000), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}
