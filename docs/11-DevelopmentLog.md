## 11/16-11/17

1、完成了01-10的开发文档的初步定稿，初步确定开发内容以及计划，后续再开发过程当中更新相关内容

2、配置git仓库初始化文件目录开始项目管理

```
news-feed-demo/
│
├── docs/           # 软件工程文档（需求、用例、架构、设计…）
│
├── backend/        # Go 后端（我们的 API 服务）
│
├── android/        # Android Studio 工程（Compose App）
│
└── README.md       # 项目总说明

```

仓库初始化

```
git init
git add .
git commit -m "Init project structure"

```

创建远程仓库

关联远程仓库

```
git branch -M main
git remote add origin https://github.com/SUPERpowerGT/toutiao-news-feed-demo.git
git push -u origin main
```

https://github.com/SUPERpowerGT/toutiao-news-feed-demo

3、启动jira来管理项目sprint以及每日功能开发需求更新

https://toutiao-develop.atlassian.net/jira/software/projects/SCRUM/boards/1

4、环境配置

后端环境配置

vscode

- go
- docker

go

```
go version go1.24.0 windows/amd64
```

postgresql

- docker拉去镜像配置用pgadmin来可视化

postman

docker desktop



客户端开发环境（windows）安卓

android studio

 https://developer.android.com/studio

配置时候注意改在d盘同一目录下包括后续sdk安装等



新建项目这里注意要修改名称和路径以及vpn不建议挂

更新gitignore再根目录（全局管理项目删掉子目录自动生成的gitignore）



**6、客户端开发（mvp）**

```
com.xuziyi.toutiaoandroid
│
├── common/            // 通用工具（扩展、转换器等）
│
├── data/repo/         // 数据层 = Repository：数据来源（本地/网络/假数据）
│       └── FeedRepository.kt
│
├── domain/model/      // 领域层（前端自己的业务模型）
│       └── FeedItem.kt
│
└── ui/
    ├── components/    // 小 UI 组件：卡片、标题、按钮等
    │       └── FeedCard.kt
    │
    ├── feed/          // 页面级 UI：FeedScreen + FeedViewModel
    │       ├── FeedScreen.kt
    │       └── FeedViewModel.kt
    │
    └── theme/         // 主题、颜色、样式

```



domain/model

标准化数据结构，保证客户端和后端的数据格式一致性

```
package com.xuziyi.toutiaoandroid.domain.model

data class FeedItem(
    val id: String,
    val title: String,
    val summary: String,
    val source: String,
    val time: String,
)
```



data/repo

调取数据给UI



ui/feed mvvm的viewmodel+screen

viewmodel

链接repo，状态管理等，负责管理状态



feedscreen

compose页面类比react



ui/components

重用组件卡片时间等



common

通用工具时间格式，mapper，日志等

```
 MainActivity
    ↓
 FeedScreen (UI 层)
    ↓ 订阅 ViewModel.state
 FeedViewModel (ViewModel 层)
    ↓ 调用 Repository.getFeed()
 FeedRepository (数据层)
    ↓
 返回 FeedItem 列表（假数据 / 本地缓存 / 网络）

```

这里简单来学习一下mvvm的标准

model+ view +viewmodel

view:

视图层，展示ui，ux，接受用户的交互，但是所有逻辑都交给vm做

不做网络请求，不访问数据库，不拼装业务逻辑



vm：

写逻辑，拿数据从repo，启动协程，管理ui，返回干净数据给ui

不做数据库代码，不屑网络框架调用，不依赖android view



model

网络请求retrofit

数据库存储room

本地缓存

业务实体dataclass

```
UI（Compose）
   │
   ▼
ViewModel（处理逻辑/状态）
   │
   ▼
Repository（数据读写）
   │
   ▼
API / Room（真正的来源）

```

这里类比到我们的项目中

```
com.xuziyi.toutiaoandroid
 ├── domain/          ← 真正的 Model（业务模型）
 ├── data/            ← 数据来源层（API/DB/Mock）
 │     └── repo/FeedRepository
 └── ui/
       ├── components/    ← 复用 UI
       └── feed/          ← Feed Feature 的全部 UI 逻辑
             ├── FeedScreen
             ├── FeedViewModel
             └── FeedUiState（可加）

```

合理的看起来

![image-20251117185020085](./11-DevelopmentLog.assets/image-20251117185020085.png)



**6、后端开发（mvp）**

打开我们的backend然后先go初始化并安装gin包

```
go mod init toutiao-backend

```

这里我还在国内用镜像更新路径

```
go env -w GO111MODULE=on
go env -w GOPROXY=https://goproxy.cn,direct
go env -w GOSUMDB=sum.golang.org
```

重新安装gin包

```
go get github.com/gin-gonic/gin
```

可以简单写一个main来测试

http://localhost:8080/feed/list



**7、后端和数据库链接（mvp）**

安装pgx v5 驱动postgresql



```
go get github.com/jackc/pgx/v5
go get github.com/jackc/pgx/v5/stdlib

```

这里注意国内vpn的问题

$Env:http_proxy="http://127.0.0.1:7890";$Env:https_proxy="http://127.0.0.1:7890"

再当前terminal配置才可以正常访问docker 镜像

```
docker compose up --build
```

http://localhost:8080/feed/list

![image-20251117230704331](./11-DevelopmentLog.assets/image-20251117230704331.png)

成功！







## 11/18

**1、后端数据库链接开发板**

我们重新审视设计了一下数据库表来保证我们的设计是合理的且能满足前端的需求以及更高的拓展性

```
-- ================================
-- ENUM 类型定义
-- ================================
DO $$ BEGIN
    CREATE TYPE news_type_enum AS ENUM ('text', 'image', 'video', 'multi_image', 'multi_video');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE TYPE media_type_enum AS ENUM ('image', 'video');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- ================================
-- 作者表
-- ================================
CREATE TABLE IF NOT EXISTS author (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(50) NOT NULL,
    avatar_url    VARCHAR(255),
    description   VARCHAR(255),
    certification VARCHAR(50),
    created_at    TIMESTAMP DEFAULT NOW()
);

-- ================================
-- 新闻主表
-- ================================
CREATE TABLE IF NOT EXISTS news (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    summary      VARCHAR(255),
    news_type    news_type_enum NOT NULL,
    author_id    BIGINT REFERENCES author(id),
    source       VARCHAR(100),
    category     VARCHAR(50),
    publish_time TIMESTAMP,
    status       SMALLINT DEFAULT 1,
    created_at   TIMESTAMP DEFAULT NOW(),
    updated_at   TIMESTAMP DEFAULT NOW()
);

-- ================================
-- 新闻内容（正文字段）
-- ================================
CREATE TABLE IF NOT EXISTS news_content (
    news_id      BIGINT PRIMARY KEY REFERENCES news(id) ON DELETE CASCADE,
    content_html TEXT,
    content_json JSONB,
    word_count   INT
);

-- ================================
-- 多媒体表（新增）
-- ================================
CREATE TABLE IF NOT EXISTS media (
    id          BIGSERIAL PRIMARY KEY,
    news_id     BIGINT REFERENCES news(id) ON DELETE CASCADE,
    group_id    BIGINT,
    media_type  media_type_enum NOT NULL,
    url         VARCHAR(255),
    cover_url   VARCHAR(255),
    duration    INT,
    width       INT,
    height      INT,
    order_index INT,
    created_at  TIMESTAMP DEFAULT NOW()
);

-- ================================
-- 互动统计表
-- ================================
CREATE TABLE IF NOT EXISTS stats (
    news_id        BIGINT PRIMARY KEY REFERENCES news(id) ON DELETE CASCADE,
    like_count     INT DEFAULT 0,
    comment_count  INT DEFAULT 0,
    favorite_count INT DEFAULT 0,
    share_count    INT DEFAULT 0,
    play_count     INT DEFAULT 0,
    version        INT DEFAULT 0,
    updated_at     TIMESTAMP DEFAULT NOW()
);

-- ================================
-- 推荐流 feed_item
-- ================================
CREATE TABLE IF NOT EXISTS feed_item (
    id           BIGSERIAL PRIMARY KEY,
    news_id      BIGINT REFERENCES news(id) ON DELETE CASCADE,
    display_type news_type_enum NOT NULL,
    weight       FLOAT,
    scene        VARCHAR(30),
    model_id     VARCHAR(50),
    publish_time TIMESTAMP,
    seq_id       BIGSERIAL,
    created_at   TIMESTAMP DEFAULT NOW()
);

```

这里由于我们之前是初步测试，直接删除镜像重新拉取建表就好

需要注意要端口正确（之前有部署其他docker监听）

