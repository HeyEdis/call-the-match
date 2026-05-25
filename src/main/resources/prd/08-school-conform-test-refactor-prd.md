# PRD: School-Conform Test Refactor

## Problem Statement

The current `call-the-match` test suite has broad functional coverage and passes locally, but the tests are not consistently written in the same style as the HOGENT EWD exercise projects in `WorkspacesIntelij`. The biggest gap is not missing behavior, but school-conform structure: MVC controller tests are mostly full `@SpringBootTest` integration tests, while the school examples prefer focused `@WebMvcTest` slices with mocked services and explicit service interaction checks.

This PRD defines a corrective test-refactor phase. The goal is to make the final test suite easy to defend: each required test category must clearly match the school examples for MVC controllers, REST controllers, security, validation annotations, custom annotations, and validator classes.

Safe passing first: keep existing verified behavior, but reshape tests toward the concrete course patterns.

## Solution

Refactor the existing test suite category by category so every required school test category is both present and written in the expected school style.

The refactor should preserve green tests while improving the test shape:

1. MVC controller tests should become focused `@WebMvcTest` slices where practical, with `@MockitoBean` services, `when(...)` setup, `verify(...)` calls, and `verify(..., never())` on invalid paths.
2. Security tests should remain full-context where needed, but should use the same readable role patterns as the security exercises: `@WithMockUser`, `@WithAnonymousUser`, form-login helpers, logout helpers, and small focused test methods.
3. REST tests should keep the existing `@WebMvcTest` + `jsonPath` + `Mockito.verify` shape, and either add mutation tests when REST mutation endpoints exist or explicitly keep the PRD scope to GET-only public REST endpoints.
4. Validation tests should stay Spring-context-free for DTO annotations, use `jakarta.validation.Validator`, prefer parameterized cases where useful, and keep validator class tests with `BeanPropertyBindingResult`.
5. Service tests may remain as extra coverage, but must not be presented as substitutes for the required school categories.

## Current Codebase State

The project already contains all required test folders:

- `src/test/java/com/example/callthematch/controller`
- `src/test/java/com/example/callthematch/security`
- `src/test/java/com/example/callthematch/restcontroller`
- `src/test/java/com/example/callthematch/validation`
- `src/test/java/com/example/callthematch/service`

The current suite includes:

- MVC tests for account, competition, team, prediction, and public browse flows.
- Security tests for guest, user, admin, form login, logout, and CSRF.
- REST tests for public match lookup and stadium capacity endpoints.
- DTO annotation validation tests.
- A custom annotation validator test for stadium checksum.
- A Spring `Validator` class test for competition validation.
- Extra service tests for team, prediction, scoring, team member, and user logic.

The current test suite passes locally after the latest validation audit:

- targeted validation slice: 25 tests, 0 failures;
- full suite: 87 tests, 0 failures.

Known dirty worktree at PRD creation time:

- `src/test/java/com/example/callthematch/validation/InputCompetitionDTOValidationTests.java`
- `src/main/resources/plan/06-testing/plan.json`
- `src/main/resources/ralph/06-testing/progress.md`

These changes belong to the previous validation audit and should not be reverted by this refactor.

### Audit Findings To Address

#### MVC Controller Tests

Current MVC tests use `@SpringBootTest` + `@AutoConfigureMockMvc`:

- `AccountControllerTests`
- `CompetitionControllerTests`
- `TeamControllerTests`
- `PredictionControllerTests`
- `PublicBrowseControllerTests`

This provides broad integration coverage, but does not match the primary Multirow school example, which uses:

- `@WebMvcTest(ContactController.class)`;
- `@MockitoBean` for the service;
- `when(...)` to define service results;
- `verify(service)` for expected calls;
- `verify(service, never())` for invalid submissions;
- `flashAttr(...)` for form DTOs;
- assertions on status, view, model attributes, redirects, and field errors.

