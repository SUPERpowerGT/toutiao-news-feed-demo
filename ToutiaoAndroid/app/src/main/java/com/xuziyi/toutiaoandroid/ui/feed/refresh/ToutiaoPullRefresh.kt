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
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*

/**
 * 今日头条风格下拉刷新：
 * —— 列表不会被遮挡，而是整体被 Header "推" 下去
 * —— 下拉阻尼、回弹动画、吸顶刷新效果
 */
@Composable
fun ToutiaoPullRefresh(
    listState: LazyListState,
    isRefreshing: Boolean,
    pullProgress: Float = 0f,
    onPull: (Float) -> Unit,           // 下拉进度回调（驱动动画）
    onRefreshTriggered: () -> Unit,    // 松手 → 执行刷新
    content: @Composable (paddingTop: Float) -> Unit
) {
    // 单位转换工具
    val density = LocalDensity.current

    // 最大可拉高度（下拉距离上限）
    val maxPullPx = with(density) { 140.dp.toPx() }

    // 刷新中吸顶时的固定高度
    val refreshHeaderPx = with(density) { 35.dp.toPx() }

    // dragOffset = 当前真实下拉距离
    var dragOffset by remember { mutableFloatStateOf(0f) }

    // 刷新中：头部高度固定在 refreshHeaderPx
    // 正常下拉：头部高度跟手指走（= dragOffset）
    val headerTargetPx by remember(isRefreshing, dragOffset) {
        mutableFloatStateOf(
            if (isRefreshing) refreshHeaderPx else dragOffset
        )
    }

    // 用动画让 Header 高度变更变得更丝滑（头条手感关键）
    val headerHeightPx by animateFloatAsState(
        targetValue = headerTargetPx.coerceIn(0f, maxPullPx),
        animationSpec = tween(200),
        label = "header-height"
    )

    // 震动反馈
    val haptic = LocalHapticFeedback.current

    // Lottie 动画加载
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("refreshAnimation.json")
    )

    // 刷新中循环播放下半段，非刷新时按进度播放
    val refreshingLoopProgress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isRefreshing,
        clipSpec = LottieClipSpec.Progress(0.52f, 1f),
        iterations = LottieConstants.IterateForever
    )

    // 判断列表是否在顶部（只有顶端才允许下拉刷新）
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&
                    listState.firstVisibleItemScrollOffset == 0
        }
    }

    // 当前下拉进度（驱动动画用）
    val currentPullProgress = remember(dragOffset, isRefreshing) {
        if (isRefreshing) 1f else (dragOffset / maxPullPx).coerceIn(0f, 1f)
    }

    /**
     * NestedScroll：整个下拉刷新的灵魂
     * - 拦截“向下滑”的手势
     * - 自己消耗掉并转换成 dragOffset
     */
    val nestedScrollConnection = remember {

        object : NestedScrollConnection {

            /** 手指向下拉时 → LazyColumn 本来不会动 → 我们拦截 */
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0 && isAtTop && !isRefreshing) {

                    val dy = available.y

                    // 阻尼：越往下拉越难拉
                    val damping = 1f / (1f + dragOffset / 200f)
                    val consumed = dy * damping

                    // 更新下拉距离
                    dragOffset = (dragOffset + consumed).coerceIn(0f, maxPullPx)

                    // 通知外部刷新头动画更新
                    onPull(dragOffset / maxPullPx)

                    // 消费掉这段位移（列表不会滚动）
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            /** 额外剩余的位移也吃掉，继续增加 dragOffset */
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
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

            /**
             * 手指松手时：
             *  - 如果达到刷新阈值 → 真刷新
             *  - 不够 → 回弹到 0
             */
            override suspend fun onPreFling(available: Velocity): Velocity {

                val hitRefresh =
                    dragOffset >= maxPullPx * 0.8f &&
                            !isRefreshing &&
                            isAtTop

                return when {

                    // 达到阈值 → 刷新
                    hitRefresh -> {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onRefreshTriggered()

                        // dragOffset 清零，接下来交给 isRefreshing 驱动吸顶高度
                        dragOffset = 0f
                        onPull(0f)
                        Velocity.Zero
                    }

                    // 未达到阈值 → 回弹
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


    //   UI：Header + 列表
    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection) // 绑定 NestedScroll
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            val headerVisible = headerHeightPx > 0.5f || isRefreshing

            // 刷新头（被推下的区域
            if (headerVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(with(density) { headerHeightPx.toDp() }),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = {
                            if (isRefreshing) refreshingLoopProgress
                            else currentPullProgress
                        },
                        modifier = Modifier
                            .height(55.dp)
                            .fillMaxWidth()
                    )
                }
            }

            // 列表内容：跟随 header 被整体推下
            content(headerHeightPx)
        }
    }
}
