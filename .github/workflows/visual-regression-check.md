---
on:
  pull_request:
    types: [opened, synchronize]
  workflow_dispatch:

steps:
  - uses: actions/checkout@v6
    with:
      persist-credentials: false
  - name: Set up JDK
    uses: actions/setup-java@v4
    with:
      java-version: '17'
      distribution: 'temurin'
      cache: maven
  - name: Build and run app in background
    run: |
      mvn -B -DskipTests package --file pom.xml
      echo "Running the app in background..."
      export SERVER_PORT=8080
      mvn spring-boot:run &
      for i in {1..60}; do
        curl -fsS "http://localhost:${SERVER_PORT}/" >/dev/null && break
        sleep 2
      done
      curl -fsS "http://localhost:${SERVER_PORT}/" >/dev/null
      sudo iptables -t nat -A OUTPUT -p tcp -d 127.0.0.1 --dport 80 -j REDIRECT --to-port ${SERVER_PORT}

tools:
  playwright:
    mode: cli
  bash: [":*"]

network:
  allowed:
    - defaults
    - playwright
    - local
    - node
    - java

permissions:
  contents: read

safe-outputs:
  add-comment:
    max: 1
  noop:
    report-as-issue: false
---

# Visual Regression Check

The dev server is running at http://localhost:8080/. Check for visual regressions
on the home, getting-started, and reference pages across three viewports:

- Mobile: 375×812
- Tablet: 768×1024
- Desktop: 1440×900

For each viewport, resize and screenshot:

```bash
playwright-cli browser_resize --width 375 --height 812
playwright-cli browser_navigate --url "http://localhost:8080/"
playwright-cli browser_take_screenshot --filename /tmp/mobile-screenshot.png --full-page true
```

Compare against baseline and report differences as a PR comment with screenshots. If there are no regressions, call noop.