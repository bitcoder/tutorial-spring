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
    command: npx
    args: ["-y", "@tavily/mcp"]
    env:
      TAVILY_API_KEY: "${{ secrets.TAVILY_API_KEY }}"
    allowed: ["search", "search_news"]
---
# Dummy aw

Look at the www.cnn.com website using `tavily` and add a comment with the top 3 news, as a very brief enumerated list of items.