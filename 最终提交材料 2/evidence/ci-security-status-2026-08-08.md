# CI and Security Status - 2026-08-08

## Final Verified Results

| Workflow | Verified Result | Evidence |
|---|---|---|
| Android CI | Historical final-code predecessor run succeeded; latest documentation retry is used for final screenshot | GitHub Actions / Android CI |
| Backend CI | Success: format, vet, race tests, coverage, binary, and image build | Run `31239956761`; artifact `backend-ci-artifacts` |
| SAST - CodeQL | Go and Java/Kotlin analysis executed on the final code line | Run `31239956737` |
| Dependency and Container Security | Success: filesystem and backend-image HIGH/CRITICAL gates passed | Run `31239956740`; artifacts `trivy-filesystem-report` and `trivy-image-report` |
| Integration, Load and DAST | Success: integration, load, and OWASP ZAP jobs passed | Run `31239956754`; artifacts `integration-load-test-results` and `zap-dast-report` |

All run links are available from:

<https://github.com/SUPERpowerGT/toutiao-news-feed-demo/actions>

## Remediation and Rescan Trail

1. Commit `0c7a0a8` introduced the complete CI/security workflow set.
2. Run `31239070096` failed during setup because the Trivy Action tag omitted the required `v` prefix.
3. Commit `c80bdd4` corrected the action tag. Run `31239156420` then executed real scans and blocked 12 HIGH/CRITICAL Go dependency findings: 10 HIGH and 2 CRITICAL.
4. The dependencies were upgraded to `pgx v5.9.0`, `x/sync v0.21.0`, and `x/text v0.39.0`; the backend moved to Go 1.25 and Alpine 3.23.
5. Local Trivy v0.70.0 rescan reported zero dependency vulnerabilities, zero secrets, and zero Dockerfile misconfigurations at HIGH/CRITICAL severity.
6. Run `31239572939` and final run `31239956740` both passed filesystem and image gates.
7. ZAP initially reported three warnings: non-storable API responses, intentional Unix timestamps, and an invalid/missing Cross-Origin-Resource-Policy value.
8. The API added restrictive security headers, including `Cross-Origin-Resource-Policy: same-origin`. Rules 10049 and 10096 were documented as justified API behavior in `.zap/rules.tsv`.
9. Local final ZAP result was 65 PASS, 0 FAIL, 0 WARN, and 2 documented IGNORE results. GitHub run `31239956754` then passed both the Integration/Load job and the DAST job.

## Artifact Screenshot Checklist

- On the Backend CI run summary, capture both green jobs and the `backend-ci-artifacts` entry.
- On the Security run summary, capture the green `filesystem-scan` and `image-scan` jobs plus both Trivy artifacts.
- On the Integration/Load/DAST run summary, capture both green jobs plus `integration-load-test-results` and `zap-dast-report`.
- On CodeQL, capture both language matrix jobs after completion.
- For remediation evidence, capture the failed Trivy gate in run `31239156420`, commit `bb0467d`, and the successful security rescan in run `31239956740`.
- Keep the full commit SHA visible whenever possible.

## Remaining Low-Severity Dependency Notice

After the remediation push, GitHub Dependabot reported one remaining low-severity alert. It is not a HIGH/CRITICAL Trivy gate failure. Open the repository Security / Dependabot page to record or resolve it separately, without describing it as a clean zero-alert Dependabot result.
