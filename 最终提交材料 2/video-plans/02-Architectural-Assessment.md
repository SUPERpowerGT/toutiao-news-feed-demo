# Video 2 - Architectural Assessment

## Delivery Information

- Required filename: `TeamXX- Architectural Assessment.mp4`
- Target duration: 10 minutes
- Primary purpose: explain the implemented architecture, major decisions, deployment, DDD boundaries, infrastructure, network, and security design.

## Required Content

- Architectural drivers, constraints, and quality attributes.
- Technology stack and reasons for selecting Android, Kotlin/Compose, Go, PostgreSQL, Room, and Docker Compose.
- Logical architecture and dependency direction.
- Android MVVM/Clean Architecture responsibilities.
- Go lightweight DDD boundaries and domain ownership.
- Implemented physical/deployment architecture.
- Client-to-backend network path, protocol, host mapping, and ports.
- PostgreSQL and Room persistence responsibilities.
- CI/CD connection to the runtime delivery model.
- Security architecture, trust boundaries, threats, and implemented controls.
- Clear separation between the current MVP and future architecture.

## Evidence Required Before Recording

- [x] DevSecOps and runtime architecture diagram `E12`.
- [x] Logical architecture diagram in report Sections 4.1.1 and 4.1.2.
- [x] Physical deployment diagram showing Android, backend, PostgreSQL, ports, and Docker network in Section 4.1.3 and E12.
- [x] Lightweight DDD boundary diagram in Section 4.2.4.
- [x] Technology decision comparison in report Sections 2.1 to 2.4.
- [x] Architecture constraints and decision rationale in the architecture overview and script.
- [x] Docker Compose configuration and healthy container evidence in E08/E26.
- [x] Security threat/control table in report Section 6.5.

## Suggested Timeline

| Time | Narration | Screen Evidence |
|---|---|---|
| 0:00-0:40 | Drivers, scope, and constraints | Architecture goals and constraints |
| 0:40-1:30 | Technology stack decisions | Technology comparison table |
| 1:30-3:00 | Overall logical architecture | Full-link logical diagram |
| 3:00-4:20 | Android architecture | UI, ViewModel, UseCase, Repository, Room/Retrofit |
| 4:20-5:40 | Backend and DDD | Handler, Service, Domain, Repository boundaries |
| 5:40-7:10 | Physical deployment and network | Docker Compose, ports, request path |
| 7:10-8:20 | Data architecture | PostgreSQL schema and Room cache roles |
| 8:20-9:20 | Security architecture | Input validation, scans, non-root container, risks |
| 9:20-10:00 | Trade-offs and evolution | As-is versus future extensions |

## Exact Recording Runbook

### 1. Prepare These Screens Before Recording

Open the following evidence in separate tabs or windows:

1. Final report Section 2.1 to 2.4 for technology decisions.
2. Final report Section 4.1 for the overall architecture.
3. Final report Section 4.2.4 for the lightweight DDD boundary.
4. `assets/E12-devsecops-runtime-architecture.png` for delivery and runtime architecture.
5. Android source folders: `ui/feed`, `domain/usecase`, `domain/repository`, and `data`.
6. Backend source folders: `api`, `application`, `domain`, and `infrastructure`.
7. `docker-compose.prod.yml` and the Docker running evidence `assets/E08-docker-compose-running.png`.
8. Final report Section 6.5 for the security threat and control table.

Use presentation mode or zoom each document so every label remains readable. Highlight current components in one colour and future components in another if annotations are available.

### 2. Exact Ten-Minute English Script

