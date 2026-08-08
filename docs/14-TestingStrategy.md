# 14. Testing Strategy

## 14.1 Purpose

This document supplements the project with a testing-oriented view, focused on three parts that are commonly required in coursework presentations:

- Unit Testing
- Integration Testing
- Stress Testing

The goal is not only to describe ideal testing theory, but to explain testing in the context of the current repository:

- Android client in `ToutiaoAndroid/`
- Go backend in `backend/`
- PostgreSQL and Docker Compose environment in `docker/` and root compose files
- Feed demo workflow based on `/seed` and `/seed/append`

This document is written so it can be used both as:

- a project design appendix
- a presentation note for a 2-minute testing section

---

## 14.2 Testing Scope in This Project

The core test target of this repository is the recommendation feed pipeline rather than a full social/news platform.

The most important business path is:

1. Initial feed load
2. Pull-to-refresh
3. Load more with cursor pagination
4. Top 5 official content in `recommend`
5. Recommendation reason and recommendation score generation
6. Client rendering of mixed feed cards

Therefore, testing should focus on the following layers:

- Client state transitions
- Backend feed business logic
- API and database collaboration
- Performance and stability under repeated refresh/load-more behavior

---

## 14.3 Current Testing Status in the Repository

### 14.3.1 Existing Automated Test Files

Current Android test files:

- [ExampleUnitTest.kt](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/test/java/com/xuziyi/toutiaoandroid/ExampleUnitTest.kt:1)
- [ExampleInstrumentedTest.kt](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/androidTest/java/com/xuziyi/toutiaoandroid/ExampleInstrumentedTest.kt:1)
- [FeedViewModelTest.kt](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/test/java/com/xuziyi/toutiaoandroid/ui/feed/FeedViewModelTest.kt:1)

Current backend automated test files:

- none yet

### 14.3.2 Practical Meaning of Current Tests

At the moment:

- `ExampleUnitTest.kt` is only a template test
- `ExampleInstrumentedTest.kt` only checks the application package name
- `FeedViewModelTest.kt` exists but is currently ignored

This means the repository already has a test structure, but not yet enough real business-level automated coverage.

### 14.3.3 Current Validation Methods Actually Used

In day-to-day development, the project is currently validated mainly through:

- backend compile check:
  - `cd backend && go build ./...`
- Android compile check:
  - `cd ToutiaoAndroid && sh gradlew app:compileDebugKotlin`
- manual API verification:
  - `GET /health`
  - `GET /seed`
  - `GET /seed/append`
  - `GET /api/v1/feed`
- manual end-to-end verification in emulator:
  - initial feed load
  - pull-to-refresh
  - load more
  - refresh banner
  - scene switching

So the project already has a usable validation workflow, but it is still stronger in manual verification than in automated testing.

---

## 14.4 Unit Testing

## 14.4.1 Definition in This Project

Unit testing means testing a small piece of logic in isolation, without relying on a real server, real database, or full UI environment.

In this project, unit testing is most valuable in:

- Android ViewModel logic
- Android UseCase logic
- backend feed service logic
- recommendation score and recommendation reason generation

## 14.4.2 Best Unit Test Targets

### Android side

The best Android unit test target is [FeedViewModel.kt](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/src/main/java/com/xuziyi/toutiaoandroid/ui/feed/FeedViewModel.kt:1).

Recommended cases:

1. Initial load success
- When `loadInitialFeedUseCase` returns feed data
- `FeedUiState` should move from `Loading` to `Success`
- `officialItems`, `mixedItems`, `nextCursor`, and `hasMore` should be correct

2. Refresh with new items
- When `refreshFeedUseCase` returns newer items
- `showUpdateBanner` should become `true`
- `newCount` should be greater than `0`
- `updateBannerText` should match the expected banner message

3. Refresh with no new items
- When refresh returns no new items
- Banner should still appear
- `updateBannerText` should be `当前已是最新内容`

4. Load more success
- When `loadMoreFeedUseCase` returns older items
- Existing list should be merged correctly
- `nextCursor` should move forward

5. Scene switching
- Switching between `recommend`, `video`, `shenzhen`, `sports`, `finance`, `tech`
- Different scenes should request different backend query paths