```
docker compose down
Remove-Item -Recurse -Force .\docker\db\data
docker compose up -d

```

这里重新确认一下相关配置

后端和数据库使用pgxv5来连接，整体式gin+pgxv5+postgresql

 

考虑到后续发布问题，这里重新更新一下docker，发布两个版本

```
docker compose -f docker-compose.dev.yml up -d
docker compose -f docker-compose.prod.yml up --build
```

并配置两个版本的env，.env.dev和.env.prod



安装env识别包，方便后端链接数据库

```
PS D:\Project\toutiao-news-feed-demo\backend> go get github.com/joho/godotenv
>>  
go: downloading github.com/joho/godotenv v1.5.1
go: added github.com/joho/godotenv v1.5.1
PS D:\Project\toutiao-news-feed-demo\backend> 
```



重新走docker

```
docker compose down
Remove-Item -Recurse -Force .\docker\db\data
docker compose -f docker-compose.dev.yml up -d
```

这边可以观察到挂起只挂起postgresql

![image-20251118143536857](./11-DevelopmentLog.assets/image-20251118143536857.png)

然后配置pgadmin

![image-20251118143608968](./11-DevelopmentLog.assets/image-20251118143608968.png)

插入测试数据，启动go后端

```
go run main.go
```

访问

```
http://localhost:8080/health

http://localhost:8080/feed/list
```

![image-20251118143711194](./11-DevelopmentLog.assets/image-20251118143711194.png)

测试成功！



**2、现在我们优化后端架构以及和数据库的表的映射以及ddd体系**

这里先简单学习一下请求链路

```
Android 客户端 → Go API → Application → Domain → Infrastructure(DB) → PostgreSQL
```

API 层：接请求（Controller） → 
Application 层：处理业务用例 → 
Domain 层：实体模型 + 业务规则 → 
Infrastructure 层：数据库、缓存等实现 → 
PostgreSQL：真实存储

这里api类比controller

负责：

- 接收 HTTP 请求
- 解析 query/body
- 调用 Application Service
- 输出 JSON

```
func (h *FeedHandler) handleGetFeed(w http.ResponseWriter, r *http.Request) {
    cursor := r.URL.Query().Get("cursor")
    limit, _ := strconv.Atoi(r.URL.Query().Get("limit"))

    result, err := h.service.GetFeed(r.Context(), cursor, limit)

    json.NewEncoder(w).Encode(result)
}
```



application类比service

负责：

- 封装“业务用例”
- 不处理 SQL
- 不处理 JSON
- 不实现数据库操作
- 只做“调用仓储 + 封装返回值”

```
items, nextCursor, err := s.repo.ListLatest(ctx, cur, limit)

return &FeedResult{
    Items: items,
    NextCursor: encodeCursor(nextCursor),
}, nil

```



domain纯净的业务模型，不依赖数据库，不依赖 HTTP，不依赖 JSON



infrastructure

写：

- PostgreSQL 访问
- Redis 访问
- Repository 的具体实现
- 配置加载
- 第三方服务 SDK

```
func (r *PGNewsRepository) ListLatest(ctx context.Context, cursor *domain.NewsCursor, limit int) {
    rows, _ := r.db.QueryContext(ctx, `
        SELECT id, title, summary, publish_time
        FROM news
        ORDER BY publish_time DESC, id DESC
        LIMIT $1
    `, limit)
}

```

创建所有domain和数据库表映射



![image-20251118152205708](./11-DevelopmentLog.assets/image-20251118152205708.png)

go开发和java开发哲学不一样，java是直接所有表映射因为有spring jpa的缘故，但是go是根据业务需求开发链路的

我们现在升级成ddd架构不用gin来打通最小链路

这里不用gin是因为这里涉及到推荐等操作，手动写感觉控性高一点！

我们实现一个简单的查询流程来看看怎么个事

这里创建如下文件

```
feed_handler.go
feed_service.go
feed_item.go
feed_item_repository.go
feed_item_repository_pg.go
logging.go
recover.go
seed.go
```

并更新执行main，然后调用seed插入数据并检查数据是否插入成功

```
http://localhost:8080/seed
http://localhost:8080/api/v1/feed

```

![image-20251118155634124](./11-DevelopmentLog.assets/image-20251118155634124.png)

成功捏



**3、客户端和后端链路（mvp）**

```
UI 层 FeedScreen.kt
        ↓ 触发事件（加载/刷新）
ViewModel FeedViewModel.kt
        ↓ suspend fun 调用仓库
Repository FeedRepository.kt
        ↓ 调用 Retrofit FeedApi
Network Retrofit
        ↓ HTTP 请求
Go 后端 /api/v1/feed
        ↓ 查询 PostgreSQL cursor 分页
DB PostgreSQL
        ↓ 返回 Json
Repository
        ↓ 保存/合并本地数据（未来 Room）
ViewModel 更新 StateFlow
        ↓
UI 层自动重组 (Composable Recompose)
```



```
data.repo/FeedRepository.kt       → Repository 层
domain.model/FeedItem.kt          → Domain/UI 模型层
ui/feed/FeedViewModel.kt          → ViewModel 层
ui/feed/FeedScreen.kt             → UI 层
ui/components/FeedCard.kt         → UI 组件层
```



这里补充一下再gradle下面syncs

```
// Retrofit + Gson
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")

// OkHttp（可选但建议）
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

```

配置网络文件

貌似出现一点问题，需要能访问本地主机

⭐ **第一步：允许明文 HTTP（Network Security Config）**

### 🔧 1. 新建文件

路径必须按照 Android 要求：

```
app/src/main/res/xml/network_security_config.xml
```

内容：

```
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
    </domain-config>
</network-security-config>
```

⭐ **第二步：修改 AndroidManifest.xml**

在 `<application>` 中加入：

```
<application
    android:usesCleartextTraffic="true"
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

完整示例：

```
<application
    android:name=".MyApp"
    android:allowBackup="true"
    android:usesCleartextTraffic="true"
    android:networkSecurityConfig="@xml/network_security_config"
    android:theme="@style/Theme.ToutiaoAndroid">
```

⭐ **第三步（确认）：BASE_URL 必须继续用**

```
http://10.0.2.2:8080
```

⚠ 不能用 `localhost` 或 `127.0.0.1`
 ⚠ 不能用你的电脑 IP

貌似还是没有改变

尝试更新新的设备来测试





## 11/19

1、修复网络连接问题模拟器

修改配置文件允许网络访问即可

2、git上传更新

确保远程仓库内容正确

本地清除缓存

```
git rm -r --cached .

```

重新追踪

```
git add .

```

检查状态https://pzjolf2f07.feishu.cn/wiki/TOQJwyoY5iq51BkMNLOcgiZRnzf?from=from_copylink

```
git stauts

```

没问题就提交push

```
git commit -m "feat: add Android client + Go backend + docs + docker compose"
git push

