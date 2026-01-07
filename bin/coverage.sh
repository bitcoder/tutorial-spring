#!/usr/bin/env bash

DEBUG=0
export XRAY_BASE_URL="https://eu.xray.cloud.getxray.app"
export JQL_QUERY="project = ST AND issuetype = Story"   

# if there is a first argument, set PROJECT_KEY to it
if [ $# -ge 1 ]; then
    export JQL_QUERY="project = $1 AND issuetype = Story"
fi

set -euo pipefail

# ========================
# Configuration
# ========================
LIMIT=100
MAX_RETRIES=5
INITIAL_BACKOFF=2
CONNECT_TIMEOUT=10
MAX_TIME=60

# ========================
# Required environment variables
# ========================
: "${XRAY_CLIENT_ID:?Missing XRAY_CLIENT_ID}"
: "${XRAY_CLIENT_SECRET:?Missing XRAY_CLIENT_SECRET}"
: "${XRAY_BASE_URL:?Missing XRAY_BASE_URL}"
: "${JQL_QUERY:?Missing JQL_QUERY}"

# ========================
# Retry helper (NO eval)
# ========================
retry_curl() {
  local attempt=1
  local backoff=$INITIAL_BACKOFF
  local response

  while true; do
    if response="$("$@")"; then
      if [[ -n "$response" ]] && ! echo "$response" | jq -e '.errors?' >/dev/null; then
        echo "$response"
        return 0
      fi
    fi

    [[ $DEBUG -eq 1 ]] && echo "Response: $response" >&2

    if (( attempt >= MAX_RETRIES )); then
      [[ $DEBUG -eq 1 ]] &&  echo "❌ Request failed after $attempt attempts" >&2
      return 1
    fi

    [[ $DEBUG -eq 1 ]] && echo "⚠️  Request failed (attempt $attempt). Retrying in ${backoff}s..." >&2
    sleep "$backoff"

    attempt=$((attempt + 1))
    backoff=$((backoff * 2))
  done
}

# ========================
# Authenticate
# ========================
echo "🔐 Authenticating with Xray..."

TOKEN=$(retry_curl curl -s \
  --connect-timeout "$CONNECT_TIMEOUT" \
  --max-time "$MAX_TIME" \
  -X POST "$XRAY_BASE_URL/api/v2/authenticate" \
  -H "Content-Type: application/json" \
  -d "$(jq -n \
      --arg id "$XRAY_CLIENT_ID" \
      --arg secret "$XRAY_CLIENT_SECRET" \
      '{client_id: $id, client_secret: $secret}')"
)
TOKEN=$(echo "$TOKEN" | tr -d '"')

echo "✅ Authenticated successfully"

# ========================
# Pagination Loop
# ========================
START=0
TOTAL_ISSUES=0
COVERED_ISSUES=0

while true; do
  # Build GraphQL query safely (multiline OK)
  GRAPHQL_QUERY=$(cat << EOF
{
  getCoverableIssues(jql: "${JQL_QUERY}", start: ${START}, limit: ${LIMIT}) {
    total
    results {
      issueId
      status {
        name
      }
    }
  }
}
EOF
)

  # Convert GraphQL → JSON safely
  GRAPHQL_PAYLOAD=$(jq -n --arg q "$GRAPHQL_QUERY" '{query: $q}')

  [[ $DEBUG -eq 1 ]] &&  echo "📡 Fetching issues (start=$START)..."

  RESPONSE=$(retry_curl curl -s \
    --connect-timeout "$CONNECT_TIMEOUT" \
    --max-time "$MAX_TIME" \
    -X POST "$XRAY_BASE_URL/api/v2/graphql" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "$GRAPHQL_PAYLOAD"
  )

  PAGE_COUNT=$(echo "$RESPONSE" | jq -r '.data.getCoverableIssues.results | length // 0')

  [[ $DEBUG -eq 1 ]] &&  echo "RESPOSNSE: $RESPONSE"

  if (( START == 0 )); then
    TOTAL_ISSUES=$(echo "$RESPONSE" | jq -r '.data.getCoverableIssues.total // 0')
    if (( TOTAL_ISSUES == 0 )); then
      echo "ℹ️  No coverable issues found."
      exit 0
    fi
  fi

  PAGE_COVERED=$(echo "$RESPONSE" | jq -r '[.data.getCoverableIssues.results[] | select(.status.name != "OK")] | length // 0')

  COVERED_ISSUES=$((COVERED_ISSUES + PAGE_COVERED))

  echo "   → $PAGE_COUNT issues, $PAGE_COVERED covered"

  # Exit pagination when last page reached
  if (( PAGE_COUNT < LIMIT )); then
    break
  fi

  START=$((START + LIMIT))
done

# ========================
# Final Percentage
# ========================
PERCENT=$(awk -v c="$COVERED_ISSUES" -v t="$TOTAL_ISSUES" \
  'BEGIN { printf "%.2f", (c / t) * 100 }')

echo "----------------------------------"
echo "Total coverable issues : $TOTAL_ISSUES"
echo "Covered issues         : $COVERED_ISSUES"
echo "Coverage percentage    : $PERCENT%"


# Export coverage as GitHub Actions output
if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
  echo "coverage_ok=$PERCENT" >> "$GITHUB_OUTPUT"
fi

# ========================
# Threshold check
# ========================
if (( $(echo "$PERCENT < 80.0" | bc -l) )); then
  echo "❌ Coverage below threshold (80%)"
  exit 1
else
  echo "✅ Coverage meets threshold"
  exit 0
fi