### Backend side

The best backend unit test target is [feed_service.go](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/backend/application/feed_service.go:1).

Recommended cases:

1. `recommend` initial load
- should return `scene = recommend`
- should include `top_items`

2. non-`recommend` initial load
- should return empty `top_items`
- should only return scene-specific normal items

3. refresh behavior
- with a newer timestamp, should return only new items
- `latest_publish_time` should be correct

4. recommendation reason generation
- `is_top_official = true` should produce `权威发布`
- official but non-top should produce `官方媒体推荐`
- hot engagement should produce `热门讨论`

5. recommendation score generation
- score should stay stable in a reasonable numeric range
- official or hot items should score higher than ordinary items under the same freshness condition

## 14.4.3 Unit Test Tools Already Prepared

Android test dependencies are already present in [app/build.gradle.kts](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/ToutiaoAndroid/app/build.gradle.kts:1):

- `junit`
- `mockk`
- `kotlinx-coroutines-test`

These are enough to write meaningful ViewModel unit tests without adding new frameworks.

Go backend unit testing can use:

- Go built-in `testing`
- table-driven tests
- mock repository implementations for `FeedItemRepository`

## 14.4.4 Example Commands

Android local unit tests:

```bash
cd ToutiaoAndroid
sh gradlew test
```

Backend unit tests:

```bash
cd backend
go test ./...
```

One-click script:

```bash
./scripts/run_unit_tests.sh
```

## 14.4.5 Expected Benefits

Unit testing gives this project three direct benefits:

- verifies recommendation logic without needing a device
- catches regressions in refresh/load-more state transitions
- makes the recommendation score and reason logic explainable and safe to evolve

---

## 14.5 Integration Testing

## 14.5.1 Definition in This Project

Integration testing verifies whether multiple modules work together correctly.

In this repository, the most meaningful integration path is:

- Go backend + PostgreSQL
- Android client + backend API
- seed/append flow + refresh flow

## 14.5.2 Best Integration Test Scenarios

### Scenario A: Backend + Database integration

This is the most important server-side integration test.

Environment:

- PostgreSQL from [docker-compose.dev.yml](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/docker-compose.dev.yml:1) or [docker-compose.prod.yml](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/docker-compose.prod.yml:1)
- backend started locally or in Docker

Steps:

1. start database
2. call `/seed`
3. call `/api/v1/feed?scene=recommend`
4. verify:
   - `top_items` exists
   - `items` exists
   - `next_cursor` exists
   - `latest_publish_time` exists

Then:

1. call `/seed/append?count=5`
2. call refresh API with previous `latest_publish_time`
3. verify:
   - newer items are returned
   - `reason` and `recommend_score` are present

### Scenario B: Android client + backend integration

This checks whether the client can consume real API data.

Steps:

1. start backend at `localhost:8080`
2. run Android emulator
3. ensure client points to `10.0.2.2:8080`
4. verify:
   - initial feed loads
   - top official cards render on `recommend`
   - pull-to-refresh shows update banner
   - appended data appears at the top after refresh
   - switching to supported scenes loads different content

### Scenario C: Seed data + refresh integration

This is one of the strongest demo-specific integration flows in the project.

The helper scripts are:

- [append_refresh_data.sh](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/scripts/append_refresh_data.sh:1)
- [reset_and_append_refresh_data.sh](/Users/zee/xuziyi/projects/toutiao-news-feed-demo/scripts/reset_and_append_refresh_data.sh:1)

These make integration verification easy and repeatable:

```bash
./scripts/reset_and_append_refresh_data.sh 5
```

This gives a predictable test environment for live presentation.

## 14.5.3 Example Integration Commands

Start DB only:

```bash
docker compose -f docker-compose.dev.yml up -d
```

Start full stack:

```bash
docker compose -f docker-compose.prod.yml up --build -d
```

Reset data:

```bash
curl http://localhost:8080/seed
```

Append newer items:

```bash
./scripts/append_refresh_data.sh 5
```

Query feed:

```bash
curl "http://localhost:8080/api/v1/feed?scene=recommend&limit=5"
```

One-click script:

