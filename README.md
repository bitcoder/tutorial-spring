# Spring tutorial

This is a simple Spring Boot tutorial to showcase the CI flow.


## Testing

This project contains unit and integration tests.
Unit tests are run using `surefire`, while integration tests can be run using `failsafe` maven plugin.


```bash
mvn test
```

```bash
mvn failsafe:integration-test
```

If we want to fail the build whenever running the IT, we can execute the maven target `failsafe:verify" after running the IT.

```bash
mvn failsafe:integration-test failsafe:verify
```

## CI

CI is implemented using GH actions, on a [worflow](./.github/workflows/maven.yml) that is triggered:

- on the main branch
- on PRs (pull-requests)

We can also trigger the workflow/build on demand, right from the [Actions page](actions/workflows/maven.yml).

## SonarCloud

This project is integrated with [SonarCloud](https://sonarcloud.io/project/overview?id=bitcoder_tutorial-spring), and PRs will have information about code quality.

## Jira (using Xray Test Management)

The testing results are pushed to Xray, a test management tool, in Jira; only the integration tests are pushed.

- test automation results will be tracked on a Test Plan issue
- automated test entities will be provisioned on Jira/Xray, if they don't exist yet
- (some) automated test are linked automatically to existing stories, if they're annotated with `@Requirement(<story_issue_key>)`; that requires using the [xray-junit-extensions](https://github.com/Xray-App/xray-junit-extensions) dependency

To push the results to Jira, a maven plugin [xray-maven-plugin](https://github.com/Xray-App/xray-maven-plugin) is used; an alternative to that would be to use the GH action [xray-action](https://github.com/mikepenz/xray-action) instead.

## Contact

If you're having the TQS classes, you can reach out using my email account at sergio dot freire (you know the rest).

Any questions related with this code, please raise issues in this GitHub project. Feel free to contribute and submit PR's
You may also find me on [Twitter](https://twitter.com/darktelecom), where I write once in a while about testing (don't use that for TQS class related topics though).
 

