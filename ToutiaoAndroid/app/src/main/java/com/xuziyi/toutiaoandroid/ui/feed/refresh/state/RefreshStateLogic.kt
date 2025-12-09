package com.xuziyi.toutiaoandroid.ui.feed.refresh.state
/**
 * 用于处理刷新头在不同阶段下的 UI 状态判断逻辑。
 *
 * 本类负责的内容：
 *  - 判断当前是否处于“回弹阶段”（isRebounding）
 *  - 判断刷新动画是否应该展示（shouldShowAnimation）
 *
 * 不负责的内容：
 *  - 手势距离与阻尼（由 PullGestureState 负责）
 *  - NestedScroll 事件处理（RawPullRefreshNestedScroll）
 *  - Banner 显示逻辑（UpdateBannerLogic）
 *
 * 设计目标：
 *  - 把与 UI 强相关的复杂状态判断集中到一个地方
 *  - 主 Composable 不再出现长条件判断，提高可读性
 *  - 动画展示逻辑可独立修改，不影响手势或滚动代码
 *
 * 使用场景：
 *  - ToutiaoPullRefresh 会调用本类来判断：
 *      - 当前是否应该显示 Lottie 动画
 *      - 下拉结束后的回弹阶段如何处理
 */

class RefreshStateLogic {

    /**
     * 计算是否处于“回弹阶段”
     * 完全复制你原来的逻辑：
     *
     * pullProgress == 0f &&
     * !isRefreshing &&
     * !showUpdateBanner &&
     * headerHeightPx < fixedHeaderPx
     */
    fun isRebounding(
        pullProgress: Float,
        isRefreshing: Boolean,
        showUpdateBanner: Boolean,
        headerHeightPx: Float,
        fixedHeaderPx: Float
    ): Boolean {
        return pullProgress == 0f &&
                !isRefreshing &&
                !showUpdateBanner &&
                headerHeightPx < fixedHeaderPx
    }

    /**
     * 控制 Lottie 动画是否显示
     *
     * 逻辑完全对应你原文件：
     *
     * !isRebounding &&
     * !showUpdateBanner &&
     * (
     *    (!isHoldingRefreshHeader && pullProgress > 0f)
     *    ||
     *    (isRefreshing && showRefreshAnimation)
     * )
     */
    fun shouldShowAnimation(
        pullProgress: Float,
        isRefreshing: Boolean,
        showUpdateBanner: Boolean,
        isHoldingRefreshHeader: Boolean,
        isRebounding: Boolean,
        showRefreshAnimation: Boolean
    ): Boolean {
        return !isRebounding &&
                !showUpdateBanner &&
                (
                        (!isHoldingRefreshHeader && pullProgress > 0f) ||
                                (isRefreshing && showRefreshAnimation)
                        )
    }
}
