#!/usr/bin/env bash

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
APPEND_COUNT="${1:-5}"

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

dump_response_preview() {
  local payload="$1"
  echo "Response preview:"
  echo "$payload" | head -c 400
  echo
}

assert_contains() {
  local haystack="$1"
  local needle="$2"
  local message="$3"

  if [[ "$haystack" == *"$needle"* ]]; then
    print_ok "$message"
  else
    print_fail "$message"
    return 1
  fi
}

assert_contains_or_dump() {
  local haystack="$1"
  local needle="$2"
  local message="$3"
  local hint="${4:-}"

  if [[ "$haystack" == *"$needle"* ]]; then
    print_ok "$message"
  else
    print_fail "$message"
    if [[ -n "$hint" ]]; then
      echo "Hint: $hint"
    fi
    dump_response_preview "$haystack"
    return 1
  fi
}

assert_status() {
  local expected="$1"
  local url="$2"
  local message="$3"
  local method="${4:-GET}"
  local actual

  actual="$(curl -sS -X "$method" -o /dev/null -w "%{http_code}" "$url")"
  if [[ "$actual" == "$expected" ]]; then
    print_ok "$message (HTTP $actual)"
  else
    print_fail "$message (expected HTTP $expected, got HTTP $actual)"
    return 1
  fi
}

extract_latest_publish_time() {
  local payload="$1"
  echo "$payload" | sed -n 's/.*"latest_publish_time":[[:space:]]*\([0-9][0-9]*\).*/\1/p' | head -n 1
}

print_section "Integration Testing"

echo "BASE_URL: $BASE_URL"

echo
echo "-> Health check"
health_response="$(curl -fsS "$BASE_URL/health")"
if [[ "$health_response" == "ok" ]]; then
  print_ok "Backend health endpoint responded with ok"
else
  print_fail "Backend health endpoint did not respond with ok"
  exit 1
fi

echo
echo "-> Reset demo data"
seed_response="$(curl -fsS "$BASE_URL/seed")"
assert_contains "$seed_response" "seed ok" "Seed reset completed"

echo
echo "-> Initial recommend feed request"
initial_response="$(curl -fsS "$BASE_URL/api/v1/feed?scene=recommend&limit=5")"
assert_contains "$initial_response" '"scene":"recommend"' "Recommend scene returned"
assert_contains "$initial_response" '"top_items":[' "Recommend top_items field exists"
assert_contains "$initial_response" '"items":[' "Recommend items field exists"
assert_contains "$initial_response" '"latest_publish_time":' "Recommend latest_publish_time exists"
assert_contains_or_dump \
  "$initial_response" \
  '"reason":' \
  "Initial recommend response contains recommendation reason" \
  "Your local backend may not be running the latest code. Try rebuilding/restarting backend first."
assert_contains_or_dump \
  "$initial_response" \
  '"recommend_score":' \
  "Initial recommend response contains recommendation score" \
  "Your local backend may not be running the latest code. Try rebuilding/restarting backend first."

latest_publish_time="$(extract_latest_publish_time "$initial_response")"
if [[ -z "$latest_publish_time" ]]; then
  print_fail "Failed to extract latest_publish_time from initial feed response"
  exit 1
else
  print_ok "Extracted latest_publish_time=$latest_publish_time"
fi

echo
echo "-> Append newer feed items"
append_response="$(curl -fsS "$BASE_URL/seed/append?count=$APPEND_COUNT")"
assert_contains "$append_response" "append seed ok" "Append refresh data completed"

echo
echo "-> Refresh recommend feed with previous latest_publish_time"
refresh_response="$(curl -fsS "$BASE_URL/api/v1/feed?scene=recommend&refresh_time=$latest_publish_time&limit=15")"
assert_contains "$refresh_response" '"scene":"recommend"' "Refresh recommend scene returned"
assert_contains_or_dump \
  "$refresh_response" \
  '"reason":' \
  "Recommendation reason field exists in refresh response" \
  "If initial response already had reason but refresh does not, verify the backend was restarted after the latest changes."
assert_contains_or_dump \
  "$refresh_response" \
  '"recommend_score":' \
  "Recommendation score field exists in refresh response" \
  "If this field is missing, your running backend is likely older than the current repository code."

echo
echo "-> Video scene request"
video_response="$(curl -fsS "$BASE_URL/api/v1/feed?scene=video&limit=5")"
assert_contains "$video_response" '"scene":"video"' "Video scene returned"
assert_contains "$video_response" '"top_items":[]' "Video scene does not return top_items"

echo
echo "-> Request validation checks"
assert_status "400" "$BASE_URL/api/v1/feed?cursor=invalid" "Invalid cursor is rejected"
assert_status "400" "$BASE_URL/api/v1/feed?scene=unknown" "Unsupported scene is rejected"
assert_status "400" "$BASE_URL/api/v1/feed?limit=101" "Out-of-range limit is rejected"
assert_status "400" "$BASE_URL/api/v1/feed?cursor=1&refresh_time=2" "Conflicting feed modes are rejected"
assert_status "405" "$BASE_URL/api/v1/feed" "Unsupported method is rejected" "POST"

echo
print_ok "Integration testing script completed"
