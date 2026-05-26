# Plan: School-Conform Test Refactor

> Source PRD: `src/main/resources/prd/08-school-conform-test-refactor-prd.md`

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

## Architectural Decisions

Durable decisions that apply across all phases:

- **Routes**: public `/home`, `/ranking`, `/competition/{id}`, `/login`, `/register`, and public REST GET endpoints; user-only `/team/**`, `/predictions/**`; admin-only match management routes under `/competition/add`, `/competition/edit/{id}`, and `/competition/{id}/result`.
- **Schema**: no schema changes are planned. Existing entities and repositories remain intact; tests should mock service boundaries where the school examples do so.
- **Key models**: `InputRegistrationDTO`, `InputTeamDTO`, `InputTeamJoinDTO`, `InputCompetitionDTO`, `InputCompetitionResultDTO`, `InputPredictionDTO`, REST response DTOs, and view response DTOs remain the primary test inputs/outputs.
- **Security**: login uses email; registered users have role `USER`; admin is not a normal user and must not enter team or prediction flows; CSRF and 403 behavior must remain explicit.
- **Validation/i18n**: DTO annotation tests use Jakarta `Validator` without Spring context; Spring validator class tests use `BeanPropertyBindingResult`; user-facing validation messages remain resource-bundle based.
- **REST/WebClient**: this refactor only covers existing public REST GET endpoints. REST mutation endpoints and WebClient tests are out of scope.
- **Testing**: final tests must visibly match the concrete school examples: Multirow for MVC, Security examples for access/login, REST examples for JSON endpoints, and Spring validation examples for DTO and validator tests.

---

## Phase 1: MVC Test Baseline And Shared Slice Setup

**User stories**: 1, 2, 8

### What To Build

Define the school-conform MVC controller test shape before converting individual controllers. Establish reusable patterns for `@WebMvcTest`, `@MockitoBean`, mocked service responses, form DTO submission, expected service calls, invalid-path `never()` checks, imported validators/advice, and retained integration tests if absolutely necessary.

### Acceptance Criteria

- [ ] A short checklist or notes section exists in the plan/progress log describing the target MVC test shape.
- [ ] The target shape explicitly follows `Spring_Boot_list_crud-opl` `ContactControllerTest`: `@WebMvcTest`, `@MockitoBean`, `when(...)`, `verify(...)`, `verify(..., never())`, status/view/model/redirect assertions.
- [ ] Decisions are recorded for when `.param(...)` is acceptable and when `flashAttr(...)` should be preferred.
- [ ] Decisions are recorded for what may remain as full integration coverage, if anything.
- [ ] No application behavior is changed in this phase.

---

## Phase 2: Account And Public Browse Controller Tests

**User stories**: 1, 2, 8

### What To Build

Convert the simplest MVC controller tests first: account registration/login and public home/ranking browsing. Use focused `@WebMvcTest` slices with mocked services and explicit controller assertions while preserving the current behavior for register, login, home, and ranking. Public competition detail coverage belongs with `CompetitionControllerTests`.

### Acceptance Criteria

- [ ] `AccountControllerTests` uses school-style MVC slice testing where practical.
- [ ] Register GET asserts status, view name, and `inputRegistrationDTO` model attribute.
- [ ] Login GET asserts status and view name.
- [ ] Valid registration submits a DTO/form request and verifies the registration service is called.
- [ ] Invalid registration returns the register view, field errors, and verifies the registration service is not called.
- [ ] Missing CSRF on registration remains tested.
- [ ] `HomeControllerTests` uses focused MVC slice testing where practical.
- [ ] `RankingControllerTests` uses focused MVC slice testing where practical.
- [ ] Home, ranking, public competition detail, not-found, and type-mismatch behavior remain covered across the focused controller test classes.
- [ ] Targeted tests pass with `.\mvnw.cmd -Dtest=AccountControllerTests,HomeControllerTests,RankingControllerTests,CompetitionControllerTests test`.

---

## Phase 3: Competition Controller Tests

**User stories**: 1, 2, 8

### What To Build

