# Plan: Controller Tests Audit

> Source PRD: `src/main/resources/prd/10-controller-tests-audit-prd.md`

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

## Architectural Decisions

Durable decisions that apply across all phases:

- **Routes**: keep the existing MVC routes for account, competition, home, locale, prediction, and team flows; keep public REST GET endpoints for matches by date and stadium capacity.
- **Schema**: no schema changes are part of this plan.
- **Key models**: controller tests may use DTOs and response records as fixtures, but must not prove service-owned domain rules through controller assertions.
- **Security**: guest, user, and admin behavior must remain explicit; admin is not a normal user and must not use team or prediction flows.
- **Validation/i18n**: invalid form submissions should assert field errors, view redisplay, and no unintended service calls; exact translated copy should not be over-asserted in controller tests.
- **REST/WebClient**: preserve existing REST controller GET tests with `jsonPath`; WebClient and new REST endpoints are out of scope.
- **Testing**: this is a test-refactor plan; keep MockMvc controller tests focused on observable web behavior and keep broad route-security coverage in `AccessSecurityTests`.
- **Dirty worktree**: do not restore or modify the deleted `src/main/resources/prd/10-accountability-audit-conversation-mode-prd.md`; do not treat untracked handoff/PRD files as production code changes.

---

## Phase 1: Baseline And Boundaries

**User stories**: 1, 5, 6

### What To Build

Establish the starting point before changing tests. Confirm the controller and REST test suite is green, then document which assertions belong in controller tests versus service, validator, security, and template tests.

### Acceptance Criteria

- [ ] Run the controller-focused baseline command from the PRD before test edits.
- [ ] Record whether MVC and REST controller tests pass before refactoring.
- [ ] Confirm `AccountControllerTests`, `CompetitionControllerTests`, `HomeControllerTests`, `LocaleControllerTests`, `PredictionControllerTests`, `TeamControllerTests`, `CompetitionRestControllerTests`, and `StadiumRestControllerTests` remain the in-scope test classes.
- [ ] Confirm `AccessSecurityTests` remains the home for broad guest/user/admin route matrix coverage.
- [ ] No production code or unrelated dirty worktree files are changed in this phase.

---

## Phase 2: Trim Team Controller Overreach

**User stories**: 1, 2, 6

### What To Build

Refactor `TeamControllerTests` so it verifies controller behavior without re-testing service-owned ranking and filtering rules. Keep the tests useful for route, status, view, model, redirects, validation redisplay, and service delegation.

### Acceptance Criteria

- [ ] `rankingShowsPublicTopTenInScoreOrder` no longer asserts top-10 size or score ordering of the fixture returned by the mocked service.
- [ ] The ranking test still asserts successful status, `team/ranking` view, `teamList` model data, and `teamService.getTop10Teams()` delegation.
- [ ] `dashboardShowsOnlyCurrentUsersTeamsAndFormModels` no longer inspects fixture owner email values.
- [ ] The dashboard test still asserts model form objects, current-user team model data, and `teamService.getCurrentUserTeams(principalEmail)` delegation.
- [ ] Team create/join invalid tests still assert redisplay and `verify(..., never())` for service mutation calls.
- [ ] Run `.\mvnw.cmd -q "-Dtest=TeamControllerTests" test` successfully.

---

## Phase 3: Combine Competition Success Paths

**User stories**: 1, 3, 6

### What To Build

Merge duplicate success-path tests in `CompetitionControllerTests` so add and edit flows each have one clear happy-path test that verifies the service call, redirect, and flash message.

### Acceptance Criteria

- [ ] The add success tests are combined into one focused test.
- [ ] The combined add test verifies `competitionService.add(...)`, redirect to `/home`, and `successMessage` flash attribute.
- [ ] The edit success tests are combined into one focused test.
- [ ] The combined edit test verifies `competitionService.update(...)`, redirect to `/competition/3`, and `successMessage` flash attribute.
- [ ] Invalid add/edit/result tests still assert redisplay, field errors or error model data as applicable, and no unintended service mutation calls.
- [ ] Run `.\mvnw.cmd -q "-Dtest=CompetitionControllerTests" test` successfully.