```

3、复习链路框架以及代码部分

客户端

4、开发功能

我们先开发客户端吧！！！！

创建新的分支

```
git checkout -b feature/android-mvp
```

我们先重新优化构建客户端的开发架构并规定好脚手架

1. **MainActivity** → setContent → `AppNavigator`
2. `AppNavigator` → 进入 `FeedScreen`
3. `FeedScreen` → 使用 `FeedViewModel`
4. `FeedViewModel` → 调用 `LoadInitialFeedUseCase`
5. `LoadInitialFeedUseCase` → 调用 `FeedRepositoryContract`
6. 实现类 `FeedRepository`（data 层）：
   - 使用 `RemoteDataSource` 调用 `RetrofitClient.feedApi` → 后端
   - 使用 `LocalDataSource` + `AppDatabase` + `FeedDao` 做缓存
7. `FeedRepository` 把 `FeedItemDto` → `FeedItem`（domain model）
8. `FeedViewModel` 收到 `List<FeedItem>` 更新 `StateFlow`
9. `FeedScreen` 收到新状态 → `FeedList` → `FeedCardFactory` → `TextCard / ImageCard / VideoCard`
10. 公共组件（`LoadingScreen` / `ErrorScreen`）参与不同状态展示
11. `DateFormatter` 格式化时间、`ApiResult` 包装网络结果



今日头条首页卡片数据类型分析

##### 视频卡片

大标题

视频封面图

视频播放按钮

视频时长

作者头像

作者名称

发布时间

点赞数量

更多按钮



##### 纯文本卡片

大标题

作者名称

评论数量



##### 单图卡片

大标题

图片

作者头像

作者名称

发布时间

点赞数量



### 场景 A：推荐列表页（首页 Feed）

这里有 **三种卡片展示形态**（暂时可以不管进详情之后的差异）：

1. **纯文字卡片**
   - 大标题
   - 来源（如“新华社”，有红 V）
   - 评论数（列表可以只显示个数字）
   - 下方：作者头像、作者名、发布时间、点赞数、更多（更多行为先不做）
2. **单图卡片**
   - 左：标题
   - 右：封面图（media 表里的一条 image）
   - 下方：作者头像、作者名、发布时间、点赞数、更多
3. **视频卡片**
   - 上：标题
   - 中：大图封面 + 播放按钮 + 时长
   - 下方：作者头像、作者名、发布时间、点赞数、更多

👉 **这里的共同点就是：**

> 每条 feed item 都要有：标题 + 类型 + 作者信息 + 发布时间 + 互动数量 + 媒体信息（图片/视频）。





### 场景 B：图文详情页（文字/单图/多图）

从列表点进去到**文章详情**的情况（你第 1、3 张图）：

- 顶部：大标题
- 正文：长文本（支持段落、加粗等，最好用 `content_html` 或 `content_json`）
- 文章下方：
  - 评论区（评论列表、回复、地区等）——你说可以先 mock / 不做
  - 分享 / 评论 / 点赞 / 收藏 的按钮和数量（显示数字就好）

📌 文字卡片 与 图文卡片 的区别现在主要是：

- 列表展示形式不同
- 进入详情后其实结构可以统一：**都是“文章 + 评论 + 操作条”**

你提到的

> “图片类型进入后底下是推荐流”
>  这个属于 **后续扩展的“相关文章推荐”功能**，可以后面加一个 `/news/{id}/related` 这样的接口，现在先不影响主体设计。



### 场景 C：视频详情页（全屏滑动）

类似抖音：

- 视频自动播放（全屏播放器）
- 右侧一列：头像、点赞、评论、收藏、分享
- 下方：标题 + 简介
- 往下滑是下一个视频

这个**从数据上看**，其实就是：

- 一条 `news`，`news_type = "video"`
- 关联一条或多条 `media`，`media_type = "video"`，要拿：视频地址、封面图、时长
- 一样有 author + stats

现在可以只做“点进去播一个视频，不支持上下滑”，但数据设计最好一次性支持完整信息。





### ✅ 首页每一条 FeedItem 需要的字段（列表用）

**基础信息**

- `id`：新闻 ID（跳详情用）
- `title`：大标题
- `summary`：摘要（有些纯文字/图文可以用）
- `news_type`：内容类型
  - `text` / `image` / `multi_image` / `video`

**作者信息（来自 author）**

- `author_name`（author.name）
- `author_avatar`（author.avatar_url）
- `author_certification`（author.certification，用于红 V、黄 V 等）

**媒体信息（来自 media）**

- `media`：一个列表，每个元素包含：
  - `media_type`：image / video
  - `url`：图片地址或视频地址
  - `cover_url`：视频封面图（图文的话可以为空或等于 url）
  - `duration`：时长（视频用）
  - `width`、`height`：需要的话可以携带，做布局优化

> 列表卡片中：
>
> - 文本卡片：media 为空
> - 单图卡片：media 中 1 条 image
> - 三图卡片：media 中 >=3 条 image
> - 视频卡片：media 中 1 条 video

**时间 & 来源**

- `publish_time`：发布时间（时间戳或已格式化字符串都行，如果你想客户端做“几小时前”就传时间戳）
- `source`：来源，如“新华社”、“头条号昵称”。如果你觉得重复，可以直接用 author_name。

**互动统计（来自 stats）**

- `like_count`
- `comment_count`
- `favorite_count`
- `share_count`
- `play_count`（视频用）



### ✅ 详情页需要的额外字段

在列表字段基础上，再加：

**正文内容（来自 news_content）**

- `content_html`：完整 HTML
- 或 `content_json`：结构化内容（比如富文本 JSON）

> MVP：可以只用 `content_html` 或只用 `content_json` 中的其中一种。

**推荐 &扩展**

- `related_items`（可选，将来扩展时再加）





这里先额外补充思考一下我们客户端数据来源的架构理念

这样的架构设计很好帮助我们区分数据来源以及分层，有点ooo的味道了

| 层级       | 职责                             | 示例             |
| ---------- | -------------------------------- | ---------------- |
| remote     | 请求服务器 → 返回 DTO            | Retrofit GET     |
| local      | SQLite / Room 本地缓存           | Room Dao         |
| datasource | remote/local 的“包装工”          | RemoteDataSource |
| repository | 业务逻辑大脑 → 提供 Domain Model | FeedRepository   |
| domain     | UI 数据结构                      | FeedItem         |
| ui         | Compose                          | FeedScreen       |

后端 application 层负责组合多个 repository 的 domain 数据，
 然后转为一个“前端专用的 DTO”，由 API 返回给前端。



所以实际上我们第一步骤就是分析usecase

然后设计前端界面

然后根据前端界面设计dto api规范

然后客户端和后端在分开开发自己的部分



## 11/20

1、完成dto设计

2、开发客户端

创建dto

创建retrofitclient（mock版本）

创建api fake版本

创建datasource

更新lcoal和remote的datasource

创建repository



11/21

sealed class





12/1

优化后端

修改数据库表

```
docker compose -f docker-compose.dev.yml down
docker compose -f docker-compose.dev.yml up -d
```

插入作者数据

```
INSERT INTO author (name, avatar_url, description, certification)
VALUES
('新华社快讯', 'https://randomuser.me/api/portraits/men/11.jpg', '国家权威新闻源', '官方认证'),
('环球热点榜', 'https://randomuser.me/api/portraits/women/12.jpg', '国际资讯第一线', NULL),
('科技每日说', 'https://randomuser.me/api/portraits/men/13.jpg', '专注科技深度报道', NULL),
('城市生活圈', 'https://randomuser.me/api/portraits/women/14.jpg', '本地热点新闻', NULL),
('体育风云', 'https://randomuser.me/api/portraits/men/15.jpg', '最全体育资讯', NULL),

('金融新观察', 'https://randomuser.me/api/portraits/men/21.jpg', '金融行业深度解读', NULL),
('动漫次元社', 'https://randomuser.me/api/portraits/women/22.jpg', 'ACG 情报站', NULL),
('历史故事局', 'https://randomuser.me/api/portraits/men/23.jpg', '讲述历史背后的故事', NULL),
('健康新生活', 'https://randomuser.me/api/portraits/women/24.jpg', '健康饮食与保健资讯', NULL),
('美食大玩家', 'https://randomuser.me/api/portraits/men/25.jpg', '全国美食探店', NULL),

('潮流时尚志', 'https://randomuser.me/api/portraits/women/31.jpg', '流行趋势与时尚信息', NULL),
('影视热搜榜', 'https://randomuser.me/api/portraits/men/32.jpg', '影视动态与新片速递', NULL),
('大数据研习社', 'https://randomuser.me/api/portraits/men/33.jpg', '大数据领域深度分享', NULL),
('汽车观察室', 'https://randomuser.me/api/portraits/men/34.jpg', '国内外汽车行业资讯', NULL),
('国际军事眼', 'https://randomuser.me/api/portraits/men/35.jpg', '军事热点解读', NULL),

('旅行环球派', 'https://randomuser.me/api/portraits/women/41.jpg', '旅行目的地探秘', NULL),
('家居搭配师', 'https://randomuser.me/api/portraits/women/42.jpg', '家居美学与生活方式', NULL),
('摄影新视界', 'https://randomuser.me/api/portraits/men/43.jpg', '摄影技巧与作品欣赏', NULL),
('职场生存指南', 'https://randomuser.me/api/portraits/women/44.jpg', '职场成长秘籍', NULL),
('创业者说', 'https://randomuser.me/api/portraits/men/45.jpg', '创业故事与行业分析', NULL),

('母婴成长日记', 'https://randomuser.me/api/portraits/women/51.jpg', '母婴知识与育儿科普', NULL),
('宠物乐园', 'https://randomuser.me/api/portraits/men/52.jpg', '萌宠日常与养宠知识', NULL),
('科学知识局', 'https://randomuser.me/api/portraits/men/53.jpg', '科普类知识分享', NULL),
('建筑观察台', 'https://randomuser.me/api/portraits/men/54.jpg', '建筑行业洞察', NULL),
('艺术拾遗录', 'https://randomuser.me/api/portraits/women/55.jpg', '艺术与文化鉴赏', NULL),

