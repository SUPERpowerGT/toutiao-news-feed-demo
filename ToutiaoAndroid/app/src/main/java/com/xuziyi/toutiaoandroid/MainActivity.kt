package com.xuziyi.toutiaoandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.xuziyi.toutiaoandroid.ui.navigation.AppNavigator
import com.xuziyi.toutiaoandroid.ui.theme.ToutiaoAndroidTheme

// MainActivity 作为整个 App 的 UI 入口（Compose 的启动点）
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 关闭系统默认的状态栏/导航栏内边距适配，启用沉浸式布局
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // setContent = Compose UI 的入口
        setContent {

            // 应用 App 的全局主题（颜色、字体、形状等）
            ToutiaoAndroidTheme {

                // App 的导航容器，负责页面切换（首页、详情页等）
                AppNavigator()
            }
        }
    }
}
