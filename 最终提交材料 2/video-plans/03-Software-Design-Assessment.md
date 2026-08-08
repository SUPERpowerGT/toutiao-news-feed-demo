# Video 3 - Technical Assessment - Software Design

## Delivery Information

- Required filename: `TeamXX- Technical Assessment - Software Design.mp4`
- Target duration: 5 minutes
- Primary purpose: demonstrate how critical requirements were transformed into classes, interactions, data models, design patterns, code, and tests.

## Critical Use Case

Use the recommendation-feed refresh and pagination flow as the main design story. It crosses the Android UI, state management, domain use cases, remote and Room data sources, API, backend service, repository, and PostgreSQL.

## Required Content

- Use Case Diagram and selected critical use case.
- Functional and non-functional requirements for the selected flow.
- Analysis-to-design transition.
- Class Diagram and responsibility allocation.
- Sequence Diagram for initial load, refresh, or pagination.
- Main interfaces and method contracts.
- Design patterns used in real code: MVVM, Repository, Factory, Adapter/Mapper, and state-based UI.
- DTO, Domain, Entity, and database schema relationship.
- Error, empty, refresh, pagination, and stale-request handling.
- Requirement-to-code-to-test traceability.

## Evidence Required Before Recording

- [x] Use Case Diagram.
- [x] Refresh and pagination Sequence Diagrams.
- [x] Class Diagram containing current implementation classes.
- [x] DTO/Domain/Entity mapping diagram and active Room fallback path.
- [x] ER Diagram or schema excerpt.
- [x] Traceability table linking FR/NFR to code and test cases.
- [x] Short code excerpts from `FeedViewModel`, UseCases, `FeedService`, and repositories.
- [x] Relevant Android and backend test cases.

## Suggested Timeline

| Time | Narration | Screen Evidence |
|---|---|---|
| 0:00-0:35 | Select the critical use case | Use Case Diagram and requirements |
| 0:35-1:25 | Analysis to class responsibilities | Class Diagram |
| 1:25-2:25 | Runtime interaction | Refresh/pagination Sequence Diagram |
| 2:25-3:20 | Patterns and boundaries | MVVM, Repository, Factory, Mapper code |
| 3:20-4:10 | Data design | DTO/Domain/Entity and PostgreSQL schema |
| 4:10-4:45 | Error and concurrency handling | UI states and request-version protection |
| 4:45-5:00 | Traceability conclusion | Requirement-to-test mapping |

## Exact Recording Runbook

### 1. Prepare These Screens Before Recording

Open these views in advance:

1. Final report Section 3.2 Use Case Diagram.
2. Final report refresh and pagination sequence diagrams.
3. Final report Section 4.7 Class Diagram.
4. `../evidence/software-design-traceability.md`.
5. `FeedViewModel.kt`, focused on `refresh()`, `loadMore()`, and `requestVersion`.
6. `FeedRepositoryContract.kt`, the three feed use cases, and `FeedCardFactory.kt`.
7. `backend/api/feed_handler.go`, `backend/application/feed_service.go`, and `backend/infrastructure/feed_item_repository_pg.go`.
8. `FeedViewModelTest.kt`, `feed_service_test.go`, and `feed_handler_test.go`.

### 2. Exact Five-Minute English Script

