package com.xuziyi.toutiaoandroid.ui.feed.refresh.scroll

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import com.xuziyi.toutiaoandroid.ui.feed.refresh.state.PullGestureState
/**
 *
 * 本类只负责：
 *  - 处理下拉手势（onPreScroll / onPostScroll）
 *  - 计算实际消耗的距离（阻尼效果由 PullGestureState 完成）
 *  - 判断是否触发刷新（onPreFling 阶段）
 *  - 控制刷新触发后的震动反馈 & 状态复位
 *
 * 本类不负责：
 *  - 动画展示逻辑（由 RefreshStateLogic 管理）
 *  - Banner 显示逻辑（由 UpdateBannerLogic 管理）
 *  - Header 高度计算（交给外层 Compose）
 *
 * 设计理念：
 *  - 将“手势驱动逻辑”从 UI 中剥离，形成独立可测试模块
 *  - 仅专注于 NestedScroll 事件，不涉及视觉渲染
 *  - 易于替换 / 扩展（未来可以适配 SmartRefresh、弹性效果等）
 *
 * 依赖模块：
 *  - PullGestureState：保存下拉距离、阻尼、进度计算
 *  - callback(onPull)：通知 UI / ViewModel 当前的刷新进度
 *  - callback(onRefreshTriggered)：触发真正的刷新行为
 *
 */

class RawPullRefreshNestedScroll(
    private val gesture: PullGestureState,
    private val maxPullPx: Float,
    private val isAtTop: () -> Boolean,
    private val isRefreshing: () -> Boolean,
    private val isHoldingRefreshHeader: () -> Boolean,
    private val onPull: (Float) -> Unit,
    private val onRefreshTriggered: () -> Unit,
    private val haptic: HapticFeedback,
    private val setDragOffset: (Float) -> Unit
) : NestedScrollConnection {

    //手指下拉不断回调
    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset {

        if (available.y > 0 && isAtTop() && !isRefreshing() && !isHoldingRefreshHeader()) {

            val consumed = gesture.applyPull(available.y, maxPullPx)

            onPull(gesture.progress(maxPullPx))

            setDragOffset(gesture.dragOffset)
            return Offset(0f, consumed)
        }

        return Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {

        if (available.y > 0 && isAtTop() && !isRefreshing() && !isHoldingRefreshHeader()) {

            val consumed = gesture.applyPull(available.y, maxPullPx)

            onPull(gesture.progress(maxPullPx))

            setDragOffset(gesture.dragOffset)
            return Offset(0f, consumed)
        }

        return Offset.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {

        val hitRefresh =
            gesture.shouldTriggerRefresh(maxPullPx) &&
                    !isRefreshing() &&
                    !isHoldingRefreshHeader() &&
                    isAtTop()

        return when {

            hitRefresh -> {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onRefreshTriggered()
                gesture.reset()
                setDragOffset(0f)
                onPull(0f)
                Velocity.Zero
            }

            gesture.dragOffset > 0f -> {
                gesture.reset()
                setDragOffset(0f)
                onPull(0f)
                Velocity.Zero
            }

            else -> available
        }
    }
}
