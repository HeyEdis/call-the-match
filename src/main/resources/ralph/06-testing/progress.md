# Ralph Progress Log: 06-testing

Each iteration appends what was done, decisions made, files changed, verification results, and blockers.

## Entries

### 2026-05-23 – reorganize-tests-to-packages

**Task:** Reorganisatie – Verplaats Bestaande Tests Naar Juiste Packages

**What changed:**
- Moved `AccessSecurityMvcTests` → `security/AccessSecurityTests`
- Moved `PublicBrowseMvcTests` → `controller/PublicBrowseControllerTests`
- Moved `TeamManagementMvcTests` → `controller/TeamControllerTests`
- Moved `MatchManagementMvcTests` → `controller/CompetitionControllerTests`
- Moved `PredictionMvcTests` → `controller/PredictionControllerTests`
- Moved `dto/InputRegistrationDTOValidationTests` → `validation/InputRegistrationDTOValidationTests`
- Moved `dto/InputTeamDTOValidationTests` → `validation/InputTeamDTOValidationTests`
- Moved `dto/InputCompetitionDTOValidationTests` → `validation/InputCompetitionDTOValidationTests`
- Moved `dto/InputPredictionDTOValidationTests` → `validation/InputPredictionDTOValidationTests`
- Moved `validator/CompetitionValidatorTests` → `validation/CompetitionValidatorTests`
- Deleted old files from root package, `dto/`, and `validator/` subpackages
- `RestControllerTests` left in root package (not in move list; will be addressed in task 7)
- `service/` tests untouched
- `CallTheMatchApplicationTests` remains in root package

**Decisions:** Kept `RestControllerTests` in place since it will be moved to `restcontroller/` package in task 7.

**Verification:** `.\mvnw.cmd test` → BUILD SUCCESS, 66 tests run, 0 failures.

**passes:** true

### 2026-05-23 - AFK audit validation-tests-supplement

**Task:** Validatie Tests - Aanvullingen Bestaande Annotaties

**What changed:**
- Audited the validation tests against `project-guidelines`, the testing PRD/plan, and the school-style validation requirement that each DTO has both negative and positive Jakarta `Validator` coverage without Spring context.
- Added `validFixtureProducesNoViolations` to `InputCompetitionDTOValidationTests` so `InputCompetitionDTO` has an explicit full positive case (`0` violations), instead of only proving the checksum field does not fail.
- Marked `validation-tests-supplement` as passing in `plan.json` after targeted verification.

**Decisions:** Kept the existing `jakarta.validation.Validator` style because it matches the school requirement for DTO annotation validation without Spring context.

**Files changed:**
- `src/test/java/com/example/callthematch/validation/InputCompetitionDTOValidationTests.java`
- `src/main/resources/plan/06-testing/plan.json`
- `src/main/resources/ralph/06-testing/progress.md`

**Verification:** `.\mvnw.cmd -Dtest="InputRegistrationDTOValidationTests,InputTeamDTOValidationTests,InputTeamJoinDTOValidationTests,InputCompetitionDTOValidationTests,InputPredictionDTOValidationTests,CompetitionValidatorTests,StadiumChecksumValidatorTests" test` -> BUILD SUCCESS, 25 tests, 0 failures. `.\mvnw.cmd test` -> BUILD SUCCESS, 87 tests, 0 failures.

**passes:** true

### 2026-05-23 – account-controller-tests

**Task:** MVC Controller Tests – AccountController (nieuw)

**What changed:**
- Created `src/test/java/com/example/callthematch/controller/AccountControllerTests.java`

**Tests added:**
- `getRegisterReturnsFormWithModel` – GET /register → 200, view `account/register`, model has `inputRegistrationDto`
- `getLoginReturnsLoginView` – GET /login → 200, view `account/login`
- `postRegisterWithValidDataRedirectsToLogin` – POST /register valid params → 302 redirect to /login
- `postRegisterWithInvalidDataReturnsRegisterViewWithFieldErrors` – POST /register blank/invalid → 200, view `account/register`, field errors
- `postRegisterWithoutCsrfTokenReturnsForbidden` – POST /register no CSRF → 403

