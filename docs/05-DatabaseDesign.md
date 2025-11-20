# **05. 数据库设计（Database Design）**

本章节介绍本系统在数据持久化层(Data Persistence Layer)的设计，包括实体建模(Entity Modeling)、表结构设计(Table Schema)、索引设计(Indexing Strategy)、数据模型演进(Evolution)以及扩展性(Extensibility)。
 该数据库作为“今日头条式推荐流 Demo”的核心组成部分，支撑多卡片渲染、多媒体管理、推荐排序、内容详情展示、互动统计、分页缓存等关键功能。

为了实现高扩展性、清晰的领域边界和专业工程实践，本项目采用 **主表 + 内容表 + 媒体表 + 作者表 + 统计表 + 推荐条目表** 的分层结构。



# **5.1 数据模型演进（Model Evolution）**



为从基础 Demo 逐渐演化为类今日头条内容平台，本系统的数据模型经过了三个阶段：

## **阶段一：单表模型（Naive Model，适合入门 Demo）**

初始阶段只使用一张表 `news` 存储全部信息：

| 字段          | 含义      |
| ------------- | --------- |
| title         | 标题      |
| content       | HTML 正文 |
| image_url     | 单图      |
| video_url     | 视频地址  |
| author        | 作者名    |
| publish_time  | 发布时间  |
| like_count    | 点赞      |
| comment_count | 评论      |

### 单表的问题：

1. **正文字段巨大** → 首页加载非常慢（I/O 放大）
2. **统计字段频繁更新** → 热点行锁冲突概率极高
3. **无法扩展多图、三图、横滑图、视频组**
4. **无法区分不同卡片类型（text/image/video）**
5. **无法支持推荐流排序（没有 weight）**
6. **无法支持 cursor 分页（字段不足）**

## **阶段二：工程化拆表（Engineering Model）**

为解决性能低、耦合高、不可扩展的问题，对模型做首次拆分：

| 表名         | 用途                     |
| ------------ | ------------------------ |
| news         | 存新闻基础元数据（轻量） |
| news_content | 存正文大字段             |
| media        | 支持多图/视频            |
| stats        | 互动统计独立管理         |
| author       | 作者信息独立化           |

### ✔ 带来的改进：

- 首页查询不再读取正文 → 性能大幅提升
- 支持多图片、多视频、多图组卡片
- 统计和内容解耦 → 不会互相锁冲突
- 多媒体结构可扩展
- 内容与展示结构分离，更利于前后端开发

## **阶段三：加入推荐系统实体（FeedItem Model）**

为支持推荐流特性，引入 **feed_item 表**，支持：

- 多推荐频道（scene）
- 推荐模型(model_id)
- 排序权重(weight)
- cursor 分页(publish_time + seq_id)
- 多种展示卡片类型(display_type)

### ✔ 最终实现能力：

- 类今日头条的推荐流首页
- 稳定的 cursor 分页（不会乱序）
- 推荐层与内容层完全解耦
- 支持未来扩展“广告、直播、游戏”卡片



# **5.2 需求分析（Requirement Analysis）**

根据系统业务，本数据库需支持以下实体：

## **（1）新闻内容（News）**

- 标题、摘要
- 发布时间、来源
- 内容类型（纯文字/图文/视频/多图）

------

## **（2）新闻正文（NewsContent）**

- 富文本 HTML
- JSON 渲染结构
- 字数统计

------

## **（3）多媒体（Media）**

支持：

- 大图
- 单图
- 右图
- 三图（图片组）
- 横滑图/横滑视频
- 视频 + 封面

------

## **（4）作者（Author）**

- 昵称
- 头像
- 认证标签
- 简介

------

## **（5）互动统计（Stats）**

- 点赞
- 评论
- 收藏
- 播放量
- 分享数
- 数据版本 version（防止覆盖）

------

## **（6）推荐流条目（FeedItem）**

- 权重排序（weight）
- 推荐策略（model_id）
- 推荐频道（scene）
- 卡片类型（display_type）
- cursor 分页字段（publish_time + seq_id）



# **5.3 逻辑数据模型（Logical Data Model, LDM）**

```
News —— 1:N —— Media
  │
  ├—— 1:1 —— NewsContent
  │
  ├—— N:1 —— Author
  │
  ├—— 1:1 —— Stats
  │
  └—— 1:N —— FeedItem
```

说明：

- **News 是核心内容实体**
- **Media 负责多媒体扩展**
- **Content 负责正文，不占用主表 I/O**
- **Stats 独立维护高频更新字段**
- **FeedItem 负责推荐排序与分页**

# 

# **5.4 物理数据模型（Physical Data Model, PDM）**

以下为最终数据库表结构（完整版）：

------

## **表 1：news（新闻主表）**

| 字段         | 类型                                               | 说明                        |
| ------------ | -------------------------------------------------- | --------------------------- |
| id           | bigserial PK                                       | 新闻 ID                     |
| title        | varchar(255)                                       | 新闻标题                    |
| summary      | varchar(255)                                       | 摘要                        |
| news_type    | enum(text, image, video, multi_image, multi_video) | 展示类型                    |
| author_id    | bigint                                             | 作者 ID                     |
| source       | varchar(100)                                       | 新闻来源                    |
| category     | varchar(50)                                        | 分类（local/hot/recommend） |
| publish_time | timestamp                                          | 发布时间                    |
| status       | smallint                                           | 0 草稿，1 已发布            |
| created_at   | timestamp                                          | 创建时间                    |
| updated_at   | timestamp                                          | 更新时间                    |

------

## **表 2：news_content（新闻正文表）**

