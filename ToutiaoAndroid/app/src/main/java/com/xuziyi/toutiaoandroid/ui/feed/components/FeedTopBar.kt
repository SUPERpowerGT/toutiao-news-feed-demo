package com.xuziyi.toutiaoandroid.ui.feed.components

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

/**
 * FeedTopBar：今日头条首页顶部栏（搜索框 + 发布按钮 + 豆包AI头像）
 *
 * 设计目的：
 *  - 提供头条首页的核心全局操作入口（搜索、发布、个人/AI入口）
 *  - 搜索框使用动态热词（hotSearchText），与后端可对接
 *  - 视觉样式完整复刻今日头条顶部栏，强调强对比度 & 大字号
 *
 * 交互亮点：
 *  - 整个搜索框可点击，触发搜索页跳转
 *  - “发布”按钮与“豆包AI”按钮均为独立可点击区域
 *  - 所有按钮都采用 Touch 友好的尺寸，并支持圆形图标背景
 *
 * 架构设计：
 *  - TopBarButton 抽象为通用组件，图标 + 文本的结构可复用
 *  - 接口函数（onSearchClick / onPublishClick / onAvatarClick）便于与 ViewModel 或 NavController 解耦
 *  - 热词文本（hotSearchText）支持从 ViewModel 注入 → 后期可直接接真实接口
 */

// 顶部栏
@Composable
fun FeedTopBar(
    // 接口预留：这里是从 ViewModel 或后端传来的热搜词
    hotSearchText: String = "折叠屏手机排行榜前十名 | 东部战区",
    onSearchClick: () -> Unit = {},
    onPublishClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {}
) {
    //布局核心参数
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

        //搜索框
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
            // 颜色改为纯黑（微透），大小稍微调大到 20dp，视觉更清晰
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.width(8.dp))

            // 字号加大到 17.sp（今日头条默认字号较大）
            // 颜色改为接近纯黑，去掉灰色滤镜
            // 增加一点点 FontWeight 让它看起来更“实”
            Text(
                text = hotSearchText, // 使用传入的接口数据
                fontSize = 17.sp,
                color = Color.Black.copy(alpha = 0.85f), // 高对比度黑色
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f) // 让文字填满剩余空间，防止被挤压
            )
        }

        Spacer(Modifier.width(12.dp))


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