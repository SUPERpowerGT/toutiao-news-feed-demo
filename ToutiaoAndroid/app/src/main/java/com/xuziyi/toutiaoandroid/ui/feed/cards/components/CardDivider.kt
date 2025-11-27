package com.xuziyi.toutiaoandroid.ui.feed.cards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CardDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.6.dp)
            .background(Color(0xFFEFEFEF))
    )
}
