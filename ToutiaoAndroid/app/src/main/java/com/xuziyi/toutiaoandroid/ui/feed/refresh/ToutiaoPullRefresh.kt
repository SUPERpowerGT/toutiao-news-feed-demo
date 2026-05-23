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

//复用组件，slot/插槽模式
//ToutiaoPullRefresh 是一个可复用的容器型 Composable，
// 负责下拉刷新交互与刷新头渲染，
// 通过 slot content 包裹任意列表内容，
// 并通过回调与 ViewModel 进行状态同步。
@Composable
fun ToutiaoPullRefresh(
    listState: LazyListState,
    isRefreshing: Boolean,
    isHoldingRefreshHeader: Boolean,
    showRefreshAnimation: Boolean,
    showUpdateBanner: Boolean,
    newCount: Int,
    updateBannerText: String?,
    pullProgress: Float = 0f,
    onPull: (Float) -> Unit,
    onRefreshTriggered: () -> Unit,
    //slot 插槽设计
    content: @Composable (paddingTop: Float) -> Unit
) {

    val density = LocalDensity.current
    val maxPullPx = with(density) { 140.dp.toPx() }
    val fixedHeaderPx = with(density) { 42.dp.toPx() }

    //3 个逻辑模块（全新拆分）
    //手指拉了多少？进度是多少？
    //生命周期十分短暂，专门解决手势state的
    val gesture = remember { PullGestureState() }
    //什么时候该显示动画？什么时候该回弹？
    val refreshLogic = remember { RefreshStateLogic() }
    //要不要显示“X 条更新”的 banner？
    val bannerLogic = remember { UpdateBannerLogic() }

    // Header 高度 = 吸顶 or 跟手
    var dragOffset by remember { mutableFloatStateOf(0f) }

    //没吸顶 → 刷新头高度 = 手指拉了多远
    //在吸顶 → 刷新头高度 = 固定高度（42dp）
    val headerTargetPx by remember(isHoldingRefreshHeader, dragOffset) {
        mutableFloatStateOf(
            if (isHoldingRefreshHeader) fixedHeaderPx else dragOffset
        )
    }

    //平滑过渡 dragOffset → headerTargetPx → headerHeightPx
    val headerHeightPx by animateFloatAsState(
        targetValue = headerTargetPx.coerceIn(0f, maxPullPx),
        animationSpec = tween(220),
        label = "header"
    )

    // Lottie 刷新动画
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("refreshAnimation.json")
    )

    //循环播放
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

    // NestedScroll监听手势，抓手指
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
            //接收用户手势
            .nestedScroll(nestedScroll)
            .clipToBounds()
    ) {
        // 将刷新头当前高度传递给内容区域，用于顶开列表并与刷新头保持视觉同步
        content(headerHeightPx)

        if (bannerLogic.shouldShowBanner(showUpdateBanner, updateBannerText)) {
            AnimatedVisibility(
                visible = true,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
                enter = fadeIn(tween(250)) + scaleIn(initialScale = 0.88f),
                exit = fadeOut(tween(180))
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(50.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = updateBannerText.orEmpty(),
                        color = Color(0xFF2F2F2F),
                        fontSize = 14.sp
                    )
                }
            }
        }

        val headerVisible = headerHeightPx > 0.5f

        if (headerVisible) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(with(density) { headerHeightPx.toDp() }),
                contentAlignment = Alignment.BottomCenter
            ) {

                //唯一出口，当前收拾看，进度是多少
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
