package com.xuziyi.toutiaoandroid.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.xuziyi.toutiaoandroid.R // 导入 R 文件，以便访问 res/font

// ------------------------------------------
// 1. 定义 Noto Sans SC 字体族 (使用所有字重文件)
// ------------------------------------------
val NotoSansFontFamily = FontFamily(
    // ❗ 修正：使用 notosanssc_medium.ttf 对应 Medium 权重
    Font(R.font.notosanssc_medium, FontWeight.Medium),

    // ❗ 修正：将 FontWeight.Normal (Regular) 也指向 Medium，作为常规字体的默认文件
    // 如果你有 notosanssc_regular.ttf，应该用它来替换
    Font(R.font.notosanssc_medium, FontWeight.Normal),

    // ❗ 修正：使用 notosanssc_bold.ttf 对应 Bold 权重
    Font(R.font.notosanssc_bold, FontWeight.Bold),

    // ❗ 额外：注册 Extrabold
    Font(R.font.notosanssc_extrabold, FontWeight.ExtraBold)
)

// ------------------------------------------
// 2. 创建自定义 Typography 对象 (AppTypography)
// ------------------------------------------
val AppTypography = Typography(

    // 覆盖 Body Large (常规正文)
    bodyLarge = TextStyle(
        fontFamily = NotoSansFontFamily, // 应用自定义字体
        fontWeight = FontWeight.Normal, // 对应 notosanssc_medium (fallback)
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    // 覆盖 Title Large (列表标题)
    titleLarge = TextStyle(
        fontFamily = NotoSansFontFamily, // 应用自定义字体
        fontWeight = FontWeight.Medium, // 对应 notosanssc_medium.ttf
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    // 覆盖 Headline Medium (重要标题/页眉)
    headlineMedium = TextStyle(
        fontFamily = NotoSansFontFamily, // 应用自定义字体
        fontWeight = FontWeight.Bold, // 对应 notosanssc_bold.ttf
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),

    // 覆盖 Label Small (按钮或标签)
    labelSmall = TextStyle(
        fontFamily = NotoSansFontFamily, // 应用自定义字体
        fontWeight = FontWeight.Medium, // 对应 notosanssc_medium.ttf
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

    // 如果需要，你可以在这里继续添加其他 Material 3 的样式，以确保完全统一。
)