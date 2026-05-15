---
description: |
  dummy aw
"on":
  slash_command:
    name: news
  workflow_dispatch:
permissions: read-all
safe-outputs:
  add-comment: null
  noop:
    report-as-issue: false
mcp-servers:
  tavily:
    type: http
    url: "https://mcp.tavily.com/mcp/"
    headers:
      Authorization: "Bearer ${{ secrets.TAVILY_API_KEY }}"
    allowed: ["*"]
network:
  allowed:
    - defaults
    - "*.tavily.com"
---
# Dummy aw

Look at the www.cnn.com website using `tavily` and add a comment with the top 3 news, as a very brief enumerated list of items.