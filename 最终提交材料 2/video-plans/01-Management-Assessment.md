# Video 1 - Management Assessment

## Delivery Information

- Required filename: `TeamXX- Management Assessment.mp4`
- Target duration: 5 minutes
- Primary purpose: prove that the project was scoped, planned, tracked, reviewed, and delivered through a credible management process.

## Required Content

The five scored areas below come directly from page 33 of `Project Requirements SE33 v2.pdf`:

- Project background, user pain point, objectives, and expected benefit.
- Project scoping through the Journey Map, features/use cases, and Product Backlog.
- Project conduct through Scrum-oriented practices, the tracking tool, all stories, Sprint/iteration backlogs, goal completion, and burndown charts.
- Project effort through overall man-days for each member and planned versus actual hours.
- Management issues and mitigation covering client/sponsor management and team management.

Sprint Review outcomes, retrospective actions, roadmap decisions, scope changes, risk owners, and acceptance status are supporting evidence that makes the five scored areas credible.

## Evidence Required Before Recording

- [x] Product Roadmap and Journey Map in final report Sections 1.5 and 1.9.
- [x] Complete Product Backlog with priority, status, acceptance criteria, and repository evidence.
- [x] Git-backed iteration evidence with goal, review outcome, action, and commit SHAs.
- [x] Burndown chart with an explicit statement that it is reconstructed.
- [x] Sole-developer effort recorded as an explicitly retrospective estimate: approximately 100 man-days / 800 planned and actual hours.
- [x] Reconstructed review record clearly labelled as reconstructed from Git.
- [x] Retrospective improvement actions linked to closure commits or verification.
- [x] Git commit evidence linked to each claimed iteration.
- [x] Client/sponsor status documented transparently: formal acceptance is unavailable.

## Suggested Timeline

| Time | Narration | Screen Evidence |
|---|---|---|
| 0:00-0:30 | Problem, users, project objective | Title, problem statement, success criteria |
| 0:30-1:00 | Scope and Journey Map | In/out-of-scope table and Journey Map |
| 1:00-1:40 | Roadmap and Product Backlog | Release roadmap and prioritized backlog |
| 1:40-2:30 | Sprint execution | Sprint goals, selected stories, milestones |
| 2:30-3:20 | Tracking and effort | Burndown, actual effort, commit/issue links |
| 3:20-4:10 | Review and retrospective | Feedback, incomplete items, improvement actions |
| 4:10-4:40 | Risks and mitigations | Risk register with owner and status |
| 4:40-5:00 | Outcome and acceptance | Delivered scope and sponsor status |

## Exact Recording Runbook and Oral Script

### Prepare These Screens

Open PDF page 33, final report Sections 1.1-1.9, `../evidence/management-product-backlog.csv`, `../evidence/git-backed-iteration-log.csv`, `../evidence/effort-tracking.csv`, `../assets/E02-burndown-chart.png`, and `../evidence/management-assessment-evidence.md`. Also prepare this command in a terminal:

```bash
git log --date=short --pretty=format:'%h  %ad  %an  %s' --reverse
```

### Exact Five-Minute English Script

