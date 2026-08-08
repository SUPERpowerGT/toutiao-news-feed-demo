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
| 4 DevSecOps | Page 36: CI/CD test and scan artifacts; containers; SAST/DAST resolution/rescan; IaC/Git; regulatory framework | CodeQL and Semgrep SAST, Trivy, opened Android/Go test evidence, integration/load artifacts, ZAP finding/remediation/rescan with final E43, container evidence, IaC/Git, and GDPR are implemented. The script accurately describes delivery-ready artifacts and ephemeral deployment verification rather than claiming production CD | Material compliant; recording pending |
| 5 Added Value | Page 38: minimum project requirements; Sponsor acceptance/go-live; advanced or innovative area | Minimum standard and explainable recommendation/performance engineering are evidenced. Internal go-live is currently only a developer declaration | **Major rubric gap:** no Sponsor-originated acceptance/feedback evidence. Page 29 says confidentiality is not a reason to omit evidence; obtain a sanitized/redacted record |
| 6 App Demo | Page 37: live system, maximum 5 minutes | E35-E42 and the runbook cover launch, channels, backend-hosted playback, refresh, pagination, Room fallback, error, and recovery | Functional content ready; final MP4, backup take, exact 1920x1080 export, duration check, privacy check, and clearly visible face remain |
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

The seven **plans and supporting materials** are prepared, subject to the stated evidence boundaries above. The submission itself is not complete until all seven MP4 files are recorded, checked for duration and 1920x1080 resolution, verified to show the speaker's face, named exactly as page 40 requires, and packaged into the required ZIP.

Do not disclose or invent confidential company details. However, PDF page 29 explicitly says confidentiality is not a reason to show no evidence. Use a redacted Sponsor email, signed acceptance note, sanitized release record, or equivalent Sponsor-originated statement. Describe internal go-live as a developer declaration until such evidence exists. Do not claim machine learning or real-time streaming without new retained evidence.

## Remaining Actions for Videos 4-6

1. Video 4: evidence and wording are ready; record the prepared script and keep E43 plus the expanded backend coverage screen readable.
2. Video 5: obtain sanitized Sponsor acceptance or feedback. This is the only remaining rubric evidence item that cannot be generated from the repository.
3. Video 6: record the verified live flow, then check duration, exact 1920x1080 dimensions, face visibility, audio, privacy, playback, and backup take.
4. Record all seven MP4 files, replace `TeamXX` and the ZIP placeholder with the actual team identifier and project title, then perform the final cross-machine ZIP test.
