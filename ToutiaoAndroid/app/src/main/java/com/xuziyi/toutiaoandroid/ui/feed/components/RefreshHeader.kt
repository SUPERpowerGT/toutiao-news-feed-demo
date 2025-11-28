package com.xuziyi.toutiaoandroid.ui.feed.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*

@Composable
fun RefreshHeader(
    pullProgress: Float,       // 跟手进度 0f ~ 1f
    isRefreshing: Boolean      // 是否处于刷新中
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("toutiao_refresh.json")
    )

    val lottieAnim = when {
        isRefreshing -> {
            // 刷新中：无限循环
            rememberLottieAnimatable().apply {
                LaunchedEffect(true) {
                    animate(
                        composition = composition,
                        iterations = LottieConstants.IterateForever
                    )
                }
            }
        }
        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {

        if (!isRefreshing) {
            // 下拉跟手动画：只用进度，不循环
            LottieAnimation(
                composition = composition,
                progress = { pullProgress }
            )
        } else {
            // 刷新中播放无限循环 Lottie
            lottieAnim?.let {
                LottieAnimation(
                    composition = composition,
                    progress = { it.progress }
                )
            }
        }
    }
}
