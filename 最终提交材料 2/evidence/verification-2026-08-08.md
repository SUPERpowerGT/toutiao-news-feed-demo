# Verification Evidence - 2026-08-08

This evidence note records commands and results observed against the local checkout and links them to the final GitHub Actions evidence captured after the workflows were pushed.

## Backend Unit Tests

Command:

```bash
cd backend
go test -race -coverprofile=coverage.out ./...
```

Result: PASS

| Package | Statement Coverage |
|---|---:|
| `toutiao-backend/api` | 54.9% |
| `toutiao-backend/application` | 64.4% |
| Overall Go statements | 23.2% |

These figures were reproduced after the final compliance/logging changes. The race detector completed without reporting a data race. Infrastructure and executable wiring remain the principal uncovered areas, so no repository-wide high-coverage claim is made.

## Android Unit Tests

Command:

```bash
cd ToutiaoAndroid
./gradlew test
```

Result: PASS (`BUILD SUCCESSFUL`)

Debug and Release test variants each executed:

- 5 `FeedViewModelTest` cases
- 2 `RefreshStateLogicTest` cases
- 1 `UpdateBannerLogicTest` case
- 1 legacy template test

All business tests completed with zero failures, zero errors, and zero skipped tests.

## API Integration Tests

Command:

```bash
bash scripts/run_integration_tests.sh 5
```

Result: PASS

Verified behavior:

- backend health and seed reset
- initial recommendation feed response
- recommendation reason and score fields
- append-and-refresh flow
- video scene separation
- invalid cursor, scene, and limit return HTTP 400
- conflicting cursor/refresh modes return HTTP 400
- unsupported HTTP method returns HTTP 405

## Load and Stress Test

Command:

```bash
bash scripts/run_stress_tests.sh 500 25
```

Environment: local Docker Compose stack, ApacheBench 2.3, PostgreSQL 16.

Result after repository connection-pool fix: PASS

| Metric | Result |
|---|---:|
| Complete requests | 500 |
| Failed requests | 0 |
| Concurrency | 25 |
| Throughput | 1267.76 requests/second |
| Mean latency | 19.72 ms |
| P50 | 18 ms |
| P95 | 35 ms |
| P99 | 40 ms |
| Maximum | 45 ms |

Before the fix, the same test exhausted the 10-connection database pool and timed out after completing only one request. The repository held outer query result sets while issuing nested media queries. The fix reads and closes the outer rows before loading media, preventing connection-pool starvation.

A second 500-request/25-concurrency run also completed with zero failures. It produced 1124.14 requests/second, P95 50 ms, P99 59 ms, and a maximum response time of 63 ms, confirming that the fix is repeatable.

## Container Verification

The production Compose configuration passed `docker compose config --quiet`. The backend image built successfully with:

- multi-stage Go build
- stripped static binary
- minimal Alpine runtime
- non-root `app` user
- container health check

Runtime inspection additionally confirmed:

- `toutiao-backend` and `toutiao-postgres` were healthy
- backend runtime identity `uid=100(app) gid=101(app)`
- private network `toutiao-news-feed-demo_toutiao_net`
- method, route, status, and duration in container logs
- valid 7.5 MB `docker save` archive with a recorded SHA-256 digest

## Final GitHub Evidence

- Android CI and downloadable APK/quality artifacts: E19
- Backend GDPR validation, tests, coverage, binary, image build/save, and artifacts: E30
- CodeQL Go and Java/Kotlin analysis: E23
- Trivy filesystem, secret, misconfiguration, and image scans: E21, with E24 as the pre-remediation failure
- OWASP ZAP DAST: E22, with E28 as the pre-remediation failure
- Container runtime and saved-image evidence: E26 and E27
