#!/usr/bin/env bash

set -euo pipefail

COUNT="${1:-5}"
BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "Appending ${COUNT} fresh feed items via ${BASE_URL}/seed/append..."
curl -fsS "${BASE_URL}/seed/append?count=${COUNT}"
echo
echo "Done."
