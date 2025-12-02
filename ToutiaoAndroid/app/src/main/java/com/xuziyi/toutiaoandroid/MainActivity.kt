package com.xuziyi.toutiaoandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.decode.VideoFrameDecoder
import com.xuziyi.toutiaoandroid.ui.navigation.AppNavigator
import com.xuziyi.toutiaoandroid.ui.theme.ToutiaoAndroidTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            val context = LocalContext.current

            val imageLoader = ImageLoader.Builder(context)
                .crossfade(true)
                .apply {
                    // Coil 2.x 用 this.components
                    components {
                        add(VideoFrameDecoder.Factory())
                    }
                }
                .build()

            CompositionLocalProvider(
                LocalImageLoader provides imageLoader
            ) {
                ToutiaoAndroidTheme {
                    AppNavigator()
                }
            }
        }
    }
}
