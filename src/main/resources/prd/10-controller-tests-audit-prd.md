# PRD: Controller Tests Audit

## Problem Statement

The `call-the-match` controller test suite is already useful and mostly school-conform, but it contains several tests that are broader or more brittle than needed for the HOGENT EWD controller-testing category. Some controller tests assert service-owned behavior, fixture shape, or exact rendered HTML/JavaScript details instead of the observable controller behavior that the school examples emphasize.

This PRD turns the controller-tests audit handoff into a concrete cleanup scope. The goal is not to throw away coverage, but to make the controller tests easier to defend: focused MockMvc tests for routes, status codes, views, model attributes, redirects, flash attributes, validation redisplay, security outcomes, and service delegation.

## Solution

Refactor the existing controller tests so they remain green while becoming smaller, clearer, and more school-shaped.

Safe passing first:

- Keep all behavior coverage that directly verifies controller responsibilities.
- Cut assertions that prove only fixture setup, service sorting/filtering, or template implementation details.
- Combine duplicate success-path tests when one test can verify the redirect, flash message, and service call.
- Keep REST controller tests in their current `@WebMvcTest` + `MockMvc` + `jsonPath` style.
- Keep broad access/security behavior in `AccessSecurityTests` instead of duplicating every role matrix case inside each controller test.

## Current Codebase State

The current codebase has MVC controller classes for account, competition, home, locale, prediction, and team flows, plus REST controller classes for competitions and stadium capacity.

Existing MVC controller tests:

- `AccountControllerTests`
- `CompetitionControllerTests`
- `HomeControllerTests`
- `LocaleControllerTests`
- `PredictionControllerTests`
- `TeamControllerTests`

Existing REST controller tests:

- `CompetitionRestControllerTests`
- `StadiumRestControllerTests`

Related security test:

- `AccessSecurityTests`

The audit handoff reports these verified results:

- 61 MVC controller tests passing.
- 5 REST controller tests passing.
- Controller tests use `@WebMvcTest`, `MockMvc`, `@MockitoBean`, status/view/model/redirect assertions, CSRF on protected POST routes, role-specific users, and REST `jsonPath`.

The current production controller shape supports the audit conclusions:

- `TeamController` delegates ranking, current-user teams, detail, scoreboard, invite-code regeneration, member removal, create, and join actions to `TeamService`.
- `CompetitionController` delegates match lookup and admin match management to services, uses DTO validation and `BindingResult`, and adds success/error flash or model messages.
- REST controllers expose public GET behavior that is already covered by focused REST tests.

Known worktree state at PRD creation:

- `src/main/resources/handoff/controller-tests-audit-handoff.md` is untracked and is the source for this PRD.
- `src/main/resources/prd/10-accountability-audit-conversation-mode-prd.md` is deleted in the worktree and must not be restored or modified as part of this PRD.

## School Requirements

This PRD supports the required EWD test categories by keeping controller tests aligned with the local course guidance:

- MVC controller tests with MockMvc.
- REST controller tests with MockMvc and JSON assertions.
- Security tests with guest, user, admin, login/logout, and CSRF coverage.
- Validation behavior visible in controller tests through invalid form submissions and field errors.
- Service/repository/validator behavior tested in their own categories instead of being over-asserted in controller tests.

The controller-test category should assert observable web behavior:

- HTTP status.
- View name.
- Model attributes.
- Redirect targets.
- Flash attributes.
- Field errors.
- Service interaction when useful.

It should not assert private methods, incidental HTML order, exact JavaScript snippets, or business rules already owned by services and validators.

## Role And Access Decisions

- **Guest**: may access public home, public ranking, public competition detail, login/register, error pages, static resources, and public REST GET endpoints.
- **User**: may access team dashboard, team detail for allowed teams, private scoreboards, and prediction flows.
- **Admin**: may access match management and official result flows only.
- **Forbidden**: admin must not join teams, manage user teams, view private team scoreboards, or submit predictions; user must not access admin match management; guest must be redirected to login for protected routes.

Controller tests may include direct role cases when the rendered controller outcome changes. The broad route matrix belongs in `AccessSecurityTests`.

## User Stories