**Verification:** `.\mvnw.cmd -Dtest=AccountControllerTests test` → BUILD SUCCESS, 5 tests, 0 failures.

**passes:** true

### 2026-05-23 – mvc-controller-tests-supplement

**Task:** MVC Controller Tests – Aanvullingen

**What changed:**
- `CompetitionControllerTests`: added `publicCompetitionListAndDetailAreAccessibleToGuest` (GET /home list + GET /competition/1 detail) and `userIsForbidenOnAdminCompetitionAddRoute` (USER → 403 on GET /competition/add)
- `TeamControllerTests`: fixed `validCreateRedirectsToDashboard` to use `UUID`-based unique team name to avoid duplicate constraint on repeated test runs
- `PredictionControllerTests`: already covered all required scenarios — no changes needed

**Verification:** `.\mvnw.cmd -Dtest="CompetitionControllerTests,TeamControllerTests,PredictionControllerTests" test` → BUILD SUCCESS, 22 tests, 0 failures.

**passes:** true

### 2026-05-23 – security-tests-extend

**Task:** Security Tests – Uitbreiden

**What changed:**
- `AccessSecurityTests`: added 3 new tests:
  - `incorrectCredentialsRedirectToLoginError` – formLogin wrong password → unauthenticated + redirect /login?error
  - `logoutRedirectsToLoginLogout` – POST /logout → 302 redirect /login?logout
  - `postWithoutCsrfReturnsForbidden` – POST /team/create no CSRF → 403
- Added `logout` and `unauthenticated` imports; added `redirectedUrlPattern` import

**Verification:** `.\mvnw.cmd -Dtest="AccessSecurityTests" test` → BUILD SUCCESS, 10 tests, 0 failures.

**passes:** true

### 2026-05-23 – validation-tests-supplement

**Task:** Validatie Tests – Aanvullingen Bestaande Annotaties

**What changed:**
- `InputRegistrationDTOValidationTests`: added `validRegistrationProducesNoViolations` (positive case: valid input = 0 violations)
- `InputTeamDTOValidationTests`: added `validTeamNameProducesNoViolations` and `validInviteCodeProducesNoViolations` (positive cases for both DTOs)
- Created `InputTeamJoinDTOValidationTests` (new file): covers blank inviteCode (negative) and valid inviteCode (positive)
- `InputCompetitionDTOValidationTests` and `InputPredictionDTOValidationTests`: already had both positive and negative cases — no changes needed

**Verification:** 21 tests (all validation classes), 0 failures.

**passes:** true

### 2026-05-23 – custom-annotation-validator-tests

**Task:** Validatie Tests – Custom Annotatie & Validator Klasse

**What changed:**
- Created `StadiumChecksumValidatorTests` in `validation/` package
- Directly instantiates `StadiumChecksumValidator`, calls `initialize()` with mocked `ValidStadiumChecksum`
- Mocks `ConstraintValidatorContext` with Mockito (incl. builder chain)
- Tests: `validChecksumReturnsTrue` (1001 % 97 = 31 ✓), `invalidChecksumReturnsFalse` (checksum 32 ✗), `nullInputReturnsTrue`, `nullStadiumCodeReturnsTrue`, `nullChecksumReturnsTrue`

**Verification:** 5 tests, 0 failures.

**passes:** true

### 2026-05-23 – rest-controller-tests

**Task:** REST Controller Tests

**What changed:**
- Moved `RestControllerTests` from root package `com.example.callthematch` to `com.example.callthematch.restcontroller`
- Updated package declaration accordingly
- Deleted old file at root

**Verification:** `.\mvnw.cmd test` → BUILD SUCCESS, 86 tests, 0 failures.

**passes:** true
