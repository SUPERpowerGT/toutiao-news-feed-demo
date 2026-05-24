<p align="right">
  <a href="README_CN.md">Chinese</a> |
  <b>English</b>
</p>

<p align="center">
  <img src="./README.assets/ic_launcher.png" width="120" />
</p>

<h1 align="center">JINRITOUTIAO</h1>

<p align="center">
  A Toutiao-style news feed demo built with Android, Go, and PostgreSQL.
</p>

# Overview

This repository is a polished end-to-end demo inspired by Jinri Toutiao.
It includes:

- An Android client built with Kotlin, Jetpack Compose, and MVVM
- A Go backend with layered application / infrastructure design
- PostgreSQL for feed storage and pagination
- Docker Compose for local database or full-stack startup

The project is designed for demo, training, and architecture practice. It already includes seeded mock content, cursor-based pagination, pull-to-refresh, load-more behavior, and a local testing script for injecting fresh feed items.

# 🎬 Demo Preview

https://github.com/user-attachments/assets/7af4154d-5152-486e-ba55-549284fff178

# Tech Stack

- Android Studio Hedgehog / Koala
- Kotlin + Jetpack Compose
- Go 1.22+
- PostgreSQL 16
- Docker Compose v2

# Project Structure

```text
ToutiaoAndroid/                   Android client
backend/                          Go backend
docker/                           PostgreSQL init scripts and local data
scripts/                          Local helper scripts
docker-compose.dev.yml            Dev compose file
docker-compose.prod.yml           Full Docker compose file
docs/                             Design notes and development logs
```

# Key Features

## Android

- Compose-based feed UI
- Custom Toutiao-style pull-to-refresh
- Cursor-based infinite scrolling
- Multi-card feed rendering
- Room local cache
- Official top cards + mixed feed cards

## Backend

- `/api/v1/feed` for initial load, refresh, and load-more
- PostgreSQL-backed cursor pagination
- Seeded mock dataset for demo usage
- `/seed` endpoint to reset demo data
- `/seed/append` endpoint to append fresh newer items for refresh testing

# Quick Start

## Recommended: Full stack with Docker

```bash
docker compose -f docker-compose.prod.yml up --build -d
curl http://localhost:8080/seed
```

## Alternative: Database in Docker, backend locally

```bash
docker compose -f docker-compose.dev.yml up -d
cd backend
go run main.go
curl http://localhost:8080/seed
```

Default local DB config:

```text
DB_HOST=localhost
DB_PORT=54320
DB_USER=toutiao
DB_PASSWORD=toutiao
DB_NAME=toutiao
```

# Android Client

Open the project in Android Studio and run the `ToutiaoAndroid` app on an emulator.

The Android client is currently configured to call:

```text
http://10.0.2.2:8080/
```

That means:

- Backend on your Mac: `http://localhost:8080`
- Android emulator access: `http://10.0.2.2:8080`

# Seed and Test Data

The database initialization SQL only creates schema. Feed data is inserted by backend seed logic.

## Reset demo data

```bash
curl http://localhost:8080/seed
```

This will clear and rebuild the demo dataset.

Note:

- `/seed` is destructive for demo data and will rebuild the seeded dataset from scratch
- This is useful for resetting the project to a known state before testing

## Append newer items for refresh testing

Use the helper script:

```bash
./scripts/append_refresh_data.sh
```

By default it appends 5 newer items.

You can also specify a count:

```bash
./scripts/append_refresh_data.sh 3
```

The script calls:

```text
GET /seed/append?count=N
```

This is useful when you want to test pull-to-refresh and need content with newer timestamps.

## Reset and append in one step

If you want a clean dataset and then immediately create a fresh batch for pull-to-refresh testing:

```bash
./scripts/reset_and_append_refresh_data.sh
```

You can also pass a custom count:

```bash
./scripts/reset_and_append_refresh_data.sh 5
```

This script does:

1. `GET /seed`
2. `GET /seed/append?count=N`

# Useful Endpoints

- `GET /health`
- `GET /seed`
- `GET /seed/append?count=5`
- `GET /api/v1/feed`

Example:

```bash
curl "http://localhost:8080/api/v1/feed?limit=3"
```

# Docker Notes

## `docker-compose.dev.yml`

Use this when you want a lighter local workflow:

- Starts PostgreSQL by default
- Exposes database on `localhost:54320`
- Includes an optional `backend` service under the `fullstack` profile
- Persists PostgreSQL data under `./docker/db/data`

## `docker-compose.prod.yml`

Use this when you want both services containerized:

- Starts PostgreSQL and backend together
- Backend connects to database using container network (`db:5432`)
- Includes database healthcheck so backend waits until Postgres is ready

## Seed SQL and persistent data

- `docker/db/init/01-schema.sql` only creates schema
- Business demo data is inserted by `GET /seed`
- Because PostgreSQL data is persisted in `./docker/db/data`, init SQL usually runs only on the first clean database startup
- If you change init SQL and want it to run again, you need to recreate the database data directory or reset the volume manually

# Common Workflow

## Test refresh with new items

```bash
./scripts/append_refresh_data.sh 5
```

Then open the app and pull to refresh.

## Reset and prepare refresh data in one command

```bash
./scripts/reset_and_append_refresh_data.sh 5
```

This is the fastest way to get back to a clean state and still have fresh newer items ready for a pull-to-refresh demo.

# Troubleshooting

## Feed API returns empty or old data

Run:

```bash
curl http://localhost:8080/seed
```

This resets the seeded dataset.

## Refresh returns no new items

That usually means there is no data with a newer `publish_time` than the current top item.

Append newer items first:

```bash
./scripts/append_refresh_data.sh
```

Or reset and prepare them in one go:

```bash
./scripts/reset_and_append_refresh_data.sh
```

## Android app cannot reach backend

Check:

- Backend is healthy on `http://localhost:8080/health`
- Emulator uses `10.0.2.2`, not `localhost`
- Docker backend has been rebuilt if you recently changed Go source

## Android still shows old feed content

This project uses Room local cache. If backend data has changed a lot but the app still looks old:

- Pull to refresh once
- Or clear app data / reinstall the app

# Documentation

More notes and development materials are available in [`docs/`](./docs).

test

# License

MIT.
