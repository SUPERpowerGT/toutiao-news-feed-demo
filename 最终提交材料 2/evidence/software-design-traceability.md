# Software Design Traceability Matrix

## Scope

This matrix supports `TeamXX- Technical Assessment - Software Design.mp4`. The selected critical use case is recommendation-feed refresh and cursor pagination. Status is based on the current repository implementation, not only on planned requirements in the report.

Status definitions:

- **Implemented + automated**: implemented and covered by a named automated test.
- **Implemented + demo**: implemented and verified through the live application or integration script, but without a dedicated automated assertion for every UI detail.
- **Scaffold only**: supporting classes exist, but the capability is not active in the current runtime path.
- **Not implemented**: must not be presented as completed.

## Requirement-to-Code-to-Test Matrix

| Requirement | Design responsibility | Current implementation evidence | Test or demonstration evidence | Status |
|---|---|---|---|---|
| FR-01: display the initial feed | `FeedScreen` observes `FeedUiState`; `FeedViewModel` invokes `LoadInitialFeedUseCase` | `ui/feed/FeedScreen.kt`, `ui/feed/FeedViewModel.kt`, `domain/usecase/LoadInitialFeedUseCase.kt` | `FeedViewModelTest.initialLoadPublishesGroupedSuccessState`; App Demo initial-load scene | Implemented + automated |
| FR-02: official Top section plus normal content | Backend service returns `top_items` and `items`; ViewModel separates official and mixed lists | `backend/application/feed_service.go`; `ProcessFeedItemUseCase.kt`; `FeedViewModel.kt` | `TestGetFeedInitialRecommendMergesAndAnnotatesItems`; `initialLoadPublishesGroupedSuccessState` | Implemented + automated |
| FR-03, FR-22, FR-23: mixed card rendering | Domain card type decision followed by one Compose rendering factory | `RenderCardTypeUseCase.kt`; `FeedCardFactory.kt`; `TextCard.kt`; `ImageCard.kt`; `VideoCard.kt`; `OfficalTopCard.kt` | App Demo shows all card types; direct factory unit test remains a recommended addition | Implemented + demo |
| FR-06 to FR-09: pull gesture and refresh lifecycle | Pull gesture components trigger `FeedViewModel.refresh`; state controls animation, fixed header, and rebound | `ToutiaoPullRefresh.kt`; `RawPullRefreshNestedScroll.kt`; `RefreshStateLogic.kt`; `FeedViewModel.refresh()` | `RefreshStateLogicTest`; `FeedViewModelTest.refreshMergesNewItemsAndFinishesRefreshState` | Implemented + automated |
| FR-10: block pagination during refresh | ViewModel guards refresh and load-more state before starting requests | `FeedViewModel.refresh()` and `FeedViewModel.loadMore()` | Existing ViewModel tests cover state completion; simultaneous-operation test remains a recommended addition | Implemented + demo |
| FR-11: updated-item banner | ViewModel builds update text; refresh UI renders it only for a valid non-blank update | `FeedViewModel.buildRefreshMessage`; `UpdateBannerLogic.kt`; `ToutiaoPullRefresh.kt` | `UpdateBannerLogicTest.bannerRequiresFlagAndNonBlankText`; App Demo refresh scene | Implemented + automated |
| FR-12: automatic load more | `FeedScreen` observes the last visible list index and invokes `loadMore()` near the footer | `FeedScreen.kt`; `FeedList.kt`; `FeedViewModel.loadMore()` | App Demo pagination scene | Implemented + demo |
| FR-13, FR-14, FR-36: cursor pagination and unique merge | API transports `next_cursor`; PostgreSQL filters by publish time; ViewModel merges by ID and updates cursor | `FeedRepositoryContract.kt`; `FeedViewModel.loadMore()`; `backend/infrastructure/feed_item_repository_pg.go`; `backend/application/feed_service.go` | `FeedViewModelTest.loadMoreMergesUniqueItemsAndUpdatesCursor`; `TestGetFeedLoadMoreForwardsLimitAndCursorMetadata`; integration script | Implemented + automated |
| FR-15, NFR-06: pagination and request error states | Initial failure becomes `FeedUiState.Error`; pagination failure becomes retryable footer state | `FeedUiState.kt`; `FeedViewModel.kt`; `LoadMoreErrorFooter.kt`; `ErrorScreen` usage in `FeedScreen.kt` | `FeedViewModelTest.initialLoadFailurePublishesErrorState`; App Demo backend-stop and retry scene | Implemented + automated/demo |
| FR-18, FR-37: channel switching | Tabs send scene keys; backend validates and filters by type, city, or category | `FeedScreen.kt`; `FeedViewModel.normalizeScene`; `backend/api/feed_handler.go`; `sceneFilterClause` in PostgreSQL repository | `sceneSwitchReloadsRequestedSceneWithoutOfficialSection`; `TestGetFeedNonRecommendDoesNotMergeTopItems`; channel seed tests | Implemented + automated |
| FR-26, FR-28: skeleton then real content | Loading state selects skeleton; success state selects the feed | `FeedLoadingPlaceholder.kt`; state branch in `FeedScreen.kt` | App Demo launch scene; initial-load ViewModel test validates transition result | Implemented + demo |
| FR-34, FR-35, FR-38: backend assembly and ranking metadata | Service calculates reasons/scores; repository joins news, author, media, statistics, and feed metadata | `backend/application/feed_service.go`; `backend/infrastructure/feed_item_repository_pg.go`; domain models | `TestRecommendationScoreRewardsMatchingOfficialContent`; backend integration verification | Implemented + automated |
| NFR-08: invalid cursor must not crash | Handler rejects malformed cursor, refresh time, limit, method, and unsupported scene | `backend/api/feed_handler.go` | `TestFeedHandlerRejectsInvalidInput` | Implemented + automated |
| NFR-09: extensible card types | Sealed card-type model and central factory isolate rendering variants | `FeedCardType.kt`; `RenderCardTypeUseCase.kt`; `FeedCardFactory.kt` | Existing text/image/video/official implementations provide structural evidence | Implemented + demo |
| NFR-12 to NFR-14: MVVM and state-driven UI | UI observes state; ViewModel invokes use cases through repository contract | `FeedScreen.kt`; `FeedViewModel.kt`; `domain/usecase`; `FeedRepositoryContract.kt` | ViewModel unit-test suite with fake repository | Implemented + automated |
| Stale response protection for refresh/pagination | Request version is captured before async work and checked before state mutation | `requestVersion` checks in `FeedViewModel.kt` | Code inspection; a dedicated stale-response timing test remains a recommended addition | Implemented + demo |
| FR-29 to FR-33, NFR-01, NFR-05: Room cache and offline feed | Initial load returns scene-filtered Room data first and refreshes cache in the background; empty cache loads from network; refresh and pagination responses are upserted | `data/local`; `LocalDataSource.getFeed`; `data/repository/FeedRepository.kt` | `FeedRepositoryTest.successfulInitialLoadWritesRemoteItemsToCache`; `cachedInitialLoadReturnsImmediatelyAndRefreshesCacheInBackground`; `cachedInitialLoadSurvivesOfflineBackgroundRefresh` | Implemented + automated |
| FR-05, FR-16: news detail navigation | Card click carries `newsId`; detail ViewModel loads the backend detail contract and the screen renders content | `AppNavigator.kt`; `NewsDetailViewModel.kt`; `NewsDetailScreen.kt`; `backend/api/news_detail_handler.go` | Live `/api/v1/news/1` returned HTTP 200; App Demo detail flow | Implemented + demo |
| FR-17: automatic video playback | Video detail uses `VideoView`, starts after preparation, pauses with lifecycle, and releases on disposal | `LifecycleVideoPlayer` in `NewsDetailScreen.kt` | App Demo video-detail flow; advanced player instrumentation remains future work | Implemented + demo |

