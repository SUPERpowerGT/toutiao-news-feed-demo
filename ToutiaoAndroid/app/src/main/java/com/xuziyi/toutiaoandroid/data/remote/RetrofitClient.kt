package com.xuziyi.toutiaoandroid.data.remote

import com.xuziyi.toutiaoandroid.data.remote.api.FakeFeedApiService
import com.xuziyi.toutiaoandroid.data.remote.api.FeedApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit 客户端（现在先不用真实调用）
 */
object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    // mock 阶段暂时不用网络
    /*
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    */

    // ===== Mock 版本：不通过 retrofit，直接使用 fake =====

    val feedApi: FeedApiService by lazy {
        FakeFeedApiService()   //使用我们稍后写的 Fake API
    }
}
