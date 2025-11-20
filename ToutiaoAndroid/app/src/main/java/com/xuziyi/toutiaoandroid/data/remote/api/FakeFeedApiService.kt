package com.xuziyi.toutiaoandroid.data.remote.api

import com.xuziyi.toutiaoandroid.data.remote.dto.*

class FakeFeedApiService : FeedApiService {

    override suspend fun getFeed(cursor: String?, refreshTime: Long?): FeedResponseDto {
        return FeedResponseDto(
            items = listOf(
                FeedItemDto(
                    id = 1,
                    title = "深圳今日晴天，气温 26℃",
                    summary = "天气晴朗，适合户外运动。",
                    newsType = "text",
                    media = emptyList(),
                    author = AuthorDto(
                        id = 100,
                        name = "深圳发布",
                        avatarUrl = null,
                        certification = "red_v"
                    ),
                    stats = StatsDto(
                        likeCount = 128,
                        commentCount = 56
                    ),
                    publishTime = System.currentTimeMillis() / 1000
                ),
                FeedItemDto(
                    id = 2,
                    title = "年轻人买房的 10 个思考",
                    summary = "你真的需要一套房吗？",
                    newsType = "image",
                    media = listOf(
                        MediaDto(
                            mediaType = "image",
                            url = "https://picsum.photos/300/200",
                            coverUrl = null,
                            duration = null,
                            width = 300,
                            height = 200
                        )
                    ),
                    author = AuthorDto(9, "南方都市报", "", "yellow_v"),
                    stats = StatsDto(88, 22),
                    publishTime = System.currentTimeMillis() / 1000 - 5000
                ),
                FeedItemDto(
                    id = 3,
                    title = "华为发布 Mate70，性能大提升！",
                    summary = null,
                    newsType = "video",
                    media = listOf(
                        MediaDto(
                            mediaType = "video",
                            url = "https://samplelib.com/lib/preview/mp4/sample-5s.mp4",
                            coverUrl = "https://picsum.photos/400/300",
                            duration = 5,
                            width = 400,
                            height = 300
                        )
                    ),
                    author = AuthorDto(20, "科技快讯", "", null),
                    stats = StatsDto(999, 300),
                    publishTime = System.currentTimeMillis() / 1000 - 8000
                )
            ),
            nextCursor = "123",
            hasMore = true,
            latestPublishTime = System.currentTimeMillis() / 1000
        )
    }

    override suspend fun getNewsDetail(id: Long): NewsDetailDto {
        return NewsDetailDto(
            id = id,
            title = "Mock 详情标题 - $id",
            contentHtml = "<p>Hello 这里是 Mock HTML 内容</p>",
            contentJson = null,
            newsType = "text",
            media = emptyList(),
            author = AuthorDto(100, "Mock 作者", "", ""),
            stats = StatsDto(100, 20),
            publishTime = System.currentTimeMillis() / 1000
        )
    }

    override suspend fun getComments(id: Long, cursor: String?): List<CommentDto> {
        return listOf(
            CommentDto(
                id = 1,
                userName = "小明",
                userAvatar = null,
                content = "这是 mock 评论内容",
                publishTime = System.currentTimeMillis() / 1000,
                likeCount = 3
            )
        )
    }
}