1. As a developer, I want controller tests to assert route, view, model, redirect, flash, and validation behavior, so that each test clearly matches the school MockMvc expectations.
2. As a developer, I want service-owned rules to be removed from controller assertions, so that sorting, filtering, scoring, and membership decisions are tested in the correct layer.
3. As a developer, I want duplicate success-path tests combined, so that the suite remains readable without losing behavior coverage.
4. As a developer, I want brittle template and JavaScript checks trimmed, so that controller tests do not fail because of harmless view markup changes.
5. As a developer, I want REST controller tests to keep their current JSON-focused school style, so that REST coverage remains easy to defend.
6. As a student, I want the final test suite to be small, green, and explainable, so that the defence can point to clear examples for MVC, REST, and security categories.

## Implementation Decisions

### Keep The Existing Controller Suite

Do not discard the existing controller tests. The audit says the suite is mostly aligned with the course examples and already verifies valuable behavior.

Preserve coverage for:

- account register/login views, valid registration redirect, invalid registration field errors, and CSRF rejection;
- competition public detail, admin add/edit/result forms, invalid form redisplay, service calls, not-found and type-mismatch error views, and role-specific rendered output where it affects the controller response;
- prediction list/form/save flows, invalid score redisplay, cutoff handling, guest redirect, and admin forbidden behavior;
- team dashboard, create/join valid and invalid flows, private detail and scoreboard access, owner actions, non-owner forbidden behavior, and guest/admin route behavior;
- REST success, empty list, bad request, not-found JSON errors, and service verification.

### Reduce Service-Owned Assertions

Trim controller tests that assert facts owned by services or repositories:

- In `TeamControllerTests.rankingShowsPublicTopTenInScoreOrder`, keep only status, view, `teamList` model attribute, and `teamService.getTop10Teams()` verification. Cut assertions that the returned fixture has at most ten teams and is sorted.
- In `TeamControllerTests.dashboardShowsOnlyCurrentUsersTeamsAndFormModels`, keep the model/form assertions and `teamService.getCurrentUserTeams(principalEmail)` verification. Cut assertions that inspect fixture owner emails.

Sorting, top-10 limiting, current-user filtering, membership decisions, and score calculations belong in service tests.

### Reduce Template And JavaScript Assertions

Avoid exact HTML/JS checks unless they prove a project requirement.

Refactor targets:

- `HomeControllerTests.homePageRendersLocaleSwitcherFragment`: cut unless the locale switcher fragment is an explicit acceptance requirement. The controller-level redirect behavior is already better covered by `LocaleControllerTests.changeLocaleRedirectsToReferer`.
- `CompetitionControllerTests.adminAddFormExposesModelAndRendersStadiumsCorrectly`: keep model assertions and at most one meaningful rendered option or `data-code` check. Cut exact text order, JavaScript path, and broad HTML-string checks.
- `TeamControllerTests.memberCanSeeInviteCodeSharePanel`: keep that invite code appears for members and regenerate controls are not shown for non-owners. Cut clipboard/JavaScript implementation checks.

Exact copy, exact HTML order, and browser behavior are template/view concerns, not core MVC controller-test concerns.

### Combine Duplicate Success Tests

Merge duplicate tests that split one controller behavior across two methods:

- Combine `CompetitionControllerTests.validAdminAddSubmissionCallsServiceAndRedirectsToHome` and `successfulAddRedirectCarriesVisibleFlashMessageToHome`.
- Combined add success coverage should verify service call, redirect to `/home`, and `successMessage` flash attribute.
- Combine `CompetitionControllerTests.validAdminEditSubmissionCallsServiceAndRedirectsToCompetition` and `successfulEditRedirectCarriesVisibleFlashMessageToDetail`.
- Combined edit success coverage should verify service call, redirect to `/competition/3`, and `successMessage` flash attribute.

The combined tests should still be short and readable.

### Clean Prediction Controller Test Setup

`PredictionControllerTests` should not mock unrelated validators unless the slice setup requires it.

Preferred cleanup:

- exclude unrelated validator advice the same way `HomeControllerTests` and `AccountControllerTests` do, when practical;
- otherwise keep only the minimum mocks needed for the `@WebMvcTest` slice to load;
- keep prediction tests focused on prediction controller behavior: list, form, save, validation, cutoff, guest redirect, and admin forbidden.

### Revisit Admin Prediction Status Assertion

