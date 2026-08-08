package com.xuziyi.toutiaoandroid.ui.feed.refresh.state

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateBannerLogicTest {
    private val logic = UpdateBannerLogic()

    @Test
    fun bannerRequiresFlagAndNonBlankText() {
        assertTrue(logic.shouldShowBanner(true, "3 条内容已更新"))
        assertFalse(logic.shouldShowBanner(false, "3 条内容已更新"))
        assertFalse(logic.shouldShowBanner(true, "  "))
        assertFalse(logic.shouldShowBanner(true, null))
    }
}
