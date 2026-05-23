# 13. 多频道 Feed 设计

本文档定义首页信息流从“单推荐流”演进到“多频道 Feed”的设计方案，目标是在尽量少破坏现有结构的前提下，支持多个真实频道，同时保持卡片渲染层复用。

------

## 13.1 设计目标

本次设计解决的是“频道”能力，而不是单纯增加几个 Tab 文案。

目标如下：

- 让不同频道返回不同内容集合，而不是同一批数据换名字
- 复用现有 Feed 列表、分页、刷新、卡片渲染能力
- 保持后端接口风格统一，避免一开始拆出过多接口
- 明确“频道”和“卡片类型”是两层不同概念
- 保证 `推荐` 频道现有逻辑保持不变
- 满足产品约束：只有 `推荐` 频道有 `Top5 官方置顶`

非目标：

- 暂不设计用户自定义频道排序
- 暂不设计“关注”关系流
- 暂不设计沉浸式视频流
- 暂不设计热榜专属榜单结构

------

## 13.2 核心概念

### 频道（Scene / Channel）

频道决定“用户看什么内容”。

例如：

- 推荐
- 科技
- 体育
- 财经
- 深圳
- 视频

频道属于数据分发与查询层概念，应由后端负责过滤、排序、分页和刷新边界。

### 卡片类型（Card Type）

卡片类型决定“内容如何展示”。

例如：

- 文本卡
- 单图卡
- 多图卡
- 视频卡
- 官方置顶卡

卡片类型属于前端渲染层概念，应根据内容字段决定，不应用来替代频道。

### 设计原则

- 频道决定“看什么”
- 卡片决定“怎么看”
- 频道和卡片类型是正交维度

------

## 13.3 API 设计方案

采用统一 Feed 接口，加频道参数：

```text
GET /api/v1/feed?scene=recommend
GET /api/v1/feed?scene=tech
GET /api/v1/feed?scene=sports
GET /api/v1/feed?scene=finance
GET /api/v1/feed?scene=shenzhen
GET /api/v1/feed?scene=video
```

保留现有分页和刷新参数：

```text
GET /api/v1/feed?scene=recommend&cursor=xxx&limit=15
GET /api/v1/feed?scene=recommend&refresh_time=xxx&limit=15
```

选择统一接口而不是“每频道一个 API 路径”的原因：

- 当前多个频道本质仍是“普通信息流”
- 返回结构一致
- 分页与刷新逻辑一致
- 对现有前后端改动最小

后续若某些频道与普通 Feed 结构明显不同，再考虑拆独立接口：

- `hot` / 热榜
- `following` / 关注流
- `video-stream` / 沉浸式视频流

------

## 13.4 频道规则

### 推荐频道

`scene = recommend`

规则：

- 保持当前推荐频道逻辑不变
- 保持当前 `Top5 官方置顶 + 普通推荐流` 结构
- 保持当前推荐页的分页、刷新和卡片组织方式
- 前端继续展示“官方置顶区 + 普通流混排区”

这里的多频道改造，不是重写推荐频道，而是：

- 给现有推荐频道补上显式 `scene = recommend` 语义
- 在不破坏现有推荐页体验的前提下纳入统一接口体系

### 其他频道

例如：

- `tech`
- `sports`
- `finance`
- `shenzhen`
- `video`

规则：

- 不返回 `Top5`
- 不复用推荐页的 Top 区结构
- 仅返回当前频道对应的普通内容卡片流
- 前端不展示官方置顶区

这是当前版本最重要的产品约束：

- 推荐频道逻辑保持现状
- 只有 `推荐` 频道有 `Top5`
- 其他频道全部没有

------

## 13.5 响应结构

