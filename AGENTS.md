# Spring Boot Tutorial Project - Developer Guide

## Project Overview

- **Purpose**: Spring Boot tutorial showcasing CI/CD flow with automated testing and Jira/Xray integration
- **Language**: Java 17 (compatible with Java 21)
- **Framework**: Spring Boot 3.5.7
- **Build Tool**: Maven 3.8+
- **Database**: H2 (development/testing), PostgreSQL (production)
- **Testing**: JUnit 5, Mockito, Spring Boot Test, Testcontainers
- **Code Quality**: SonarCloud integration
- **Test Management**: Xray for Jira Cloud
- **Deployment**: Render.com (Docker-based)

## Maven Commands

Always use `mvn` command instead of `mvnw` (the Maven wrapper) when running Maven commands.

### Common Commands

- **Build and run unit tests**: `mvn clean package`
- **Run unit tests only**: `mvn test`
- **Run integration tests**: `mvn integration-test`
- **Run integration tests with verification**: `mvn integration-test verify`
- **Full build with all tests**: `mvn clean verify`
- **Generate coverage reports**: `mvn clean verify` (JaCoCo reports in `target/site/jacoco-*`)

## Project Structure

### Main Source Code

- **`src/main/java/com/sergiofreire/xray/tutorials/springboot/`**
  - `SpringbootApplication.java` - Main Spring Boot application entry point
  - `boundary/` - REST controllers and DTOs (presentation layer)
    - `UserRestController.java` - REST API endpoints for user management
    - `GreetingController.java` - Greeting page controller
    - `IndexController.java` - Index page controller
    - `ResourceNotFoundException.java` - Custom exception handler
  - `data/` - JPA entities and repositories (data layer)
    - `User.java` - User entity with validation
    - `UserRepository.java` - Spring Data JPA repository
  - `services/` - Business logic layer
    - `UserService.java` - User service interface
    - `UserServiceImpl.java` - User service implementation

### Test Code

- **`src/test/java/com/sergiofreire/xray/tutorials/springboot/`**
  - Unit tests: `*Test.java` (run by Surefire plugin)
    - `UserRepositoryTest.java` - Repository layer tests
    - `UserServiceUnitTest.java` - Service layer unit tests with mocks
  - Integration tests: `*IT.java` (run by Failsafe plugin)
    - `UserRestControllerIT.java` - REST API integration tests
    - `GreetingControllerMockedIT.java` - Controller integration tests
    - `IndexControllerIT.java` - Index page integration tests
    - `IndexControllerMockedIT.java` - Mocked controller tests

### Configuration Files

- `pom.xml` - Maven build configuration with plugins for testing, coverage, and Xray integration
- `src/main/resources/application.properties` - Spring Boot configuration
- `src/test/resources/xray-junit-extensions.properties` - Xray JUnit extensions config
- `Dockerfile` - Multi-stage Docker build for deployment

### Additional Directories

- `.github/workflows/` - GitHub Actions CI/CD pipelines
- `.github/agents/` - Agent configuration files for Xray automation
- `.github/prompts/` - Prompt templates for common tasks
- `.agents/skills/` - Custom agent skills for test management and Xray operations
- `bin/` - Utility scripts (e.g., `coverage.sh` for coverage analysis)

## Testing Best Practices

### Test Naming Conventions

- Unit tests: `<ClassName>Test.java` (e.g., `UserServiceUnitTest.java`)
- Integration tests: `<ClassName>IT.java` (e.g., `UserRestControllerIT.java`)

### Test Organization

- Unit tests are located in `src/test/java` and mirror the main source structure
- Integration tests are also in `src/test/java` but use the `IT` suffix
- Test resources (properties, configuration) in `src/test/resources`

### Running Tests

- Unit tests run automatically during `mvn package` or `mvn test`
- Integration tests run during `mvn integration-test` or `mvn verify`
- Tests run in CI on Java 17 and Java 21 (matrix build)

### Test Coverage

- JaCoCo configured for unit and integration test coverage
- Separate coverage files: `jacoco-unit-tests.exec` and `jacoco-it-tests.exec`
- Merged coverage report: `target/site/jacoco-merged-test-coverage-report/`
- Coverage thresholds: 80% minimum for overall and changed files (enforced in PRs)

### Xray Integration

- Integration tests are automatically pushed to Xray on Jira Cloud
- Tests can be linked to requirements using `@Requirement(<issue-key>)` annotation
- Requires `xray-junit-extensions` dependency
- Test results tracked in Test Plan (configured via `XRAYCLOUD_TEST_PLAN_KEY` variable)
- Results include test environment information (e.g., `java17`, `java21`)

## Code Quality and Security

### SonarCloud Integration

- Project: `bitcoder_tutorial-spring`
- Quality gate checks on all PRs
- View results: https://sonarcloud.io/project/overview?id=bitcoder_tutorial-spring

### Known Issues

**CRITICAL - Mass Assignment Vulnerability** (Tracked in ST-328)
- **Location**: `UserRestController.java` line 25
- **Issue**: `createUser()` method accepts JPA entity directly as `@RequestBody`
- **Risk**: Attackers can inject arbitrary fields including `id`
- **Fix**: Implement DTO pattern - see Jira ticket ST-328 for detailed implementation plan
- **Effort**: ~15 minutes

## CI/CD Pipeline

