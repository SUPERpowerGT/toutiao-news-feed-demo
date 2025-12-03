package com.xuziyi.toutiaoandroid.ui.feed.refresh

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import kotlin.math.roundToInt

/**
 * 今日头条风格下拉刷新（Header 在列表上方一起下移，不遮挡文字）
 */
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
    val maxPullPx = with(density) { 140.dp.toPx() }     // 最大下拉距离
    val refreshHeaderPx = with(density) { 35.dp.toPx() } // 刷新中的吸顶高度

    // 真实下拉距离（目标值）
    var dragOffset by remember { mutableStateOf(0f) }

    // 下拉距离 + 吸顶高度 -> 最终 Header 高度目标
    val headerTargetPx by remember(isRefreshing, dragOffset) {
        mutableStateOf(
            if (isRefreshing) refreshHeaderPx else dragOffset
        )
    }

    // Header 实际高度（带动画）
    val headerHeightPx by animateFloatAsState(
        targetValue = headerTargetPx.coerceIn(0f, maxPullPx),
        animationSpec = tween(200),
        label = "header-height"
    )

    // 反馈与 Lottie
    val haptic = LocalHapticFeedback.current

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("refreshAnimation.json")
    )

    val refreshingLoopProgress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isRefreshing,
        clipSpec = LottieClipSpec.Progress(0.52f, 1f),
        iterations = LottieConstants.IterateForever
    )

    // 列表是否在顶部
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset == 0
        }
    }

    // 当前进度（用于下拉阶段的 Lottie）
    val currentPullProgress = remember(dragOffset, maxPullPx, isRefreshing, pullProgress) {
        if (isRefreshing) 1f
        else (dragOffset / maxPullPx).coerceIn(0f, 1f)
    }

    // NestedScroll：只控制 dragOffset，不直接对内容做 offset
    val nestedScrollConnection = remember {

        object : NestedScrollConnection {

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // 手指向下拉 && 在顶部 && 不在刷新中 -> 消费下拉
                if (available.y > 0 && isAtTop && !isRefreshing) {
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
                // 额外剩余的向下位移也吃掉，保证只要在顶部，全部转成 Header 高度
                if (available.y > 0 && isAtTop && !isRefreshing) {
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

                val hitRefresh = dragOffset >= maxPullPx * 0.8f &&
                        !isRefreshing &&
                        isAtTop

                return when {

                    // 达到阈值 → 触发刷新，Header 吸顶停留
                    hitRefresh -> {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onRefreshTriggered()
                        // 触发刷新后，交给 isRefreshing 控制 headerTargetPx -> refreshHeaderPx
                        dragOffset = 0f
                        onPull(0f)
                        Velocity.Zero
                    }

                    // 没达到阈值但有下拉距离 → 回弹到 0
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

    // ========= UI 布局 =========
    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            val headerVisible = headerHeightPx > 0.5f || isRefreshing

            // ① Header 区域：占据真实高度，下面的列表整体被顶下去
            if (headerVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            with(density) { headerHeightPx.toDp() }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = {
                            if (isRefreshing) refreshingLoopProgress
                            else currentPullProgress
                        },
                        modifier = Modifier
                            .height(55.dp)   // Lottie 自身高度
                            .fillMaxWidth()
                    )
                }
            }

            // ② 列表内容：自然排在 Header 下面，不再被覆盖
            content(headerHeightPx)
        }
    }
}
