

<p align="right">
  <a href="README_CN.md">Chinese</a> |
  <b>English</b>
</p>

<p align="center">
  <img src="./README.assets/ic_launcher.png" width="120" />
</p>

<h1 align="center">JINRITOUTIAO</h1>

<p align="center">
  A minimal yet polished news feed demo inspired by <b>Toutiao (今日头条)</b>.<br/>
  Android · Kotlin · Jetpack Compose · Go Backend · PostgreSQL
</p>
<p align="center">
  A clean end-to-end demo built for the <b>ByteDance Engineering Training Camp</b>, recreating the core Toutiao feed experience with modern Android and Go architecture.
</p>



# 📱 **Toutiao News Feed Demo**

A minimal yet polished **Today’s Headlines (Jinri Toutiao)**–style news feed application.

This project was developed as part of the **ByteDance Engineering Training Camp**, showcasing a clean and modern **Android + Go backend** architecture with:

- **Android** — Kotlin · Jetpack Compose · MVVM  
- **Backend** — Go · DDD layered design  
- **Database** — PostgreSQL  
- **Environment** — Docker Compose local stack  

> This repository highlights the core implementation of the demo.  
> Full documentation, architecture diagrams, API specifications, and development logs are available in Feishu.



# 🎬 Demo Preview

https://github.com/SUPERpowerGT/toutiao-news-feed-demo/releases/download/v0.1.0-demo/demo_video.mp4

# ✨ Features

## **Android Client (Jetpack Compose)**

- Clean MVVM architecture with Repository pattern  
- Fully custom pull-to-refresh interaction (Toutiao-style animation & physics)  
- Infinite feed with cursor-based pagination  
- Multi-type feed card system:
  - Text Card  
  - Image Card  
  - Video Card  
  - Official Top Card  
- Skeleton loading system for fast perceived performance  
- Room-based local caching for instant first-screen loading  
- Adaptive launcher icons following Jinri Toutiao branding  
- Well-organized UI layer with navigation, theming, and modular components  

## **Go Backend**

- DDD-inspired modular design (`api`, `domain`, `service`, `infrastructure`)  
- Efficient cursor-based pagination for mobile feed loading  
- Normalized PostgreSQL schema (feed_item / author / media / stats)  
- Pre-seeded demo dataset for instant development & testing  
- Fully dockerized development environment  



# ⚙️Environment Requirements

🧩 **Runtime Environment**

- Android Studio Hedgehog / Koala
- Go 1.22+
- PostgreSQL 16 (via Docker)
- Docker / Docker Compose v2

🔌 **External Services Used**

- PostgreSQL (local Docker instance)

🔧 **Development Tools**

- Postman / cURL for API testing
- Jetpack Compose tooling
- GoLand or VSCode (optional)



# 🚀 Quick Start

### **Backend + Database**

Start PostgreSQL via Docker:

```
docker compose -f docker-compose.dev.yml up --build
```

Then, in another terminal, start the Go backend:

```
cd backend
go run main.go
```

Backend will be available at:

```
http://10.0.2.2:8080    ← Android emulator gateway
```

### **Android Client**

Open the project in **Android Studio** and run on an emulator:

```
cd ToutiaoAndroid
```

Recommended:

```
Pixel 6 · API 34
```



# 📘 Full Documentation

Comprehensive documentation — including architecture design, C4 diagrams, API specifications,
data models, feed rendering design, local caching strategy, development logs, and future planning — 
is available in the Feishu Documentation Center:

👉 **Feishu Documentation Center**  
<p align="left">
  <a href="https://ai.feishu.cn/wiki/VTX4wVANsikETMkhyAfcUBX2n9f" target="_blank">
    📘 Full Documentation (Feishu Wiki)
  </a>
</p>



# 📁 Project Structure

```
ToutiaoAndroid/                   # Android client
  ├── ui/                         # Compose UI (screens, components, theming)
  ├── data/                       # Repository + Retrofit/Room data sources
  ├── domain/                     # Domain models & use cases
  └── common/                     # Shared utilities and helpers

backend/                          # Go backend service
  ├── api/                        # HTTP handlers & routing
  ├── domain/                     # Core domain models & interfaces
  ├── service/                    # Business logic implementation
  └── infrastructure/             # PostgreSQL repository, configs, DB setup

docker/                           # Database init & persistent storage
  └── db/
      ├── data/                   # PostgreSQL volume (persistent)
      └── init/                   # SQL initialization scripts

docker-compose.dev.yml            # Local PostgreSQL development environment

```



# 📄 License

Distributed under the **MIT License**.

This repository is a training/demo project.

