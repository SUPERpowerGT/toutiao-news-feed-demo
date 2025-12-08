package com.xuziyi.toutiaoandroid.ui.feed.refresh

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
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
    isHoldingRefreshHeader: Boolean,
    pullProgress: Float = 0f,
    onPull: (Float) -> Unit,
    onRefreshTriggered: () -> Unit,
    content: @Composable (paddingTop: Float) -> Unit
) {
    val density = LocalDensity.current

    // 最大下拉距离
    val maxPullPx = with(density) { 140.dp.toPx() }

    // 刷新锁定高度（类似今日头条固定吸顶高度）
    val fixedRefreshPx = with(density) { 42.dp.toPx() }

    // 当前下拉距离
    var dragOffset by remember { mutableFloatStateOf(0f) }

    // 头部需要的真实高度：跟手 or 固定
    val headerTargetPx by remember(isHoldingRefreshHeader, dragOffset) {
        mutableFloatStateOf(
            if (isHoldingRefreshHeader) fixedRefreshPx else dragOffset
        )
    }

    // 让头部过渡更丝滑
    val headerHeightPx by animateFloatAsState(
        targetValue = headerTargetPx.coerceIn(0f, maxPullPx),
        animationSpec = tween(220),
        label = "header"
    )

    val haptic = LocalHapticFeedback.current

    // 加载 Lottie 动画
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("refreshAnimation.json")
    )

    val refreshingLoopProgress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isRefreshing,
        clipSpec = LottieClipSpec.Progress(0.52f, 1f),
        iterations = LottieConstants.IterateForever,
        speed = 0.6f
    )

    // 判断是否在顶部
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset == 0
        }
    }

    // NestedScroll 处理下拉手势
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0 && isAtTop && !isRefreshing && !isHoldingRefreshHeader) {

                    val dy = available.y
                    val damping = 1f / (1f + dragOffset / 200f)
                    val consumed = dy * damping

                    dragOffset = (dragOffset + consumed).coerceIn(0f, maxPullPx)
                    onPull(dragOffset / maxPullPx)

                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0 && isAtTop && !isRefreshing && !isHoldingRefreshHeader) {

                    val dy = available.y
                    val damping = 1f / (1f + dragOffset / 200f)
                    val consumed = dy * damping

                    dragOffset = (dragOffset + consumed).coerceIn(0f, maxPullPx)
                    onPull(dragOffset / maxPullPx)

                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }


            override suspend fun onPreFling(available: Velocity): Velocity {

                val hitRefresh =
                    dragOffset >= maxPullPx * 0.8f &&
                            !isRefreshing &&
                            !isHoldingRefreshHeader &&
                            isAtTop

                return when {

                    // 触发刷新
                    hitRefresh -> {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onRefreshTriggered()
                        dragOffset = 0f
                        onPull(0f)
                        Velocity.Zero
                    }

                    // 回弹
                    dragOffset > 0f -> {
                        dragOffset = 0f
                        onPull(0f)
                        Velocity.Zero
                    }

                    else -> available
                }
            }
        }
    }

    // 完整 UI：动画悬浮 + 列表偏移
    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
            .clipToBounds()   // ⭐ 必须添加，允许动画负offset被裁剪
    ) {

        // 列表整体下移（跟手）
        content(headerHeightPx)

        // ⭐ 动画悬浮：下拉一点点时只露出一点
        if (headerHeightPx > 0f) {
            val animationHeightPx = with(density) { 36.dp.toPx() }

            LottieAnimation(
                composition = composition,
                progress = {
                    if (isRefreshing) refreshingLoopProgress else pullProgress
                },
                modifier = Modifier
                    .height(36.dp)
                    .align(Alignment.TopCenter)
                    .offset {
                        // ⭐ 保留负值 → 动画从上方“慢慢拉出来”
                        val y = headerHeightPx - animationHeightPx
                        IntOffset(0, y.toInt())
                    }
            )
        }
    }

}
