---
description: |
    Issue clarifier

on:
    issues:
        types: [opened, reopened]

permissions: read-all

safe-outputs:
    add-comment:
    noop:
        report-as-issue: false
---
# Issue Clarifier

Analyze the current issue and ask for additional details if the issue is unclear, ambiguous, or contraditory.
