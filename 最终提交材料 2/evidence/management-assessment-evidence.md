# Management Assessment Evidence Pack

## Source and Honesty Statement

This pack supports `TeamXX- Management Assessment.mp4` without modifying the final report. The source rubric is page 33 of `Project Requirements SE33 v2.pdf`, which sets a maximum duration of five minutes.

The report's March-to-August Sprint schedule, `sprint-backlog.csv`, and `burndown.csv` are reconstructed planning and traceability artifacts. They are not presented as contemporaneous Jira exports. The Git-backed execution record in this pack uses repository dates and commit SHAs. Effort is an approximate retrospective self-declaration by the sole developer, not a contemporaneous timesheet. Formal sponsor feedback is unavailable and is not invented.

## Rubric Coverage

| PDF Requirement | Evidence to Show | Status |
|---|---|---|
| Project justification | Final report Sections 1.1, 1.2, and 1.4 | Ready |
| Project journey map | Final report Section 1.9 | Ready |
| Scope: features or use cases | Final report Sections 1.3 and 3.2 | Ready |
| Product backlog: all stories/use cases | `management-product-backlog.csv` | Ready |
| Agile/Scrum conduct | Reconstructed iteration goals, reviews, and actions below | Ready with reconstruction disclosure |
| Tracking tool | Git/GitHub history and commit SHAs | Ready; Jira was not used |
| Sprint backlogs, goals, completion, and burndown | `git-backed-iteration-log.csv`, `sprint-backlog.csv`, `burndown.csv`, and E02 | Ready with reconstruction disclosure |
| Overall effort for each member | `effort-tracking.csv`: sole developer, approximately 100 man-days | Ready as a retrospective estimate |
| Planned versus actual hours | `effort-tracking.csv`: approximately 800 planned and 800 actual hours | Ready as a retrospective estimate |
| Client/sponsor management | Scope/acceptance handling and transparent status below | Ready; no formal acceptance evidence |
| Team management | One-person delivery model and Git identities below | Ready |

## Project Justification

The project addresses the gap between a static mobile list and a resilient news-feed experience. Its goals are to deliver a working Android, Go, and PostgreSQL path; support refresh and cursor pagination; render multiple content types and channels; preserve cached content during backend failure; and make ranking behavior explainable. The expected benefit is faster content discovery, clearer recommendation reasons, and a maintainable end-to-end implementation that can be verified through tests and runtime evidence.

## Scope Baseline

| In Scope and Delivered | Deferred or Outside Final Scope |
|---|---|
| Android Compose feed and explicit UI states | User authentication and account synchronization |
| Go REST API and PostgreSQL persistence | Search and social interaction workflows |
| Refresh, cursor pagination, and duplicate prevention | Production-scale personalised machine learning |
| Room cache-first feed and offline fallback | Advanced cache expiry and eviction policy |
| Multi-channel and multi-card rendering | Advanced streaming, preloading, and adaptive bitrate |
| News detail and basic lifecycle-aware video playback | Production deployment and formal go-live |
| Automated tests, Docker, load test, and CI definitions | Formal sponsor acceptance where evidence is unavailable |

## Project Journey

```mermaid
flowchart LR
    A[Problem and Scope] --> B[Architecture and Backlog]
    B --> C[Android and Backend Foundation]
    C --> D[Feed Integration]
    D --> E[Refresh Pagination and Cache]
    E --> F[Architecture and Test Refinement]
    F --> G[Multi-channel Value]
    G --> H[Verification and Final Delivery]
```

## Product and Iteration Control

The complete delivery backlog is stored in `management-product-backlog.csv`. P0 identifies required delivery and verification work, P1 identifies experience and explainability enhancements, and P2 identifies explicitly deferred scope. PB-11 remains in progress until final GitHub Actions runs and artifacts exist; PB-13 remains deferred.