Extra concern: `PredictionControllerTests` injects repositories directly to prepare test data. That makes the test more of an integration test and less school-style controller testing.

#### Security Tests

`AccessSecurityTests` uses `@SpringBootTest` + `@AutoConfigureMockMvc`, which is acceptable for full security wiring and appears in the JDBC/JPA security examples. However, the exercise projects often use:

- `@WithMockUser`;
- `@WithAnonymousUser`;
- `formLogin(...)`;
- `logout(...)`;
- short, focused tests per role/access case.

The current security tests are functionally strong but group many route checks in larger methods and mostly use `.with(user(...).roles(...))` instead of annotation-based users.

#### REST Tests

`CompetitionRestControllerTests` and `StadiumRestControllerTests` already closely match the REST exercise style:

- `@WebMvcTest`;
- `@MockitoBean`;
- `MockMvc`;
- `jsonPath`;
- `Mockito.when`;
- `Mockito.verify`;
- status and JSON-body assertions;
- error JSON with `status`, `message`, and `timestamp`.

The remaining scope issue is that the broad testing plan mentions REST mutation tests (`POST`, `PUT`, `DELETE`, valid JSON, invalid JSON), while the current REST implementation exposes only public GET endpoints. The refactor must choose one school-conform path:

- either keep REST testing scoped to implemented GET endpoints and adjust plan expectations;
- or add REST mutation endpoints later and then write the matching REST mutation tests.

For this PRD, implementation of new REST mutation endpoints is out of scope unless the user explicitly chooses that path.

#### Validation Tests

The validation tests are the closest to the school examples:

- DTO validation uses `Validation.buildDefaultValidatorFactory().getValidator()`.
- Positive and negative cases exist.
- `CompetitionValidatorTests` uses `BeanPropertyBindingResult`.
- `StadiumChecksumValidatorTests` directly instantiates the custom validator and tests valid, invalid, and null-safe cases.

Minor cleanup remains:

- reduce duplicate `InputTeamJoinDTO` coverage between `InputTeamDTOValidationTests` and `InputTeamJoinDTOValidationTests`;
- consider parameterized tests for repeated invalid input cases;
- optionally introduce small test builder helpers where they improve readability, following `BuilderDTO` from the validation and multirow examples.

## School Requirements

This PRD supports the final required EWD test categories:

- MVC controller tests with MockMvc.
- REST controller tests with MockMvc and JSON assertions.
- Security tests for guest, user, admin, form login, logout, and CSRF.
- Validation tests for existing Jakarta annotations.
- Tests for at least one custom annotation and its `ConstraintValidator`.
- Tests for validator classes using Spring `Validator` style and `BeanPropertyBindingResult`.

Course-style expectations:

- MVC controller tests should be focused and service-mocked where practical.
- REST controller tests should be focused and service-mocked.
- Security tests may use full context, but must clearly express role access.
- DTO validation tests should not load a Spring context.
- Service tests are useful extra coverage but are not a replacement for the required categories.

## Role And Access Decisions

The refactor must preserve and test the current project access rules:

- **Guest**: may access public home, public ranking, public competition detail, login, register, static resources, error pages, and public REST GET endpoints.
- **User**: may access team dashboard, team detail for allowed teams, private team scoreboards, and prediction flows.
- **Admin**: may access match management and official result flows only.
- **Forbidden**: admin must not join teams, manage user teams, view private scoreboards, or submit predictions; user must not access admin match management; guest must be redirected to login for protected pages.

Login uses email. Registered accounts receive role `USER` by default.

## User Stories

