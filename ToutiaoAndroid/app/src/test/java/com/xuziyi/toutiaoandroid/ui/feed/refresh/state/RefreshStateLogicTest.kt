package com.xuziyi.toutiaoandroid.ui.feed.refresh.state

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RefreshStateLogicTest {
    private val logic = RefreshStateLogic()

    @Test
    fun reboundRequiresIdleCollapsedHeader() {
        assertTrue(
            logic.isRebounding(
                pullProgress = 0f,
                isRefreshing = false,
                showUpdateBanner = false,
                headerHeightPx = 40f,
                fixedHeaderPx = 80f
            )
        )
        assertFalse(
            logic.isRebounding(
                pullProgress = 0.2f,
                isRefreshing = false,
                showUpdateBanner = false,
                headerHeightPx = 40f,
                fixedHeaderPx = 80f
            )
        )
    }

    @Test
    fun animationIsVisibleForPullOrActiveRefreshOnly() {
        assertTrue(
            logic.shouldShowAnimation(
                pullProgress = 0.5f,
                isRefreshing = false,
                showUpdateBanner = false,
                isHoldingRefreshHeader = false,
                isRebounding = false,
                showRefreshAnimation = false
            )
        )
        assertFalse(
            logic.shouldShowAnimation(
                pullProgress = 0.5f,
                isRefreshing = true,
                showUpdateBanner = true,
                isHoldingRefreshHeader = true,
                isRebounding = false,
                showRefreshAnimation = true
            )
        )
    }
}
