package com.xuziyi.toutiaoandroid.data.remote

import com.xuziyi.toutiaoandroid.data.remote.api.FeedApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor // 🎯 1. 导入日志拦截器
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit 客户端
 */
object RetrofitClient {

    // 使用 adb reverse 调试时，可以改为 http://localhost:8080/
    // 模拟器测试url
    private const val BASE_URL = "http://10.0.2.2:8080/"
    //真机测试url
    //private const val BASE_URL = "http://192.168.3.39:8080/"


    private val okHttpClient: OkHttpClient by lazy {
        //创建日志拦截器
        val logging = HttpLoggingInterceptor().apply {
            // 设置日志级别，BODY 会打印请求头、请求体、响应头和响应体
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        OkHttpClient.Builder()
            .addInterceptor(logging) //将日志拦截器添加到 OkHttpClient
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val feedApi: FeedApiService by lazy {
        retrofit.create(FeedApiService::class.java)
    }
}