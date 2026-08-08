# Video 7 - Presentation Assessment CI/CD Demo

## Delivery Information

- Required filename: `TeamXX- Presentation Assessment CICD Demo.mp4`
- Maximum duration: 5 minutes
- Primary purpose: demonstrate a real automated change-to-artifact pipeline rather than only showing a pipeline diagram.

## Required Demo Flow

- Identify the commit SHA used for the demonstration.
- Show the commit or pull request triggering GitHub Actions.
- Show Android CI stages and generated artifacts.
- Show Backend CI stages and coverage artifact.
- Show Integration, Load, and DAST workflow results.
- Show CodeQL and Trivy security results.
- Show a quality gate preventing success when a check fails, if a controlled example is available.
- Show the corrected commit or successful rerun.
- Download or open at least one generated artifact.
- Connect the successful build to the Docker/demo release process.

## Evidence Required Before Recording

- [ ] Final code is committed and pushed.
- [ ] Android CI is green.
- [ ] Backend CI is green.
- [ ] Integration and Load job is green.
- [ ] OWASP ZAP job has a downloadable report.
- [ ] CodeQL analysis is complete.
- [ ] Trivy filesystem and image scans are complete.
- [ ] APK, backend binary/coverage, test logs, and security reports are downloadable.
- [ ] Optional controlled failure and subsequent fixed run are prepared.

## Suggested Timeline

| Time | Action | Narration |
|---|---|---|
| 0:00-0:25 | Show commit/PR | Explain trigger and commit SHA |
| 0:25-1:15 | Open workflow overview | Explain automated quality gates |
| 1:15-2:00 | Android and Backend CI | Tests, lint/vet, coverage, build artifacts |
| 2:00-2:45 | Integration and load | API checks and 500/25 test artifact |
| 2:45-3:35 | Security jobs | CodeQL, Trivy, and ZAP results |
| 3:35-4:15 | Failure/remediation | Failed gate, fix commit, successful rerun |
| 4:15-4:45 | Artifacts and release | Open APK/report and relate to Docker build |
| 4:45-5:00 | Close | Summarize traceability and release readiness |

## Recording Notes

- Pre-run workflows before recording; do not wait for a full pipeline on camera.
- Use a prepared browser tab for each result and switch quickly.
- Keep commit SHA visible so evidence belongs to one known version.
- Do not describe Postman as DAST or a successful YAML parse as a security scan.
- If no controlled failure is available, explain the configured `exit-code: 1` gate and show a genuine historical failure only if it exists.

## Definition of Done

- [ ] Video is five minutes or shorter.
- [ ] A real GitHub Actions run is shown.
- [ ] Build, test, scan, and artifact stages are visible.
- [ ] At least one artifact is opened.
- [ ] Quality-gate and remediation behavior are explained truthfully.
- [ ] Commit-to-artifact traceability is clear.

