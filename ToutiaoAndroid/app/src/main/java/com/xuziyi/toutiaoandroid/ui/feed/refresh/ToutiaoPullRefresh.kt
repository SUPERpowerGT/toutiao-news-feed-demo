package com.xuziyi.toutiaoandroid.ui.feed.refresh

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*

@Composable
fun ToutiaoPullRefresh(
    listState: LazyListState,
    isRefreshing: Boolean,
    isHoldingRefreshHeader: Boolean,
    showRefreshAnimation: Boolean,
    showUpdateBanner: Boolean,
    newCount: Int,
    pullProgress: Float = 0f,
    onPull: (Float) -> Unit,
    onRefreshTriggered: () -> Unit,
    content: @Composable (paddingTop: Float) -> Unit
) {
    val density = LocalDensity.current

    val maxPullPx = with(density) { 140.dp.toPx() }
    val fixedHeaderPx = with(density) { 42.dp.toPx() }

    var dragOffset by remember { mutableFloatStateOf(0f) }

    // ⭐ Header 高度 = 吸顶 or 跟手
    val headerTargetPx by remember(isHoldingRefreshHeader, dragOffset) {
        mutableFloatStateOf(
            if (isHoldingRefreshHeader) fixedHeaderPx else dragOffset
        )
    }

    val headerHeightPx by animateFloatAsState(
        targetValue = headerTargetPx.coerceIn(0f, maxPullPx),
        animationSpec = tween(220),
        label = "header"
    )

    // Lottie 刷新动画
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("refreshAnimation.json")
    )

    val refreshingLoop by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isRefreshing,
        clipSpec = LottieClipSpec.Progress(0.52f, 1f),
        iterations = LottieConstants.IterateForever,
        speed = 0.65f
    )

    val haptic = LocalHapticFeedback.current

    // 是否在列表顶部
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset == 0
        }
    }


    // Nested Scroll（保持稳定）
    val nestedScroll = remember {
        object : NestedScrollConnection {

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0 && isAtTop && !isRefreshing && !isHoldingRefreshHeader) {

                    val damping = 1f / (1f + dragOffset / 200f)
                    val consumed = available.y * damping

                    dragOffset = (dragOffset + consumed)
                        .coerceIn(0f, maxPullPx)

                    onPull(dragOffset / maxPullPx)
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0 && isAtTop && !isRefreshing && !isHoldingRefreshHeader) {

                    val damping = 1f / (1f + dragOffset / 200f)
                    val consumed = available.y * damping

                    dragOffset = (dragOffset + consumed)
                        .coerceIn(0f, maxPullPx)

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
                    hitRefresh -> {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onRefreshTriggered()
                        dragOffset = 0f
                        onPull(0f)
                        Velocity.Zero
                    }

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


    //UI 部分
    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(nestedScroll)
            .clipToBounds()
    ) {

        val contentOffset = headerHeightPx
        content(contentOffset)

        val headerVisible = headerHeightPx > 0.5f

        if (headerVisible) {

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(with(density) { headerHeightPx.toDp() }),
                contentAlignment = Alignment.BottomCenter
            ) {

                // 最终版动画显示逻辑（不会丢失动画）
                // 判断是否处于“回弹阶段”
                val isRebounding =
                    pullProgress == 0f &&       // 没有下拉
                            !isRefreshing &&            // 不是刷新中
                            !showUpdateBanner &&        // 不是展示 banner 中
                            headerHeightPx < fixedHeaderPx // 正在向上回弹收缩

                // 最终动画显示逻辑
                val shouldShowAnim =
                    !isRebounding &&            // 回弹阶段禁止动画（解决你现在的问题）
                            !showUpdateBanner &&        // banner 阶段禁止动画
                            (
                                    // 下拉阶段：未吸顶 & 未刷新
                                    (!isHoldingRefreshHeader && pullProgress > 0f)
                                            ||
                                            // 刷新阶段：吸顶 + 正在刷新 + 动画开关=true
                                            (isRefreshing && showRefreshAnimation)
                                    )


                when {

                    //刷新完成 → Banner（带淡入缩放）
                    showUpdateBanner && newCount > 0 -> {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(250)) + scaleIn(initialScale = 0.88f),
                            exit = fadeOut(tween(180))
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)      //贴底显示
                                    .background(Color(0x33FF0000))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$newCount 条内容已更新",
                                    color = Color(0xFFFF4444),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    //Lottie 刷新动画
                    shouldShowAnim -> {
                        LottieAnimation(
                            composition = composition,
                            progress = {
                                if (isRefreshing) refreshingLoop else pullProgress
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .align(Alignment.BottomCenter)
                        )
                    }

                    //空内容，不渲染（避免闪烁）
                    else -> {}
                }
            }
        }
    }
}