| Time | Screen Evidence | English Narration |
|---|---|---|
| 0:00-0:35 | Title slide, then architecture drivers and constraints | "Hello. This video presents the architecture of our modern recommendation feed demo. The architecture is driven by four main requirements: fast initial rendering, state-safe pull-to-refresh, stable cursor pagination, and flexible rendering of text, image, video, and official content. Our main constraints were a small project team, a mobile-first user interface, a limited delivery period, and the need for a fully reproducible local demonstration. Therefore, we prioritised clear boundaries, testability, and deployment simplicity over premature distribution." |
| 0:35-1:30 | Technology decision comparison in report Sections 2.1 to 2.4 | "For the Android client, we selected Kotlin and Jetpack Compose instead of XML layouts. A recommendation feed is highly dynamic, and Compose allows card rendering and loading states to be expressed directly from immutable UI state. We selected MVVM with StateFlow because loading, refreshing, pagination, empty, and error conditions must remain explicit and observable. For the backend, we selected Go rather than a heavier Java service or a JavaScript runtime. Go provides a small deployment artifact, straightforward concurrency, and simple HTTP service composition. PostgreSQL is the source of truth because the feed model requires relational joins, stable ordering, arrays, and indexed cursor queries. Finally, we selected cursor pagination instead of offset pagination because a changing news feed can otherwise duplicate or skip records between requests." |
| 1:30-2:25 | Overall implemented architecture diagram | "This is the implemented end-to-end architecture. It has three runtime tiers: the Android client, one Go backend service, and PostgreSQL. The user interacts with Compose screens. FeedViewModel coordinates use cases and exposes FeedUiState. The repository calls the Retrofit API, which sends an HTTP request to the Go handler. The handler validates request parameters and calls the application service. The service applies feed rules and delegates SQL access to the PostgreSQL repository. The response then travels back as JSON, is mapped into Android domain models, and drives Compose rendering. Dependencies point inward through interfaces: the UI does not know SQL, and the backend business layer does not construct database connections." |
| 2:25-3:45 | Android directory tree, FeedScreen, FeedViewModel, use cases, repository contract | "On Android, the presentation layer contains FeedScreen, FeedViewModel, FeedUiState, the refresh components, and the card factory. FeedScreen renders state and forwards gestures; it does not perform network calls. FeedViewModel owns the current scene and coordinates initial load, refresh, load more, stale-request protection, and retry behavior. The domain layer contains three feed use cases and FeedRepositoryContract. The data layer contains the repository, Retrofit, DTO mappers, and Room. Successful remote results are upserted into Room, while an initial network failure falls back to scene-filtered cached data. This cache boundary is verified by repository unit tests. Detail navigation uses the same layered approach through NewsDetailViewModel and the backend detail API." |
| 3:45-4:35 | FeedCardFactory and card implementations | "The client also separates content semantics from visual rendering. API content types are mapped to domain models, and FeedCardFactory selects OfficialTopCard, TextCard, ImageCard, or VideoCard. This is preferable to placing content-type conditions throughout the screen. Adding a new card type can be isolated to the model, mapper, factory, and new Composable. Recommendation reasons are returned by the backend, while the Android layer decides how those reasons are displayed. This preserves a clear ownership boundary between ranking metadata and presentation." |
| 4:35-5:50 | Backend directory tree, main.go, feed_handler.go, feed_service.go, repository file | "The Go backend is a modular monolith with lightweight domain-driven boundaries. It is not a deployed microservice system. The API package owns HTTP parsing, supported scene validation, response status, and JSON formatting. The application package owns use-case rules such as the official top section, recommendation reasons, scene handling, and pagination metadata. The domain package defines FeedItem and the repository interface without PostgreSQL details. The infrastructure package implements that interface using PostgreSQL queries and media attachment. Composition happens in main.go, where the database, repository, service, and handler are wired together. This structure keeps the handler thin, allows the service to be unit tested with a fake repository, and keeps SQL changes out of the domain model." |
| 5:50-6:40 | DDD boundary diagram and database schema | "The main bounded context is the Feed context. A feed aggregate combines news content with author, media, statistics, category, city, publication time, and ranking metadata. PostgreSQL normalises these concerns into news, feed_item, author, media, stats, and news_content tables. The repository joins them into the domain response. The feed_item table stores channel filtering and ranking fields, while media remains one-to-many so image and video records can evolve independently. Cursor pagination uses publication time and stable ordering rather than a growing numeric offset. This design directly supports refresh, load more, and multi-channel filtering." |
| 6:40-7:40 | `docker-compose.prod.yml`, Docker screenshot, request path and ports | "The implemented physical deployment is intentionally small. Docker Compose runs the Go backend and PostgreSQL on a private Docker network named toutiao_net. PostgreSQL listens inside that network on port 5432 and is not published to external clients in the production compose file. The backend waits for the database health check and publishes port 8080 to the host. On the Android emulator, Retrofit uses ten dot zero dot two dot two, port 8080. This address maps the emulator to the host machine, which then forwards the request into the backend container. Database credentials are supplied through container environment variables for this local demo. A real deployment would move them to managed secrets rather than committing production credentials." |
| 7:40-8:25 | E12 diagram, focusing first on solid implemented paths | "The architecture also connects runtime delivery to CI and security verification. A code change enters GitHub Actions, where Android and backend builds, unit tests, integration checks, security scans, and artifact creation act as quality gates. The resulting backend can be packaged as a non-root container, while the Android build produces an application artifact. The solid path in this diagram represents the implemented project: repository, CI workflows, Android client, Go backend, PostgreSQL, and Docker Compose. Workflow execution evidence is assessed separately in the CI/CD and DevSecOps videos, so this section focuses on how delivery supports the runtime architecture." |
| 8:25-9:15 | Security architecture and threat/control table | "Security controls are placed at the trust boundaries. The public HTTP handler validates scene, cursor, refresh time, and limit values before application logic executes. Repository queries use parameterised SQL rather than string-concatenated user input. Middleware provides request logging and panic recovery, while final recordings and logs must not expose private data. Automated workflows add CodeQL, dependency, filesystem, container, and dynamic checks. The backend container runs as a non-root application user. Remaining risks are documented honestly: local development still uses clear-text HTTP, seed maintenance endpoints are not authenticated, and the demo does not yet provide production rate limiting or managed secret storage. These require HTTPS, authentication, restricted administrative routes, and platform controls before public deployment." |
| 9:15-9:50 | Future physical architecture, visibly labelled future/not implemented | "This larger cloud architecture is an evolution blueprint, not the current deployment. WAF, API Gateway, Redis, object storage, service discovery, message queues, centralised observability, and horizontally scaled service instances are future options. We would introduce them only when measured traffic, availability, or operational requirements justify their cost. The present modular monolith reduces deployment and debugging complexity while preserving boundaries that can later be extracted. This is a deliberate trade-off: we accept single-service scaling limits in exchange for reliable delivery and clear ownership at the current project scale." |
| 9:50-10:00 | Return to implemented architecture overview | "In summary, the architecture aligns the Android state-driven client, a testable Go modular backend, PostgreSQL cursor queries, Docker deployment, and automated quality controls. It meets the current feed requirements while clearly separating implemented capabilities from future scale-out options. Thank you." |

