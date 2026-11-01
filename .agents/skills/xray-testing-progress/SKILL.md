---
name: xray-testing-progress
description: Shows requirement coverage and testing progress based on Xray test management data.
allowed-tools: mcp-graphql
---

# Show Testing Progress from Xray

This skill provides testing progress information from Xray test management, including Test Plan progress and overall requirement coverage.

## Flow

Depending on the user's request, determine which type of progress information is needed:

1. **Test Plan Progress**: Shows the status and progress of tests within a specific Test Plan
2. **Requirement Coverage**: Shows overall coverage for a project, sprint, or version/release

Follow the specific instructions for each type of progress information to ensure consistent and accurate reporting.

## Specific Tasks

* **Test Plan Progress** [references/testplan-progress.md](references/testplan-progress.md) - Show progress of tests within a Test Plan
* **Requirement Coverage** [references/requirement-coverage.md](references/requirement-coverage.md) - Show overall requirement coverage for projects, sprints, or versions

## General Guidelines

- Always use the `mcp-graphql` tool for data extraction
- Results are typically paginated; use `start` and `limit` arguments as needed
- Format output with visual bars and color-coded status indicators
- Include links to relevant Jira/Xray issues and reports

# Don'ts

- Never use other tools or commands than the provided ones
- Do not proceed without required information from the user (e.g., project key, Test Plan key)