| 字段         | 类型      | 说明          |
| ------------ | --------- | ------------- |
| news_id      | bigint PK | 对应新闻 ID   |
| content_html | text      | HTML 正文     |
| content_json | jsonb     | JSON 渲染结构 |
| word_count   | int       | 字数统计      |

------

## **表 3：media（多媒体表）**

| 字段        | 类型               | 说明                     |
| ----------- | ------------------ | ------------------------ |
| id          | bigserial PK       | 媒体 ID                  |
| news_id     | bigint             | 对应新闻 ID              |
| group_id    | bigint             | 媒体分组 ID（三图/横滑） |
| media_type  | enum(image, video) | 图片 or 视频             |
| url         | varchar            | 资源地址                 |
| cover_url   | varchar            | 视频封面                 |
| duration    | int                | 视频时长（秒）           |
| width       | int                | 宽度                     |
| height      | int                | 高度                     |
| order_index | int                | 组内排序                 |
| created_at  | timestamp          | 创建时间                 |

------

## **表 4：author（作者表）**

| 字段          | 类型         | 说明     |
| ------------- | ------------ | -------- |
| id            | bigserial PK | 作者 ID  |
| name          | varchar(50)  | 昵称     |
| avatar_url    | varchar      | 头像     |
| description   | varchar(255) | 简介     |
| certification | varchar(50)  | 认证信息 |
| created_at    | timestamp    |          |

------

## **表 5：stats（互动统计表）**

| 字段           | 类型      | 说明           |
| -------------- | --------- | -------------- |
| news_id        | bigint PK | 对应新闻 ID    |
| like_count     | int       | 点赞           |
| comment_count  | int       | 评论           |
| favorite_count | int       | 收藏           |
| share_count    | int       | 分享           |
| play_count     | int       | 播放量（视频） |
| version        | int       | 数据版本防覆盖 |
| updated_at     | timestamp | 最近更新时间   |

------

## **表 6：feed_item（推荐流条目表 推荐池）**

| 字段         | 类型                                  | 说明                              |
| ------------ | ------------------------------------- | --------------------------------- |
| id           | bigserial PK                          | 条目 ID                           |
| news_id      | bigint                                | 对应内容 ID                       |
| display_type | enum(text, image, video, multi_video) | 控制卡片渲染逻辑                  |
| weight       | float                                 | 推荐排序权重                      |
| scene        | varchar(30)                           | 频道（recommend/local/social/）   |
| model_id     | varchar(50)                           | 推荐模型/召回源                   |
| publish_time | timestamp                             | cursor 主排序字段                 |
| seq_id       | bigserial                             | cursor 次级排序字段，保证严格顺序 |
| created_at   | timestamp                             | 创建时间                          |



# **5.5 ER 图（Mermaid 完整版）**



```
erDiagram

    NEWS ||--|| NEWS_CONTENT : has
    NEWS ||--|| STATS : has
    NEWS ||--o{ MEDIA : contains
    NEWS }o--|| AUTHOR : written_by
    NEWS ||--o{ FEED_ITEM : appears_in

    NEWS {
        bigint id PK
        varchar title
        varchar summary
        enum news_type
        bigint author_id
        varchar source
        varchar category
        timestamp publish_time
        smallint status
        timestamp created_at
        timestamp updated_at
    }

    NEWS_CONTENT {
        bigint news_id PK
        text content_html
        jsonb content_json
        int word_count
    }

    MEDIA {
        bigint id PK
        bigint news_id FK
        bigint group_id
        enum media_type
        varchar url
        varchar cover_url
        int duration
        int width
        int height
        int order_index
    }

    AUTHOR {
        bigint id PK
        varchar name
        varchar avatar_url
        varchar description
        varchar certification
    }

    STATS {
        bigint news_id PK
        int like_count
        int comment_count
        int favorite_count
        int share_count
        int play_count
        int version
    }

    FEED_ITEM {
        bigint id PK
        bigint news_id FK
        enum display_type
        float weight
        varchar scene
        varchar model_id
        timestamp publish_time
        bigint seq_id
        timestamp created_at
    }
```

# 

# **5.6 索引设计（Index Design）**



为提升查询性能、推荐流分页能力，本项目设计如下索引：

------

## **（1）feed_item 分页索引**

```
CREATE INDEX idx_feed_scene_cursor
ON feed_item (scene, publish_time DESC, seq_id DESC);
```

用途：

- 支持稳定 cursor 分页
- 支持不同频道筛选
- 解决 publish_time 冲突

------

## **（2）media 媒体关联索引**

```
CREATE INDEX idx_media_news
ON media (news_id);
```

用于三图、多图组、横滑图一次性查询。

------

## **（3）news 发布时间索引**

```
CREATE INDEX idx_news_publish_time
ON news (publish_time DESC);
```

适用于频道页/详情页加载。

------

## **（4）stats 主键索引**

主键即天然索引，高频写入表保持单行更新性能高。

# 

# **5.7 扩展性设计（Extensibility Notes）**



### 多卡片类型扩展

仅需扩展以下字段：

```
news_type
display_type
media_type
```

即可支持：

- 广告卡片
- 电商商品卡片
- 直播卡片
- 游戏卡片

------

### 推荐策略扩展

feed_item 拆表使系统可支持：

- 不同召回源（recall）
- 不同排序模型（ranker）
- 多频道（scene）
- AB 实验
- 曝光/点击日志追踪

------

### 评论系统扩展

新增：

- comment
- comment_reply

不会影响现有结构。

------

### 用户体系 / 个性化推荐扩展

通过：

- user
- user_profile
- user_behavior_log

可进一步接入：

- 个性化推荐
- 协同过滤
- 视频内容推荐模型