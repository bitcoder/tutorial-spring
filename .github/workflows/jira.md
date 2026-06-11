---
"on":
  reaction: eyes
  slash_command:
    name: jira
permissions:
  contents: read
  issues: read
safe-outputs:
  add-comment: null
  noop:
    report-as-issue: false
tools:
  github:
    toolsets:
    - default

mcp-servers:
  atlassian-mcp-server:
    registry: https://api.mcp.github.com/v0.1/servers/com.atlassian/atlassian-mcp-server
    url: https://mcp.atlassian.com/v1/mcp
    type: http
    allowed: ["*"]
    headers:
      Authorization: "Bearer ${{ secrets.MCP_ATLASSIAN_API_KEY }}"
---
# Add info based on Jira

Use "${{ steps.sanitized.outputs.text }}" as context for obtaining the issue information Jira. Add the info as a brief comment on the current GitHub issue.
If no errors found: Call noop celebrating no errors.