1. As a developer, I want MVC controller tests to follow the Multirow `@WebMvcTest` pattern, so that the controller layer can be defended in the same style as the school exercises.
2. As a developer, I want controller tests to mock services and verify service interactions, so that controller responsibilities are tested separately from persistence and seed data.
3. As a developer, I want security tests to use clear guest, user, admin patterns from the security exercises, so that access decisions are easy to inspect during evaluation.
4. As a developer, I want REST controller tests to match the REST examples with `jsonPath` and mocked services, so that REST behavior and error JSON are school-conform.
5. As a developer, I want the REST test scope to match the implemented REST API, so that the plan does not claim mutation coverage when only GET endpoints exist.
6. As a developer, I want DTO validation tests to remain Spring-context-free, so that annotation validation is tested in isolation.
7. As a developer, I want validator class tests to use `BeanPropertyBindingResult`, so that custom validation logic follows the validation exercise pattern.
8. As a student, I want the final test suite to be easy to explain, so that the defence can point to one clear school example per test category.

## Implementation Decisions

### MVC Controller Test Refactor

Refactor MVC tests toward the Multirow controller pattern:

- Use `@WebMvcTest({Controller}.class)` where practical.
- Mock controller dependencies with `@MockitoBean`.
- Use DTO fixtures or builder helpers for form submissions.
- Use `flashAttr(...)` for form DTOs when matching the school examples and when it reflects the controller method signature.
- Keep `.param(...)` where CSRF/security/form encoding behavior is the point of the test.
- Assert status, view name, model attributes, redirects, and field errors.
- Verify expected service calls on successful paths.
- Verify services are not called on invalid validation paths.
- Avoid direct repository access in controller tests.

Target files:

- `AccountControllerTests`
- `CompetitionControllerTests`
- `TeamControllerTests`
- `PredictionControllerTests`
- `PublicBrowseControllerTests`

Special target:

- Remove direct repository setup from `PredictionControllerTests`; replace it with mocked service responses or move that behavior to a separate integration test if needed.

### Security Test Refactor

Refactor `AccessSecurityTests` for readability and stronger resemblance to the security exercises:

- Keep `@SpringBootTest` + `@AutoConfigureMockMvc` if full security wiring and real login are needed.
- Use `@WithAnonymousUser` for anonymous access cases where suitable.
- Use `@WithMockUser(username = "...", roles = "...")` for role access cases where suitable.
- Keep `formLogin("/login").userParameter("email")` for real login behavior.
- Keep `logout("/logout")` for logout behavior.
- Keep CSRF tests with a missing CSRF token.
- Split large role/access test methods into smaller tests when it improves readability and traceability.

Required coverage to preserve:

- Guest public routes.
- Guest redirect to login for protected user/admin routes.
- USER allowed on team and prediction routes.
- USER forbidden on admin routes.
- ADMIN allowed on match management routes.
- ADMIN forbidden on team and prediction routes.
- Correct email/password login authenticates.
- Incorrect credentials redirect to `/login?error`.
- Logout redirects to `/login?logout`.
- POST without CSRF returns 403.

### REST Test Scope Decision

Keep existing REST GET tests and make the scope explicit:

- `GET /api/matches?date=...` success with JSON array.
- `GET /api/matches?date=...` empty array.
- invalid date returns 400 error JSON.
- `GET /api/stadiums/{id}/capacity` success with JSON object.
- missing stadium returns 404 error JSON.

If no REST mutation endpoints exist, do not invent mutation tests. Instead, update the testing plan so REST mutation criteria are not marked as required for this GET-only REST block.

If REST mutation endpoints are later added, add tests following `EmployeeRestMockTest`:

- POST with valid JSON returns 200 or 201.
- POST with duplicate/invalid data returns an error JSON.
- DELETE success returns JSON.
- DELETE missing resource returns 404 error JSON.

### Validation Test Refactor

Keep validation tests close to `Spring_validatie`:

- DTO annotation tests use `jakarta.validation.Validator` from `Validation.buildDefaultValidatorFactory()`.
- Include both valid and invalid cases.
- Prefer `@ParameterizedTest` for repeated invalid inputs.
- Keep custom Spring `Validator` tests with `BeanPropertyBindingResult`.
- Keep custom `ConstraintValidator` tests direct and Spring-context-free.
- Remove duplicate `InputTeamJoinDTO` validation from `InputTeamDTOValidationTests`, because `InputTeamJoinDTOValidationTests` already exists.
- Consider small builder helpers only where they reduce noise.

