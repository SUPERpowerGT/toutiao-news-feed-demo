#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APPEND_COUNT="${1:-5}"
STRESS_TOTAL="${2:-200}"
STRESS_CONCURRENCY="${3:-20}"

echo
echo "Running all test stages..."

"$ROOT_DIR/run_unit_tests.sh"
"$ROOT_DIR/run_integration_tests.sh" "$APPEND_COUNT"
"$ROOT_DIR/run_stress_tests.sh" "$STRESS_TOTAL" "$STRESS_CONCURRENCY"

echo
echo "[PASS] All test stages completed"
