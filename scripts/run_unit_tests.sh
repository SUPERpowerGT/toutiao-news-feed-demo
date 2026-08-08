#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

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

run_step() {
  local description="$1"
  shift

  echo
  echo "-> $description"
  if "$@"; then
    print_ok "$description"
  else
    print_fail "$description"
    return 1
  fi
}

print_section "Unit Testing"

run_step "Backend unit tests (go test ./...)" \
  bash -lc "cd \"$ROOT_DIR/backend\" && go test ./..."

run_step "Backend compile verification (go build ./...)" \
  bash -lc "cd \"$ROOT_DIR/backend\" && go build ./..."

run_step "Android JVM unit tests (sh gradlew test)" \
  bash -lc "cd \"$ROOT_DIR/ToutiaoAndroid\" && sh gradlew test"

run_step "Android Kotlin compile verification (sh gradlew app:compileDebugKotlin)" \
  bash -lc "cd \"$ROOT_DIR/ToutiaoAndroid\" && sh gradlew app:compileDebugKotlin"

echo
print_ok "Unit testing script completed"
