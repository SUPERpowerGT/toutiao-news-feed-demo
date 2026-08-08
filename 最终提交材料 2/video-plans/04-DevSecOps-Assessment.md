# Video 4 - Technical Assessment - DevSecOps

## Delivery Information

- Required filename: `TeamXX- Technical Assessment - DevSecOps.mp4`
- Target duration: 10 minutes
- Primary purpose: prove a working build, test, security, container, deployment, audit, remediation, and evidence pipeline.

## Required Content

- DevSecOps pipeline architecture and quality gates.
- Android CI: unit tests, lint, APK build, and uploaded reports/artifact.
- Backend CI: formatting, vet, race tests, coverage, binary, and container build.
- Unit test evidence with case counts and coverage.
- API integration test evidence.
- Load/stress test configuration and metrics.
- CodeQL SAST result.
- Trivy dependency, secret, misconfiguration, and image scan results.
- OWASP ZAP DAST result.
- Vulnerability finding, remediation decision, and rescan outcome.
- Multi-stage non-root backend container and health check.
- Docker Compose as Infrastructure as Code.
- Runtime logs, health status, and fault diagnosis example.
- Git history and traceability from change to pipeline run.

## Evidence Required Before Recording

- [ ] Successful Android CI run and downloaded quality artifact.
- [ ] Successful Backend CI run and coverage artifact.
- [ ] Successful Integration and Load workflow artifact.
- [ ] CodeQL result for Go and Java/Kotlin.
- [ ] Trivy filesystem and container-image reports.
- [ ] OWASP ZAP report.
- [ ] At least one initial finding and rescan, or an explicit zero-finding report with tool/version/commit SHA.
- [ ] Docker image build and non-root user evidence.
- [ ] Container health and structured log screenshot.
- [ ] Git commit SHA associated with all shown results.

## Suggested Timeline

| Time | Narration | Screen Evidence |
|---|---|---|
| 0:00-0:50 | Pipeline and quality-gate overview | E12 architecture diagram |
| 0:50-2:00 | Android CI | Test, lint, APK, artifact pages |
| 2:00-3:10 | Backend CI | Format, vet, race, coverage, image build |
| 3:10-4:15 | Automated testing | Unit and integration reports |
| 4:15-5:10 | Load/stress testing | 500/25 metrics and before/after result |
| 5:10-6:15 | CodeQL SAST | Analysis result and code location |
| 6:15-7:25 | Trivy security | Dependency, secret, config, image reports |
| 7:25-8:10 | OWASP ZAP DAST | Target, rules, findings, report |
| 8:10-9:05 | Remediation and rescan | Finding, fix commit, clean/accepted rescan |
| 9:05-9:40 | Container and operations | Non-root image, health, logs, Compose |
| 9:40-10:00 | Audit trail conclusion | Commit to artifact traceability |

## Recording Notes

- Do not record this video before the workflows have run on GitHub.
- A workflow YAML file is design evidence, not execution evidence.
- Postman is integration evidence, not DAST evidence.
- Show tool version, commit SHA, scan target, severity, result, and artifact where possible.
- Do not hide failed scans; explain remediation or documented risk acceptance.

## Definition of Done

- [ ] All four test levels required by the rubric are addressed.
- [ ] SAST, DAST, dependency, secret, configuration, and image scans show real results.
- [ ] A remediation/rescan loop is demonstrated.
- [ ] Container management, logs, IaC, and Git audit are visible.
- [ ] Every screenshot belongs to the final or clearly identified commit.

