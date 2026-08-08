#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CONTROL_FILE="$ROOT_DIR/compliance/gdpr-controls.json"

jq -e '
  (.controls | length) as $control_count |
  .framework == "EU General Data Protection Regulation (GDPR)" and
  .assessment_type == "applicability and control mapping, not certification" and
  ($control_count >= 4) and
  ([.controls[].id] | unique | length == $control_count) and
  (all(.controls[]; (.reference | length > 0) and (.objective | length > 0) and (.evidence | length > 0) and (.status | length > 0)))
' "$CONTROL_FILE" >/dev/null

echo "GDPR control mapping valid: $(jq '.controls | length' "$CONTROL_FILE") controls"
