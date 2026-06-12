---
description: |
  Fetches manual tests from Xray using a predefined JQL, caches the list in cache-memory,
  and automatically implements one automated JUnit 5 test per run following the
  project conventions. Creates a pull request for each successfully automated test and
  updates the cache to track progress.

on:
  schedule:
    - cron: "0 8 * * 1"  # Weekly on Monday at 08:00 UTC
  workflow_dispatch:

permissions:
  contents: read
  pull-requests: read

strict: false

steps:
  - name: Checkout repository
    uses: actions/checkout@v4
    with:
      fetch-depth: 0
      persist-credentials: false
  - name: Set up JDK
    uses: actions/setup-java@v4
    with:
      java-version: '17'
      distribution: 'temurin'
      cache: maven
  - name: Authenticate with Xray
    id: auth
    env:
      XRAYCLOUD_CLIENT_ID: ${{ secrets.XRAYCLOUD_CLIENT_ID }}
      XRAYCLOUD_CLIENT_SECRET: ${{ secrets.XRAYCLOUD_CLIENT_SECRET }}
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
  graphql:
    type: "stdio"
    command: "npx"
    args: [ "mcp-graphql" ]
    allowed: ["*"]
    env:
      ENDPOINT: "https://xray.cloud.getxray.app/api/v2/graphql"
      ALLOW_MUTATIONS: "true"
      HEADERS: "{\\\"Authorization\\\":\\\"Bearer $XRAY_AUTH_TOKEN\\\"}"

network:
  allowed:
    - defaults
    - "*.cloud.getxray.app"

tools:
  cache-memory: true
  cli-proxy: true
  bash:
    - "mvn*"
    - "git*"
    - "cat*"
    - "echo*"
    - "tee*"
    - "mkdir*"
    - "ls*"
    - "curl*"
    - "gh*"
    - "date*"
    - "jq*"
    - "cp*"
    - "pwd*"
    - "rm*"

safe-outputs:
  create-pull-request:
    title-prefix: "[automator] "
    labels: [automated-test, automator]
  noop:
    report-as-issue: false

engine: copilot

timeout-minutes: 30
---
# Test Automator

You are an automation engineer. Your job is to pick one pending manual test from Xray and implement it as a JUnit 5 automated test following this repository's conventions. If successful, create a pull request. Process exactly **one** test per run.

## Context

- Repository: `${{ github.repository }}`
- Working directory: `${{ github.workspace }}`
- Cache file: `/tmp/gh-aw/cache-memory/automator-pending-xray-tests.json`
- Predefined JQL: `project = ST AND issuetype = Test AND testType in (Manual)`

---

## Step 1 – Load or Initialize the Cache

The cache is stored in `/tmp/gh-aw/cache-memory/automator-pending-xray-tests.json`.

### If the cache file does not exist (first run):

Use the `graphql` MCP tool to fetch manual tests from Xray:

```graphql
{
  getTests(jql: "project = ST AND issuetype = Test AND testType in (Manual)", limit: 100) {
    total
    results {
      issueId
      jira(fields: ["key", "summary", "description", "status"])
      testType { name }
      steps {
        id
        action
        data
        result
      }
    }
  }
}
```

Build a JSON structure:

```json
{
  "fetched_at": "<ISO-8601 timestamp>",
  "tests": [
    {
      "key": "ST-XXX",
      "summary": "...",
      "steps": [
        { "action": "...", "data": "...", "result": "..." }
      ],
      "status": "pending"
    }
  ]
}
```

Write the JSON above to `/tmp/gh-aw/cache-memory/automator-pending-xray-tests.json`.

### If the cache file exists:

Read the file and parse the JSON. If `fetched_at` is older than 7 days, re-fetch from Xray and merge: keep the existing `status` for tests already in the cache, add new tests as `"pending"`. Overwrite `/tmp/gh-aw/cache-memory/automator-pending-xray-tests.json` with the merged JSON.

---

## Step 2 – Find the Next Test to Automate