The verified execution sequence is stored in `git-backed-iteration-log.csv`. It contains five completed Git-backed iterations and one uncommitted finalisation stage. Each completed row gives a goal, selected work, commit SHAs, reconstructed review outcome, and a retrospective action. The word "reconstructed" must remain visible during recording.

The existing burndown chart E02 is a final traceability reconstruction from backlog completion, not a historical Jira export. It can demonstrate trend, but it must not be described as live daily tracking.

## Review and Retrospective Record

| Evidence Review | Observed Outcome | Improvement Action | Closure Evidence | Classification |
|---|---|---|---|---|
| Feed integration review | Multi-card feed rendered, but refresh and pagination were incomplete | Split refresh and load more into separate testable flows | Commits `8c7a6d8`, `35d724a`, and `9447406` | Reconstructed from Git |
| Reliability review | Network failure could leave the feed unavailable | Add Room data, skeleton, and explicit error states | Commits `58457b0`, `f290eca`, and `26556e6` | Reconstructed from Git |
| Architecture review | Feed UI and data responsibilities required clearer boundaries | Refine repository/use-case/ViewModel boundaries and add tests | Commits `06d8dc4`, `1db5c6c`, and `7cd77ef` | Reconstructed from Git |
| Final performance review | Nested database reads exhausted the connection pool under load | Close outer rows before related-media queries and repeat the stress test | `verification-2026-08-08.md`: 500 requests, zero failures | Final verification record |

## Management Risks and Outcomes

| Risk or Issue | Owner Role | Mitigation | Current Outcome |
|---|---|---|---|
| Refresh and pagination races | Android owner | Operation guards, request versions, and ID-based merge | Covered by ViewModel tests |
| Weak-network blank feed | Android/data owner | Scene-filtered Room cache-first flow | Demonstrable in App Demo |
| Database pool exhaustion | Backend owner | Close outer query rows before nested media loading | 500 requests at concurrency 25 with zero failures |
| Scope expansion during finalisation | Project owner | P0/P1/P2 backlog and explicit deferred scope | Advanced ML, auth, and streaming remain deferred |
| CI evidence not tied to final code | DevSecOps owner | Commit and push final code, then retain artifacts and SHA | Open until final CI run |
| Formal sponsor acceptance unavailable | Project owner | State the limitation and use technical acceptance evidence | No sponsor acceptance claim is made |

## Effort and Team Evidence

This was a one-person project completed by Xu Ziyi as the sole developer. The delivery period was 2026-03-23 to 2026-08-14. Using the developer-confirmed working intensity of approximately 20 workdays per month for about five months, planned effort is reported as approximately 100 man-days or 800 hours. Actual effort is retrospectively reported at approximately the same level: 100 man-days or 800 hours. This is an honest retrospective estimate, not a daily timesheet, and should be described that way in the recording.

Git currently contains 59 commits under the author identities `SUPERpowerGT`, `Zee`, and `xu ziyi`. The developer confirmed that this is a solo project, so these identities must not be presented as three team members. Git activity provides an audit trail, while `effort-tracking.csv` provides the separate retrospective effort declaration. The 840-hour value in the final report is a calendar-based capacity baseline and is not used as actual effort in this video.

## Sponsor and Acceptance Status

Formal sponsor or mentor acceptance was not available at the time of evidence preparation. No acceptance or go-live claim is made. Technical acceptance evidence consists of the running Android application, backend health/API evidence, PostgreSQL/Docker evidence, automated tests, and repeatable stress-test results.

## Recording Evidence Order

1. PDF page 33 and final report project justification.
2. Final report scope and Project Journey Map.
3. `management-product-backlog.csv`.
4. `git-backed-iteration-log.csv` and a filtered `git log` view.
5. E02 with the reconstruction disclosure.
6. `effort-tracking.csv` and the Review/Retrospective table.
7. Risk, CI status, and sponsor status.

## Remaining External Evidence

- A final GitHub CI/security run and artifacts for the final committed code.
- Original Jira/board screenshots, only if a tracking tool other than GitHub was used.
- Formal sponsor or mentor feedback, only if it exists.
