package com.xuziyi.toutiaoandroid.ui.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xuziyi.toutiaoandroid.R

// ========================
// 顶部栏
// ========================
@Composable
fun FeedTopBar(
    // 🔥 接口预留：这里是从 ViewModel 或后端传来的热搜词
    // 默认值给了你截图里的文本，方便预览
    hotSearchText: String = "折叠屏手机排行榜前十名 | 东部战区",
    onSearchClick: () -> Unit = {},
    onPublishClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {}
) {
    // 1. 布局核心参数
    val barHeight = 40.dp
    val iconContainerSize = 26.dp
    val themeRed = Color(0xFFFF4D4F)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(themeRed)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // --- 搜索框 ---
        Row(
            modifier = Modifier
                .weight(1f)
                .height(barHeight)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onSearchClick)
                .padding(start = 12.dp, end = 12.dp), // 调整左右内边距
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🔥 优化 1：图标
            // 颜色改为纯黑（微透），大小稍微调大到 20dp，视觉更清晰
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.width(8.dp))

            // 🔥 优化 2：文字
            // 字号加大到 15.sp（今日头条默认字号较大）
            // 颜色改为接近纯黑，去掉灰色滤镜
            // 增加一点点 FontWeight 让它看起来更“实”
            Text(
                text = hotSearchText, // 使用传入的接口数据
                fontSize = 15.sp,
                color = Color.Black.copy(alpha = 0.85f), // 高对比度黑色
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f) // 让文字填满剩余空间，防止被挤压
            )
        }

        Spacer(Modifier.width(12.dp))

        // --- 右侧按钮区域 ---

        // 发布按钮
        TopBarButton(
            label = "发布",
            totalHeight = barHeight,
            containerSize = iconContainerSize,
            onClick = onPublishClick
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = themeRed,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(10.dp))

        // 豆包AI按钮
        TopBarButton(
            label = "豆包AI",
            totalHeight = barHeight,
            containerSize = iconContainerSize,
            onClick = onAvatarClick
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_doubao_avatar),
                contentDescription = null,
                modifier = Modifier
                    .size(iconContainerSize)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

/**
 * 通用按钮组件
 */
@Composable
fun TopBarButton(
    label: String,
    totalHeight: Dp,
    containerSize: Dp,
    onClick: () -> Unit,
    iconContent: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .height(totalHeight)
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(containerSize)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            iconContent()
        }

        // 底部文字
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.White,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}