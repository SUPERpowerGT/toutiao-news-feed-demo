/*
package com.xuziyi.toutiaoandroid.data.remote.api

import com.xuziyi.toutiaoandroid.data.remote.dto.*

class FakeFeedApiService : FeedApiService {

    override suspend fun getFeed(cursor: String?, refreshTime: Long?): FeedResponseDto {
        return FeedResponseDto(
            items = listOf(

                // ========== 1. 纯文本：官方媒体 ==========
                FeedItemDto(
                    id = 1,
                    title = "北京以规划引领超大城市治理的启示",
                    summary = "开创新时代。",
                    contentType = "text",
                    media = emptyList(),
                    author = AuthorDto(
                        id = 100,
                        name = "新华网",
                        avatarUrl = null,
                        certification = "red_v"
                    ),
                    stats = StatsDto(114, 514),
                    publishTime = now(),
                    category = "politics",
                    subCategory = "beijing",
                    tags = listOf("热点", "北京"),
                    city = "beijing",
                    isOfficialMedia = true,
                    isTopOfficial = true,       // ⭐ MOCK：让它进 前5 官方专区
                    source = "新华网",
                    weight = 999f
                ),

                // ========== 2. 文本 + 官方媒体 TOP5 ==========
                FeedItemDto(
                    id = 2,
                    title = "42.195公里，连接了多少“大湾区奇迹？”",
                    summary = "港珠澳大桥！启动",
                    contentType = "text",
                    media = emptyList(),
                    author = AuthorDto(
                        id = 101,
                        name = "新华社",
                        avatarUrl = null,
                        certification = "red_v"
                    ),
                    stats = StatsDto(777, 161),
                    publishTime = now(),
                    category = "finance",
                    city = "shenzhen",
                    isOfficialMedia = true,
                    isTopOfficial = false,        // ⭐ 也作为官方 TOP
                    source = "新华社",
                    weight = 800f
                ),

                // ========== 3. 单图 ==========
                FeedItemDto(
                    id = 3,
                    title = "年轻人买房的 10 个思考",
                    summary = "你真的需要一套房吗？",
                    contentType = "image",
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
                    author = AuthorDto(
                        id = 9,
                        name = "南方都市报",
                        avatarUrl = "https://i.pravatar.cc/60?u=9",
                        certification = "yellow_v"
                    ),
                    stats = StatsDto(88, 22),
                    publishTime = now() - 5000,
                    category = "property",
                    city = "shenzhen",
                    isOfficialMedia = true,
                    isTopOfficial = false,
                    source = "南方都市报",
                    weight = 300f
                ),

                // ========== 4. 视频 ==========
                FeedItemDto(
                    id = 4,
                    title = "华为发布 Mate70，性能大提升！",
                    summary = null,
                    contentType = "video",
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
                    author = AuthorDto(
                        id = 20,
                        name = "央广网",
                        avatarUrl = "",
                        certification = null
                    ),
                    stats = StatsDto(999, 300),
                    publishTime = now() - 8000,
                    category = "tech",
                    city = "shenzhen",
                    isOfficialMedia = true,
                    isTopOfficial = false,
                    source = "央广网",
                    weight = 700f
                )
            ),
            nextCursor = "123",
            hasMore = true,
            latestPublishTime = now()
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
            author = AuthorDto(
                id = 100,
                name = "Mock 作者",
                avatarUrl = "",
                certification = ""
            ),
            stats = StatsDto(100, 20),
            publishTime = now()
        )
    }

    override suspend fun getComments(id: Long, cursor: String?): List<CommentDto> {
        return listOf(
            CommentDto(
                id = 1,
                userName = "小明",
                userAvatar = null,
                content = "这是 mock 评论内容",
                publishTime = now(),
                likeCount = 3
            )
        )
    }

    private fun now() = System.currentTimeMillis() / 1000
}
*/