Refactor competition MVC tests for school-style controller coverage. Keep all current behavior around public match detail, admin add/edit/result forms, validation errors, redirects or error views, and user-forbidden admin routes, but move away from broad context dependence where possible.

### Acceptance Criteria

- [ ] `CompetitionControllerTests` uses `@WebMvcTest` with mocked controller dependencies where practical.
- [ ] Public competition list/detail behavior remains covered with status, view, and model assertions.
- [ ] Admin add, edit, and result GET flows expose the expected model attributes.
- [ ] Valid admin add/edit/result POST flows verify expected service calls and redirects where applicable.
- [ ] Invalid admin add/edit/result POST flows return the correct view, field errors, and verify write services are not called.
- [ ] Not-found and type-mismatch paths remain covered.
- [ ] USER access to admin competition routes remains forbidden.
- [ ] Targeted test passes with `.\mvnw.cmd -Dtest=CompetitionControllerTests test`.

---

## Phase 4: Team Controller Tests

**User stories**: 1, 2, 8

### What To Build

Refactor team MVC tests to use mocked services and focused controller assertions. Preserve dashboard, create, join, detail, scoreboard, owner-only actions, duplicate/invalid flows, and guest/user/admin boundaries.

### Acceptance Criteria

- [ ] `TeamControllerTests` uses `@WebMvcTest` with mocked controller dependencies where practical.
- [ ] Dashboard test asserts status, view, and model attributes for team list and form DTOs.
- [ ] Valid create verifies service call and redirect.
- [ ] Invalid create returns dashboard view, field errors, and verifies create service is not called.
- [ ] Duplicate team-name flow remains covered.
- [ ] Valid join verifies service call and redirect.
- [ ] Invalid/unknown invite-code flow returns dashboard view and field errors or error model as appropriate.
- [ ] Detail and scoreboard tests cover member vs non-member access.
- [ ] Owner-only actions cover allowed owner behavior and forbidden non-owner behavior.
- [ ] Guest and admin restrictions for team routes remain explicit.
- [ ] Targeted test passes with `.\mvnw.cmd -Dtest=TeamControllerTests test`.

---

## Phase 5: Prediction Controller Tests

**User stories**: 1, 2, 8

### What To Build

Refactor prediction MVC tests so they no longer prepare data through repositories. Replace repository setup with mocked service responses and explicit service verification, while preserving list, form, submit, cutoff, admin-forbidden, guest redirect, and non-member access behavior.

### Acceptance Criteria

- [ ] `PredictionControllerTests` uses `@WebMvcTest` with mocked controller dependencies where practical.
- [ ] Direct repository injection and repository setup are removed from the controller test.
- [ ] Prediction list asserts status, view, and model attributes from mocked service data.
- [ ] Prediction form asserts status, view, competition model data, and prefilled input DTO where applicable.
- [ ] Invalid prediction submit returns the form view with field errors and verifies save service is not called.
- [ ] Valid prediction submit verifies save service call and redirect.
- [ ] Closed/cutoff prediction behavior remains covered.
- [ ] Guest and admin access restrictions remain explicit.
- [ ] Non-member private scoreboard access remains covered in the appropriate controller/security test.
- [ ] Targeted test passes with `.\mvnw.cmd -Dtest=PredictionControllerTests test`.

---

## Phase 6: Security Tests School-Style Cleanup

**User stories**: 3, 8

### What To Build

Refactor `AccessSecurityTests` for readability and closer alignment with `EWDJ_Security`. Keep full security wiring if needed, but express access cases with small focused tests, `@WithMockUser`, `@WithAnonymousUser`, `formLogin`, `logout`, and CSRF checks.

### Acceptance Criteria