### Continuous Integration

- **Tool**: GitHub Actions
- **Workflow**: `.github/workflows/maven.yml`
- **Triggers**:
  - Push to `main` branch
  - Pull requests to `main`
  - Manual workflow dispatch
- **Matrix**: Builds on Java 17 and Java 21
- **Steps**:
  1. Checkout code
  2. Set up JDK
  3. Build and run unit tests
  4. Run integration tests
  5. Report coverage to PR
  6. Push results to Xray (always runs, even on failure)

### Continuous Deployment

- **Production**: Deploys on commits to `main` branch
  - URL: https://tutorial-spring.onrender.com
- **Staging**: Deploys on pull requests (example on `lh_tests_on_staging` branch)
  - URL: https://tutorial-spring-staging.onrender.com
- **Platform**: Render.com
- **Method**: Docker-based deployment using `Dockerfile`

## Development Workflow

### Making Changes

1. Create a feature branch from `main`
2. Make your changes
3. Run tests locally: `mvn clean verify`
4. Commit and push to GitHub
5. Create a pull request
6. CI runs automatically and reports:
   - Build status
   - Test results
   - Code coverage
   - SonarCloud quality gate status
7. Review and merge when all checks pass

### Environment Configuration

- **Development**: Uses H2 in-memory database (automatically configured)
- **Production**: Uses PostgreSQL (configured via environment variables on Render)
- **Database**: Spring Boot auto-configures based on classpath dependencies

## Coding Standards and Conventions

### Entity Best Practices

- Use JPA validation annotations (`@NotBlank`, `@Size`, etc.)
- Implement proper `equals()` and `hashCode()` methods
- Use generated values for IDs (`@GeneratedValue`)

### REST API Best Practices

- **IMPORTANT**: Never accept JPA entities directly as `@RequestBody` parameters
- Use DTOs (Data Transfer Objects) to expose only safe fields to clients
- Return appropriate HTTP status codes (200, 201, 404, etc.)
- Handle exceptions with custom exception handlers
- Use proper REST naming conventions (`/api/users`, `/api/users/{id}`)

### Service Layer

- Define interfaces for services
- Implement business logic in service classes
- Use constructor injection (not field injection)
- Keep controllers thin - delegate to services

### Testing

- Write unit tests for services with mocked dependencies
- Write integration tests for controllers and repositories
- Use meaningful test method names
- Use `@SpringBootTest` for integration tests
- Use `@WebMvcTest` for controller-only tests
- Link tests to Jira requirements with `@Requirement` annotation when applicable

## Dependencies

### Key Dependencies

- **Spring Boot Starters**: web, data-jpa, validation, thymeleaf, test
- **Database**: H2 (test), PostgreSQL (production)
- **Testing**: JUnit Jupiter 5.14.1, Mockito, Testcontainers
- **Xray**: xray-junit-extensions 0.9.0, xray-maven-plugin 0.9.0
- **Coverage**: JaCoCo 0.8.14
- **Validation**: Hibernate Validator (via spring-boot-starter-validation)

## Useful Scripts

### Coverage Analysis

- Script: `bin/coverage.sh`
- Purpose: Analyze test coverage and check against thresholds
- Can be integrated with Xray to track coverage metrics

## Jira Integration

### Xray Test Management

- **Project**: ST (https://sergiofreire.atlassian.net/browse/ST)
- **Test Results**: Automatically pushed from CI pipeline
- **Configuration**: Requires `XRAYCLOUD_CLIENT_ID` and `XRAYCLOUD_CLIENT_SECRET` secrets
- **Test Plan**: Configured via `XRAYCLOUD_TEST_PLAN_KEY` repository variable
- **Features**:
  - Automatic test provisioning in Jira
  - Linking tests to requirements via annotations
  - Test execution tracking with environment info
  - Coverage reporting

### Available Agent Skills

Located in `.agents/skills/`:
- `java-test-runner` - Run Java tests using Maven Surefire and Failsafe
- `xray-automated-tests-ids` - Extract test IDs from JUnit XML reports
- `xray-data-extraction` - Extract testing data from Xray
- `xray-entities-representation` - Work with Xray entities (test runs, statuses, coverage)
- `xray-folders-management` - Manage folders in Xray Test Repository
- `xray-testing-progress` - Show requirement coverage and testing progress

## Troubleshooting

### Maven Wrapper Issues

- If `./mvnw` fails, use `mvn` directly (already installed)
- Maven wrapper files may not be present in `.mvn/wrapper/`

### Test Failures

- Check test logs in console output
- Review stack traces for root cause
- Verify database state if using persistent storage
- Check for environment-specific issues (Java version, OS)

### Coverage Reports

- Reports generated in `target/site/jacoco-*` directories
- Open `index.html` in browser to view detailed coverage
- Merged report combines unit and integration test coverage

### Docker Build

- Multi-stage build: first stage builds with Maven, second stage runs with JRE
- Base images: `maven:3.8.4-openjdk-17-slim` (build), `ibm-semeru-runtimes:open-17-jre-noble` (runtime)
- JAR name: `springboot-0.0.1-SNAPSHOT.jar`

## Contact and Support

- **Issues**: Raise issues in GitHub repository
- **Pull Requests**: Contributions welcome via PR
- **Jira Project**: https://sergiofreire.atlassian.net/browse/ST
