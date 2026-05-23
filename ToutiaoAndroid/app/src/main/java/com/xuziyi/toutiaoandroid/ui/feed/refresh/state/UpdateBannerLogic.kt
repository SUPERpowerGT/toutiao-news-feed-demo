package com.xuziyi.toutiaoandroid.ui.feed.refresh.state
/**
 * 本类负责：
 *  - 根据刷新完成事件 & 服务器返回的新内容数量
 *    判断 Banner 是否应该展示（shouldShowBanner）
 *
 * 本类不负责：
 *  - Banner 的动画实现（由 Composable 自身负责）
 *  - 刷新动画和下拉过程（RefreshStateLogic / PullGestureState）
 *  - NestedScroll 逻辑（RawPullRefreshNestedScroll）
 *
 * 设计目标：
 *  - 分离 Banner 显示逻辑，使刷新头更清晰、易维护
 *  - 如果未来要做“渐隐”、“停留 1.5 秒自动消失”等逻辑，
 *    可以在本类扩展，而无需改主文件
 *
 * 使用场景：
 *  - 在刷新成功后根据最终文案决定 Banner 是否展示
 */

class UpdateBannerLogic {

    /**
     * 是否应该显示刷新提示 Banner
     *
     * 新逻辑：
     * showUpdateBanner && updateBannerText 非空
     */
    fun shouldShowBanner(
        showUpdateBanner: Boolean,
        updateBannerText: String?
    ): Boolean {
        return showUpdateBanner && !updateBannerText.isNullOrBlank()
    }
}
