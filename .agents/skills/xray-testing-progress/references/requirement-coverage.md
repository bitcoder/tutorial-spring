# Requirement Coverage Progress

This reference provides instructions for showing requirement coverage and testing progress for a project, sprint, or version/release.

## Flow

The user must provide the project/space key. Ask for the **Project/Space key** if not provided.

The user may optionally:
- Request coverage for the current open sprint in a project
- Request coverage for a specific version/release

### Obtaining Coverage Data

Use the `mcp-graphql` tool to obtain covered items (e.g., Story, Epic and similar issues) and their status based on coverage information. Use the `getCoverableIssues` query with the `jql` argument.

**Example query for project with key "ST":**

```graphql
query {
  getCoverableIssues(jql: "project=ST", limit: 100) {
    results {
      issueId
      jira(fields: ["key", "summary"])
      status(isFinal: true) {
        name
      }
    }			
  }
}
```

### JQL Variations

- **For current open sprint:** Use JQL like `project = ST and sprint in openSprints()`
- **For specific version/release:** Include `fixVersion` in JQL, e.g., `project = ST and fixVersion = v1.0`

**Important:** Results are paginated. Make additional requests using the `start` and `limit` arguments on the GraphQL function as needed.

## Output Format

Build the URL for the Overall Requirement/Test Coverage report using the project key. The `project.id` argument is static.

The output should show:
- A link to the overall requirement coverage report
- Color-coded status percentages
- A visual bar showing coverage distribution
- Lists of uncovered and failing requirements

**Example output:**

```
[Overall requirement coverage](https://remotejirainstance.atlassian.net/plugins/servlet/ac/com.xpandit.plugins.xray/test-coverage-report-page?project.key=ST&project.id=10001):
30% OK ✅, 50% NOK ❌, 20% UNCOVERED ⚠️
🟩🟩🟩🟥🟥⬜⬜⬜⬜⬜ 30% OK

⚠️ Uncovered requirements:
- [CALC-1](remotejirainstance.atlassian.net/browse/CALC-1): homepage
- [CALC-2](remotejirainstance.atlassian.net/browse/CALC-2): billing

❌ NOK requirements:
- [CALC-3](remotejirainstance.atlassian.net/browse/CALC-3): login
```

The bar must show colors based on the different statuses. The shown percentage after the bar corresponds to the tests passing percentage.

## Guidelines

- Never proceed without a project key from the user
- Ensure proper pagination handling for large datasets
- Use consistent color coding: ✅ (OK), ❌ (NOK), ⚠️ (UNCOVERED)
