# **10. 数据流与用例（Data Flow & Use Cases）**

本章节描述客户端与后端之间的数据流转过程，并结合典型操作（首屏加载、下拉刷新、分页加载、打开详情页）展示系统在不同交互场景下的行为逻辑。
 数据流由客户端的 MVVM 架构、后端的 DDD 分层架构以及 Room 缓存共同构成。

------

# **10.1 整体数据流（Overview）**

整体数据流遵循以下路径：

```
UI → ViewModel → Repository → (Room / Retrofit) → API → Service → Repository → PostgreSQL
                                                             ↓
UI ← ViewModel ← Repository ← (Room / Retrofit) ← API Response
```

分为两部分：

- **客户端内部数据流（UI → VM → Repo → Room/Network）**
- **服务端内部数据流（API → Application → Domain → Infra → DB）**

该结构确保：

- UI 不直接处理数据
- Repository 控制本地与远端策略
- Service 控制业务逻辑
- DB 层只承担数据持久化

------

# **10.2 用例一：首屏加载（First Load）**

### 10.2.1 操作场景

用户打开 App，进入首页。

### 10.2.2 数据流过程

步骤如下：

1. **UI 调用 ViewModel.loadFeed()**
2. ViewModel 首先加载 **Room 缓存**
3. UI 立即展示缓存（保证首屏速度）
4. 同时 ViewModel 发起网络请求（Retrofit）
5. 后端根据 cursor = null 返回最新推荐流
6. Repository 写入 Room（覆盖旧数据）
7. ViewModel 更新 StateFlow
8. UI 重新渲染，呈现最新数据

### 效果

- 首屏加载时间控制在 100–150ms
- 网络返回后自动刷新
- 无需等待

------

# **10.3 用例二：下拉刷新（Pull to Refresh）**

### 10.3.1 操作场景

用户在首页顶部下拉刷新列表。

### 10.3.2 数据流过程

1. UI 触发 `ViewModel.refresh()`
2. ViewModel 清空当前状态并展示刷新动画
3. Repository 调用 `/api/feed`（不带 cursor）
4. 后端返回最新一页推荐内容
5. Repository 覆盖写入 Room 缓存
6. ViewModel 设置 `isRefreshing = false`
7. UI 更新列表

### 刷新数据不重复的原因

后端 cursor-based 查询保证：

- 新内容永远排在旧内容之前
- 客户端刷新不会出现重复数据
- 而分页读取 cursor 也不会倒序出现

------

# **10.4 用例三：列表分页加载（Load More / Infinite Scroll）**

### 10.4.1 操作场景

用户滑动到底部触发“加载更多”。

### 10.4.2 数据流过程

1. UI 调用 `ViewModel.loadMore()`
2. ViewModel 从 `uiState.nextCursor` 取出鼠标
3. Repository 调用 `/api/feed?cursor={nextCursor}`
4. 后端 cursor 逻辑：
   - 以 `publish_time` 与 `seq_id` 为分页条件
   - 返回比 cursor 更旧的一批数据
5. 新数据写入 Room（追加）
6. ViewModel 合并列表 items
7. UI 渲染新增内容

### 不会出现重复或跳页原因：

- cursor 为严格的排序基准
- 后端 SQL 始终按 (publish_time, seq_id) 递减
- 客户端合并列表时以 `news_id/feed_id` 去重

------

# **10.5 用例四：打开新闻详情页（News Detail）**

### 10.5.1 操作场景

用户点击首页某卡片。

### 10.5.2 数据流过程

1. UI 跳转到 DetailScreen（先展示骨架屏）
2. ViewModel 发起 `/api/news/{id}` 请求
3. 服务端从：
   - `news`
   - `news_content`
   - `media`
      中聚合新闻完整内容
4. 构建 JSON 格式的内容结构（用于 Compose 渲染）
5. 客户端解析：
   - 文本段落
   - 图片块
   - 视频块
6. UI 按 JSON 中的顺序进行图文混排

------

# **10.6 用例五：弱网 / 离线阅读（Offline Fallback）**

### 10.6.1 操作场景

用户在弱网或无网环境下打开首页。

### 10.6.2 数据流过程

1. ViewModel 检测到网络不可用
2. 自动选择 Room 缓存数据
3. UI 展示缓存内容
4. 用户仍可浏览部分新闻详情（若缓存命中）

说明：

- Demo 不涉及完整离线能力，但基本阅读可完成
- 符合课程要求的“加强体验但不复杂化”原则

------

# **10.7 服务端关键数据流（FeedItem → DTO）**

服务端对于推荐流的处理流程如下：

```
feed_item → 查询 news → 查询 media → 查询 author → 查询 stats
                                                       ↓
                                       聚合为 FeedItemDTO
```

特点：

- 后端完成字段整理
- 客户端直接渲染，不做额外拼接
- 避免移动端多次发起请求

------

# **10.8 数据流小结**

- 客户端采用 MVVM + Repository 数据管道
- 后端采用 DDD 分层，逻辑清晰
- Room 缓存使首页访问速度稳定
- cursor-based 分页保证数据稳定性与非重复性
- 图文混排通过 JSON 控制顺序
- 缓存、多卡片渲染、分页刷新构成完整的推荐流系统主链路

这些流程共同支撑推荐流 Demo 的 3 个核心目标：

1. 首页秒开
2. 列表流畅
3. 弱网可用