('游戏情报站', 'https://randomuser.me/api/portraits/men/61.jpg', '游戏动态与攻略分享', NULL),
('数码新玩意', 'https://randomuser.me/api/portraits/men/62.jpg', '数码产品评测', NULL),
('财经热搜榜', 'https://randomuser.me/api/portraits/men/63.jpg', '最新财经动态', NULL),
('地理观测者', 'https://randomuser.me/api/portraits/men/64.jpg', '地理科普与世界观察', NULL),
('宇宙探索号', 'https://randomuser.me/api/portraits/men/65.jpg', '宇宙科学与天文资讯', NULL),

('手工爱好者', 'https://randomuser.me/api/portraits/women/71.jpg', '手工制作兴趣分享', NULL),
('金融日报', 'https://randomuser.me/api/portraits/men/72.jpg', '每日金融快讯', NULL),
('户外大玩家', 'https://randomuser.me/api/portraits/men/73.jpg', '户外探险与装备分享', NULL),
('心理小课堂', 'https://randomuser.me/api/portraits/women/74.jpg', '心理学知识与案例分析', NULL),
('全球热点速递', 'https://randomuser.me/api/portraits/men/75.jpg', '全球新闻一网打尽', NULL),

('区块链研习者', 'https://randomuser.me/api/portraits/men/81.jpg', '区块链技术资讯', NULL),
('文化随笔集', 'https://randomuser.me/api/portraits/women/82.jpg', '文化观点与随笔', NULL),
('新能源观察员', 'https://randomuser.me/api/portraits/men/83.jpg', '新能源发展趋势', NULL),
('业界风向标', 'https://randomuser.me/api/portraits/men/84.jpg', '行业趋势解读', NULL),
('本地事件通', 'https://randomuser.me/api/portraits/women/85.jpg', '本地资讯热点', NULL),

('社会纪实派', 'https://randomuser.me/api/portraits/men/91.jpg', '社会热点深度报道', NULL),
('乡村生活志', 'https://randomuser.me/api/portraits/women/92.jpg', '乡村人文与故事', NULL),
('AI 科技站', 'https://randomuser.me/api/portraits/men/93.jpg', '人工智能资讯', NULL),
('医疗健康圈', 'https://randomuser.me/api/portraits/men/94.jpg', '医疗科普与资讯', NULL),
('国际时事评论', 'https://randomuser.me/api/portraits/men/95.jpg', '国际局势分析评论', NULL);

```



news

```
INSERT INTO news (title, summary, news_type, author_id, source, category, publish_time)
VALUES
-- 作者 1（新华社快讯）
('AI 峰会今日召开，全球科技巨头齐聚上海',
 '本次大会聚焦大模型竞争与通用智能趋势，引发全球技术圈关注。',
 'image', 1, '新华社快讯', '科技', NOW() - INTERVAL '1 hour'),

('长三角铁路客流创新高 春节返程高峰提前到来',
 '铁路部门预计本周客流量将持续攀升，部分线路加开临时列车。',
 'image', 1, '新华社快讯', '社会', NOW() - INTERVAL '3 hours'),

-- 作者 2（环球热点榜）
('欧洲多国遭遇强风暴侵袭 交通大面积瘫痪',
 '罕见风暴影响多国，机场高速相继关闭，民众被要求减少出行。',
 'image', 2, '环球热点榜', '国际', NOW() - INTERVAL '2 hours'),

('中东局势再度紧张，多方呼吁尽快恢复谈判',
 '多国外交官正在推动新的会谈进程，试图降低地区紧张局势。',
 'image', 2, '环球热点榜', '国际', NOW() - INTERVAL '6 hours'),

-- 作者 3（科技每日说）
('国产旗舰手机发布 搭载自研芯片性能大增',
 '新款旗舰发布后，相关话题迅速登上热搜榜前列。',
 'image', 3, '科技每日说', '科技', NOW() - INTERVAL '1 hour'),

('AI 绘图工具升级，多模态生成能力显著增强',
 '最新升级的模型可以同时生成图片和短视频，引发讨论。',
 'video', 3, '科技每日说', '科技', NOW() - INTERVAL '4 hours'),

-- 作者 4（城市生活圈）
('杭州发布新的公共交通优惠政策',
 '符合条件的市民将享受公交地铁 8 折票价优惠，政策将于下月实施。',
 'image', 4, '城市生活圈', '民生', NOW() - INTERVAL '5 hours'),

('本地夜市火爆，年轻人成为主力消费群体',
 '多地夜市开设直播区，吸引大量游客参与互动。',
 'image', 4, '城市生活圈', '生活方式', NOW() - INTERVAL '8 hours'),

-- 作者 5（体育风云）
('中国短道速滑队打破赛季最佳成绩',
 '在世界赛场上再获佳绩，队员状态正佳。',
 'video', 5, '体育风云', '体育', NOW() - INTERVAL '40 minutes'),

('CBA 强强对话上演，广东队惊险取胜',
 '最后两分钟双方交替领先，现场氛围火爆。',
 'image', 5, '体育风云', '体育', NOW() - INTERVAL '5 hours'),

-- 作者 6（金融新观察）
('人民币汇率短期波动，专家：属正常市场表现',
 '分析人士表示短期波动不必过度解读，长期走势仍保持稳定。',
 'image', 6, '金融新观察', '财经', NOW() - INTERVAL '30 minutes'),

('A 股三连涨，科技和新能源板块领涨',
 '市场信心回暖，多家机构上调下半年预期。',
 'image', 6, '金融新观察', '财经', NOW() - INTERVAL '6 hours'),

-- 作者 7（动漫次元社）
('超人气动画新 PV 发布，粉丝热情高涨',
 '制作组公布新一季更新计划，画面质量大幅提升。',
 'image', 7, '动漫次元社', '娱乐', NOW() - INTERVAL '1 hour'),

('经典动画改编真人剧，定档暑期上映',
 '官方今日公开主演名单，引发大量讨论。',
 'image', 7, '动漫次元社', '娱乐', NOW() - INTERVAL '9 hours'),

-- 作者 8（历史故事局）
('考古新发现：千年古墓中的珍贵文物曝光',
 '专家表示部分文物具有极高历史研究价值。',
 'image', 8, '历史故事局', '历史', NOW() - INTERVAL '3 hours'),

('史学家解读：三国时期真正的经济实力构成',
 '通过最新论文数据复盘当时区域的经济格局。',
 'image', 8, '历史故事局', '历史', NOW() - INTERVAL '10 hours'),

-- 作者 9（健康新生活）
('专家提醒：冬季感冒高发，做好防护很关键',
 '建议补充维生素、多喝水、多通风。',
 'image', 9, '健康新生活', '健康', NOW() - INTERVAL '2 hours'),

('坚持运动的好处：研究表明心血管健康显著提升',
 '每天坚持 30 分钟运动即可降低多项健康风险。',
 'image', 9, '健康新生活', '健康', NOW() - INTERVAL '7 hours'),

-- 作者 10（美食大玩家）
('长沙夜市美食盘点：这 10 家店值得一试',
 '从烧烤到甜品，应有尽有，网友纷纷打卡。',
 'image', 10, '美食大玩家', '美食', NOW() - INTERVAL '1 hour'),

('五分钟学会做正宗台湾卤肉饭',
 '简单食材即可做出地道风味，附详细步骤。',
 'image', 10, '美食大玩家', '美食', NOW() - INTERVAL '5 hours');


```

```
INSERT INTO news (title, summary, news_type, author_id, source, category, publish_time)
VALUES
-- 作者 11（潮流时尚志）
('2025 春季时装周开幕，设计师新秀大放异彩',
 '本届时装周聚焦环保材料和未来主义风格，引发关注。',
 'image', 11, '潮流时尚志', '时尚', NOW() - INTERVAL '1 hour'),

('今年最火的穿搭趋势：极简风卷土重来',
 '时尚达人分享 2025 极简穿搭技巧，易学又高级。',
 'text', 11, '潮流时尚志', '时尚', NOW() - INTERVAL '4 hours'),

-- 作者 12（影视热搜榜）
('票房爆冷！话题大作上映首日表现不佳',
 '业内人士分析剧情质量或成主要原因。',
 'image', 12, '影视热搜榜', '娱乐', NOW() - INTERVAL '2 hours'),

('明星新剧即将开机，剧组公布完整演员阵容',
 '官方发布花絮视频，粉丝期待值拉满。',
 'video', 12, '影视热搜榜', '娱乐', NOW() - INTERVAL '8 hours'),

-- 作者 13（大数据研习社）
('AI 监管框架发布，算法透明度成核心要求',
 '新的政策将影响多个行业的数据处理方式。',
 'text', 13, '大数据研习社', '科技', NOW() - INTERVAL '30 minutes'),

('大型数据公司市值飙升，背后原因是什么？',
 '分析师认为多行业数字化需求推动了增长。',
 'image', 13, '大数据研习社', '财经', NOW() - INTERVAL '6 hours'),

