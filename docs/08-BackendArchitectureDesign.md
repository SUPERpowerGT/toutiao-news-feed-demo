# **08. 后端架构设计（Go + DDD 风格）**

本章节描述推荐流 Demo 后端的整体架构，包括分层结构、目录组织、主要流程和数据持久化方案。本项目采用 Go 语言，并以“轻量级 DDD（Domain-Driven Design）”方式划分业务边界，以保证代码结构清晰、可维护、可扩展。

------

## **8.1 架构分层结构**

后端代码按照“API → Application → Domain → Infrastructure”四层组织，职责边界如下：

```
backend/
 ├─ api/            // HTTP Handler / 路由
 ├─ application/    // 应用服务（UseCase：获取推荐流）
 ├─ domain/         // 领域模型（News / FeedItem）+ Repository 接口
 ├─ infrastructure/ // Repository 实现、PostgreSQL 访问、配置
 ├─ middleware/     // 日志、Panic 恢复
 └─ main.go
```

### **API 层（api/）**

- 提供 HTTP 接口，如：`GET /api/v1/feed`
- 解析输入参数（cursor、limit、scene）
- 调用 Application 层
- 输出统一 JSON 响应格式

### **Application 层（application/）**

- 实现“推荐流查询”“新闻详情查询”等用例逻辑
- 维护分页逻辑（cursor-based pagination）
- 对接 Domain 层的 Repository 接口
- 不依赖具体数据库实现

### **Domain 层（domain/）**

- 定义核心领域模型：`FeedItem`、`News`、`Author`
- 定义仓储接口：`FeedRepository`、`NewsRepository`
- 包含基础的业务规则，如 cursor 结构与排序优先级

### **Infrastructure 层（infrastructure/）**

- PostgreSQL 数据访问（Repository 实现）
- 数据库连接、配置加载、表迁移脚本
- 存放与技术细节相关的代码

这种分层结构保证：

- API 与业务逻辑解耦
- 业务逻辑与数据库解耦
- Repository 可轻松替换（例如后续接入 Redis 或 mock）

------

## **8.2 推荐流分页与排序策略**

推荐流返回内容由 `feed_item` 表驱动。分页方式采用：

### **cursor + limit 模式（推荐系统常用）**

相比 page/pageSize：

- 不会出现“翻页错乱”
- 新数据不会打乱用户正在浏览的结果
- 性能更稳定

### **cursor 设计**

cursor 由以下字段组合编码：

- `publish_time`（主排序字段）
- `seq_id`（同一时刻的严格顺序）

服务端从 cursor 中解析出：

```
Cursor{
    PublishTime: xxx
    SeqId: xxx
}
```

用于构造 SQL 的 WHERE 条件。

### **排序规则**

推荐流按以下优先级排序：

1. **权重 weight（推荐算法输出）**
2. **发布时间 publish_time**
3. **seq_id（严格的全局顺序）**

可适应未来引入简单推荐策略。

------

## **8.3 核心流程：查询推荐流（GET /api/v1/feed）**

### 步骤 1：API 层解析参数

- scene（频道）
- cursor（分页游标）
- limit（单页数量）

### 步骤 2：Application 层处理业务逻辑

- 解析 cursor
- 调用 `FeedRepository.QueryFeed()`
- 批量查询关联的新闻、媒体、作者、统计信息
- 按卡片类型转换为前端需要的结构体（DTO）

### 步骤 3：Infrastructure 层访问数据库

Repository 按 cursor 执行分页查询（PostgreSQL）：

```
ORDER BY publish_time DESC, seq_id DESC
LIMIT :limit
```

### 步骤 4：构造下一页 cursor

取当前列表最后一条记录的：

- publish_time
- seq_id

进行编码。

------

## **8.4 数据持久化设计（后端视角）**

后端基于 PostgreSQL 做数据持久化。
 主要存储内容包括：

- `news`：新闻主表
- `news_content`：正文表（HTML/JSON）
- `media`：多媒体资源
- `author`：作者信息
- `stats`：统计数据
- `feed_item`：推荐流条目（排序驱动）

数据库索引：

- `feed_item(scene, publish_time, seq_id)` 用于 cursor 分页
- `news(publish_time)`
- `media(news_id)`
- `stats(news_id)`

表结构与前端 Room Entity 保持语义一致，降低心智负担。

------

## **8.5 CI/CD（课程级别）**

采用简单的 CI 配置，通过 GitHub Actions 完成自动化构建与基本检查。

### 流水线内容：

1. 拉取代码
2. 设置 Go 环境
3. 执行单元测试：`go test ./...`
4. 构建后端可执行文件：`go build ./...`
    5.（可选）构建 Docker 镜像

若未来部署到 Render/Railway，可在流水线中增加自动部署步骤。

------

## **8.6 测试策略**

### 后端测试

- Application 层：
  - cursor 解析
  - 分页逻辑
- Repository 层：
  - feed_item 分页查询
  - news/media/stats 的数据聚合

### 端到端测试（可选）

- 测试 `/api/v1/feed` 接口返回结构是否符合客户端需要

------

## **8.7 日志与基础监控**

后端提供基础中间件：

- 请求日志（方法、路径、耗时）
- Panic 恢复，保证服务不崩溃
- 简易计数器（成功 / 失败请求数）

客户端记录网络异常原因，在弱网场景便于定位问题。

------

## **8.8 小结**

本后端架构基于 Go + 轻量 DDD 分层，通过清晰的“API → Application → Domain → Infrastructure”方式组织代码，实现结构清晰、边界明确的推荐流服务。
 设计重点包括：

- cursor-based 分页
- 数据聚合（news + media + author + stats）
- 可扩展的 repository 层
- 与前端 Room 缓存模型语义一致

此架构既能满足本次 Demo 的开发目标，也为未来扩展简单推荐策略、加入缓存、分服务拆分等提供了基础。