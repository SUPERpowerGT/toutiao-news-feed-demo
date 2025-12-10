package com.xuziyi.toutiaoandroid.ui.feed.refresh.state
/**
 * 下拉刷新手势的核心“距离与阻尼”状态容器。
 *
 * 本类只负责：
 *  - 记录当前 dragOffset（下拉位移）
 *  - 根据阻尼公式计算实际消耗距离（applyPull）
 *  - 将位移映射为 0f~1f 的刷新进度（progress）
 *  - 判断是否达到触发刷新的阈值（shouldTriggerRefresh）
 *
 * 本类不负责：
 *  - NestedScroll 事件接入（由 RawPullRefreshNestedScroll 处理）
 *  - Header 高度、动画渲染（在外层 Compose 完成）
 *  - Banner / 刷新 UI 状态（由 RefreshStateLogic 负责）
 *
 * 设计理念：
 *  - 单一职责：专注手势物理逻辑，不参与 UI
 *  - 可测试：所有方法均为纯逻辑，易写单元测试
 *  - 解耦：UI 如何渲染、是否刷新，均由外部控制
 *
 * 使用场景：
 *  - RawPullRefreshNestedScroll 将手势事件转换为 dragOffset
 *  - 外层根据 progress() 绘制下拉动画
 *  - progress >= triggerThreshold 时触发刷新
 */
class PullGestureState(
    private val triggerThreshold: Float = 0.8f
) {

    var dragOffset: Float = 0f
        private set

    /**
     * 阻尼计算（完全复制你原来的公式）
     */
    fun applyPull(availableY: Float, maxPull: Float): Float {
        val damping = 1f / (1f + dragOffset / 200f)
        val consumed = availableY * damping

        dragOffset = (dragOffset + consumed).coerceIn(0f, maxPull)

        return consumed
    }

    fun reset() {
        dragOffset = 0f
    }

    fun progress(maxPull: Float): Float {
        return (dragOffset / maxPull).coerceIn(0f, 1f)
    }

    fun shouldTriggerRefresh(maxPull: Float): Boolean {
        return progress(maxPull) >= triggerThreshold
    }
}