-- 作者 14（汽车观察室）
('全新电动 SUV 发布，续航突破 900 公里',
 '业内认为这款车将重新定义电车市场天花板。',
 'image', 14, '汽车观察室', '汽车', NOW() - INTERVAL '1 hour'),

('车企财报出炉，销量回暖但利润承压',
 '多家车企强调将持续投入智能驾驶研发。',
 'text', 14, '汽车观察室', '汽车', NOW() - INTERVAL '7 hours'),

-- 作者 15（国际军事眼）
('多国举行联合军演，战机编队画面曝光',
 '本次军演规模空前，涉及多种作战力量。',
 'video', 15, '国际军事眼', '军事', NOW() - INTERVAL '50 minutes'),

('国际防务展今日开幕，新型装备亮相',
 '多款无人机和防空系统成为展会焦点。',
 'image', 15, '国际军事眼', '军事', NOW() - INTERVAL '9 hours'),

-- 作者 16（旅行环球派）
('盘点 2025 年最值得去的五大旅行目的地',
 '从极光之城到海岛秘境，总有一处让你心动。',
 'text', 16, '旅行环球派', '旅游', NOW() - INTERVAL '3 hours'),

('徒步爱好者的天堂！新线路正式对外开放',
 '路线难度适中，沿途风景壮丽。',
 'image', 16, '旅行环球派', '旅游', NOW() - INTERVAL '10 hours'),

-- 作者 17（家居搭配师）
('北欧风装修指南：简单 3 步营造高级感',
 '软装搭配技巧让小户型也能轻松变美。',
 'text', 17, '家居搭配师', '家居', NOW() - INTERVAL '1 hour'),

('2025 家居流行色公布，温柔色系大热',
 '设计师推荐将流行色与木质家具搭配使用。',
 'image', 17, '家居搭配师', '家居', NOW() - INTERVAL '6 hours'),

-- 作者 18（摄影新视界）
('如何用手机拍出大片感？专业摄影师分享技巧',
 '掌握构图与光线，随手也能出氛围感大片。',
 'image', 18, '摄影新视界', '摄影', NOW() - INTERVAL '40 minutes'),

('百年历史的老相机拍卖，最终价格令人惊讶',
 '收藏者称其具备极高纪念价值。',
 'text', 18, '摄影新视界', '摄影', NOW() - INTERVAL '12 hours'),

-- 作者 19（职场生存指南）
('职场沟通中最容易踩坑的三件事，你中招了吗？',
 '专家指出沟通技巧是职场晋升的关键能力之一。',
 'text', 19, '职场生存指南', '职场', NOW() - INTERVAL '2 hours'),

('如何提升效率？这 5 个时间管理方法值得一试',
 '简单有效的技巧帮助你更轻松完成工作任务。',
 'image', 19, '职场生存指南', '职场', NOW() - INTERVAL '8 hours'),

-- 作者 20（创业者说）
('创业如何从 0 到 1？资深创业者分享心得',
 '从市场选择到团队搭建，这些经验你一定用得上。',
 'text', 20, '创业者说', '商业', NOW() - INTERVAL '1 hour'),

('融资环境变化，初创企业如何应对？',
 '业内人士建议企业保持现金流和核心竞争力。',
 'image', 20, '创业者说', '商业', NOW() - INTERVAL '7 hours');


```

```
INSERT INTO news (title, summary, news_type, author_id, source, category, publish_time)
VALUES
-- 作者 21（母婴成长日记）
('宝宝辅食怎么添加？营养师给出专业建议',
 '不同月龄段的宝宝辅食需求不同，家长需根据情况调整。',
 'text', 21, '母婴成长日记', '母婴', NOW() - INTERVAL '1 hour'),

('孩子发烧不要慌！儿科医生教你三步应对',
 '家长最关心的退烧问题，医生给出科学方法。',
 'image', 21, '母婴成长日记', '健康', NOW() - INTERVAL '5 hours'),

-- 作者 22（宠物乐园）
('狗狗一直掉毛怎么办？兽医：这 3 点最关键',
 '换季掉毛是正常现象，但也可能是营养问题导致。',
 'text', 22, '宠物乐园', '宠物', NOW() - INTERVAL '2 hours'),

('萌猫日常合集爆火，网友：治愈系天花板',
 '短视频平台宠物内容持续走红，带动相关话题讨论度提升。',
 'video', 22, '宠物乐乐园', '宠物', NOW() - INTERVAL '9 hours'),

-- 作者 23（科学知识局）
('科学家揭示：为何我们会做梦？',
 '最新研究表明，梦境与大脑记忆修复密切相关。',
 'text', 23, '科学知识局', '科学', NOW() - INTERVAL '30 minutes'),

('空间望远镜捕捉到罕见星云照片，震撼发布',
 '高清照片展示星云壮丽细节，吸引大量天文爱好者关注。',
 'image', 23, '科学知识局', '科学', NOW() - INTERVAL '7 hours'),

-- 作者 24（建筑观察台）
('全球最高木结构建筑完工，引发建筑界热议',
 '这种新型结构实现环保与稳定性的平衡。',
 'image', 24, '建筑观察台', '建筑', NOW() - INTERVAL '1 hour'),

('老城区改造工程启动，专家：“保留历史肌理很重要”',
 '改造方案将兼顾居住改善与文化保护。',
 'text', 24, '建筑观察台', '建筑', NOW() - INTERVAL '10 hours'),

-- 作者 25（艺术拾遗录）
('被遗忘的画家：女性艺术家的复兴之路',
 '越来越多博物馆开始重新展出昔日被忽略的女性艺术家作品。',
 'text', 25, '艺术拾遗录', '艺术', NOW() - INTERVAL '2 hours'),

('现代艺术展今日开幕，沉浸式体验成最大亮点',
 '大量新媒体艺术作品吸引年轻观众前来打卡。',
 'image', 25, '艺术拾遗录', '艺术', NOW() - INTERVAL '8 hours'),

-- 作者 26（游戏情报站）
('年度最期待游戏定档，玩家：终于来了！',
 '制作组公布全新战斗系统细节，引发热烈讨论。',
 'video', 26, '游戏情报站', '游戏', NOW() - INTERVAL '1 hour'),

('独立游戏爆火，三人团队打造千万级口碑',
 '独立游戏产业再次证明创意才是核心驱动力。',
 'text', 26, '游戏情报站', '游戏', NOW() - INTERVAL '5 hours'),

-- 作者 27（数码新玩意）
('折叠屏手机销量激增，厂商集体加码新品布局',
 '折叠屏行业竞争进入新阶段，产品创新成为关键。',
 'image', 27, '数码新玩意', '科技', NOW() - INTERVAL '40 minutes'),

('智能穿戴设备再升级，“健康监测”成最大亮点',
 '新发布的手表支持更多监测项目，引发关注。',
 'text', 27, '数码新玩意', '科技', NOW() - INTERVAL '7 hours'),

-- 作者 28（财经热搜榜）
('全球股市普涨，科技板块领跑市场',
 '多国股指创今年新高，投资者信心增强。',
 'image', 28, '财经热搜榜', '财经', NOW() - INTERVAL '1 hour'),

('通胀压力仍存，专家建议保持审慎投资策略',
 '多家机构预计下季度通胀将逐步回落。',
 'text', 28, '财经热搜榜', '财经', NOW() - INTERVAL '6 hours'),

-- 作者 29（地理观测者）
('南极冰川发生断裂，科学家紧急评估影响',
 '该断裂或将影响未来海平面变化趋势。',
 'image', 29, '地理观测者', '科学', NOW() - INTERVAL '2 hours'),

('世界上最神秘的三处地理奇观，你去过几个？',
 '这些自然奇观长期吸引探索者深入研究。',
 'text', 29, '地理观测者', '探索', NOW() - INTERVAL '8 hours'),

-- 作者 30（宇宙探索号）
('火星车传回最新高清地表照片，细节令人震惊',
 '科学团队称照片中疑似存在沉积结构。',
 'image', 30, '宇宙探索号', '宇宙', NOW() - INTERVAL '1 hour'),

('天文学家发现类地行星大气组成重要线索',
 '这项发现可能会改变我们对生命存在形式的理解。',
 'text', 30, '宇宙探索号', '宇宙', NOW() - INTERVAL '9 hours');

```

```
INSERT INTO news (title, summary, news_type, author_id, source, category, publish_time)
VALUES
-- 作者 31（手工爱好者）
('简单 5 步做出精美香薰蜡烛，零基础也能完成',
 '所需材料都很容易买到，是入门最友好的手工项目之一。',
 'text', 31, '手工爱好者', '手工', NOW() - INTERVAL '1 hour'),

