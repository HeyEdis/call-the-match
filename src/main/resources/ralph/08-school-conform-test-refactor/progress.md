# Ralph Progress Log: 08-school-conform-test-refactor

Each iteration appends what was done, decisions made, files changed, verification results, and blockers.

## Entries

### mvc-test-baseline-slice-setup - MVC Test Baseline And Shared Slice Setup

Target MVC test shape:

- Use `@WebMvcTest({Controller}.class)` for school-style controller slices where practical.
- Mock controller dependencies with `@MockitoBean`.
- Arrange service results with `when(...)` or `doNothing(...)`.
- Assert status, view name, model attributes, redirects, and field errors with MockMvc matchers.
- Verify successful service calls with `verify(...)`.
- Verify invalid form paths do not call write services with `verify(..., never())`.
- Keep repository access out of controller tests.
- Import or mock MVC-only collaborators such as validators, advice, current-user helpers, and formatters only when the slice requires them.

Form submission decision:

- Prefer `flashAttr(...)` for DTO-backed form submissions because it matches the Multirow `ContactControllerTest` pattern and binds directly to controller method DTO arguments.
- Use `.param(...)` when the test is specifically about HTTP form encoding, CSRF/security behavior, missing request data, or type conversion from raw request parameters.

Integration coverage decision:

- Full `@SpringBootTest` MVC coverage may remain only when it proves behavior that a slice cannot reasonably prove, such as full security wiring, real authentication/login, or cross-layer integration.
- Retained integration tests must be clearly named or justified and must not replace the required `@WebMvcTest` controller slices.

Files changed:

- `src/main/resources/ralph/08-school-conform-test-refactor/progress.md`
- `src/main/resources/plan/08-school-conform-test-refactor/plan.json`

Verification:

- No application or test behavior changed; this baseline task records the school-style MVC test decisions only.

### account-public-browse-controller-tests - Account And Public Browse Controller Tests

What changed:

- Converted `AccountControllerTests` from full `@SpringBootTest` coverage to a focused `@WebMvcTest(AccountController.class)` slice.
- Mocked `UserService` and verified successful registration calls.
- Used `flashAttr(...)` for DTO-backed valid and invalid registration form submissions.
- Verified invalid registration does not call `userService.register(...)`.
- Kept missing-CSRF registration coverage by importing the Spring Boot 4 servlet security auto-configuration and project `SecurityConfig` into the account slice.
- Converted `PublicBrowseControllerTests` to a focused `@WebMvcTest` slice for `HomeController`, `RankingController`, and `CompetitionController`.
- Mocked `CompetitionService` and `TeamService`, asserted view/model behavior, and verified service calls.
- Added nested DTO fixture data needed for Thymeleaf rendering in the sliced public match views.

Decisions:

- Used Spring Boot 4 test imports: `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest` and `AutoConfigureMockMvc`.
- Mocked `CompetitionValidator` where controller advice is loaded by the MVC slice.
- Kept the public browse not-found and type-mismatch assertions in the controller slice with `GlobalExceptionAdvice`.

Files changed:

- `src/test/java/com/example/callthematch/controller/AccountControllerTests.java`
- `src/test/java/com/example/callthematch/controller/PublicBrowseControllerTests.java`
- `src/main/resources/ralph/08-school-conform-test-refactor/progress.md`
- `src/main/resources/plan/08-school-conform-test-refactor/plan.json`

Verification:

- `.\mvnw.cmd "-Dtest=AccountControllerTests,PublicBrowseControllerTests" test` passed: 8 tests, 0 failures, 0 errors.

### rest-get-test-scope-verification - REST GET Test Scope Verification

What changed:

- No test code changes were needed for the REST GET scope.
- Verified the existing REST tests already use the school-style `@WebMvcTest` shape with mocked services, `jsonPath`, and service/date formatter verification.
- Confirmed this feature plan and PRD scope REST testing to implemented public GET endpoints only and do not require POST/PUT/DELETE mutation coverage.

Files changed:

- `src/main/resources/ralph/08-school-conform-test-refactor/progress.md`
- `src/main/resources/plan/08-school-conform-test-refactor/plan.json`

Verification:

- `.\mvnw.cmd "-Dtest=CompetitionRestControllerTests,StadiumRestControllerTests" test` passed: 5 tests, 0 failures, 0 errors.

### validation-test-cleanup - Validation Test Cleanup

What changed:

- Removed duplicate `InputTeamJoinDTO` validation coverage from `InputTeamDTOValidationTests`.
- Kept `InputTeamJoinDTOValidationTests` as the dedicated owner for blank and valid invite-code cases.
- Left the existing Spring-context-free Jakarta Validator DTO tests, direct `StadiumChecksumValidatorTests`, and `BeanPropertyBindingResult`-based `CompetitionValidatorTests` intact.

Files changed:

- `src/test/java/com/example/callthematch/validation/InputTeamDTOValidationTests.java`
- `src/main/resources/ralph/08-school-conform-test-refactor/progress.md`
- `src/main/resources/plan/08-school-conform-test-refactor/plan.json`

Verification:

- `.\mvnw.cmd "-Dtest=*ValidationTests,CompetitionValidatorTests,StadiumChecksumValidatorTests" test` passed: 23 tests, 0 failures, 0 errors.

### AFK stop - remaining high-risk refactors skipped

Skipped tasks:

- `competition-controller-tests-school-style`
- `team-controller-tests-school-style`
- `prediction-controller-tests-school-style`
- `security-tests-school-style-cleanup`
- `final-verification-and-plan-alignment`

Reason:

- The remaining controller/security refactors affect admin/user/guest boundaries, current-user flows, ownership checks, and security wiring. In AFK mode these are risky decisions under the Ralph rules, so they were left with `"passes": false` rather than guessed through unattended.
- Final plan alignment also remains blocked until the remaining controller/security test-shape tasks are actually completed.

Verification:

- Full regression check after the safe tasks: `.\mvnw.cmd test` passed: 85 tests, 0 failures, 0 errors.