建议统一响应结构，避免前端为不同频道维护多套解析逻辑：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "scene": "recommend",
    "top_items": [],
    "items": [],
    "next_cursor": 1715950000,
    "has_more": true,
    "latest_publish_time": 1715959999
  }
}
```

约定：

- `recommend` 频道：`top_items` 可能非空
- 非 `recommend` 频道：`top_items` 固定为空数组

这样做的好处：

- 前端只维护一套 DTO
- 频道差异体现在字段值，而不是接口结构
- 后续新增频道不会继续放大前端分支判断

------

## 13.6 当前版本的频道映射

在不立刻修改数据库结构的前提下，第一阶段先使用现有字段映射频道：

- `recommend`：默认全量推荐流
- `tech`：`category = 科技`
- `sports`：`category = 体育`
- `finance`：`category = 财经`
- `shenzhen`：`city = 深圳`
- `video`：`content_type = video`

这样可以快速验证“多频道”体验是否成立，同时最大限度复用当前 seed 数据。

------

## 13.7 数据模型建议

### 第一阶段

暂不强制新增数据库字段，优先利用现有内容属性：

- `content_type`
- `category`
- `sub_category`
- `tags`
- `city`

优点：

- 改动小
- 可以快速落地
- 适合当前 Demo 阶段

缺点：

- 频道归属依赖规则推导
- 一条内容可能同时满足多个频道条件
- 后续扩展会逐步增加维护成本

### 第二阶段

在多频道方案跑通后，可考虑给 `feed_item` 增加正式字段：

```text
scene / channel_key
```

目的：

- 显式表达内容属于哪个频道
- 降低靠 `category/city/content_type` 推导的复杂度
- 让 seed 数据、过滤规则、缓存边界更稳定

结论：

- 当前先用规则映射
- 后续再评估是否正式落库 `scene`

------

## 13.8 后端改造点

### API 层

在 `GET /api/v1/feed` 中增加 `scene` 参数解析。

默认值建议：

- 若客户端未传 `scene`，默认按 `recommend` 处理

### Application 层

`FeedService` 需要将 `scene` 作为一等输入参数，参与以下逻辑：

- 首次加载
- 下拉刷新
- 加载更多

即：

- cursor 属于某个频道
- latestPublishTime 属于某个频道
- refresh 也必须只针对当前频道

### Repository 层

Repository 按 `scene` 生成不同查询条件。

推荐频道：

- 查询 `Top5 官方置顶`
- 查询普通推荐流

其他频道：

- 不查 `Top5`
- 仅查普通流

因此 Repository 的实现建议是：

- 保持统一入口
- 在内部根据 `scene == recommend` 决定是否加载 top 区

不建议为每个频道复制一套 Repository 方法。

------

## 13.9 前端改造点

### Tab 模型

当前 `FeedTabItem` 只有标题，后续需要补充 `sceneKey`：

```kotlin
data class FeedTabItem(
    val id: Long,
    val title: String,
    val sceneKey: String,
    val showDot: Boolean = false
)
```

### ViewModel 状态隔离

当前 Feed 状态基本是单份状态，后续多频道必须按频道隔离：

- 每个频道独立保存列表数据
- 每个频道独立保存 `nextCursor`
- 每个频道独立保存 `latestPublishTime`
- 每个频道独立保存 `hasMore`
- 每个频道独立保存滚动位置与刷新状态

否则会出现：

- 切换频道数据串台
- `loadMore` 使用了其他频道的 cursor
- 刷新把新内容合并到错误频道
- 本地缓存互相污染

### UI 展示规则

- `recommend`：显示 `OfficialTopCard + FeedList`
- 非 `recommend`：只显示普通 FeedList

前端不应通过“是否恰好拿到官方媒体内容”来猜测频道，而应直接依据 `sceneKey` 决定布局。

------

## 13.10 缓存策略建议

当前本地缓存如果仍使用单份 Feed 数据，会与多频道设计冲突。

因此建议：

- 本地缓存按频道隔离
- 每条缓存数据都带 `sceneKey`

第一阶段即使不做复杂缓存结构，也应至少保证：

- `recommend` 的缓存不会覆盖 `tech`
- `video` 的加载更多不会污染 `shenzhen`

如果短期不调整 Room 结构，则应先降低缓存参与度，避免状态正确性问题。

------

## 13.11 第一批落地频道建议

不建议一开始把所有频道都做完，建议先做 3 个：

- `recommend`
- `video`
- `shenzhen`

原因：

- `recommend` 是基线频道
- `video` 能快速验证“内容类型驱动频道”
- `shenzhen` 能快速验证“地域驱动频道”

三者跑通后，再逐步补：

- `sports`
- `finance`
- `tech`

------

## 13.12 演进路线

### 阶段一：统一接口 + 规则映射

- `/api/v1/feed?scene=...`
- 仅 `recommend` 有 `Top5`
- 其他频道走普通流
- 前端按频道隔离状态

### 阶段二：稳定多频道缓存

- 本地缓存引入 `sceneKey`
- 每频道独立的分页和刷新边界

### 阶段三：频道能力增强

- 用户可编辑频道顺序
- 更丰富的频道入口
- 更细粒度的频道专属排序

### 阶段四：专属频道拆分

当频道形态与普通 Feed 明显不同，再拆专属接口：

- 热榜
- 关注
- 沉浸式视频

------

## 13.13 小结

本方案的核心不是“增加多个 Tab”，而是让“频道”成为真实的数据查询维度。

在当前 Demo 阶段，推荐采用：

- 统一 Feed API + `scene` 参数
- 利用现有字段做第一阶段频道映射
- 只有 `recommend` 频道保留 `Top5 官方置顶`
- 其他频道只返回普通流

这样既能快速落地，也能为后续真正的多频道产品形态保留扩展空间。

------

## 13.14 现有代码支持度检查

本节用于回答一个实际问题：

- 目前代码是否已经支持这套设计
- 哪些层已经有基础
- 哪些层仍需要改造

结论先行：

- 数据库层“部分支持”
- 后端接口与查询层“当前不支持”
- Android 前端 UI 层“有 Tab 外壳，但状态和数据层当前不支持”

### 数据库层

当前数据库对多频道设计是“有基础但未用起来”。

已经具备的条件：

- `feed_item` 表已有 `scene` 字段
- `feed_item` 表已有 `category`
- `feed_item` 表已有 `sub_category`
- `feed_item` 表已有 `content_type`
- `feed_item` 表已有 `city`
- `feed_item` 表已有 `is_top_official`
- `feed_item` 表已有 `seq_id`

这意味着：

- 数据表结构并不阻止我们做多频道
- 推荐频道与普通频道的查询条件可以落在现有表上
- 后续若要引入正式频道键，优先可以复用已有 `scene`

当前不足：

- 初始化 SQL 中未看到针对 `scene + publish_time + seq_id` 的索引定义
- `scene` 虽然存在，但当前 seed 数据没有按真实频道写入

因此数据库结论是：

- 表结构支持
- 索引需要补强
- 数据内容当前还不支持真实多频道

### Seed 数据层

当前 seed 逻辑是本设计的一个关键短板。

检查结果：

- `feed_item` 写入时 `scene` 被固定写成 `"home"`
- `display_type` 被固定写成 `"recommend"`
- `category/city/content_type` 虽然是多样的
- 但没有将文章显式归属到 `recommend/tech/sports/...` 等频道

这意味着：

- 现有数据可以用于“规则映射型频道”
- 但不能直接用于“基于 scene 字段的真实频道过滤”

换句话说：

- 如果第一阶段按 `category/city/content_type` 做频道，当前 seed 基本够用
- 如果第一阶段就想完全依赖 `scene` 查询，当前 seed 还不够

### 后端 API 层

当前后端 API 还不支持多频道设计。

现状：

- `GET /api/v1/feed` 当前只解析 `cursor`
- 当前只解析 `refresh_time`
- 当前只解析 `limit`
- 当前没有解析 `scene`

这意味着：

- 客户端目前无法通过接口参数切换频道
- 后端也无法基于频道做不同查询分支

因此 API 层需要最少改造：

- 增加 `scene` 参数解析
- 给 `scene` 提供默认值 `recommend`
- 将 `scene` 透传到 service 层

### 后端 Application 层

当前 `FeedService` 仍是单频道思维。

现状：

- `GetFeed()` 目前只接收 `cursor`
- `GetFeed()` 目前只接收 `refreshTime`
- `GetFeed()` 目前只接收 `limit`
- service 不知道当前请求属于哪个频道

结果：

- 首次加载无法按频道分流
- 刷新无法保证“只刷新当前频道”
- 加载更多无法保证“当前频道的 cursor 只查当前频道”

因此 Application 层当前不支持多频道，需要把 `scene` 升级成一等参数。

### 后端 Repository 层

当前 Repository 是本设计中改造量最大的后端层。

现状：

- `ListInitial()` 没有 `scene`
- `ListFeed()` 没有 `scene`
- `ListNewer()` 没有 `scene`
- SQL 查询没有按 `scene` 过滤
- SQL 查询固定使用 `is_top_official = TRUE/FALSE` 分两段查

目前行为等价于：

- 所有频道都共享同一套推荐流
- 所有刷新都默认包含 Top 区逻辑
- 所有加载更多都不区分频道

这与设计目标存在三个直接冲突：

- 不能按 `scene` 查询不同内容
- 不能实现“只有推荐频道有 Top5”
- 不能保证推荐频道逻辑完全保持现状
- 不能给不同频道维护各自分页边界

因此 Repository 结论是：

- 当前查询能力不支持设计
- 但已有字段足够支持改造

### Android 网络请求层

当前 Android 端的网络协议层还没有频道参数。

现状：

- `FeedApiService.getFeed()` 没有 `scene` query
- `RemoteDataSource` 的 `loadInitialFeed/refreshFeed/loadMore` 都没有 `scene`

这意味着：

- 即使前端 UI 有多个 tab
- 也无法真正请求不同频道的数据

### Android DTO 层

当前 Android 的 Feed DTO 也还没准备好“推荐频道独有 Top5”。

现状：

- `FeedResponseDto` 只有 `items`
- 没有 `top_items`
- 没有 `scene`

这意味着：

- 当前 DTO 仍然默认“后端只返回一份列表”
- 与本设计中的统一响应结构还不一致

### Android 状态层

当前前端状态管理对多频道不友好。

现状：

- `FeedViewModel` 维护的是单份 `FeedUiState`
- `FeedScreen` 虽然有多个 tab
- 但所有 tab 共享同一个 ViewModel 数据状态

会产生的问题：

- 切 tab 后数据会复用上一频道
- `nextCursor` 会串台
- `latestPublishTime` 会串台
- `loadMore` 会对错误频道继续翻页
- `refresh` 会把新数据并入错误频道

因此前端状态层当前不支持多频道设计。

### Android 本地缓存层

当前 Room 缓存是本设计中前端侧最大的结构性阻塞点之一。

现状：

- `feed_items` 本地表没有 `sceneKey`
- DAO 查询是 `SELECT * FROM feed_items ORDER BY publishTime DESC`
- `LocalDataSource.getAllFeedItems()` 读取的是“全量唯一列表”
- `saveFeedItems()` 写入时也没有区分频道

这意味着：

- 当前缓存是单频道缓存
- 多频道情况下会互相覆盖或混存

因此缓存层结论是：

- 现状不支持多频道隔离
- 必须增加本地 `sceneKey`，或在第一阶段弱化本地缓存

------

## 13.15 支持度总结表

| 层级 | 当前是否支持 | 说明 |
| --- | --- | --- |
| 数据库表结构 | 部分支持 | 已有 `scene/category/content_type/city/is_top_official/seq_id` |
| 数据库索引 | 不足 | 缺少面向 `scene + publish_time + seq_id` 的明确索引 |
| Seed 数据 | 部分支持 | 内容属性丰富，但 `scene` 仍固定写死 |
| 后端 API | 不支持 | 尚未接收 `scene` 参数 |
| 后端 Service | 不支持 | 仍按单频道逻辑组织 |
| 后端 Repository | 不支持 | SQL 没有按频道过滤，Top 逻辑对所有请求生效 |
| Android 网络层 | 不支持 | 请求没有 `scene` |
| Android DTO | 不支持 | 响应没有 `scene/top_items` |
| Android ViewModel 状态 | 不支持 | 只有单份 Feed 状态 |
| Android 本地缓存 | 不支持 | 没有 `sceneKey`，会串频道 |

------

## 13.16 针对当前代码的设计修正

基于现状检查，本设计可以继续成立，但需要补充三个务实约束。

### 约束一：第一阶段先使用规则型频道

因为当前 seed 数据的 `scene` 固定为 `"home"`，所以第一阶段不应强依赖数据库中的 `scene` 值来分频道。

第一阶段建议：

- `recommend`：全量流
- `tech`：`category = 科技`
- `sports`：`category = 体育`
- `finance`：`category = 财经`
- `shenzhen`：`city = 深圳`
- `video`：`content_type = video`

这样可以先把产品形态跑通。

### 约束二：只有推荐频道走 Top 区双查询

当前 Repository 的初始加载与刷新都带有“Top5 + Normal15”结构。

这必须改成：

- `scene == recommend` 时：保持现有推荐频道逻辑不变，继续查 `top_items + items`
- 其他频道：只查 `items`

否则就会违反产品规则。

### 约束三：缓存隔离必须早于多频道正式上线

如果不先处理缓存隔离，即使后端先支持了 `scene`，客户端也会在切换频道后出现明显串台问题。

因此前端实施顺序上，缓存与状态隔离不能拖到太后面。

------

## 13.17 数据库改造建议

当前数据库不需要大改表结构，但建议补两类增强。

### 建议一：补索引

建议新增：

- `feed_item(scene, publish_time DESC, seq_id DESC)`
- `feed_item(category, publish_time DESC, seq_id DESC)`
- `feed_item(city, publish_time DESC, seq_id DESC)` 或按实际热点场景选择
- `feed_item(content_type, publish_time DESC, seq_id DESC)` 或按实际热点场景选择

说明：

- 若第一阶段主要靠规则映射频道，则 `category/city/content_type` 过滤会频繁使用
- 若第二阶段转为正式 `scene` 驱动，则 `scene` 索引会更关键

### 建议二：明确 `scene` 的长期语义

当前表中已经有 `scene` 字段，但 seed 写的是 `"home"`。

建议长期收敛为：

- `scene` 表示正式频道键
- 例如 `recommend / tech / sports / finance / shenzhen / video`

如果未来还需要表达“页面来源”或“入口位置”，应使用其他字段，不要继续复用 `scene`。

------

## 13.18 推荐实施顺序

结合当前代码状态，建议按下面顺序实施，而不是同时大改所有层。

1. 后端 API / Service / Repository 增加 `scene`
2. 先实现“只有 recommend 有 Top5”
3. 第一阶段频道先按 `category/city/content_type` 规则过滤
4. Android 网络层和 DTO 接上 `scene/top_items`
5. Android ViewModel 改为按频道隔离状态
6. Room 缓存增加 `sceneKey`
7. 再决定第二阶段是否让数据库 `scene` 成为正式频道字段

这个顺序的目的：

- 先打通接口和业务语义
- 再解决客户端状态正确性
- 最后再决定是否升级为更强的数据模型

------

## 13.19 开发任务拆解

本节将设计方案拆成可以直接执行的开发任务，按“数据库 / 后端 / Android 前端”三层组织，并尽量对应到当前仓库中的实际文件。

### 第一阶段目标

第一阶段不是一次性完成所有频道，而是完成下面这件事：

- `recommend`
- `video`
- `shenzhen`

三类频道跑通，并满足：

- 统一接口 `GET /api/v1/feed?scene=...`
- `recommend` 继续保持当前逻辑
- 只有 `recommend` 有 `Top5`
- 其他频道不返回 `Top5`
- 前端切频道不会串数据

------

## 13.20 数据库任务

### D1. 补频道查询相关索引

目标：

- 提升按频道分页、刷新查询的性能

建议修改文件：

- [`docker/db/init/01-schema.sql`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/docker/db/init/01-schema.sql:1)

建议新增索引：

```sql
CREATE INDEX IF NOT EXISTS idx_feed_item_scene_publish_seq
ON feed_item(scene, publish_time DESC, seq_id DESC);