('手工达人分享绝美编织包教程，春季新宠',
 '编织包因其清新风格深受年轻人喜爱。',
 'image', 31, '手工爱好者', '手工', NOW() - INTERVAL '6 hours'),

-- 作者 32（金融日报）
('美联储政策会议召开，未来利率走向成焦点',
 '市场预计短期内不会大幅调整利率，但仍存不确定性。',
 'text', 32, '金融日报', '财经', NOW() - INTERVAL '2 hours'),

('人民币兑美元小幅回升，投资者信心增强',
 '多项经济数据表现稳定，汇率走势趋于平稳。',
 'image', 32, '金融日报', '财经', NOW() - INTERVAL '8 hours'),

-- 作者 33（户外大玩家）
('新晋徒步路线曝光：绝美雪山路线适合轻装备',
 '路线风光壮丽，非常适合周末短途徒步。',
 'image', 33, '户外大玩家', '户外', NOW() - INTERVAL '1 hour'),

('野营安全指南：新手最容易忽略的五件事',
 '专业玩家提醒，安全意识始终是第一位的。',
 'text', 33, '户外大玩家', '户外', NOW() - INTERVAL '10 hours'),

-- 作者 34（心理小课堂）
('如何缓解焦虑？心理学家推荐三种有效方法',
 '通过调整呼吸节奏与专注注意力可以显著降低焦虑感。',
 'text', 34, '心理小课堂', '心理', NOW() - INTERVAL '40 minutes'),

('长期熬夜对心理健康影响被证实，年轻人需警惕',
 '研究显示睡眠不足会增加情绪波动风险。',
 'image', 34, '心理小课堂', '心理', NOW() - INTERVAL '7 hours'),

-- 作者 35（全球热点速递）
('南亚多地暴雨引发洪灾，救援正在进行',
 '大量房屋受损，当地政府已启动紧急应对机制。',
 'image', 35, '全球热点速递', '国际', NOW() - INTERVAL '1 hour'),

('全球粮食供应链面临挑战，专家呼吁加强合作',
 '国际组织表示，跨国协同是缓解危机的重要手段。',
 'text', 35, '全球热点速递', '国际', NOW() - INTERVAL '9 hours'),

-- 作者 36（区块链研习者）
('加密货币市场再度波动，投资者态度趋于谨慎',
 '多国监管政策调整引发市场短期震荡。',
 'text', 36, '区块链研习者', '科技', NOW() - INTERVAL '2 hours'),

('Web3 游戏生态增长迅速，用户规模翻倍',
 '去中心化游戏为玩家带来全新体验，市场关注度提升。',
 'image', 36, '区块链研习者', '科技', NOW() - INTERVAL '8 hours'),

-- 作者 37（文化随笔集）
('关于“慢生活”的文化解读：为何越来越多人选择放慢脚步',
 '快节奏带来的压力促使人们重新审视日常生活方式。',
 'text', 37, '文化随笔集', '文化', NOW() - INTERVAL '1 hour'),

('古典文学阅读热潮来袭，线下书店销量上涨',
 '读者对经典作品的兴趣明显提升，相关活动热度不减。',
 'image', 37, '文化随笔集', '文化', NOW() - INTERVAL '11 hours'),

-- 作者 38（新能源观察员）
('太阳能储能系统迎重大突破，成本有望再降低',
 '新技术的应用将推动新能源产业进一步发展。',
 'image', 38, '新能源观察员', '科技', NOW() - INTERVAL '50 minutes'),

('新能源汽车销量连涨六个月，行业保持高景气度',
 '专家称政策与市场需求共同推动行业增长。',
 'text', 38, '新能源观察员', '汽车', NOW() - INTERVAL '6 hours'),

-- 作者 39（业界风向标）
('国内互联网巨头公布新战略，AI 仍是核心方向',
 '新战略强调智能化转型，加大云计算投入力度。',
 'text', 39, '业界风向标', '科技', NOW() - INTERVAL '2 hours'),

('多家初创公司融资成功，行业信心回暖',
 '本季度融资规模呈现明显上升趋势。',
 'image', 39, '业界风向标', '商业', NOW() - INTERVAL '9 hours'),

-- 作者 40（本地事件通）
('地铁新线路开通，市民出行更加便捷',
 '新开通线路将极大缓解城市交通压力。',
 'image', 40, '本地事件通', '民生', NOW() - INTERVAL '1 hour'),

('旧小区电梯更新工程启动，居民拍手称赞',
 '多栋楼将陆续完成电梯升级施工。',
 'text', 40, '本地事件通', '民生', NOW() - INTERVAL '7 hours');

```



meida

```
INSERT INTO media (news_id, media_type, url, cover_url, width, height, order_index)
VALUES
-- 新闻 1
(1, 'image', 'https://picsum.photos/seed/1001/800/600', NULL, 800, 600, 1),
(1, 'image', 'https://picsum.photos/seed/1002/800/600', NULL, 800, 600, 2),

-- 新闻 2
(2, 'image', 'https://picsum.photos/seed/1003/800/600', NULL, 800, 600, 1),
(2, 'image', 'https://picsum.photos/seed/1004/800/600', NULL, 800, 600, 2),

-- 新闻 3
(3, 'image', 'https://picsum.photos/seed/1005/800/600', NULL, 800, 600, 1),
(3, 'image', 'https://picsum.photos/seed/1006/800/600', NULL, 800, 600, 2),

-- 新闻 4
(4, 'image', 'https://picsum.photos/seed/1007/800/600', NULL, 800, 600, 1),
(4, 'image', 'https://picsum.photos/seed/1008/800/600', NULL, 800, 600, 2),

-- 新闻 5
(5, 'image', 'https://picsum.photos/seed/1009/800/600', NULL, 800, 600, 1),
(5, 'image', 'https://picsum.photos/seed/1010/800/600', NULL, 800, 600, 2),

-- 新闻 6（video + 图片）
(6, 'video',
     'https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',
     'https://picsum.photos/seed/1011/800/600',
     NULL, NULL, 1),
(6, 'image', 'https://picsum.photos/seed/1012/800/600', NULL, 800, 600, 2),

-- 新闻 7
(7, 'image', 'https://picsum.photos/seed/1013/800/600', NULL, 800, 600, 1),
(7, 'image', 'https://picsum.photos/seed/1014/800/600', NULL, 800, 600, 2),

-- 新闻 8
(8, 'image', 'https://picsum.photos/seed/1015/800/600', NULL, 800, 600, 1),
(8, 'image', 'https://picsum.photos/seed/1016/800/600', NULL, 800, 600, 2),

-- 新闻 9（video + 图片）
(9, 'video',
     'https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',
     'https://picsum.photos/seed/1017/800/600',
     NULL, NULL, 1),
(9, 'image', 'https://picsum.photos/seed/1018/800/600', NULL, 800, 600, 2),

-- 新闻 10
(10, 'image', 'https://picsum.photos/seed/1019/800/600', NULL, 800, 600, 1),
(10, 'image', 'https://picsum.photos/seed/1020/800/600', NULL, 800, 600, 2),

-- 新闻 11
(11, 'image', 'https://picsum.photos/seed/1021/800/600', NULL, 800, 600, 1),
(11, 'image', 'https://picsum.photos/seed/1022/800/600', NULL, 800, 600, 2),

-- 新闻 12
(12, 'image', 'https://picsum.photos/seed/1023/800/600', NULL, 800, 600, 1),
(12, 'image', 'https://picsum.photos/seed/1024/800/600', NULL, 800, 600, 2),

-- 新闻 13
(13, 'image', 'https://picsum.photos/seed/1025/800/600', NULL, 800, 600, 1),
(13, 'image', 'https://picsum.photos/seed/1026/800/600', NULL, 800, 600, 2),

-- 新闻 14（video + 图片）
(14, 'video',
     'https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',
     'https://picsum.photos/seed/1027/800/600',
     NULL, NULL, 1),
(14, 'image', 'https://picsum.photos/seed/1028/800/600', NULL, 800, 600, 2),

-- 新闻 15（video + 图片）
(15, 'video',
     'https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',
     'https://picsum.photos/seed/1029/800/600',
     NULL, NULL, 1),
(15, 'image', 'https://picsum.photos/seed/1030/800/600', NULL, 800, 600, 2),

-- 新闻 16
(16, 'image', 'https://picsum.photos/seed/1031/800/600', NULL, 800, 600, 1),
(16, 'image', 'https://picsum.photos/seed/1032/800/600', NULL, 800, 600, 2),