---

## Phase 4: Reduce Brittle View Assertions

**User stories**: 1, 4, 6

### What To Build

Trim exact HTML and JavaScript assertions that make controller tests fragile. Keep meaningful rendered-output checks only where they directly prove controller behavior or an explicit project requirement.

### Acceptance Criteria

- [ ] `HomeControllerTests.homePageRendersLocaleSwitcherFragment` is removed or reduced unless the locale switcher fragment is treated as an explicit acceptance requirement.
- [ ] `LocaleControllerTests.changeLocaleRedirectsToReferer` remains the controller-level proof for locale redirect behavior.
- [ ] `CompetitionControllerTests.adminAddFormExposesModelAndRendersStadiumsCorrectly` keeps model assertions and at most one meaningful rendered data-flow check.
- [ ] Exact text order, JavaScript path, and broad HTML string assertions are removed from the competition add-form test where they do not prove controller behavior.
- [ ] `TeamControllerTests.memberCanSeeInviteCodeSharePanel` keeps invite-code/member visibility behavior but removes clipboard or JavaScript implementation assertions.
- [ ] Run the changed Home, Locale, Competition, and Team controller tests successfully.

---

## Phase 5: Clean Prediction Slice Setup

**User stories**: 1, 2, 6

### What To Build

Clean `PredictionControllerTests` so the `@WebMvcTest` slice depends only on what prediction controller behavior needs. Preserve coverage for list, form, save, validation, cutoff, guest redirect, and admin forbidden behavior.

### Acceptance Criteria

- [ ] Unrelated validator mocks are removed when the test slice can load without them.
- [ ] If validator advice must be excluded, use the same style already used by nearby controller tests.
- [ ] Prediction tests still assert view names, model data, redirects, validation redisplay, cutoff behavior, and service calls.
- [ ] Invalid prediction submission still verifies no unintended save call.
- [ ] Guest and admin behavior remains covered either in `PredictionControllerTests` where controller-specific or in `AccessSecurityTests` where route-matrix-specific.
- [ ] Run `.\mvnw.cmd -q "-Dtest=PredictionControllerTests" test` successfully.

---

## Phase 6: Admin Prediction Status Decision

**User stories**: 1, 2, 6

### What To Build

Resolve the questionable admin prediction-status assertion in `CompetitionControllerTests`. This phase is intentionally separate because it may be either a pure test cleanup or a small production behavior fix.

### Acceptance Criteria

- [ ] Decide whether this phase is test-only or includes a production controller change.
- [ ] If test-only, remove the brittle verification that admin detail view calls prediction status lookup while preserving the rendered admin outcome assertion.
- [ ] If production behavior is changed, ensure admins do not request prediction status on competition detail.
- [ ] If production behavior is changed, update the test to verify prediction status is not requested for admin users.
- [ ] User behavior on competition detail still verifies prediction status lookup when it affects rendered output.
- [ ] Run `.\mvnw.cmd -q "-Dtest=CompetitionControllerTests,AccessSecurityTests" test` successfully if production behavior changes.

---

## Phase 7: Controller And REST Verification

**User stories**: 5, 6

### What To Build

Run the final controller-focused verification and confirm the suite remains school-conform. REST controller tests should remain in their current focused JSON style and should not expand into WebClient or mutation endpoint work.

### Acceptance Criteria

- [ ] Run:

```powershell
.\mvnw.cmd -q "-Dtest=AccountControllerTests,CompetitionControllerTests,PredictionControllerTests,TeamControllerTests,HomeControllerTests,LocaleControllerTests,CompetitionRestControllerTests,StadiumRestControllerTests" test
```

- [ ] All MVC controller tests pass.
- [ ] All REST controller tests pass.
- [ ] REST tests still use `@WebMvcTest`, `MockMvc`, `jsonPath`, and service verification.
- [ ] No WebClient tests or new REST mutation endpoints are introduced.
- [ ] The final diff contains only the intended test cleanup and this planning work.
- [ ] The final explanation maps the result back to the school categories: MVC controller tests, REST controller tests, and security boundary preservation.