CREATE INDEX IF NOT EXISTS idx_feed_item_category_publish_seq
ON feed_item(category, publish_time DESC, seq_id DESC);

CREATE INDEX IF NOT EXISTS idx_feed_item_city_publish_seq
ON feed_item(city, publish_time DESC, seq_id DESC);

CREATE INDEX IF NOT EXISTS idx_feed_item_content_type_publish_seq
ON feed_item(content_type, publish_time DESC, seq_id DESC);
```

状态：

- 必做

### D2. 明确第一阶段不改表结构

目标：

- 第一阶段不新增表
- 不强制新增字段
- 先基于现有字段完成多频道规则映射

涉及文件：

- 文档即可，无需立即改代码

状态：

- 已确认

### D3. 第二阶段再决定是否正式启用 `scene` 为频道键

目标：

- 当第一阶段产品形态稳定后
- 再决定 seed 和查询是否全面切换到 `scene`

涉及文件：

- [`docker/db/init/01-schema.sql`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/docker/db/init/01-schema.sql:1)
- [`backend/seed/seed.go`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/backend/seed/seed.go:1)

状态：

- 暂缓

------

## 13.21 后端任务

### B1. API 增加 `scene` 参数

目标：

- 让客户端能显式请求频道

建议修改文件：

- [`backend/api/feed_handler.go`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/backend/api/feed_handler.go:1)

具体改动：

- 解析 `scene`
- 默认值设为 `recommend`
- 透传给 `FeedService.GetFeed()`

状态：

- 必做

### B2. Service 层把 `scene` 升级成一等参数

目标：

- 首次加载、刷新、加载更多全部按频道执行

建议修改文件：

- [`backend/application/feed_service.go`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/backend/application/feed_service.go:1)

具体改动：

- `GetFeed()` 新增 `scene string`
- 初始加载根据 `scene` 决定是否加载 `Top5`
- 刷新逻辑根据 `scene` 选择查询策略
- 分页逻辑根据 `scene` 约束 cursor 查询范围

状态：

- 必做

### B3. Repository 接口增加 `scene`

目标：

- 从领域边界上明确“频道是查询维度”

建议修改文件：

- [`backend/domain/feed_item_repository.go`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/backend/domain/feed_item_repository.go:1)

建议接口方向：

```go
ListInitial(ctx context.Context, scene string) ([]FeedItem, []FeedItem, *int64, int64, error)
ListFeed(ctx context.Context, scene string, cursor *int64, limit int) ([]FeedItem, *int64, int64, error)
ListNewer(ctx context.Context, scene string, refreshTime int64) ([]FeedItem, []FeedItem, error)
```

说明：

- 推荐频道需要 `topItems + items`
- 普通频道只需要 `items`
- 也可以通过统一 DTO 包装，而不是直接返回两段数组

状态：

- 必做

### B4. Repository SQL 支持频道过滤

目标：

- 真正让不同频道返回不同内容

建议修改文件：

- [`backend/infrastructure/feed_item_repository_pg.go`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/backend/infrastructure/feed_item_repository_pg.go:1)

具体改动：

- `recommend`：
  - 不重写现有推荐逻辑
  - 继续保留当前 `top query + normal query`
  - 普通流继续作为现有推荐流
- `video`：
  - `WHERE f.content_type = 'video'`
- `shenzhen`：
  - `WHERE f.city = '深圳'`
- 第二批频道：
  - `tech -> WHERE f.category = '科技'`
  - `sports -> WHERE f.category = '体育'`
  - `finance -> WHERE f.category = '财经'`

状态：

- 必做

### B5. 只允许推荐频道返回 Top5

目标：

- 严格满足产品规则，并保证推荐频道行为不变

建议修改文件：

- [`backend/infrastructure/feed_item_repository_pg.go`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/backend/infrastructure/feed_item_repository_pg.go:1)
- [`backend/application/feed_service.go`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/backend/application/feed_service.go:1)

具体改动：

- `scene != recommend` 时
  - 不执行 Top 查询
  - 返回 `top_items = []`
- 刷新时也遵循相同规则
- `scene == recommend` 时
  - 保持当前推荐频道的 Top5 与普通流逻辑
  - 不因为多频道改造改变推荐页已有行为

状态：

- 必做

### B6. 调整响应 DTO

目标：

- 后端响应和设计文档对齐

建议修改文件：

- [`backend/application/feed_service.go`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/backend/application/feed_service.go:1)

建议结构：

```go
type FeedResponse struct {
    Scene             string            `json:"scene"`
    TopItems          []domain.FeedItem `json:"top_items"`
    Items             []domain.FeedItem `json:"items"`
    NextCursor        *int64            `json:"next_cursor"`
    HasMore           bool              `json:"has_more"`
    LatestPublishTime int64             `json:"latest_publish_time"`
}
```

状态：

- 必做

### B7. Seed 数据保持“规则映射可用”

目标：

- 确保 `video/shenzhen/tech/sports/finance` 这些频道用现有内容属性就能查到合理内容

建议检查文件：

- [`backend/seed/seed.go`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/backend/seed/seed.go:1)

具体检查点：

- `category` 是否覆盖科技/体育/财经
- `city` 是否有足够深圳数据
- `content_type = video` 的数据量是否足够支撑视频频道

状态：

- 必做，但优先级低于 API/Repository 改造

------

## 13.22 Android 前端任务

### F1. Tab 增加 `sceneKey`

目标：

- Tab 真正绑定频道语义，而不是只有标题

建议修改文件：

- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/ui/feed/components/FeedTabItem.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/ui/feed/components/FeedTabItem.kt:1)
- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/ui/feed/FeedScreen.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/ui/feed/FeedScreen.kt:1)

建议数据结构：

```kotlin
data class FeedTabItem(
    val id: Long,
    val title: String,
    val sceneKey: String,
    val showDot: Boolean = false
)
```

状态：

- 必做

### F2. 网络层支持 `scene`

目标：

- 发请求时能带频道参数

建议修改文件：

- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/data/remote/api/FeedApiService.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/data/remote/api/FeedApiService.kt:1)
- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/data/datasource/RemoteDataSource.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/data/datasource/RemoteDataSource.kt:1)

具体改动：

- `getFeed()` 新增 `scene`
- `loadInitialFeed(scene)`
- `refreshFeed(scene, latestPublishTime)`
- `loadMore(scene, cursor)`

状态：

- 必做

### F3. DTO 支持 `scene/top_items`

目标：

- 和后端新的统一响应结构保持一致

建议修改文件：

- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/data/remote/dto/FeedResponseDto.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/data/remote/dto/FeedResponseDto.kt:1)

建议字段：

- `scene`
- `topItems`
- `items`
- `nextCursor`
- `hasMore`
- `latestPublishTime`

状态：

- 必做

### F4. Repository 接口支持 `scene`

目标：

- 从领域层开始按频道组织数据流

建议修改文件：

- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/domain/repository/FeedRepositoryContract.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/domain/repository/FeedRepositoryContract.kt:1)
- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/data/repository/FeedRepository.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/data/repository/FeedRepository.kt:1)

方向：

- `loadInitialFeed(scene)`
- `refreshFeed(scene, latestPublishTime)`
- `loadMore(scene, cursor)`

状态：

- 必做

### F5. ViewModel 改为按频道隔离状态

目标：

- 解决多频道串台问题

建议修改文件：

- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/ui/feed/FeedViewModel.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/ui/feed/FeedViewModel.kt:1)
- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/ui/feed/FeedUiState.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/ui/feed/FeedUiState.kt:1)

建议方向：

- 用 `Map<String, FeedUiState.Success>` 管理每个 `scene`
- 当前选中频道只读取自己的状态
- `refresh/loadMore` 都显式带 `scene`

第一阶段不一定要做到最复杂，但至少要保证：

- `recommend` 和 `video` 互不污染
- `recommend` 和 `shenzhen` 互不污染

状态：

- 必做

### F6. FeedList 按频道决定是否展示官方置顶区

目标：

- 只有推荐频道显示 Top 区，并保持推荐页现有结构不变

建议修改文件：

- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/ui/feed/components/FeedList.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/ui/feed/components/FeedList.kt:1)
- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/ui/feed/FeedScreen.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/ui/feed/FeedScreen.kt:1)

规则：

- `scene == recommend`：渲染 `officialItems`
- 其他频道：忽略 `officialItems`

状态：

- 必做

### F7. Room 缓存增加 `sceneKey`

目标：

- 防止本地缓存跨频道串台

建议修改文件：

- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/data/local/entity/FeedItemEntity.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/data/local/entity/FeedItemEntity.kt:1)
- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/data/local/db/FeedItemDao.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/data/local/db/FeedItemDao.kt:1)
- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/data/local/LocalDataSource.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/data/local/LocalDataSource.kt:1)
- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/data/local/mapper/FeedLocalMapper.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/data/local/mapper/FeedLocalMapper.kt:1)

建议改动：

- `FeedItemEntity` 增加 `sceneKey`
- DAO 增加 `getFeedItemsByScene(sceneKey)`
- DAO 增加 `clearScene(sceneKey)`
- LocalDataSource 改为按频道读写缓存

状态：

- 必做

### F8. UseCase 层透传 `scene`

目标：

- 保持调用链一致

建议修改文件：

- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/domain/usecase/LoadInitialFeedUseCase.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/domain/usecase/LoadInitialFeedUseCase.kt:1)
- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/domain/usecase/RefreshFeedUseCase.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/domain/usecase/RefreshFeedUseCase.kt:1)
- [`ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/domain/usecase/LoadMoreFeedUseCase.kt`](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/domain/usecase/LoadMoreFeedUseCase.kt:1)

状态：

- 必做

------

## 13.23 推荐迭代切片

为了降低风险，建议按 3 个小迭代推进，而不是一次改完。

### 迭代一：后端打通频道语义

目标：

- `GET /api/v1/feed?scene=...` 生效
- 只有 `recommend` 有 `Top5`

交付物：

- 后端 API / Service / Repository 改造完成
- 使用 curl 可验证不同频道返回不同数据

### 迭代二：前端打通真实频道切换

目标：

- Tab 不再是假切换
- `recommend / video / shenzhen` 能分别请求自己的数据

交付物：

- Android 网络层与 ViewModel 支持 `scene`
- UI 能按频道显示不同列表

### 迭代三：缓存与状态隔离收口

目标：

- 消除串台和缓存污染

交付物：

- Room 增加 `sceneKey`
- 每频道分页和刷新状态独立

------

## 13.24 验收标准

完成第一阶段后，至少应满足以下验收标准：

### 后端

- 请求 `scene=recommend` 时返回 `top_items`
- 请求 `scene=recommend` 时推荐频道行为与当前版本一致
- 请求 `scene=video` 时 `top_items` 为空
- 请求 `scene=shenzhen` 时仅返回 `city = 深圳` 的内容
- 不同 `scene` 的 `refresh` 和 `loadMore` 不互相污染

### Android

- 切换 `推荐 / 视频 / 深圳` 能看到明显不同内容
- 只有推荐页显示官方置顶区
- 推荐页的现有交互和内容组织不被多频道改造破坏
- 视频频道不会出现推荐页的 Top5
- 切频道后分页状态不串
- 下拉刷新只影响当前频道

### 数据

- 视频频道有足够内容支撑首屏
- 深圳频道有足够内容支撑首屏
- 推荐频道依然保持当前 Demo 观感
