---
description: |
  dummy coverage
"on":
  slash_command:
    name: coverage
  workflow_dispatch:
permissions: read-all
safe-outputs:
  add-comment: null
  noop:
    report-as-issue: false
network:
  allowed:
    - defaults
    - "*.tavily.com"
    - "*.cloud.getxray.app"
steps:
  - name: Authenticate with Xray
    id: auth
    env:
      XRAYCLOUD_CLIENT_ID: $\{\{ secrets.XRAYCLOUD_CLIENT_ID }}
      XRAYCLOUD_CLIENT_SECRET: $\{\{ secrets.XRAYCLOUD_CLIENT_SECRET }}
    run: |
      RESPONSE=$(curl -s -X POST "https://xray.cloud.getxray.app/api/v2/authenticate" \
        -H "Content-Type: application/json" \
        -d "{
          \"client_id\": \"$XRAYCLOUD_CLIENT_ID\", 
          \"client_secret\": \"$XRAYCLOUD_CLIENT_SECRET\" 
        }")

      # Xray returns token as a JSON string (quoted), so strip quotes
      TOKEN=$(echo $RESPONSE | tr -d '"')
      echo "RESPONSE=$RESPONSE"
      echo "TOKEN=$TOKEN"
      echo "XRAY_AUTH_TOKEN=$TOKEN" >> $GITHUB_ENV
mcp-servers:
  tavily:
    type: http
    url: "https://mcp.tavily.com/mcp/"
    headers:
      Authorization: "Bearer ${{ secrets.TAVILY_API_KEY }}"
    allowed: ["*"]
  graphql:
    type: "stdio"
    command: "npx"
    args: [ "mcp-graphql" ]
    allowed: ["*"]
    env:
      ENDPOINT: "https://xray.cloud.getxray.app/api/v2/graphql"
      ALLOW_MUTATIONS: "true"
      HEADERS: "{\\\"Authorization\\\":\\\"Bearer $XRAY_AUTH_TOKEN\\\"}"

---
# Dummy Coverage

Obtain current requirement coverage for project ST based on Xray and the xray-testing-progress skill.