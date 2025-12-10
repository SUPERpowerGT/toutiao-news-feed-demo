<p align="right">
  <b>简体中文</b> |
  <a href="README.md">英文</a>
</p>


<p align="center">
  <img src="./README.assets/ic_launcher.png" width="120" />
</p>

<h1 align="center">今日头条（JINRITOUTIAO）</h1>

<p align="center">
  一款高度还原 <b>今日头条（Toutiao）</b> 新闻流体验的轻量级 Demo<br/>
  Android · Kotlin · Jetpack Compose · Go  · PostgreSQL
</p>

<p align="center">
  本项目基于 <b>字节跳动工程训练营</b> 实战任务开发，使用现代 Android 与 Go 架构实现一个端到端的头条推荐流 Demo
</p>



# 📱 项目简介

这是一个精致的小型 **今日头条风格推荐流应用 Demo**。

项目展示了一个现代的 **Android + Go 后端** 实战架构，包含：

- **Android** — Kotlin · Jetpack Compose · MVVM  
- **后端** — Go · DDD 模块化设计  
- **数据库** — PostgreSQL  
- **环境** — Docker Compose 本地集群  

> 当前仓库主要包含 Demo 的核心代码逻辑。  
> 完整文档、架构图、API 说明、开发日志均托管在飞书文档中心。



# 🎬 Demo 演示视频
https://github.com/user-attachments/assets/7af4154d-5152-486e-ba55-549284fff178



# ✨ 核心功能特色

### Android 客户端（Jetpack Compose）

- 干净的 MVVM 架构 + Repository 模式  
- 自定义今日头条风格下拉刷新（物理动画 + Lottie）  
- 基于 **cursor** 的无限加载机制  
- 多类型卡片渲染体系：
  - 文字卡片  
  - 图片卡片  
  - 视频卡片  
  - 官方置顶卡片  
- Skeleton 骨架屏加载  
- Room 本地缓存，支持首页“秒开”  
- 自适应启动图标（今日头条风格）  
- 模块化 UI 设计：导航、主题、通用组件  

### Go 后端服务

- 基于 DDD 的模块化目录结构：`api`, `domain`, `service`, `infrastructure`  
- 高效光标分页（cursor pagination），适配移动端推荐流  
- 规范化数据库模型（feed_item / author / media / stats）  
- 预置 Demo 数据集，克隆即用  
- 完整 Docker 化开发环境  



# ⚙️ 环境要求

### 🧩 运行环境

- Android Studio Hedgehog / Koala  
- Go 1.22+  
- PostgreSQL 16（通过 Docker 启动）  
- Docker / Docker Compose v2  

### 🔌 外部服务

- PostgreSQL（本地 Docker 实例）

### 🔧 开发辅助工具

- Postman / cURL（调试 API）  
- Jetpack Compose 预览工具  
- GoLand / VSCode（可选）  



# 🚀 快速开始

### 启动后端与数据库

启动 PostgreSQL（Docker）

```bash
docker compose -f docker-compose.dev.yml up --build

```

启动 Go 后端

```
cd backend
go run main.go
```

后端服务默认运行在：

```
http://10.0.2.2:8080    ← Android 模拟器访问宿主机网关
```

### 启动 Android 客户端

```
cd ToutiaoAndroid
```

在 Android Studio 中打开项目，选择模拟器运行，推荐配置：

```
Pixel 6 · API 34
```

# 📘 完整文档（飞书）

本项目的完整技术文档包括但不限于：

- 架构设计与模块划分
- C4 架构图
- API 规格说明
- 数据模型与字段设计
- 推荐流卡片渲染流程
- 本地缓存与离线策略
- 开发日志与迭代规划

可在飞书文档中心查看：

👉 **飞书文档中心**

<p align="left">   <a href="https://ai.feishu.cn/wiki/VTX4wVANsikETMkhyAfcUBX2n9f" target="_blank">     📘 点击进入（Feishu Wiki）   </a> </p>



# 📁 项目结构

```
ToutiaoAndroid/                   # Android 客户端
  ├── ui/                         # Compose UI（界面、组件、主题）
  ├── data/                       # Repository + Retrofit/Room 数据源
  ├── domain/                     # 领域模型 & UseCase
  └── common/                     # 公共工具与通用代码

backend/                          # Go 后端服务
  ├── api/                        # HTTP 路由与 Handler
  ├── domain/                     # 领域模型与接口定义
  ├── service/                    # 业务逻辑实现
  └── infrastructure/             # PostgreSQL 仓储实现、配置等

docker/                           # 数据库初始化与持久化
  └── db/
      ├── data/                   # PostgreSQL 持久化数据卷
      └── init/                   # 初始化 SQL 脚本

docker-compose.dev.yml            # 本地开发环境 PostgreSQL 配置
```



# 📄 许可

本项目基于 **MIT License** 开源。

本仓库仅用于学习与 Demo 展示。