## Critical Refresh Sequence

1. The user pulls down in `ToutiaoPullRefresh`.
2. `RawPullRefreshNestedScroll` reaches its threshold and calls `FeedViewModel.refresh()`.
3. The ViewModel checks state guards, increments `requestVersion`, and records the current version.
4. `RefreshFeedUseCase` invokes `FeedRepositoryContract.refreshFeed(scene, latestPublishTime)`.
5. `FeedRepository` calls `RemoteDataSource`, which uses Retrofit to request `/api/v1/feed` with `refresh_time`.
6. The Go handler validates the request and invokes `FeedService.GetFeed`.
7. `FeedItemRepositoryPG.ListNewer` queries PostgreSQL for records newer than the supplied timestamp.
8. The backend assembles items, recommendation metadata, and the newest timestamp into JSON.
9. Android maps DTOs into domain models.
10. The ViewModel rejects the result if its request version is stale; otherwise, it merges unique IDs, updates state, and completes the refresh UI.

## Critical Pagination Sequence

1. `FeedScreen` detects that the viewport is near the list end.
2. `FeedViewModel.loadMore()` checks `isLoadingMore` and `hasMore` guards.
3. `LoadMoreFeedUseCase` passes the current scene and cursor through the repository contract.
4. The backend validates the cursor and runs the next-page PostgreSQL query.
5. Android merges unique items by ID and replaces `nextCursor` and `hasMore` with server values.
6. Failure preserves existing content and exposes `LoadMoreErrorFooter` for retry.

## Recording Integrity Notes

- Present Room cache fallback with the named repository tests and live offline demonstration.
- Present news detail and basic lifecycle-aware video playback as implemented; keep advanced streaming optimisation out of scope.
- Show automated test names on screen rather than claiming generic test coverage.
- Use the App Demo as runtime evidence for UI details that do not yet have dedicated Compose UI tests.
