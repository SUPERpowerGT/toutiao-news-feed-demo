# PDF Video Compliance Audit

## Source and Scope

This audit checks the seven plans directly against `Project Requirements SE33 v2.pdf`, especially assessment rubric pages 33-38 and final-deliverable pages 39-40.

## Common Submission Requirements

| PDF Requirement | Prepared Status | Final Verification |
|---|---|---|
| Exactly seven named presentation videos | Seven matching planning files exist | Pending seven exported MP4 files |
| Durations follow pages 33-38 | Plans target 5, 10, 5, 10, 5, max 5, max 5 minutes | Verify each exported duration |
| HD 1920x1080 | Included in common recording controls | Verify every exported file |
| Speaker view with face clearly visible | Included in common recording controls | Must be present in every recording |
| Every member contributes when team has more than one member | Project is documented as one person | Not applicable to additional members |
| One ZIP named `<TeamXX- Project Title>.zip` | Naming rule documented | Pending final packaging and cross-machine test |
| Peer assessment | Page 39 exempts one- or two-member teams | Not required for this one-person project |

## Video-by-Video Rubric Review

| Video | PDF Rubric | Prepared Coverage | Audit Result Before Recording |
|---:|---|---|---|
| 1 Management | Page 33: justification, scope/journey/backlog, Scrum/tracking/Sprints/burndown, member effort, planned vs actual, client/sponsor and team mitigation | Exact script and evidence cover all areas; reconstructed and retrospective records are clearly labelled | Material compliant; recording pending |
| 2 Architecture | Page 34: logical decisions/overview/DDD/deployment; physical decisions/stack/infrastructure/network/deployment; security threats/mitigations | Report diagrams, E08/E12/E26, trade-offs, ports, network, current/future separation, and threat table prepared | Material compliant; recording pending |
| 3 Software Design | Page 35: use case; analysis-to-design class and sequence diagrams; design patterns; schemas/models | Refresh/pagination use case is traced through diagrams, patterns, models, code, and tests | Material compliant; recording pending |
| 4 DevSecOps | Page 36: CI/CD test and scan artifacts; containers; SAST/DAST resolution/rescan; IaC/Git; regulatory framework | All controls and artifacts are prepared. Trivy and ZAP have genuine remediation loops. CodeQL completed but produced no retained actionable finding | Partially constrained: no CodeQL SAST finding-to-fix evidence; disclose in video |
| 5 Added Value | Page 38: minimum project requirements; sponsor acceptance/go-live; advanced or innovative area | Minimum standard and explainable recommendation/performance engineering are evidenced. Sponsor acceptance and go-live are unavailable | Partially constrained: sponsor criterion cannot be claimed; disclose in video |
| 6 App Demo | Page 37: live system, maximum 5 minutes | Exact live full-stack runbook covers launch, channels, refresh, pagination, offline fallback, error, and recovery | Content prepared; final emulator rehearsal and recording pending |
| 7 CI/CD Demo | Page 37: DevSecOps demonstration, maximum 5 minutes | Exact script covers commit trigger, jobs, artifacts, enforced failures, remediation, saved image, and opened E31/E32 artifact | Material compliant; recording pending |

## Narration Duration Check

| Video | Narration Words | Duration Limit | Recording Guidance |
|---:|---:|---:|---|
| 1 | 600 | 5 minutes | About 120 words per minute |
| 2 | 1,155 | 10 minutes | About 116 words per minute with diagram transitions |
| 3 | 562 | 5 minutes | About 112 words per minute |
| 4 | 1,254 | 10 minutes | About 125 words per minute; rehearse security terminology |
| 5 | 647 | 5 minutes | About 129 words per minute; keep screen changes prepared |
| 6 | 276 | Maximum 5 minutes | Narration is intentionally shorter to allow live interactions and recovery waits |
| 7 | 581 | Maximum 5 minutes | About 116 words per minute with artifact switching |

## Honest Submission Position

The seven **plans and supporting materials** are prepared, subject to the two evidence limitations above. The submission itself is not complete until all seven MP4 files are recorded, checked for duration and 1920x1080 resolution, verified to show the speaker's face, named exactly as page 40 requires, and packaged into the required ZIP.

Do not claim sponsor acceptance, production go-live, machine learning, real-time streaming, or a remediated CodeQL alert without new retained evidence.