| Time | Screen Evidence | Exact English Narration |
|---|---|---|
| 0:00-0:35 | PDF rubric, then report Sections 1.1 and 1.4 | "This Management Assessment follows the five areas on page thirty-three of the project rubric. The project addresses the gap between a static mobile list and a resilient news-feed experience. Its goal is to deliver a working Android, Go, and PostgreSQL path with refresh, pagination, multiple card types, explainable channels, and offline availability. The expected benefit is faster content discovery together with an implementation that can be tested, maintained, and demonstrated end to end." |
| 0:35-1:10 | In-scope/out-of-scope table and Project Journey Map | "Scope was controlled around the recommendation-feed journey: define the problem and architecture, establish the Android and backend foundation, integrate the feed, add refresh, pagination, and cache, then verify and prepare delivery. Authentication, search, social interaction, production machine learning, advanced streaming, and formal go-live were kept outside the delivered scope. This boundary prevented the finalisation stage from turning future ideas into unsupported completion claims." |
| 1:10-1:50 | `management-product-backlog.csv` | "The Product Backlog contains thirteen traceable stories or use cases. P zero identifies core delivery and verification, P one covers user-experience and explainability enhancements, and P two identifies deferred scope. Each item has a status, acceptance criterion, and repository path. Core feed, cards, refresh, pagination, Room fallback, channels, detail, basic video, Docker, tests, recommendation reasons, and final CI security evidence are complete. The final workflows retained backend, coverage, integration, load, Trivy, and ZAP artifacts. Authentication and social features remain explicitly deferred." |
| 1:50-2:45 | `git-backed-iteration-log.csv`, then terminal `git log` | "The project was conducted in Scrum-oriented increments, with Git and GitHub used as the tracking and audit tool rather than Jira. Five completed iterations are supported by real dates and commit SHAs. The first established the repository and Android baseline. The second delivered the initial end-to-end multi-card feed. The third added refresh, load more, Room data, skeleton and error states, unit tests, and Android CI. The fourth refined navigation, architecture, dependencies, and tests. The fifth added repeatable demo tooling, independent feed scenes, and recommendation signals. The current finalisation work is still uncommitted, so it is not presented as a completed Git iteration. Review outcomes and retrospective actions were reconstructed from the repository on August eighth and are labelled as reconstruction, not as original meeting minutes." |
| 2:45-3:20 | E02 burndown, with its disclosure visible | "The burndown shows the planned and reconstructed decline in remaining backlog items across the assessment Sprint model. It is useful for explaining delivery trend, but it is not a live Jira export and I do not present it as one. The stronger execution evidence is the linked commit history, where goals can be connected to delivered source changes. Remaining work is visible rather than hidden, especially final CI artifacts and submission recording." |
| 3:20-4:00 | `effort-tracking.csv` | "This was a one-person project, and I was the sole developer across Android, backend, database, testing, DevSecOps, and documentation. The delivery period was March twenty-third to August fourteenth. At an average intensity of twenty working days per month over approximately five months, planned effort was about one hundred man-days, or eight hundred hours. My retrospective actual-effort estimate is also approximately one hundred man-days, or eight hundred hours. This is an honest retrospective estimate rather than a daily timesheet. The Git identities SUPERpowerGT, Zee, and Xu Ziyi all belong to this solo project and must not be counted as three members." |
| 4:00-4:38 | Review/retrospective table | "The reconstructed reviews still show a clear improvement loop. Incomplete feed interaction led to separate refresh and pagination increments. Reliability concerns led to Room fallback, skeleton loading, and explicit error states. Architecture concerns led to repository, use-case, and ViewModel boundaries plus tests. Finally, a database connection-pool failure led to corrected query lifecycle management. The repeated stress test then completed five hundred requests at concurrency twenty-five with zero failures." |
| 4:38-4:55 | Management risk table | "Key risks were concurrency defects, weak-network failure, database pool exhaustion, uncontrolled scope, and CI evidence not matching final code. Mitigations were linked to implementation. Final CI then retained tests, load results, Trivy reports, and ZAP evidence, closing the delivery risk." |
| 4:55-5:00 | Sponsor status | "Formal sponsor acceptance is unavailable, so no acceptance or go-live claim is made. Thank you." |

### Recording Controls

- Keep the words `reconstructed` and `capacity baseline` visible whenever those artifacts are discussed.
- State clearly that this was a one-person project and the three Git author names are repository identities, not three members.
- Describe 800 hours as an approximate retrospective estimate and do not say that the report's 840-hour capacity baseline was actual effort.
- Keep the exported video at five minutes or less and record at 1920x1080 with the speaker's face clearly visible.

## Recording Notes

- Use data and screenshots rather than explaining Scrum theory.
- Clearly distinguish original records from evidence reconstructed for final traceability.
- Do not claim 840 actual hours unless supported by real daily or weekly records.
- Do not describe unfinished items as completed.

## Definition of Done

- [ ] Video is no longer than the approved duration.
- [ ] Every management claim points to visible evidence.
- [ ] Real owners, dates, effort, and outcomes are shown.
- [ ] Review, retrospective, and sponsor status are explicitly covered.
- [ ] Speaker and slides remain readable throughout.
