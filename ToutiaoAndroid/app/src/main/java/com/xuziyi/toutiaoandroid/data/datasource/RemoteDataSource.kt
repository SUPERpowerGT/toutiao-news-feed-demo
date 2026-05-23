package com.xuziyi.toutiaoandroid.data.datasource

import com.xuziyi.toutiaoandroid.common.extensions.ApiDataNullException
import com.xuziyi.toutiaoandroid.common.extensions.ApiException
import com.xuziyi.toutiaoandroid.data.remote.api.FeedApiService
import com.xuziyi.toutiaoandroid.data.remote.dto.FeedResponseDto
/**
 * RemoteDataSource
 * 负责发起实际的网络请求，并处理 ApiResponse<T> 的解包和状态检查。
 */
class RemoteDataSource(private val api: FeedApiService) {

    // 核心逻辑：封装一个私有函数来处理解包和异常
    private suspend fun getAndUnwrapFeed(
        scene: String,
        cursor: Long?,
        refreshTime: Long?
    ): FeedResponseDto {
        // api.getFeed(...) 现在返回 ApiResponse<FeedResponseDto>
        val apiResponse = api.getFeed(scene = scene, cursor = cursor, refreshTime = refreshTime)

        //检查业务状态码 (假设 code == 0 为成功)
        if (apiResponse.code != 0) {
            val errorMessage = apiResponse.message ?: "API 请求失败，无具体消息"
            throw ApiException(apiResponse.code, errorMessage)
        }

        //检查 data 字段是否为空
        val feedData = apiResponse.data
        if (feedData == null) {
            throw ApiDataNullException()
        }

        //成功，返回解包后的业务 DTO
        return feedData
    }

    // 调用封装的私有函数
    suspend fun loadInitialFeed(scene: String): FeedResponseDto {
        return getAndUnwrapFeed(scene = scene, cursor = null, refreshTime = null)
    }

    // 调用封装的私有函数
    suspend fun refreshFeed(scene: String, latestPublishTime: Long): FeedResponseDto {
        return getAndUnwrapFeed(scene = scene, cursor = null, refreshTime = latestPublishTime)
    }

    // 调用封装的私有函数
    suspend fun loadMore(scene: String, cursor: Long): FeedResponseDto {
        return getAndUnwrapFeed(scene = scene, cursor = cursor, refreshTime = null)
    }
}