### Plan And Ralph Updates

After refactoring tests, update the relevant plan/progress artifacts so they reflect actual school-conform status:

- update or create a plan for this PRD;
- do not claim REST mutation coverage unless those endpoints and tests exist;
- only set task `passes` to true after targeted tests and full `mvn test` pass.

## Testing Decisions

This PRD is itself a test-quality refactor. Verification must be done in layers:

1. Run targeted MVC controller tests after each controller refactor.
2. Run targeted security tests after security refactor.
3. Run targeted REST controller tests after REST scope cleanup.
4. Run targeted validation tests after validation cleanup.
5. Run full verification with `.\mvnw.cmd test`.

Expected commands:

- `.\mvnw.cmd -Dtest=AccountControllerTests test`
- `.\mvnw.cmd -Dtest=CompetitionControllerTests test`
- `.\mvnw.cmd -Dtest=TeamControllerTests test`
- `.\mvnw.cmd -Dtest=PredictionControllerTests test`
- `.\mvnw.cmd -Dtest=PublicBrowseControllerTests test`
- `.\mvnw.cmd -Dtest=AccessSecurityTests test`
- `.\mvnw.cmd -Dtest=CompetitionRestControllerTests,StadiumRestControllerTests test`
- `.\mvnw.cmd -Dtest=*ValidationTests,CompetitionValidatorTests,StadiumChecksumValidatorTests test`
- `.\mvnw.cmd test`

The refactor is successful when:

- all current behavioral coverage is preserved or intentionally moved to a more suitable test category;
- MVC tests visibly match the Multirow controller testing style;
- security tests visibly match the Security exercise style;
- REST tests visibly match the REST exercise style;
- validation tests visibly match the Spring validation exercise style;
- full test suite passes.

## REST And WebClient Decisions

REST is relevant only for the existing public REST GET endpoints in this refactor. WebClient is out of scope.

The REST/WebClient feature block remains separate. This PRD must not introduce WebClient tests or new REST mutation endpoints unless the user explicitly expands the scope.

## Out Of Scope

- Adding new application features.
- Adding new REST mutation endpoints just to satisfy broad test examples.
- Changing production behavior unless a test refactor exposes a genuine bug.
- Rewriting service tests unless they block school-conform controller/security/REST/validation coverage.
- End-to-end browser tests.
- Performance tests.
- Repository-layer tests.
- Reverting existing user or previous-agent work.

## Sources

1. Existing `call-the-match` codebase, especially `src/test/java/com/example/callthematch`.
2. Existing testing PRD: `src/main/resources/prd/06-testing-prd.md`.
3. Existing testing plan: `src/main/resources/prd/06-testing/plan.md`.
4. Project guidelines: `.agents/skills/project-guidelines/SKILL.md`.
5. Testing guideline reference: `.agents/skills/project-guidelines/references/testing.md`.
6. Multirow controller example: `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Multirow\Spring_Boot_list_crud-opl`.
7. Security examples: `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Security`.
8. REST examples: `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_REST`.
9. Validation examples: `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Spring\Spring_validatie`.
10. Git repository: `https://github.com/HeyEdis/call-the-match.git`.
11. User decisions and audit findings from the current conversation.

## Further Notes

- The highest-risk refactor is converting MVC tests to `@WebMvcTest`, because controllers may depend on security, advice, formatters, validators, or model setup that must be imported or mocked explicitly.
- Convert one controller test class at a time and run targeted tests after every conversion.
- Preserve current integration value only where it proves something the slice tests cannot. If needed, keep one or two clearly named integration tests, but do not let them replace school-style MVC controller tests.
- Keep the final suite boring and explainable: for each required test category, it should be obvious which exercise project inspired the test shape.
