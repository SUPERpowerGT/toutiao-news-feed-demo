package com.xuziyi.toutiaoandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.xuziyi.toutiaoandroid.ui.feed.FeedScreen
import com.xuziyi.toutiaoandroid.ui.theme.ToutiaoAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToutiaoAndroidTheme {
                FeedScreen()
            }
        }
    }
}