```bash
./scripts/run_integration_tests.sh 5
```

## 14.5.4 Expected Benefits

Integration testing proves that:

- the backend can read real PostgreSQL data
- refresh behavior depends on real newer timestamps
- the client and server contracts match
- the recommendation demo is not just a static UI mock

---

## 14.6 Stress Testing

## 14.6.1 Definition in This Project

Stress testing checks system behavior under repeated or high-frequency requests.

For this project, stress testing does not need to simulate industrial traffic.
Instead, it should answer practical demo-level questions:

- Can the backend survive repeated feed polling?
- Does pagination stay stable under repeated requests?
- Does refresh still work after many seed append operations?
- Does the system remain responsive under short bursts?

## 14.6.2 Best Stress Test Targets

### Backend feed endpoint

Primary target:

- `GET /api/v1/feed?scene=recommend`

Why:

- it is the core feature of the project
- it performs data aggregation
- it includes top items, normal items, recommendation score, and reason generation

### Repeated refresh workflow

Secondary target:

- alternate between:
  - `/seed/append`
  - `/api/v1/feed?refresh_time=...`

Why:

- this simulates the most demo-relevant high-frequency scenario
- it tests recommendation refresh consistency

## 14.6.3 Recommended Stress Test Method

Simple command-line pressure tools are enough.

Examples:

Using `ab`:

```bash
ab -n 200 -c 20 "http://127.0.0.1:8080/api/v1/feed?scene=recommend&limit=15"
```

Using `hey`:

```bash
hey -n 500 -c 30 "http://127.0.0.1:8080/api/v1/feed?scene=recommend&limit=15"
```

If `ab` or `hey` is unavailable, repeated curl loops can still provide lightweight pressure:

```bash
for i in {1..100}; do
  curl -s "http://localhost:8080/api/v1/feed?scene=recommend&limit=15" > /dev/null
done
```

One-click script:

```bash
./scripts/run_stress_tests.sh 200 20
```

## 14.6.4 Suggested Metrics

For a course project, the following metrics are enough:

- average response time
- max response time
- number of failed requests
- whether the backend crashes
- whether refresh and pagination remain logically correct

It is not necessary to build a full observability platform.
Simple logs and successful repeated responses are already useful evidence.

## 14.6.5 Expected Findings

Stress testing in this project is mainly used to observe:

- whether SQL queries stay responsive
- whether recommendation score and reason generation add noticeable overhead
- whether repeated refresh operations cause logical inconsistency
- whether the backend remains available under small traffic bursts

---

## 14.7 Recommended Test Plan for This Repository

For this project, the most practical testing plan is:

### Layer 1: Compile and static validation

- `cd backend && go build ./...`
- `cd ToutiaoAndroid && sh gradlew app:compileDebugKotlin`

### Layer 2: Unit testing

- `FeedViewModel` state transitions
- backend `FeedService` recommendation logic

### Layer 3: Integration testing

- `/seed`
- `/seed/append`
- `/api/v1/feed`
- emulator refresh verification

### Layer 4: Stress testing

- repeated `recommend` scene requests
- repeated append + refresh workflow

This plan matches the current maturity of the repository much better than trying to introduce very heavy enterprise-style test infrastructure.

## 14.7.1 One-Click Test Scripts Added to This Repository

To make the testing section easier to demonstrate in a terminal, the repository can use the following scripts:

- `./scripts/run_unit_tests.sh`
- `./scripts/run_integration_tests.sh 5`
- `./scripts/run_stress_tests.sh 200 20`
- `./scripts/run_all_tests.sh 5 200 20`

Their roles are:

- `run_unit_tests.sh`
  - runs backend `go test`
  - runs backend `go build`
  - runs Android `sh gradlew test`
  - runs Android `sh gradlew app:compileDebugKotlin`

- `run_integration_tests.sh`
  - checks `/health`
  - resets data with `/seed`
  - checks `recommend` feed response
  - appends new data with `/seed/append`
  - verifies refresh response contains `reason` and `recommend_score`
  - checks a non-recommend scene such as `video`

