# Spring tutorial

[![build workflow](https://github.com/bitcoder/tutorial-spring/actions/workflows/maven.yml/badge.svg)](https://github.com/bitcoder/tutorial-spring/actions/workflows/maven.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=bitcoder_tutorial-spring&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=bitcoder_tutorial-spring)
[![Jira](https://img.shields.io/badge/jira-%230A0FFF.svg?style=for-the-badge&logo=jira&logoColor=white)](https://sergiofreire.atlassian.net/browse/ST)


This is a simple Spring Boot tutorial to showcase the CI flow:

- make local changes & push them
- CI tool runs build
- CI tool runs tests
- merge

This tutorial also pushes the results to Jira; the results are visible and its impacts can be tracked in Jira using a test management tool called Xray; this integration is optional but is shown as an example of how CI related results can be tracked elsewhere, including in the popular Jira issue tracker.

## Testing

This project contains unit and integration tests.
Unit tests are run using `surefire`, while integration tests can be run using `failsafe` maven plugin.


```bash
mvn test
```

```bash
mvn integration-test
```

If we want to fail the build whenever running the IT, we can execute the maven target `failsafe:verify` or just `verify` after running the IT.

```bash
mvn integration-test verify
```

## CI

CI is implemented using GH actions, on a [workflow](https://github.com/bitcoder/tutorial-spring/blob/main/.github/workflows/maven.yml) that is triggered:

- on the main branch
- on PRs (pull-requests)

We can also trigger the workflow/build on demand, right from the [Actions page](https://github.com/bitcoder/tutorial-spring/actions/workflows/maven.yml).

## SonarCloud

This project is integrated with [SonarCloud](https://sonarcloud.io/project/overview?id=bitcoder_tutorial-spring), and PRs will have information about code quality.

## Jira (using Xray Test Management)

The testing results are pushed to [Xray](https://www.getxray.app/), a test management tool, in Jira; only the integration tests are pushed.

- test automation results will be tracked on a Test Plan issue
- automated test entities will be provisioned on Jira/Xray, if they don't exist yet
- (some) automated test are linked automatically to existing stories, if they're annotated with `@Requirement(<story_issue_key>)` ([example](https://github.com/bitcoder/tutorial-spring/blob/2f0f43779c5f207409600997eb1d1320413e76b3/src/test/java/com/sergiofreire/xray/tutorials/springboot/IndexControllerMockedIT.java#L29)); that requires using the [xray-junit-extensions](https://github.com/Xray-App/xray-junit-extensions) dependency

To push the results to Jira, a maven plugin [xray-maven-plugin](https://github.com/Xray-App/xray-maven-plugin) is used; an alternative to that would be to use the GH action [xray-action](https://github.com/mikepenz/xray-action) instead. You choose :)

## Contact

If you're having the TQS classes, you can reach out using my email account at sergio dot freire (you know the rest).

Any questions related with this code, please raise issues in this GitHub project. Feel free to contribute and submit PR's
You may also find me on [Twitter](https://twitter.com/darktelecom), where I write once in a while about testing (don't use that for TQS class related topics though).
 