-- 新闻 17
(17, 'image', 'https://picsum.photos/seed/1033/800/600', NULL, 800, 600, 1),
(17, 'image', 'https://picsum.photos/seed/1034/800/600', NULL, 800, 600, 2),

-- 新闻 18（text → 默认 1 张图片）
(18, 'image', 'https://picsum.photos/seed/1035/800/600', NULL, 800, 600, 1),

-- 新闻 19
(19, 'image', 'https://picsum.photos/seed/1036/800/600', NULL, 800, 600, 1),
(19, 'image', 'https://picsum.photos/seed/1037/800/600', NULL, 800, 600, 2),

-- 新闻 20
(20, 'image', 'https://picsum.photos/seed/1038/800/600', NULL, 800, 600, 1),
(20, 'image', 'https://picsum.photos/seed/1039/800/600', NULL, 800, 600, 2);

```

```
INSERT INTO media (news_id, media_type, url, cover_url, width, height, order_index)
VALUES
-- 新闻 21
(21, 'image', 'https://picsum.photos/seed/2001/800/600', NULL, 800, 600, 1),
(21, 'image', 'https://picsum.photos/seed/2002/800/600', NULL, 800, 600, 2),

-- 新闻 22（text）
(22, 'image', 'https://picsum.photos/seed/2003/800/600', NULL, 800, 600, 1),

-- 新闻 23
(23, 'image', 'https://picsum.photos/seed/2004/800/600', NULL, 800, 600, 1),
(23, 'image', 'https://picsum.photos/seed/2005/800/600', NULL, 800, 600, 2),

-- 新闻 24（video）
(24, 'video',
 'https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',
 'https://picsum.photos/seed/2006/800/600',
 NULL, NULL, 1),
(24, 'image', 'https://picsum.photos/seed/2007/800/600', NULL, 800, 600, 2),

-- 新闻 25（text）
(25, 'image', 'https://picsum.photos/seed/2008/800/600', NULL, 800, 600, 1),

-- 新闻 26
(26, 'image', 'https://picsum.photos/seed/2009/800/600', NULL, 800, 600, 1),
(26, 'image', 'https://picsum.photos/seed/2010/800/600', NULL, 800, 600, 2),

-- 新闻 27
(27, 'image', 'https://picsum.photos/seed/2011/800/600', NULL, 800, 600, 1),
(27, 'image', 'https://picsum.photos/seed/2012/800/600', NULL, 800, 600, 2),

-- 新闻 28（text）
(28, 'image', 'https://picsum.photos/seed/2013/800/600', NULL, 800, 600, 1),

-- 新闻 29（video）
(29, 'video',
 'https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',
 'https://picsum.photos/seed/2014/800/600',
 NULL, NULL, 1),
(29, 'image', 'https://picsum.photos/seed/2015/800/600', NULL, 800, 600, 2),

-- 新闻 30
(30, 'image', 'https://picsum.photos/seed/2016/800/600', NULL, 800, 600, 1),
(30, 'image', 'https://picsum.photos/seed/2017/800/600', NULL, 800, 600, 2),

-- 新闻 31
(31, 'image', 'https://picsum.photos/seed/2018/800/600', NULL, 800, 600, 1),
(31, 'image', 'https://picsum.photos/seed/2019/800/600', NULL, 800, 600, 2),

-- 新闻 32（text）
(32, 'image', 'https://picsum.photos/seed/2020/800/600', NULL, 800, 600, 1),

-- 新闻 33
(33, 'image', 'https://picsum.photos/seed/2021/800/600', NULL, 800, 600, 1),
(33, 'image', 'https://picsum.photos/seed/2022/800/600', NULL, 800, 600, 2),

-- 新闻 34（text）
(34, 'image', 'https://picsum.photos/seed/2023/800/600', NULL, 800, 600, 1),

-- 新闻 35
(35, 'image', 'https://picsum.photos/seed/2024/800/600', NULL, 800, 600, 1),
(35, 'image', 'https://picsum.photos/seed/2025/800/600', NULL, 800, 600, 2),

-- 新闻 36（text）
(36, 'image', 'https://picsum.photos/seed/2026/800/600', NULL, 800, 600, 1),

-- 新闻 37（text）
(37, 'image', 'https://picsum.photos/seed/2027/800/600', NULL, 800, 600, 1),

-- 新闻 38
(38, 'image', 'https://picsum.photos/seed/2028/800/600', NULL, 800, 600, 1),
(38, 'image', 'https://picsum.photos/seed/2029/800/600', NULL, 800, 600, 2),

-- 新闻 39（text）
(39, 'image', 'https://picsum.photos/seed/2030/800/600', NULL, 800, 600, 1),

-- 新闻 40
(40, 'image', 'https://picsum.photos/seed/2031/800/600', NULL, 800, 600, 1),
(40, 'image', 'https://picsum.photos/seed/2032/800/600', NULL, 800, 600, 2);

```

```
INSERT INTO media (news_id, media_type, url, cover_url, width, height, order_index)
VALUES
-- 新闻 41（text）
(41, 'image', 'https://picsum.photos/seed/3001/800/600', NULL, 800, 600, 1),

-- 新闻 42
(42, 'image', 'https://picsum.photos/seed/3002/800/600', NULL, 800, 600, 1),
(42, 'image', 'https://picsum.photos/seed/3003/800/600', NULL, 800, 600, 2),

-- 新闻 43（text）
(43, 'image', 'https://picsum.photos/seed/3004/800/600', NULL, 800, 600, 1),

-- 新闻 44（video）
(44, 'video',
 'https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',
 'https://picsum.photos/seed/3005/800/600',
 NULL, NULL, 1),
(44, 'image', 'https://picsum.photos/seed/3006/800/600', NULL, 800, 600, 2),

-- 新闻 45（text）
(45, 'image', 'https://picsum.photos/seed/3007/800/600', NULL, 800, 600, 1),

-- 新闻 46
(46, 'image', 'https://picsum.photos/seed/3008/800/600', NULL, 800, 600, 1),
(46, 'image', 'https://picsum.photos/seed/3009/800/600', NULL, 800, 600, 2),

-- 新闻 47
(47, 'image', 'https://picsum.photos/seed/3010/800/600', NULL, 800, 600, 1),
(47, 'image', 'https://picsum.photos/seed/3011/800/600', NULL, 800, 600, 2),

-- 新闻 48（text）
(48, 'image', 'https://picsum.photos/seed/3012/800/600', NULL, 800, 600, 1),

-- 新闻 49（text）
(49, 'image', 'https://picsum.photos/seed/3013/800/600', NULL, 800, 600, 1),

-- 新闻 50
(50, 'image', 'https://picsum.photos/seed/3014/800/600', NULL, 800, 600, 1),
(50, 'image', 'https://picsum.photos/seed/3015/800/600', NULL, 800, 600, 2),

-- 新闻 51
(51, 'image', 'https://picsum.photos/seed/3016/800/600', NULL, 800, 600, 1),
(51, 'image', 'https://picsum.photos/seed/3017/800/600', NULL, 800, 600, 2),

-- 新闻 52（text）
(52, 'image', 'https://picsum.photos/seed/3018/800/600', NULL, 800, 600, 1),

-- 新闻 53
(53, 'image', 'https://picsum.photos/seed/3019/800/600', NULL, 800, 600, 1),
(53, 'image', 'https://picsum.photos/seed/3020/800/600', NULL, 800, 600, 2),

-- 新闻 54（text）
(54, 'image', 'https://picsum.photos/seed/3021/800/600', NULL, 800, 600, 1),

-- 新闻 55
(55, 'image', 'https://picsum.photos/seed/3022/800/600', NULL, 800, 600, 1),
(55, 'image', 'https://picsum.photos/seed/3023/800/600', NULL, 800, 600, 2),

-- 新闻 56（text）
(56, 'image', 'https://picsum.photos/seed/3024/800/600', NULL, 800, 600, 1),

-- 新闻 57（video）
(57, 'video',
 'https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4',
 'https://picsum.photos/seed/3025/800/600',
 NULL, NULL, 1),
(57, 'image', 'https://picsum.photos/seed/3026/800/600', NULL, 800, 600, 2),

-- 新闻 58（text）
(58, 'image', 'https://picsum.photos/seed/3027/800/600', NULL, 800, 600, 1),

-- 新闻 59
(59, 'image', 'https://picsum.photos/seed/3028/800/600', NULL, 800, 600, 1),
(59, 'image', 'https://picsum.photos/seed/3029/800/600', NULL, 800, 600, 2),

-- 新闻 60（text）
(60, 'image', 'https://picsum.photos/seed/3030/800/600', NULL, 800, 600, 1);

