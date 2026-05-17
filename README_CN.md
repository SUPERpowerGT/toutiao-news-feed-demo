<p align="right">
  <b>简体中文</b> |
  <a href="README.md">English</a>
</p>

<p align="center">
  <img src="./README.assets/ic_launcher.png" width="120" />
</p>

<h1 align="center">今日头条（JINRITOUTIAO）</h1>

<p align="center">
  一个基于 Android、Go 和 PostgreSQL 的今日头条风格推荐流 Demo。
</p>

# 项目简介

这是一个偏完整的端到端推荐流演示项目，灵感来自今日头条首页信息流。仓库内已经包含：

- Kotlin + Jetpack Compose 实现的 Android 客户端
- Go 后端服务
- PostgreSQL 数据存储与分页
- Docker Compose 本地启动方案

项目适合做训练营作业展示、架构练习和移动端联调。当前已经具备推荐流首屏加载、下拉刷新、上拉分页、预置 mock 数据，以及“手动追加一批更晚时间数据”这种刷新测试能力。

# 技术栈

- Android Studio Hedgehog / Koala
- Kotlin + Jetpack Compose
- Go 1.22+
- PostgreSQL 16
- Docker Compose v2

# 项目结构

```text
ToutiaoAndroid/                   Android 客户端
backend/                          Go 后端
docker/                           PostgreSQL 初始化脚本和本地数据
scripts/                          本地辅助脚本
docker-compose.dev.yml            开发环境 compose
docker-compose.prod.yml           全栈 Docker compose
docs/                             设计文档和开发日志
```

# 核心能力

## Android 客户端

- Compose 推荐流页面
- 今日头条风格自定义下拉刷新
- 基于 cursor 的无限分页
- 多卡片类型混排
- Room 本地缓存
- 官方置顶卡片 + 普通推荐流卡片

## Go 后端

- `/api/v1/feed` 支持首屏、刷新、加载更多
- PostgreSQL 驱动的 cursor 分页
- 内置 Demo mock 数据
- `/seed` 用于重置整套数据
- `/seed/append` 用于追加一批“更晚时间”的新内容，方便测试下拉刷新

# 快速开始

## 推荐：后端和数据库都走 Docker

```bash
docker compose -f docker-compose.prod.yml up --build -d
curl http://localhost:8080/seed
```

## 可选：数据库走 Docker，后端本地运行

```bash
docker compose -f docker-compose.dev.yml up -d
cd backend
go run main.go
curl http://localhost:8080/seed
```

本地运行后端时，默认数据库配置是：

```text
DB_HOST=localhost
DB_PORT=54320
DB_USER=toutiao
DB_PASSWORD=toutiao
DB_NAME=toutiao
```

# Android 客户端运行

在 Android Studio 中打开项目并运行 `ToutiaoAndroid`。

当前客户端默认请求地址是：

```text
http://10.0.2.2:8080/
```

也就是说：

- 你本机访问后端：`http://localhost:8080`
- Android 模拟器访问宿主机：`http://10.0.2.2:8080`

# Seed 与刷新测试数据

数据库初始化 SQL 只负责建表，不会自动写入推荐流业务数据。实际 mock 数据由后端 seed 逻辑插入。

## 重置整套 Demo 数据

```bash
curl http://localhost:8080/seed
```

这个接口会清空并重建当前演示数据。

注意：

- `/seed` 对演示数据来说是“重置型”操作，会把当前数据清掉再重新灌入
- 如果你想回到一个确定、干净的演示状态，这个接口很好用

## 追加一批“更新”的内容，用于测试下拉刷新

使用脚本：

```bash
./scripts/append_refresh_data.sh
```

默认会追加 5 条新的内容。

也可以手动指定数量：

```bash
./scripts/append_refresh_data.sh 3
```

它底层调用的是：

```text
GET /seed/append?count=N
```

这个脚本很适合联调下拉刷新，因为刷新逻辑依赖“数据库里确实存在更晚的 `publish_time` 数据”。

## 一步完成“重置 + 追加刷新数据”

如果你想先把整套数据重置干净，再立刻补一批新的刷新数据，可以直接执行：

```bash
./scripts/reset_and_append_refresh_data.sh
```

也可以手动指定数量：

```bash
./scripts/reset_and_append_refresh_data.sh 5
```

这个脚本本质上会顺序执行：

1. `GET /seed`
2. `GET /seed/append?count=N`

# 常用接口

- `GET /health`
- `GET /seed`
- `GET /seed/append?count=5`
- `GET /api/v1/feed`

例如：

```bash
curl "http://localhost:8080/api/v1/feed?limit=3"
```

# Docker 说明

## `docker-compose.dev.yml`

适合轻量开发：

- 默认只启动 PostgreSQL
- 数据库暴露在 `localhost:54320`
- 提供了一个挂在 `fullstack` profile 下的可选后端服务
- PostgreSQL 数据会持久化在 `./docker/db/data`

## `docker-compose.prod.yml`

适合整套容器化运行：

- 同时启动 PostgreSQL 和后端
- 后端通过容器网络访问数据库：`db:5432`
- 包含数据库健康检查，避免后端抢跑

## 初始化 SQL 与持久化数据

- `docker/db/init/01-schema.sql` 只负责建表
- 业务 mock 数据需要通过 `GET /seed` 插入
- 由于 PostgreSQL 数据持久化在 `./docker/db/data`，初始化 SQL 通常只会在“第一次干净建库”时执行
- 如果你改了初始化 SQL 且想让它重新执行，需要手动清理数据库数据目录或重建卷

# 常见操作流程

## 测试下拉刷新

```bash
./scripts/append_refresh_data.sh 5
```

然后回到客户端执行下拉刷新。

## 一条命令重置并准备刷新数据

```bash
./scripts/reset_and_append_refresh_data.sh 5
```

如果你想在演示前快速恢复成“干净基础数据 + 一批可刷出的新内容”，这个命令会更顺手。

# 常见问题

## `feed` 接口为空或者还是旧数据

执行：

```bash
curl http://localhost:8080/seed
```

重新灌入基础数据。

## 下拉刷新拿不到新内容

这通常不是接口坏了，而是数据库里没有比当前首条更晚的 `publish_time`。

先追加新数据：

```bash
./scripts/append_refresh_data.sh
```

或者一步重置并补新数据：

```bash
./scripts/reset_and_append_refresh_data.sh
```

再回到客户端刷新。

## Android 连接不上后端

请检查：

- `http://localhost:8080/health` 是否正常
- 模拟器访问是否使用 `10.0.2.2`
- 如果你刚改过 Go 代码，Docker 后端是否已经重新构建

## Android 还是显示旧内容

这个项目用了 Room 本地缓存。如果后端数据已经变了，但客户端看起来还是旧的，可以：

- 先手动下拉刷新一次
- 或者清应用数据 / 卸载重装

# 更多文档

更多设计说明和开发过程记录见 [`docs/`](./docs)。

# License

MIT。
