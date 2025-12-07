package com.xuziyi.toutiaoandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import com.xuziyi.toutiaoandroid.R

/**
 * 应用主界面的底部导航栏（Tab Bar）。
 * - 负责显示四个导航 Tab（首页 / 视频 / 商城 / 我的）
 * - 自身不负责导航逻辑，仅通过 onTabSelected 回调通知外部
 * - MainNavigator 会使用 BottomNavBar 来驱动导航切换
 */
@Composable
fun BottomNavBar(
    selectedIndex: Int = 0,       // 当前选中的 Tab（由外部状态提供）
    onTabSelected: (Int) -> Unit = {}  // 用户点击 Tab 后的回调，由导航层处理页面切换
) {

    // 底部导航的四个 Tab 数据
    // 包含标题 + 默认图标 + 高亮图标
    val tabs = listOf(
        BottomNavData("首页", R.drawable.ic_shouye, R.drawable.ic_shouye_highlight),
        BottomNavData("视频", R.drawable.ic_video, R.drawable.ic_video_highlight),
        BottomNavData("商城", R.drawable.ic_shopping, R.drawable.ic_shopping_highlight),
        BottomNavData("未登录", R.drawable.ic_unlogin, R.drawable.ic_unlogin_highlight)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            // 自动避让系统底部导航栏区域（适配全面屏）
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {

        // 水平排列 4 个 Tab
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 渲染每一个 Tab 项
            tabs.forEachIndexed { index, tab ->
                BottomNavItem(
                    label = tab.label,
                    icon = if (selectedIndex == index) tab.iconSelected else tab.iconNormal,
                    isSelected = selectedIndex == index,
                    onClick = { onTabSelected(index) }  // 点击事件回调给外部
                )
            }
        }
    }
}

/**
 * 底部导航项的数据结构
 * - label: 显示文字
 * - iconNormal: 未选中图标
 * - iconSelected: 选中图标
 */
data class BottomNavData(
    val label: String,
    val iconNormal: Int,
    val iconSelected: Int
)

/**
 * 单个底部导航项（图标 + 文字）。
 * 这是 BottomNavBar 的 UI 子组件。
 */
@Composable
private fun BottomNavItem(
    label: String,
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }  // 用户点击后通知外部
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Tab 图标（使用资源图片，不自动 tint）
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = Color.Unspecified,            // 保留原图色彩
            modifier = Modifier.size(24.dp)      // 控制图标大小
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Tab 文本样式：选中加粗 + 红色
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFFFF4D4F) else Color.Black
        )
    }
}
