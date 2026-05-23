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
