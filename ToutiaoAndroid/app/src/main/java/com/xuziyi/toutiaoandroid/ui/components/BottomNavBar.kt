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

@Composable
fun BottomNavBar(
    selectedIndex: Int = 0,
    onTabSelected: (Int) -> Unit = {}
) {

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
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 6.dp),   // 上面加 padding
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            tabs.forEachIndexed { index, tab ->
                BottomNavItem(
                    label = tab.label,
                    icon = if (selectedIndex == index) tab.iconSelected else tab.iconNormal,
                    isSelected = selectedIndex == index,
                    onClick = { onTabSelected(index) }
                )
            }
        }
    }
}

data class BottomNavData(
    val label: String,
    val iconNormal: Int,
    val iconSelected: Int
)

@Composable
private fun BottomNavItem(
    label: String,
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp)   //图标缩小一点
        )

        Spacer(modifier = Modifier.height(6.dp))  // 图标与文字间距更大

        Text(
            text = label,
            fontSize = 12.sp,                          // 字体变小一点
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFFFF4D4F) else Color.Black
        )
    }
}
