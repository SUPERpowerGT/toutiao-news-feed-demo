package com.xuziyi.toutiaoandroid.ui.feed.cards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun CardCoverImage(
    url: String?,
    width: Dp,
    height: Dp
) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = Modifier
            .width(width)
            .height(height)
            .background(Color(0xFFEAEAEA), RoundedCornerShape(6.dp)),
        contentScale = ContentScale.Crop
    )
}