- [ ] Security tests still cover guest public routes.
- [ ] Security tests still cover guest redirect to login for protected user/admin routes.
- [ ] USER access to team and prediction routes remains covered.
- [ ] USER forbidden access to admin match-management routes remains covered.
- [ ] ADMIN access to match-management routes remains covered.
- [ ] ADMIN forbidden access to team, prediction, and private scoreboard routes remains covered.
- [ ] Correct email/password form login authenticates.
- [ ] Incorrect credentials redirect to `/login?error`.
- [ ] Logout redirects to `/login?logout`.
- [ ] POST without CSRF returns 403.
- [ ] Larger grouped tests are split where it improves traceability.
- [ ] Targeted test passes with `.\mvnw.cmd -Dtest=AccessSecurityTests test`.

---

## Phase 7: REST Test Scope And School-Style Verification

**User stories**: 4, 5, 8

### What To Build

Keep REST tests scoped to the currently implemented public GET endpoints. Verify they remain aligned with `EWDJ_REST`: `@WebMvcTest`, mocked service dependencies, `jsonPath`, `Mockito.when`, `Mockito.verify`, list/detail success cases, and error JSON with `status`, `message`, and `timestamp`.

REST mutation tests are explicitly out of scope because the project currently exposes only GET REST endpoints.

### Acceptance Criteria

- [ ] `CompetitionRestControllerTests` keeps `@WebMvcTest`, mocked dependencies, `jsonPath`, and `Mockito.verify`.
- [ ] `GET /api/matches?date=...` success returns a JSON array with expected fields.
- [ ] `GET /api/matches?date=...` with no matches returns an empty JSON array.
- [ ] Invalid date returns 400 error JSON with `status`, `message`, and `timestamp`.
- [ ] `StadiumRestControllerTests` keeps `@WebMvcTest`, mocked dependencies, `jsonPath`, and `Mockito.verify`.
- [ ] `GET /api/stadiums/{id}/capacity` success returns expected JSON.
- [ ] Missing stadium returns 404 error JSON with `status`, `message`, and `timestamp`.
- [ ] Plan/progress text does not claim POST/PUT/DELETE REST coverage for this GET-only API.
- [ ] Targeted tests pass with `.\mvnw.cmd -Dtest=CompetitionRestControllerTests,StadiumRestControllerTests test`.

---

## Phase 8: Validation Test Cleanup

**User stories**: 6, 7, 8

### What To Build

Clean up validation tests to match `Spring_validatie`: DTO annotation tests without Spring context, parameterized invalid cases where useful, optional builder helpers where they reduce noise, validator class tests with `BeanPropertyBindingResult`, and direct custom `ConstraintValidator` tests.

### Acceptance Criteria

- [ ] DTO validation tests use `jakarta.validation.Validator` without loading Spring context.
- [ ] Each DTO validation test has at least one positive and one negative case.
- [ ] Repeated invalid cases are parameterized where this improves readability.
- [ ] Duplicate `InputTeamJoinDTO` coverage is removed from `InputTeamDTOValidationTests` because `InputTeamJoinDTOValidationTests` exists.
- [ ] `InputTeamJoinDTOValidationTests` remains present and covers blank and valid invite codes.
- [ ] `CompetitionValidatorTests` keeps `BeanPropertyBindingResult` and direct validator invocation.
- [ ] `StadiumChecksumValidatorTests` remains direct and Spring-context-free, with valid, invalid, and null-safe cases.
- [ ] Targeted validation tests pass with `.\mvnw.cmd -Dtest=*ValidationTests,CompetitionValidatorTests,StadiumChecksumValidatorTests test`.

---

## Phase 9: Final Verification And Plan Alignment

**User stories**: 8

### What To Build

Run the final verification pass and align plan/progress artifacts with what is actually implemented. The final suite should be easy to defend: each required school category should point to a concrete local exercise-project style.

### Acceptance Criteria

- [ ] All controller tests pass.
- [ ] Security tests pass.
- [ ] REST GET tests pass.
- [ ] Validation tests pass.
- [ ] Full suite passes with `.\mvnw.cmd test`.
- [ ] Plan/progress artifacts do not claim REST mutation coverage.
- [ ] Any intentionally retained full integration test is clearly justified.
- [ ] The final test suite can be summarized by category: MVC, security, REST GET, DTO validation, custom annotation validator, Spring validator class, and extra service tests.
