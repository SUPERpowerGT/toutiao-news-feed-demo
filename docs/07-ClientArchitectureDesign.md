# **07. Client Architecture Design**

本章节介绍 Android 客户端的整体架构设计，包括层次结构、技术栈选择、核心模块划分、数据流向与状态管理方式。
 本 Demo 使用 **Kotlin + Jetpack Compose + MVVM + Repository Pattern + Room** 实现新闻推荐流首页与新闻详情页。

------

# **7.1 技术选型（Technology Stack）**

| 技术                   | 用途                                      |
| ---------------------- | ----------------------------------------- |
| **Kotlin**             | Android 主语言，协程支持更友好            |
| **Jetpack Compose**    | 声明式 UI，引擎支持高性能列表渲染         |
| **ViewModel**          | 状态管理，保存首页刷新/分页状态           |
| **StateFlow**          | 单向数据流（UI 不直接修改数据）           |
| **Repository Pattern** | 数据来源统一（本地 Room + 网络 Retrofit） |
| **Retrofit + OkHttp**  | 网络请求框架，用于访问 Go 后端接口        |
| **Room (SQLite)**      | 本地缓存，用于首页秒开与去重              |
| **Coil**               | 图片与视频封面加载                        |
| **Kotlin Coroutines**  | 异步编程模型，支持挂起与并发              |

> 架构整体遵循：**UI = f(State)**，使 UI 更新与数据变更解耦。

------

# **7.2 客户端整体架构（Overall Architecture）**

```
┌──────────────────────────────┐
│            UI 层             │ (Compose)
│ FeedScreen / DetailScreen    │
└──────────────▲──────────────┘
               │ StateFlow
┌──────────────┴──────────────┐
│          ViewModel 层        │
│ FeedViewModel / DetailVM     │
└──────────────▲──────────────┘
               │ suspend fun
┌──────────────┴──────────────┐
│        Repository 层         │
│ FeedRepository / NewsRepo    │
└───────▲───────────▲─────────┘
        │Local       │Remote
┌───────┴──────┐  ┌──┴────────┐
│    Room DB    │  │ Retrofit  │
└───────┬──────┘  └──┬────────┘
        │             │
┌───────┴────────────┴────────┐
│        本地缓存 + 远端 API     │
└──────────────────────────────┘
```

------

# **7.3 模块划分（Modules）**

客户端主要模块如下：

| 模块            | 描述                                    |
| --------------- | --------------------------------------- |
| **ui/**         | Compose 页、卡片组件                    |
| **viewmodel/**  | 状态管理（StateFlow）                   |
| **repository/** | 数据来源整合                            |
| **network/**    | Retrofit 接口定义                       |
| **database/**   | Room 数据库实体与 DAO                   |
| **model/**      | UI 模型（UI Model）                     |
| **mapper/**     | DTO → DB → UI 转换器（Adapter Pattern） |

------

# **7.4 数据流动（Data Flow）**

### 推荐流（Feed）数据流动：

```
UI → ViewModel → Repository → Retrofit/Room
                         ↓
                     返回数据
UI ← ViewModel ← Repository ← Retrofit/Room
```

### 流程说明：

1. **UI 发起事件**：刷新 / 加载更多
2. ViewModel 处理事件，调用 Repository
3. Repository 判断本地/远端数据来源
4. Retrofit 从 Go 后端获取 feed
5. 保存到 Room（缓存）
6. ViewModel 转换为 UI Model（Mapper）
7. Compose 根据 StateFlow 自动更新界面

> 客户端不直接处理数据，而是依赖 ViewModel 暴露的 StateFlow。

------

# **7.5 状态管理（MVVM + StateFlow）**

所有 UI 使用以下模式：

```
data class FeedUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val items: List<FeedUiModel> = emptyList(),
    val nextCursor: String? = null,
    val error: String? = null
)
```

ViewModel：

```
class FeedViewModel(
    private val repo: FeedRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState

    fun loadFeed() { ... }
    fun refresh() { ... }
    fun loadMore() { ... }
}
```

Compose：

```
@Composable
fun FeedScreen(vm: FeedViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    FeedList(state.items)
}
```

------

# **7.6 推荐流 Render 层设计（Compose UI）**

Compose 实现卡片列表：

```
LazyColumn {
    items(state.items) { item ->
        when (item.displayType) {
            "text" -> TextCard(item)
            "image" -> LargeImageCard(item)
            "multi_image" -> ThreeImageCard(item)
            "video" -> VideoCard(item)
        }
    }
}
```

每种卡片对应一个独立的 @Composable，遵循 Single Responsibility。

------

# **7.7 本地缓存（Room）设计**

用于：

- 首页秒开
- 刷新/分页去重
- 基本离线能力

示例：

```
@Entity(primaryKeys = ["feedId"])
data class FeedEntity(
    val feedId: Long,
    val newsId: Long,
    val title: String,
    val publishTime: Long,
    val cursorSeq: Long
)
```

DAO：

```
@Dao
interface FeedDao {
    @Query("SELECT * FROM FeedEntity ORDER BY publishTime DESC, cursorSeq DESC")
    fun getAll(): Flow<List<FeedEntity>>
}
```

Repository 中组合本地/远端：

```
suspend fun getFeed(cursor: String?) =
    if (cursor == null) loadFirstPage()
    else loadNextPage(cursor)
```

------

# **7.8 Retrofit 网络模块设计**

```
interface ApiService {
    @GET("/api/feed")
    suspend fun getFeed(
        @Query("cursor") cursor: String?,
        @Query("limit") limit: Int = 10
    ): FeedResponse
}
```

使用 OkHttp 处理超时、拦截器等。

------

# **7.9 数据模型转换（Mapper / Adapter）**

数据经过三层：

```
Retrofit DTO → Room Entity → UI Model
```

示例：

```
fun FeedDto.toEntity(): FeedEntity { ... }

fun FeedEntity.toUiModel(): FeedUiModel { ... }
```

这一层就是典型的 **Adapter Pattern**。

------

# **7.10 错误处理（Error Handling）**

- Retrofit 使用 Result 封装
- ViewModel 统一处理错误
- UI 层展示 SnackBar 或 Toast

------

# 🎉 结束语

本架构遵循：

- MVVM
- 单向数据流 UDF
- Repository Pattern
- 本地缓存 + 远端同步
- Compose 声明式 UI

结构清晰、可扩展性强，适合 Demo 开发展示与后续扩展。