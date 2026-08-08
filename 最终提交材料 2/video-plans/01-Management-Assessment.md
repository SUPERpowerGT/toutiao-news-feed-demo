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
- [x] Sprint completion summary shows 24 of 24 items complete and every Sprint goal achieved.
- [x] Client/sponsor management and sole-developer team management have explicit mitigations.

## Suggested Timeline

| Time | Narration | Screen Evidence |
|---|---|---|
| 0:00-0:30 | Problem, users, project objective | Title, problem statement, success criteria |
| 0:30-1:00 | Scope and Journey Map | In/out-of-scope table and Journey Map |
| 1:00-1:40 | Product and Sprint backlogs | Release stories, Sprint tasks, completion counts |
| 1:40-2:35 | Scrum conduct and tracking | Sprint goals, Git/GitHub traceability, burndown |
| 2:35-3:15 | Project effort | Sole member, man-days, planned versus actual hours |
| 3:15-4:20 | Management issues | Client/sponsor and sole-developer team management |
| 4:20-5:00 | Improvement, final evidence, acceptance | Remediation loop, final CI, sponsor status |

## Exact Recording Runbook and Oral Script

### Prepare These Screens

Open PDF page 33, final report Sections 1.1-1.11, `../evidence/management-product-backlog.csv`, `../evidence/sprint-backlog.csv`, `../evidence/git-backed-iteration-log.csv`, `../evidence/effort-tracking.csv`, `../assets/E02-burndown-chart.png`, and `../evidence/management-assessment-evidence.md`. Also prepare this command in a terminal:

```bash
git log --date=short --pretty=format:'%h  %ad  %an  %s' --reverse
```

Prepare these CI images in this order for the final minute:

1. `../assets/E24-trivy-remediation-before.png` - historical security-gate failure.
2. `../assets/E21-container-security-final-success.png` - successful rescan with two retained artifacts.
3. `../assets/E18-github-actions-final-overview.png` - final five-workflow acceptance view.

The red E24 image must be introduced as an earlier finding that triggered remediation. Do not leave it on screen at the end; finish on the green E18 overview.

### Exact Five-Minute English Script

| Time | Screen Evidence | Exact English Narration |
|---|---|---|
| 0:00-0:35 | PDF rubric, then report Sections 1.1 and 1.4 | "This Management Assessment follows the five areas on page thirty-three of the project rubric. The project addresses the gap between a static mobile list and a resilient news-feed experience. Its goal is to deliver a working Android, Go, and PostgreSQL path with refresh, pagination, multiple card types, explainable channels, and offline availability. The expected benefit is faster content discovery together with an implementation that can be tested, maintained, and demonstrated end to end." |
| 0:35-1:10 | In-scope/out-of-scope table and Project Journey Map | "Scope was controlled around the recommendation-feed journey: define the problem and architecture, establish the Android and backend foundation, integrate the feed, add refresh, pagination, and cache, then verify and prepare delivery. Authentication, search, social interaction, production machine learning, advanced streaming, and formal go-live were kept outside the delivered scope. This boundary prevented the finalisation stage from turning future ideas into unsupported completion claims." |
| 1:10-1:45 | `management-product-backlog.csv` | "The Product Backlog records twelve stories committed to this release. They cover the feed, card types, refresh, pagination, Room fallback, channels, detail, basic video, Docker, automated tests, CI security, and recommendation reasons. Every committed story has an acceptance criterion, repository evidence, and Done status. Authentication, search, and social interaction are shown separately as PB thirteen, a future epic outside this release. It was never accepted into a Sprint, so it is not presented as an incomplete committed story." |
| 1:45-2:30 | Sprint Completion Summary and `sprint-backlog.csv` | "The work was organised through six reconstructed Sprints across the March twenty-third to August fourteenth assessment period. Each Sprint had four backlog items and an explicit goal. The completion summary shows twenty-four of twenty-four Sprint items complete and all six goals achieved. Git and GitHub were the tracking and audit tools rather than Jira. The Sprint schedule is a reconstructed management view, while the separate Git-backed iteration log preserves actual repository dates and commit SHAs. I do not present these records as original Jira exports or meeting minutes." |
| 2:30-3:05 | `git-backed-iteration-log.csv`, terminal `git log`, then E02 | "The Git-backed log traces the foundation, end-to-end feed, interaction and cache work, architecture refinement, repeatable multi-channel demonstration, and final DevSecOps closure. The linked commits make each outcome auditable. The burndown is also explicitly labelled as reconstructed from backlog and delivery evidence. It shows remaining Sprint work declining to zero, but it is not described as live daily tracking." |
| 3:05-3:40 | `effort-tracking.csv` | "This was a one-person project. I was the sole developer for Android, backend, database, testing, DevSecOps, and documentation. At approximately twenty working days per month for five months, planned effort was one hundred man-days, or eight hundred hours. My retrospective actual estimate is also approximately one hundred man-days and eight hundred hours. This is not a daily timesheet. SUPERpowerGT, Zee, and Xu Ziyi are three Git identities for the same person, not three members." |
| 3:40-4:20 | Report Section 1.11 and Team Management table | "Client and sponsor management focused on scope alignment, visible acceptance criteria, progress evidence, and transparent status. Formal sponsor approval is unavailable, so technical verification is not presented as sponsor sign-off. Team management for a sole developer focused on controlling role switching and review risk. Sprint goals and P zero, P one, and future-scope priorities limited concurrent work. Automated tests, Git history, CodeQL, Trivy, and ZAP provided independent quality gates where a second developer review was unavailable." |
| 4:20-4:45 | Review table, then E24 and E21 | "The review loop produced measurable mitigation. Refresh concerns led to isolated state flows, network failure led to Room fallback, and database pool exhaustion led to corrected query lifecycle management and a repeated load test with zero failures. Trivy then blocked real dependency findings; dependencies and the container base were upgraded, and both security scans passed on rescan." |
| 4:45-4:55 | E18 final GitHub Actions overview | "The final evidence commit shows all five project workflows green, tying the management closure to tested final code." |
| 4:55-5:00 | Sponsor status | "Formal sponsor acceptance is unavailable, so no acceptance or go-live claim is made. Thank you." |

### Recording Controls

- Keep the words `reconstructed` and `retrospective estimate` visible whenever those artifacts are discussed.
- State clearly that this was a one-person project and the three Git author names are repository identities, not three members.
- Describe 800 hours as an approximate retrospective estimate, not a contemporaneous daily timesheet.
- Keep the exported video at five minutes or less and record at 1920x1080 with the speaker's face clearly visible.

## Recording Notes

- Use data and screenshots rather than explaining Scrum theory.
- Clearly distinguish original records from evidence reconstructed for final traceability.
- State that PB-13 is a future epic outside the release, not an unfinished committed Sprint story.
- Do not describe unfinished items as completed.

## Definition of Done

- [ ] Video is no longer than the approved duration.
- [ ] Every management claim points to visible evidence.
- [ ] Real owners, dates, effort, and outcomes are shown.
- [ ] Review, retrospective, and sponsor status are explicitly covered.
- [ ] Speaker and slides remain readable throughout.