From the cache JSON, pick the **first** test whose `status` is `"pending"`.

- If **no pending tests** are found: call `noop` with message `"All tests in the cache have been processed."` and stop.
- If one is found: proceed with it. Store its `key` as `CURRENT_KEY`.

---

## Step 3 – Evaluate Automatability

A test is **clear enough to automate** when ALL of the following hold:

1. Every step has a non-empty, unambiguous `action`.
2. The expected `result` per step is specific and verifiable in code (e.g., HTTP status code, response field, database state).
3. No step requires human visual judgment or an unavailable external system.
4. The behaviour under test maps to an existing controller, service, or repository in the codebase.

Read the relevant source to confirm (e.g., `src/main/java/.../boundary/`, `services/`, `data/`).

If the test is **NOT** automatable:
1. Update `/tmp/gh-aw/cache-memory/automator-pending-xray-tests.json`: set `"status": "skipped"`, add `"skip_reason": "<brief explanation>"` for the relevant test entry, and overwrite the file.
2. Call `noop` with message `"Skipped CURRENT_KEY: <reason>."` and stop.

---

## Step 4 – Implement the Automated Test

### Decide the test type

| Scenario | Type | Naming |
|---|---|---|
| Pure business logic, no I/O | Unit test | `XxxTest.java` |
| REST endpoint or DB interaction | Integration test | `XxxIT.java` |

### Repository conventions

- **Package**: `com.sergiofreire.xray.tutorials.springboot`
- **Source root**: `src/test/java/com/sergiofreire/xray/tutorials/springboot/`
- **Unit tests**: `@ExtendWith(MockitoExtension.class)`, `@InjectMocks`, `@Mock(strictness = Strictness.LENIENT)`
- **Integration tests**: `@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)` + `@AutoConfigureTestDatabase`, `@Autowired TestRestTemplate`
- **Xray link**: annotate every test method with `@Requirement("CURRENT_KEY")` from `app.getxray.xray.junit.customjunitxml.annotations.Requirement`
- **Assertions**: AssertJ (`assertThat(...)`)

Add the new test method(s) to an existing test class if it fits naturally; otherwise create a new file.

Write the Java file to disk using bash. Example for a new file:

```bash
cat > "${{ github.workspace }}/src/test/java/com/sergiofreire/xray/tutorials/springboot/NewTestIT.java" << 'JAVA_EOF'
package com.sergiofreire.xray.tutorials.springboot;
// ... imports and class body ...
JAVA_EOF
```

---

## Step 5 – Run the Tests

Run only the affected test class to keep the build fast.

**Integration test:**
```bash
cd ${{ github.workspace }} && mvn failsafe:integration-test failsafe:verify -Dit.test=<ClassName>
```

**Unit test:**
```bash
cd ${{ github.workspace }} && mvn surefire:test -Dtest=<ClassName>
```

**If the tests fail:**
- Review the failure output and fix the implementation; retry up to **3 attempts** total.
- After 3 failed attempts:
  1. Update `/tmp/gh-aw/cache-memory/automator-pending-xray-tests.json`: set `"status": "skipped"`, `"skip_reason": "implementation_failed after 3 attempts"`, and overwrite the file.
  2. Call `noop` with a brief explanation and stop.

---

## Step 6 – Create the Pull Request

Once all tests pass, call `create-pull-request` with:
- `title`: `automate CURRENT_KEY: <short summary>` (title-prefix `[automator] ` is added automatically)
- `branch`: `automator/CURRENT_KEY-automated-test`
- `base`: `main`
- `body`: include the Xray test key (with link), the manual test steps covered, the file and method names created, and any implementation notes

The gh-aw framework will diff your workspace changes, commit them to the branch, and open the PR automatically.

After calling `create-pull-request`, update `/tmp/gh-aw/cache-memory/automator-pending-xray-tests.json`:
- Set the test entry's `"status"` to `"pr_created"`
- Add `"pr_branch": "automator/CURRENT_KEY-automated-test"`
