package com.xuziyi.toutiaoandroid.ui.feed.refresh

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*

@Composable
fun ToutiaoPullRefresh(
    isRefreshing: Boolean,
    pullProgress: Float = 0f,
    onPull: (Float) -> Unit,
    onRefreshTriggered: () -> Unit,
    content: @Composable (paddingTop: Float) -> Unit
) {
    val density = LocalDensity.current
    val maxPullPx = with(density) { 140.dp.toPx() }

    var dragOffset by remember { mutableStateOf(0f) }

    val haptic = LocalHapticFeedback.current

    // 刷新中固定高度
    val refreshingHeight by animateFloatAsState(
        targetValue = if (isRefreshing) 70f else 0f,
        animationSpec = tween(180)
    )

    // 加载 "头条下拉刷新动画"
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("refreshAnimation.json")
    )

    // 刷新中的“尾段循环”动画
    val refreshingLoopProgress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isRefreshing,
        // 循环只播放：0.52f ~ 1.0f 之间（约等于 20f → 38f）
        clipSpec = LottieClipSpec.Progress(0.52f, 1f),
        iterations = LottieConstants.IterateForever
    )

    // 手势逻辑
    val nestedScrollConnection = remember {

        object : NestedScrollConnection {

            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {

                val dy = available.y

                if (dy > 0 && !isRefreshing) {
                    val damping = 1f / (1f + (dragOffset / 200f))
                    val consumed = dy * damping

                    dragOffset = (dragOffset + consumed)
                        .coerceIn(0f, maxPullPx)

                    val progress = dragOffset / maxPullPx
                    onPull(progress)

                    return Offset.Zero
                }

                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {

                val dy = available.y

                if (dy > 0 && !isRefreshing) {
                    val damping = 1f / (1f + (dragOffset / 200f))
                    val consumedPull = dy * damping

                    dragOffset = (dragOffset + consumedPull)
                        .coerceIn(0f, maxPullPx)

                    val progress = dragOffset / maxPullPx
                    onPull(progress)

                    return Offset.Zero
                }

                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {

                if (!isRefreshing && dragOffset >= maxPullPx * 0.9f) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onRefreshTriggered()
                }

                dragOffset = 0f
                onPull(0f)

                return available
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {

        val offsetY = dragOffset + refreshingHeight

        // ======= 下拉 & 刷新动画 =======
// 是否需要显示刷新头：下拉中 OR 刷新中
        val showHeader = pullProgress > 0f || isRefreshing

        if (showHeader) {

            // 头条真实效果：下拉阶段高度 0 → 55dp
            val headerHeight = when {
                isRefreshing -> 55.dp
                pullProgress > 0f -> 55.dp * pullProgress
                else -> 0.dp
            }

            LottieAnimation(
                composition = composition,
                progress = {
                    if (isRefreshing) refreshingLoopProgress else pullProgress
                },
                modifier = Modifier
                    .offset {
                        // 下拉时：动画头跟内容有“拖拽阻尼”
                        IntOffset(0, (offsetY * 0.35f).toInt())
                    }
                    .height(headerHeight)
                    .fillMaxWidth()
            )
        }


        // ======= 内容整体下移 =======
        Box(
            modifier = Modifier.offset {
                IntOffset(0, offsetY.toInt())
            }
        ) {
            content(offsetY)
        }
    }
}
