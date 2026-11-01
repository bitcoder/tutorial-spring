---
agent: 'agent'
model: GPT-4o
tools: ['githubRepo', 'search/codebase', 'jira']
description: 'Report a Bug issue in Jira'
---
Your goal is to create a Bug issue in Jira, describing the detailed problem, steps to reproduce, the code that may be introducing the problem, and the severity of the problem

Ask for the affected version and environment if not provided.
Ask also for the related requirement issue key, if not provided; it can be empty.

Fields to set on the Bug:
* summary
* description
* component
* severity
* affected version
* environment (the field value should be in valid ADF - Atlassian Document Format)

If requirement is provided, create a issue link between the created Bug and the requirement issue.

