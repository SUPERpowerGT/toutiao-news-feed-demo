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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.xuziyi.toutiaoandroid.ui.feed.refresh.state.*
import com.xuziyi.toutiaoandroid.ui.feed.refresh.scroll.RawPullRefreshNestedScroll
/**
 * 本组件本身只负责：
 *  - 渲染刷新头 UI（Lottie 动画 & Banner）
 *  - 根据下拉距离为内容添加 paddingTop
 *  - 调用拆分后的逻辑模块决定 *何时显示动画、何时显示 Banner*
 *  - 将 NestedScroll 事件委托给 RawPullRefreshNestedScroll
 *
 * 本组件不负责：
 *  - 手势阻尼 / 距离计算（PullGestureState 负责）
 *  - 是否进入刷新状态的条件判断（RefreshStateLogic）
 *  - “有 X 条内容更新” 横幅逻辑（UpdateBannerLogic）
 *  - NestedScroll 手势事件捕获（RawPullRefreshNestedScroll）
 *
 * 架构拆分的设计目标：
 *  ------------------------------------------------------
 *  1) UI、手势、状态判断彻底解耦，提高可维护性。
 *  2) 任何一层逻辑都可被独立测试（特别是 PullGestureState）。
 *  3) 保持顶层 Composable 清晰，读起来像“声明式 UI”而不是混乱的逻辑堆。
 *  4) 结构接近头条、抖音 App 内部刷新头组件的真实工程写法。
 *
 * 使用方式（外部调用者 ViewModel 只需要做三件事）：
 *  ------------------------------------------------------
 *  1) updatePullProgress(progress) → 在下拉过程中更新 UI 状态
 *  2) refresh() → 触发实际的网络刷新
 *  3) 监听 newCount / showUpdateBanner 控制“有 X 条更新”提示
 *
 * 刷新头生命周期：
 *  ------------------------------------------------------
 *   下拉中 → progress 从 0 → 1
 *   达到阈值 → 触发刷新（haptic + 回调）
 *   刷新中 → Lottie 动画进入循环播放
 *   刷新结束 → 展示“X 条内容已更新”Banner（可淡入淡出）
 *   回弹阶段 → 刷新头收起，恢复正常列表
 *
 * 如需扩展：
 *  ------------------------------------------------------
 *  想增加“刷新成功后自动滚回顶部” → 在 onRefreshTriggered 后加入 scrollToTop
 *  想增加“下拉触发音效/震动更多效果” → 修改 RawPullRefreshNestedScroll 即可
 *  想更换动画类型 → 替换 refreshAnimation.json 即可
 *  想加入 StickyHeader 固定吸顶 → 修改 headerHeightPx 的计算方式即可
 *
 * 该组件的职责非常单一：
 *  → **渲染刷新头 UI + 组合拆分后的逻辑模块**
 */

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

    //3 个逻辑模块（全新拆分）
    val gesture = remember { PullGestureState() }
    val refreshLogic = remember { RefreshStateLogic() }
    val bannerLogic = remember { UpdateBannerLogic() }

    // Header 高度 = 吸顶 or 跟手
    var dragOffset by remember { mutableFloatStateOf(0f) }

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

    // NestedScroll → 使用 gestureState 进行逻辑拆分
    val nestedScroll = remember {
        RawPullRefreshNestedScroll(
            gesture = gesture,
            maxPullPx = maxPullPx,
            isAtTop = { isAtTop },
            isRefreshing = { isRefreshing },
            isHoldingRefreshHeader = { isHoldingRefreshHeader },
            onPull = onPull,
            onRefreshTriggered = onRefreshTriggered,
            haptic = haptic,
            setDragOffset = { dragOffset = it }
        )
    }


    // UI（完全不动）
    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(nestedScroll)
            .clipToBounds()
    ) {

        content(headerHeightPx)

        val headerVisible = headerHeightPx > 0.5f

        if (headerVisible) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(with(density) { headerHeightPx.toDp() }),
                contentAlignment = Alignment.BottomCenter
            ) {

                //pullProgress 替换成 gesture.progress，但保持参数名一致
                val progress = gesture.progress(maxPullPx)

                //回弹判断交给 refreshLogic
                val isRebounding = refreshLogic.isRebounding(
                    pullProgress = progress,
                    isRefreshing = isRefreshing,
                    showUpdateBanner = showUpdateBanner,
                    headerHeightPx = headerHeightPx,
                    fixedHeaderPx = fixedHeaderPx
                )

                // 动画显示判断交给 refreshLogic
                val shouldShowAnim = refreshLogic.shouldShowAnimation(
                    pullProgress = progress,
                    isRefreshing = isRefreshing,
                    showUpdateBanner = showUpdateBanner,
                    isHoldingRefreshHeader = isHoldingRefreshHeader,
                    isRebounding = isRebounding,
                    showRefreshAnimation = showRefreshAnimation
                )

                when {

                    //Banner 显示逻辑移到 bannerLogic
                    bannerLogic.shouldShowBanner(showUpdateBanner, newCount) -> {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(250)) + scaleIn(initialScale = 0.88f),
                            exit = fadeOut(tween(180))
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(50.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "今日头条推荐引擎有 $newCount 条更新",
                                    color = Color(0xFF2F2F2F),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    shouldShowAnim -> {
                        LottieAnimation(
                            composition = composition,
                            progress = {
                                if (isRefreshing) refreshingLoop else progress
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }
}
