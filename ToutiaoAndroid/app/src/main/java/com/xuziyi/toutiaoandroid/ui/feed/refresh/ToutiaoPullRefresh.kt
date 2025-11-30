package com.xuziyi.toutiaoandroid.ui.feed.refresh

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
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
    listState: LazyListState,
    isRefreshing: Boolean,
    pullProgress: Float = 0f,
    onPull: (Float) -> Unit,
    onRefreshTriggered: () -> Unit,
    content: @Composable (paddingTop: Float) -> Unit
) {
    val density = LocalDensity.current
    val maxPullPx = with(density) { 140.dp.toPx() }   // 最大下拉距离
    var dragOffset by remember { mutableStateOf(0f) }

    val haptic = LocalHapticFeedback.current

    // 刷新中固定吸顶高度（头条约 30dp~40dp）
    val refreshingHeight by animateFloatAsState(
        targetValue = if (isRefreshing) with(density) { 30.dp.toPx() } else 0f,
        animationSpec = tween(200)
    )

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("refreshAnimation.json")
    )

    val refreshingLoopProgress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isRefreshing,
        clipSpec = LottieClipSpec.Progress(0.52f, 1f),
        iterations = LottieConstants.IterateForever
    )

    // ⭐ 正确的顶部判断方式
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset == 0
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {

            // 手指拖动时
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {

                val dy = available.y
                if (dy > 0 && !isRefreshing && isAtTop) {

                    val damping = 1f / (1f + dragOffset / 200f)
                    val consumed = dy * damping

                    dragOffset = (dragOffset + consumed).coerceIn(0f, maxPullPx)
                    onPull(dragOffset / maxPullPx)
                }
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {

                val dy = available.y
                if (dy > 0 && !isRefreshing && isAtTop) {

                    val damping = 1f / (1f + dragOffset / 200f)
                    val consumedPull = dy * damping

                    dragOffset = (dragOffset + consumedPull).coerceIn(0f, maxPullPx)
                    onPull(dragOffset / maxPullPx)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {

                val flingUp = available.y < 0

                if (!isRefreshing && isAtTop && flingUp) {
                    if (dragOffset >= maxPullPx * 0.8f) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onRefreshTriggered()
                    }
                }

                // 松手 → 清零下拉
                dragOffset = 0f
                onPull(0f)

                return available
            }
        }
    }

    // ⭐ 最终偏移量（动画头 + 内容一起）
    val offsetY = dragOffset + refreshingHeight

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {

        val showHeader = pullProgress > 0f || isRefreshing

        if (showHeader) {

            // 下拉阶段逐渐增加高度
            val headerHeight =
                if (isRefreshing) 35.dp
                else 55.dp * pullProgress

            LottieAnimation(
                composition = composition,
                progress = {
                    if (isRefreshing) refreshingLoopProgress else pullProgress
                },
                modifier = Modifier
                    .offset {
                        IntOffset(0, (offsetY * 0.35f).toInt())
                    }
                    .height(headerHeight)
                    .fillMaxWidth()
            )
        }

        // 内容整体下移
        Box(
            modifier = Modifier.offset {
                IntOffset(0, offsetY.toInt())
            }
        ) {
            content(offsetY)
        }
    }
}