`CompetitionControllerTests.adminDoesNotReceivePredictionStatusOnCompetitionDetail` currently verifies a prediction-status lookup for `admin@example.com`.

That assertion locks in questionable behavior because admins should not participate in prediction flows. Prefer one of these outcomes:

- cut the service verification while keeping the rendered admin behavior assertion; or
- later refactor `CompetitionController` so admins do not request prediction status, then update the test to verify the service is not called for admin users.

Do not make this PRD change production behavior unless the implementation task explicitly includes that controller refactor.

### Separation Of Responsibilities

Use this split while refactoring:

- Controller tests: route, status, view, model attributes, redirects, flash attributes, service delegation, basic access behavior when it changes the controller result.
- Service tests: top-10 limiting, sorting, current-user filtering, membership/ownership decisions, score calculations.
- Validator tests: field-specific rules, duplicate team names, invite-code existence, stadium checksum, date bounds, time conflicts.
- Security tests: broad guest/user/admin route matrix, login/logout, CSRF.
- Template/view checks: exact HTML, JavaScript snippets, exact text order. Avoid in controller tests unless explicitly required.

## Testing Decisions

This PRD changes only test shape, not intended application behavior.

After refactoring, run the full controller-focused command from the handoff:

```powershell
.\mvnw.cmd -q "-Dtest=AccountControllerTests,CompetitionControllerTests,PredictionControllerTests,TeamControllerTests,HomeControllerTests,LocaleControllerTests,CompetitionRestControllerTests,StadiumRestControllerTests" test
```

Expected result:

- MVC controller tests pass.
- REST controller tests pass.
- No production-code behavior changes are required for the basic cleanup.

If the admin prediction-status behavior is refactored in production code, also run:

```powershell
.\mvnw.cmd -q "-Dtest=CompetitionControllerTests,AccessSecurityTests" test
```

The final suite should remain school-conform:

- MockMvc controller assertions are focused on observable web behavior.
- Invalid form submissions verify field errors and no unintended service call.
- REST controller tests use `jsonPath` and verify service calls.
- Broad role matrix coverage remains in security tests.

## REST And WebClient Decisions

REST is relevant only because two existing REST controller test classes were part of the audit. Keep them in scope for verification and preserve their school-style shape.

WebClient is out of scope. This PRD must not add WebClient tests or new REST endpoints.

## Out Of Scope

- Rewriting the whole test suite.
- Adding new application features.
- Adding new REST mutation endpoints.
- Changing service, repository, validator, or template behavior unless a test cleanup reveals a genuine bug and the user explicitly approves the production change.
- Replacing service tests or validation tests.
- Browser/end-to-end tests.
- Restoring or modifying unrelated deleted PRD files in the dirty worktree.

## Sources

1. Controller tests audit handoff: `src/main/resources/handoff/controller-tests-audit-handoff.md`.
2. Existing `call-the-match` codebase, especially `src/main/java/com/example/callthematch/controller` and `src/test/java/com/example/callthematch`.
3. Project guidelines: `.agents/skills/project-guidelines/SKILL.md`.
4. Testing guideline reference: `.agents/skills/project-guidelines/references/testing.md`.
5. Existing testing PRD: `src/main/resources/prd/06-testing-prd.md`.
6. Existing school-conform test refactor PRD: `src/main/resources/prd/08-school-conform-test-refactor-prd.md`.
7. Exercise evidence from the handoff: Spring validation `RegistrationControllerTest`, security `SecurityTest`, REST fruit `FruitRestControllerTest`, and notes from `C:\Users\Armour\Documents\HOGENT\EWD\Notes\08-05-26-REST.md`.
8. Git repository: `https://github.com/HeyEdis/call-the-match.git`.
9. User request from the current conversation: create a PRD from the controller-tests-audit handoff file.

## Further Notes

- The safest implementation sequence is: trim `TeamControllerTests`, combine duplicate `CompetitionControllerTests`, reduce brittle HTML/JS checks, clean `PredictionControllerTests` setup, then decide whether the admin prediction-status lookup should stay as-is or become a production fix.
- Keep each cleanup small and rerun the targeted controller command after each group of changes.
- This PRD is narrower than `08-school-conform-test-refactor-prd.md`: it is specifically about the audited controller and REST controller tests, not the full validation/security/service test strategy.
