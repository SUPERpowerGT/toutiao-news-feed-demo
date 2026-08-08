#!/usr/bin/env bash

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
TOTAL_REQUESTS="${1:-200}"
CONCURRENCY="${2:-20}"
TARGET_URL="$BASE_URL/api/v1/feed?scene=recommend&limit=15"

print_section() {
  echo
  echo "========================================"
  echo "$1"
  echo "========================================"
}

print_ok() {
  echo "[PASS] $1"
}

print_fail() {
  echo "[FAIL] $1"
}

print_section "Stress Testing"

echo "BASE_URL: $BASE_URL"
echo "TOTAL_REQUESTS: $TOTAL_REQUESTS"
echo "CONCURRENCY: $CONCURRENCY"
echo "TARGET_URL: $TARGET_URL"

echo
echo "-> Health check before stress test"
health_response="$(curl -fsS "$BASE_URL/health")"
if [[ "$health_response" != "ok" ]]; then
  print_fail "Backend is not healthy before stress test"
  exit 1
fi
print_ok "Backend health endpoint responded with ok"

echo
if command -v hey >/dev/null 2>&1; then
  echo "-> Running stress test with hey"
  hey -n "$TOTAL_REQUESTS" -c "$CONCURRENCY" "$TARGET_URL"
  print_ok "Stress test completed with hey"
elif command -v ab >/dev/null 2>&1; then
  echo "-> Running stress test with ab"
  ab_output="$(ab -n "$TOTAL_REQUESTS" -c "$CONCURRENCY" "$TARGET_URL")"
  echo "$ab_output"
  failed_requests="$(echo "$ab_output" | awk '/Failed requests:/ { print $3 }')"
  if [[ -z "$failed_requests" || "$failed_requests" != "0" ]]; then
    print_fail "Stress test reported ${failed_requests:-unknown} failed requests"
    exit 1
  fi
  print_ok "Stress test completed with ab"
else
  echo "-> Neither hey nor ab is installed, falling back to sequential curl loop"
  start_ts="$(date +%s)"
  for ((i=1; i<=TOTAL_REQUESTS; i++)); do
    curl -fsS "$TARGET_URL" > /dev/null
    if (( i % 20 == 0 )); then
      echo "   completed $i requests"
    fi
  done
  end_ts="$(date +%s)"
  elapsed="$((end_ts - start_ts))"
  echo "Sequential fallback completed in ${elapsed}s"
  print_ok "Stress test completed with curl fallback"
fi