### 3. Recording Controls

- Speak at approximately 125 to 140 words per minute and rehearse once with a timer.
- Keep the current implementation diagram visible before showing the future blueprint.
- When showing code, display only the class or function being discussed; do not scroll through entire files.
- Keep `FeedViewModel`, `FeedRepositoryContract`, `FeedService`, and `FeedItemRepositoryPG` names readable.
- Do not claim that HTTPS, authentication, Redis, WAF, API Gateway, cloud deployment, or centralised monitoring is currently active.
- If the script exceeds ten minutes, shorten the technology comparison and card-factory sections, not the implementation/future distinction or security risks.

### 4. Definition of a Successful Take

- The recording is no longer than ten minutes.
- The speaker is visible and the narration is in English.
- At least three decisions include a reason and trade-off: Compose, Go modular monolith, and cursor pagination.
- Android, backend, database, Docker network, and request ports are visibly mapped.
- Room cache writes and offline fallback are connected to their repository tests.
- Every cloud-scale component is explicitly described as future architecture.

## Recording Notes

- Begin with the implemented architecture, not the future cloud blueprint.
- Keep WAF, Redis, API Gateway, and centralized monitoring visibly marked as not implemented.
- Explain why the current solution is a modular monolith, not a deployed microservice system.
- Connect diagram boxes to real source folders and configuration files.

## Definition of Done

- [ ] Logical, physical, deployment, DDD, and security views are covered.
- [ ] At least three architectural decisions include alternatives and trade-offs.
- [ ] Network ports and Docker boundaries are visible.
- [ ] Implemented and future components cannot be confused.
- [ ] Diagram text is readable in the exported video.
