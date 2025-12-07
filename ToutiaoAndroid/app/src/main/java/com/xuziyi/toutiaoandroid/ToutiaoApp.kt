package com.xuziyi.toutiaoandroid

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.VideoFrameDecoder

// 定义一个类 ToutiaoApp，它继承 Application，并且实现了 ImageLoaderFactory 接口
class ToutiaoApp : Application(), ImageLoaderFactory {

    // 实现接口要求的方法，用来创建一个全局唯一的 ImageLoader
    override fun newImageLoader(): ImageLoader {
        // 用 Builder 模式配置一个 ImageLoader
        return ImageLoader.Builder(this) // this = Application Context
            .crossfade(true)             // 开启淡入动画
            .components {                // 添加组件（视频帧解码器）
                add(VideoFrameDecoder.Factory())
            }
            .build()                     // 构建 ImageLoader 对象
    }
}
