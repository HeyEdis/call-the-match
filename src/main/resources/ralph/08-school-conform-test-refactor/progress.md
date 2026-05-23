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

### competition-controller-tests-school-style - Competition Controller Tests

What changed:

- Converted `CompetitionControllerTests` from a full `@SpringBootTest` test to a focused `@WebMvcTest(CompetitionController.class)` slice.
- Imported the project `SecurityConfig`, Boot 4 servlet security auto-configuration, and `CompetitionValidatorAdvice` so admin/user access and the DTO validator binder remain active in the slice.
- Mocked `CompetitionService`, `CountryService`, `StadiumService`, and `CompetitionValidator`.
- Preserved public competition list/detail coverage with status, view, model, and service verification.
- Preserved USER-forbidden access to the admin add route.
- Covered admin add, edit, and result GET forms with expected model attributes and the stadium checksum UI assertions.
- Added valid admin add, edit, and result POST coverage with redirect assertions and service verification.
- Kept invalid admin add, edit, and result POST coverage with field-error assertions and `verify(..., never())` checks for write services.
- Kept not-found and type-mismatch error-page coverage.

Decisions:

- Used local DTO/model fixtures instead of database seed data.
- Configured the mocked `CompetitionValidator` to support `InputCompetitionDTO`, matching Spring's validator contract in the `@InitBinder` slice.
- Kept security filters enabled in this MVC slice because the task explicitly requires USER-forbidden admin route coverage.

Files changed:

- `src/test/java/com/example/callthematch/controller/CompetitionControllerTests.java`
- `src/main/resources/ralph/08-school-conform-test-refactor/progress.md`
- `src/main/resources/plan/08-school-conform-test-refactor/plan.json`

Verification:

- `.\mvnw.cmd "-Dtest=CompetitionControllerTests" test` passed: 6 tests, 0 failures, 0 errors.

### team-controller-tests-school-style - Team Controller Tests

What changed:

- Converted `TeamControllerTests` from full `@SpringBootTest` coverage to a focused `@WebMvcTest(TeamController.class)` slice.
- Mocked `TeamService` and verified service calls for dashboard, create, join, detail, scoreboard, invite-code regeneration, and member removal flows.
- Kept security filters enabled with the project `SecurityConfig` and Spring Boot 4 servlet security auto-configuration.
- Covered dashboard model attributes for the user's teams and form DTOs.
- Covered valid create/join redirects and invalid create/join form errors with `verify(..., never())` on validation failures.
- Added duplicate team-name and unknown invite-code field-error coverage.
- Preserved member/non-member detail and scoreboard access behavior through mocked service success and `AccessDeniedException` paths.
- Preserved owner and non-owner management behavior for invite-code regeneration and member removal.
- Preserved guest redirect and admin-forbidden team route checks.

Decisions:

- Used local fixture helpers that build real `TeamDTO`, `TeamScoreboardDTO`, `TeamMember`, `Team`, and `MyUser` objects for Thymeleaf rendering.
- Added a mocked `CompetitionValidator` because the MVC slice loads the global `CompetitionValidatorAdvice`; this keeps unrelated validator dependencies out of the team controller test.

Files changed:

- `src/test/java/com/example/callthematch/controller/TeamControllerTests.java`
- `src/main/resources/ralph/08-school-conform-test-refactor/progress.md`
- `src/main/resources/plan/08-school-conform-test-refactor/plan.json`

Verification:

- `.\mvnw.cmd "-Dtest=TeamControllerTests" test` passed: 13 tests, 0 failures, 0 errors.

### prediction-controller-tests-school-style - Prediction Controller Tests

What changed:

- Converted `PredictionControllerTests` from full `@SpringBootTest` coverage with repository setup to a focused `@WebMvcTest(PredictionController.class)` slice.
- Removed direct `CompetitionRepository`, `PredictionRepository`, and `UserRepository` usage from the controller test.
- Mocked `PredictionService` and verified list, form, valid save, invalid save, and cutoff save interactions.
- Kept guest redirect and admin-forbidden prediction route coverage with security filters enabled.
- Moved reusable prediction DTO/overview fixtures into the `com.example.callthematch.support` test package.
- Reused shared competition fixture data from the `support` package for prediction form rendering.

Decisions:

- Non-member private scoreboard behavior remains covered by `TeamControllerTests`, which is the more appropriate controller slice for `/team/{id}/scoreboard`.
- Added a mocked `CompetitionValidator` because the MVC slice loads the global `CompetitionValidatorAdvice`; this keeps unrelated competition validation dependencies out of the prediction controller test.
- Kept `.param(...)` for prediction POSTs because these tests exercise request parameter binding and validation errors on the form DTO.

Files changed:

- `src/test/java/com/example/callthematch/controller/PredictionControllerTests.java`
- `src/test/java/com/example/callthematch/support/TestPredictions.java`
- `src/main/resources/ralph/08-school-conform-test-refactor/progress.md`
- `src/main/resources/ralph/08-school-conform-test-refactor/TODO.md`
- `src/main/resources/plan/08-school-conform-test-refactor/plan.json`

Verification:

- `.\mvnw.cmd "-Dtest=PredictionControllerTests" test` passed: 6 tests, 0 failures, 0 errors.
- `.\mvnw.cmd "-Dtest=CompetitionControllerTests,PublicBrowseControllerTests,TeamControllerTests,PredictionControllerTests" test` passed: 28 tests, 0 failures, 0 errors.

Review cleanup:

- Moved repeated competition, team, and prediction controller-test fixtures into `src/test/java/com/example/callthematch/support`.
- Updated the affected controller tests to use the shared support fixtures instead of duplicating local object creation.
- Removed completed competition, team, and prediction controller items from `TODO.md`; remaining Ralph TODOs now focus on security cleanup and final verification.
