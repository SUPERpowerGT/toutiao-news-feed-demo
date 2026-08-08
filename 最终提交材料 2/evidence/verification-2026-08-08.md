# Verification Evidence - 2026-08-08

This evidence note records commands and results observed against the current local checkout. It is not a substitute for GitHub Actions artifacts; CI artifacts should be captured again after the workflows are pushed.

## Backend Unit Tests

Command:

```bash
cd backend
go test -race -coverprofile=coverage.out ./...
```

Result: PASS

| Package | Statement Coverage |
|---|---:|
| `toutiao-backend/api` | 91.4% |
| `toutiao-backend/application` | 75.3% |

The race detector completed without reporting a data race.

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

## Remaining External Evidence

The following evidence can only be produced after pushing the workflows:

- Backend CI run and downloadable coverage artifact
- Android unit-test/lint artifact
- CodeQL SAST result
- Trivy filesystem, secret, misconfiguration, and image scan result
- OWASP ZAP DAST report