```

```
INSERT INTO media (news_id, media_type, url, cover_url, width, height, order_index)
VALUES
-- 新闻 61（text）
(61, 'image', 'https://picsum.photos/seed/4001/800/600', NULL, 800, 600, 1),

-- 新闻 62
(62, 'image', 'https://picsum.photos/seed/4002/800/600', NULL, 800, 600, 1),
(62, 'image', 'https://picsum.photos/seed/4003/800/600', NULL, 800, 600, 2),

-- 新闻 63（text）
(63, 'image', 'https://picsum.photos/seed/4004/800/600', NULL, 800, 600, 1),

-- 新闻 64
(64, 'image', 'https://picsum.photos/seed/4005/800/600', NULL, 800, 600, 1),
(64, 'image', 'https://picsum.photos/seed/4006/800/600', NULL, 800, 600, 2),

-- 新闻 65
(65, 'image', 'https://picsum.photos/seed/4007/800/600', NULL, 800, 600, 1),
(65, 'image', 'https://picsum.photos/seed/4008/800/600', NULL, 800, 600, 2),

-- 新闻 66（text）
(66, 'image', 'https://picsum.photos/seed/4009/800/600', NULL, 800, 600, 1),

-- 新闻 67（text）
(67, 'image', 'https://picsum.photos/seed/4010/800/600', NULL, 800, 600, 1),

-- 新闻 68
(68, 'image', 'https://picsum.photos/seed/4011/800/600', NULL, 800, 600, 1),
(68, 'image', 'https://picsum.photos/seed/4012/800/600', NULL, 800, 600, 2),

-- 新闻 69
(69, 'image', 'https://picsum.photos/seed/4013/800/600', NULL, 800, 600, 1),
(69, 'image', 'https://picsum.photos/seed/4014/800/600', NULL, 800, 600, 2),

-- 新闻 70（text）
(70, 'image', 'https://picsum.photos/seed/4015/800/600', NULL, 800, 600, 1),

-- 新闻 71
(71, 'image', 'https://picsum.photos/seed/4016/800/600', NULL, 800, 600, 1),
(71, 'image', 'https://picsum.photos/seed/4017/800/600', NULL, 800, 600, 2),

-- 新闻 72（text）
(72, 'image', 'https://picsum.photos/seed/4018/800/600', NULL, 800, 600, 1),

-- 新闻 73
(73, 'image', 'https://picsum.photos/seed/4019/800/600', NULL, 800, 600, 1),
(73, 'image', 'https://picsum.photos/seed/4020/800/600', NULL, 800, 600, 2),

-- 新闻 74（text）
(74, 'image', 'https://picsum.photos/seed/4021/800/600', NULL, 800, 600, 1),

-- 新闻 75
(75, 'image', 'https://picsum.photos/seed/4022/800/600', NULL, 800, 600, 1),
(75, 'image', 'https://picsum.photos/seed/4023/800/600', NULL, 800, 600, 2),

-- 新闻 76（text）
(76, 'image', 'https://picsum.photos/seed/4024/800/600', NULL, 800, 600, 1),

-- 新闻 77（text）
(77, 'image', 'https://picsum.photos/seed/4025/800/600', NULL, 800, 600, 1),

-- 新闻 78
(78, 'image', 'https://picsum.photos/seed/4026/800/600', NULL, 800, 600, 1),
(78, 'image', 'https://picsum.photos/seed/4027/800/600', NULL, 800, 600, 2),

-- 新闻 79（text）
(79, 'image', 'https://picsum.photos/seed/4028/800/600', NULL, 800, 600, 1),

-- 新闻 80
(80, 'image', 'https://picsum.photos/seed/4029/800/600', NULL, 800, 600, 1),
(80, 'image', 'https://picsum.photos/seed/4030/800/600', NULL, 800, 600, 2);

```

status

```
INSERT INTO stats (news_id, like_count, comment_count, favorite_count, share_count, play_count, version)
VALUES
(1, 1520, 120, 80, 45, 12800, 1),
(2, 980, 66, 42, 30, 10200, 1),
(3, 2230, 150, 120, 70, 15800, 1),
(4, 1750, 90, 60, 48, 14200, 1),
(5, 2600, 210, 150, 110, 18500, 1),
(6, 3100, 260, 180, 140, 98200, 1),
(7, 1450, 80, 55, 35, 11800, 1),
(8, 920, 40, 30, 22, 8400, 1),
(9, 2000, 110, 90, 60, 17000, 1),
(10, 1880, 95, 75, 50, 15000, 1),

(11, 1320, 60, 43, 28, 11200, 1),
(12, 980, 55, 38, 26, 8800, 1),
(13, 2450, 140, 100, 70, 16000, 1),
(14, 1880, 90, 62, 50, 13800, 1),
(15, 2750, 180, 130, 98, 120500, 1),
(16, 1500, 70, 48, 33, 10200, 1),
(17, 1650, 85, 60, 40, 12500, 1),
(18, 900, 44, 30, 20, 7600, 1),
(19, 2100, 120, 90, 60, 14000, 1),
(20, 1750, 88, 72, 45, 12100, 1),

(21, 1200, 70, 50, 30, 11000, 1),
(22, 1880, 92, 65, 44, 14500, 1),
(23, 1600, 85, 58, 39, 11800, 1),
(24, 2100, 130, 95, 70, 17500, 1),
(25, 980, 42, 33, 20, 7800, 1),
(26, 3200, 250, 180, 120, 105000, 1),
(27, 1250, 65, 48, 33, 9800, 1),
(28, 1750, 90, 72, 45, 13800, 1),
(29, 2100, 118, 88, 68, 16000, 1),
(30, 1500, 70, 55, 36, 11200, 1),

(31, 1100, 54, 40, 28, 9200, 1),
(32, 1880, 96, 70, 50, 14500, 1),
(33, 2300, 130, 95, 68, 17500, 1),
(34, 950, 48, 35, 22, 8100, 1),
(35, 2000, 115, 88, 66, 15800, 1),
(36, 2700, 160, 120, 90, 13000, 1),
(37, 1500, 82, 56, 38, 10800, 1),
(38, 980, 44, 32, 20, 7600, 1),
(39, 2200, 125, 92, 70, 16500, 1),
(40, 1750, 90, 68, 48, 13800, 1),

(41, 1420, 70, 52, 33, 11200, 1),
(42, 1980, 88, 65, 45, 13600, 1),
(43, 2250, 140, 98, 75, 16800, 1),
(44, 1550, 78, 60, 40, 12000, 1),
(45, 2850, 200, 150, 110, 122000, 1),
(46, 1350, 68, 50, 32, 10200, 1),
(47, 1650, 82, 58, 38, 11800, 1),
(48, 900, 42, 30, 20, 7000, 1),
(49, 2300, 135, 105, 72, 17500, 1),
(50, 1880, 95, 72, 48, 13800, 1),

(51, 1200, 66, 48, 30, 11000, 1),
(52, 950, 44, 35, 22, 8200, 1),
(53, 2600, 160, 120, 85, 15600, 1),
(54, 1750, 90, 65, 45, 12200, 1),
(55, 3100, 240, 180, 120, 130500, 1),
(56, 1320, 60, 45, 28, 10800, 1),
(57, 1680, 85, 62, 40, 12500, 1),
(58, 980, 48, 32, 20, 7600, 1),
(59, 2150, 118, 88, 58, 16000, 1),
(60, 1500, 70, 55, 36, 11000, 1),

(61, 1100, 54, 40, 28, 9000, 1),
(62, 1750, 90, 65, 45, 13800, 1),
(63, 2300, 135, 98, 72, 16800, 1),
(64, 1500, 75, 56, 40, 11800, 1),
(65, 2000, 120, 88, 60, 17000, 1),
(66, 1250, 65, 48, 32, 9800, 1),
(67, 1600, 78, 60, 38, 12500, 1),
(68, 880, 40, 30, 18, 7200, 1),
(69, 2100, 115, 85, 66, 15800, 1),
(70, 1800, 90, 70, 50, 14000, 1),

(71, 2600, 170, 130, 90, 110500, 1),
(72, 2400, 150, 120, 85, 98000, 1),
(73, 1500, 82, 60, 38, 11800, 1),
(74, 1880, 96, 75, 52, 13800, 1),
(75, 2300, 135, 100, 70, 17500, 1),
(76, 2100, 118, 88, 60, 16000, 1),
(77, 2450, 140, 105, 78, 18000, 1),
(78, 1980, 90, 70, 48, 14200, 1),
(79, 1750, 88, 68, 45, 12000, 1),
(80, 1650, 82, 58, 40, 11000, 1);

```

