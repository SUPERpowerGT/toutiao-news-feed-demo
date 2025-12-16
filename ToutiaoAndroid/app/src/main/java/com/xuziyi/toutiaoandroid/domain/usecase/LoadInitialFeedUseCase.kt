package com.xuziyi.toutiaoandroid.domain.usecase

import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import com.xuziyi.toutiaoandroid.domain.repository.FeedRepositoryContract

class LoadInitialFeedUseCase(
    private val repo: FeedRepositoryContract
) {
    //妙妙小工具语法糖，调用函数无需声明方法名称，根据传入参数类型自动判断
    suspend operator fun invoke(): List<FeedItem> {
        return repo.loadInitialFeed()
    }
}