| Time | Screen Evidence | English Narration |
|---|---|---|
| 0:00-0:30 | Use Case Diagram with refresh and load-more highlighted | "Hello. This software design assessment traces one critical use case from requirement to implementation and test: refreshing and paginating the recommendation feed. The selected requirements include pull-to-refresh, cursor-based load more, duplicate prevention, explicit error states, and state-driven rendering. This flow is valuable because it crosses the Android presentation, domain, and data layers, the Go API and application layers, and PostgreSQL." |
| 0:30-1:15 | Current class diagram | "The class responsibilities follow the dependency direction shown here. FeedScreen renders FeedUiState and forwards gestures. FeedViewModel owns refresh, pagination, channel, retry, and concurrency state. LoadInitialFeedUseCase, RefreshFeedUseCase, and LoadMoreFeedUseCase express application intent through FeedRepositoryContract. FeedRepository implements that contract using RemoteDataSource and Retrofit. On the server, FeedHandler validates HTTP input, FeedService owns feed assembly and recommendation metadata, and FeedItemRepositoryPG owns SQL and cursor queries. These boundaries prevent UI classes from knowing transport or database details." |
| 1:15-2:15 | Refresh sequence diagram, then briefly show the named methods | "During refresh, the pull component reaches its threshold and invokes FeedViewModel refresh. The ViewModel checks that another refresh is not active, increments requestVersion, and passes the current scene and latest publication time to RefreshFeedUseCase. The repository calls the remote API with refresh_time. The Go handler validates this parameter and calls FeedService. The PostgreSQL repository selects only records newer than that timestamp and returns assembled feed items. Android maps the DTO response into domain models. Before changing UI state, the ViewModel compares the captured request version with the latest version. A stale result is ignored; a valid result is merged by unique item ID, the latest timestamp is updated, and the refresh animation completes. Pagination follows the same boundary chain but sends next_cursor and requests older records." |
| 2:15-3:05 | Repository contract, card type use case, factory, mapper | "Several patterns solve specific project problems. MVVM isolates state and lifecycle-aware coroutine work from Compose. The Repository pattern gives all use cases one data contract and keeps Retrofit outside the domain layer. Adapter-style mappers convert transport DTOs into strongly typed domain models, protecting the UI from API schema details. RenderCardTypeUseCase applies the card-selection rule, and FeedCardFactory dispatches to text, image, video, or official Composables in one place. State-based UI models loading, success, refresh, pagination failure, and full-screen failure explicitly instead of relying on scattered view flags." |
| 3:05-3:45 | DTO/Domain/Entity diagram and PostgreSQL tables | "The data design separates transport, business meaning, and persistence. Retrofit DTOs mirror JSON. FeedItem and related domain models provide types used by ViewModel and card rendering. PostgreSQL separates feed_item, news, author, media, stats, and news_content. On Android, successful remote pages are mapped and upserted into Room. If initial loading fails, FeedRepository requests scene-filtered cached domain models from LocalDataSource. This gives the active runtime path a tested offline fallback while preserving Retrofit as the source of fresh content." |
| 3:45-4:30 | `FeedViewModel` guards, error UI, then tests | "Correctness depends on failure and concurrency handling. Refresh and load-more guards prevent duplicate operations. Request versions prevent an older response from overwriting a newer channel or refresh result. Load-more merges by ID, preserving order while removing duplicates. An initial failure becomes FeedUiState Error with Retry, while pagination failure preserves visible cards and adds a retryable footer. Automated evidence includes initialLoadPublishesGroupedSuccessState, refreshMergesNewItemsAndFinishesRefreshState, loadMoreMergesUniqueItemsAndUpdatesCursor, backend cursor metadata tests, and handler input-validation tests." |
| 4:30-5:00 | Traceability matrix | "This traceability matrix connects each selected requirement to its design owner, real source files, and named tests or live evidence. The completed path now includes Room cache fallback, ID-based news detail navigation, a PostgreSQL-backed detail API, and lifecycle-aware basic video playback. The remaining limits are explicit: cache expiration and advanced video preloading are future optimisations. This demonstrates how requirements were transformed into implemented and testable software design decisions. Thank you." |

### 3. Recording Controls

- Follow the refresh path end to end; do not spend time listing unrelated classes.
- Keep class names, method names, and test names readable.
- Use the traceability matrix as the final proof, not as the opening slide.
- Demonstrate Room fallback, detail navigation, and basic video playback without claiming advanced cache eviction or streaming optimisation.
- Speak at approximately 130 words per minute and keep the final export at five minutes or less.

### 4. Definition of a Successful Take

- One use case is visibly traced from requirements to design, code, runtime sequence, and tests.
- MVVM, Repository, Mapper, Factory, and state-based UI are connected to project-specific problems.
- Refresh, pagination, errors, duplicate prevention, and stale-response protection are covered.
- Implemented and incomplete requirements are clearly distinguished.
- The final filename is `TeamXX- Technical Assessment - Software Design.mp4`.

## Recording Notes

- Follow one use case end to end instead of listing every class.
- Use current class and method names; remove obsolete design artifacts.
- Explain why each pattern solves a project-specific problem.
- Do not call simple layering a design pattern unless the relationship is demonstrated.

## Definition of Done

- [ ] One critical use case is traced from requirement to test.
- [ ] Class and sequence diagrams match current code.
- [ ] Design patterns are supported by visible implementation evidence.
- [ ] Data design and error paths are included.
- [ ] Video remains within five minutes.