- `run_stress_tests.sh`
  - stress-tests `GET /api/v1/feed?scene=recommend`
  - prefers `hey`
  - falls back to `ab`
  - if neither exists, uses a repeated `curl` loop

- `run_all_tests.sh`
  - runs unit testing, integration testing, and stress testing in sequence

This makes the testing workflow presentation-friendly, because the tester can execute one command and immediately see `PASS/FAIL` output in the terminal.

---

## 14.8 Limitations of Current Testing

The repository still has several testing limitations:

1. Business unit tests are not yet fully implemented
- `FeedViewModelTest.kt` is still ignored
- backend has no real `_test.go` files yet

2. Integration testing is mostly manual
- current validation is reliable, but not fully scripted

3. Stress testing is not yet standardized
- the repo has helper scripts for data generation
- but no dedicated pressure-test script yet

These are acceptable for the current stage of the project, but should be acknowledged honestly in a school report.

---

## 14.9 Suggested Next Improvements

If testing is to be strengthened in the next iteration, the best sequence is:

1. implement real `FeedViewModel` unit tests
2. add backend `feed_service_test.go`
3. add a simple stress-test helper script, such as:
   - `scripts/stress_feed.sh`
4. add a short testing section to README

This order gives the highest value with the lowest disruption.

---

## 14.10 Two-Minute Presentation Script

The following script is suitable for a short coursework presentation section.

### English Version

This project uses three levels of testing: unit testing, integration testing, and stress testing.

For unit testing, the key focus is the recommendation feed logic. On the Android side, the most important target is the `FeedViewModel`, especially initial loading, pull-to-refresh, load-more, and refresh banner behavior. On the backend side, the best unit test target is the feed service, where we can verify top-item rules, scene behavior, recommendation reasons, and recommendation scores.

For integration testing, we focus on the collaboration between the Go backend, PostgreSQL, and the Android client. The repository already provides a practical workflow using `/seed` and `/seed/append`, so we can reset data, append newer items, and verify that refresh returns new feed content correctly. This is useful because it proves that the system is a real end-to-end demo rather than a static UI.

For stress testing, we mainly test the `/api/v1/feed` endpoint under repeated requests. Since this is a coursework project, the goal is not extreme production-scale benchmarking, but checking whether the backend remains stable, whether response times stay acceptable, and whether refresh and pagination remain correct under burst traffic.

Overall, the project already has a good manual validation workflow, and the next step is to strengthen real automated tests around the core recommendation feed path.

### Chinese Version

本项目的测试主要分为三层：单元测试、集成测试和压力测试。

在单元测试方面，最核心的是推荐流业务逻辑。Android 侧最适合测试的是 `FeedViewModel`，重点包括首屏加载、下拉刷新、上拉分页以及刷新提示 Banner。后端侧最适合测试的是 `FeedService`，因为这里可以验证推荐频道 Top5 规则、不同 scene 的行为、推荐理由以及推荐分数生成逻辑。

在集成测试方面，我们重点验证 Go 后端、PostgreSQL 和 Android 客户端之间是否能够正确协作。仓库里已经提供了 `/seed` 和 `/seed/append` 这套很实用的测试流程，所以可以先重置数据，再追加更新内容，然后验证客户端刷新后是否真的拿到了更晚时间的新数据。这一点可以证明本项目是一个真实联动的端到端系统，而不只是静态页面演示。

在压力测试方面，我们主要针对 `/api/v1/feed` 这个核心接口进行重复请求测试。由于这是课程项目，所以压力测试的目标不是做工业级大规模压测，而是验证后端在短时间重复请求下是否还能保持稳定，响应时间是否可接受，以及刷新和分页逻辑是否还能保持正确。

总体来说，这个项目目前已经具备比较完整的手动验证流程，下一步最值得加强的是围绕推荐流主链路补齐真正的自动化测试。

---

## 14.11 Conclusion

For this repository, testing should be centered around the recommendation feed, because that is the strongest and most complete business path in the project.

The recommended testing strategy is:

- use unit tests to verify isolated recommendation logic
- use integration tests to verify backend, database, and client collaboration
- use stress testing to verify short-burst stability of the feed API

This approach matches both the technical structure of the codebase and the presentation needs of a school project.
