# Test Plan Progress

This reference provides instructions for showing the progress of a specific Test Plan, detailing the percentage of tests passing and other statuses.

## Flow

Ask for the **Test Plan issue key** if not provided.

Use the `mcp-graphql` tool to obtain the Tests on the Test Plan and their status. For this, use the `getTestPlans` query with the `jql` argument.

### Obtaining Test Plan Data

**Example query for Test Plan with key "ST-3":**

```graphql
query {
  getTestPlans(jql: "key=ST-3", limit: 10) {
    results {
      issueId
      jira(fields: ["key", "summary"])
      tests(limit: 100) {
        results {
          jira(fields: ["key", "summary"])
          status {
            name
          }
        }
      }
    }
  }
}
```

**Important:** Results are paginated. Make additional requests using the `start` and `limit` arguments on the GraphQL function as needed, both for Test Plans and for tests within a Test Plan.

## Output Format

The output should show:
- Test Plan key and summary as a link
- Color-coded status percentages for tests
- A visual bar showing test status distribution
- List of failed tests (if any)

**Example output:**

```
Progress of Test Plan [CALC-123](remotejirainstance.atlassian.net/browse/CALC-123): regression testing
30% tests passing ✅, 20% failing ❌, 50% to do ⚠️
🟩🟩🟩🟥🟥⬜⬜⬜⬜⬜ 30% PASSING

❌ Failed tests:
- [CALC-1](remotejirainstance.atlassian.net/browse/CALC-1): valid login scenario
- [CALC-2](remotejirainstance.atlassian.net/browse/CALC-2): invalid login scenario
```

The bar must show colors based on the different test statuses. The shown percentage after the bar corresponds to the tests passing percentage.

## Guidelines

- Never proceed without a Test Plan key from the user
- Ensure proper pagination handling for Test Plans with many tests
- Use consistent color coding: ✅ (PASSING), ❌ (FAILING), ⚠️ (TO DO/TODO/Not executed)
- Include test summary information for better context
