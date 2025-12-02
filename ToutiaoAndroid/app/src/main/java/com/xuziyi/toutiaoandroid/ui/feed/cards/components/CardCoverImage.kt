package com.xuziyi.toutiaoandroid.ui.feed.cards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers

@Composable
fun CardCoverImage(
    url: String?,
    width: Dp,
    height: Dp
) {
    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .crossfade(true)

            // ⭐ 限制最大 decode 尺寸，避免解码原图
            .size(
                width = width.value.toInt().coerceAtLeast(200),
                height = height.value.toInt().coerceAtLeast(150)
            )

            // ⭐ 强制硬件加速，提升滚动性能
            .allowHardware(true)

            .build(),
        contentDescription = null,
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFEAEAEA)),
        contentScale = ContentScale.Crop
    )
}

