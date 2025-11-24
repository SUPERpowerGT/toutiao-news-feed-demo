package com.xuziyi.toutiaoandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.xuziyi.toutiaoandroid.ui.navigation.AppNavigator
import com.xuziyi.toutiaoandroid.ui.theme.ToutiaoAndroidTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //允许应用内容扩展到状态栏区域（刘海屏同步bar颜色）
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ToutiaoAndroidTheme {
                AppNavigator()
            }
        }
    }
}
