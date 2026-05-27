# Controller Tests Audit Handoff

Date: 2026-05-27  
Project: `call-the-match`

## Suggested Skills

- `project-guidelines`: use first. The audit must be checked against the local EWD school conventions, notes, and exercise projects.
- `diagnose`: use only if a controller test starts failing or a MockMvc/security setup behaves unexpectedly.
- `handoff`: use again after decisions are made or tests are refactored.

## Purpose

This handoff captures the audit of all controller tests so another chat/agent can continue from the same reasoning. The user is auditing whether the tests are school-conform, whether cases are missing, and which tests are overdone enough to cut.

## Evidence Sources

Primary project guideline:

- `.agents/skills/project-guidelines/references/testing.md`

Important rules from that file:

- MVC controller tests should use `MockMvc`.
- Assert HTTP status, view name, model attributes, redirects, and service interaction when useful.
- REST controller tests should follow the fruit REST exercise pattern with `@WebMvcTest`, `MockMvc`, mocked services, `jsonPath`, status checks, and service verification.
- Security tests should use `@WithMockUser`, `@WithAnonymousUser`, and form login helpers where appropriate.
- Keep tests focused and small.
- Test observable behavior and required school outcomes, not private methods or incidental implementation details.

Exercise evidence:

- `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Spring\Spring_validatie\Spring_Boot_Validation\src\test\java\com\example\spring_boot_validation\controller\RegistrationControllerTest.java`
  - Shows `@WebMvcTest`, `MockMvc`, `@MockitoBean`, GET form test, valid POST test, invalid POST test, `model().attributeHasFieldErrors`, and `verify(..., never())`.
- `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Security\Spring_Boot_security_roles\src\test\java\com\example\spring_boot_security_roles\controller\SecurityTest.java`
  - Shows `@WithMockUser`, form login helpers, forbidden checks, and redirect checks.
- `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_REST\Spring_Boot_rest_fruit_start\src\test\java\com\example\spring_boot_rest_fruit_start\controller\FruitRestControllerTest.java`
  - Shows REST controller test style: `@WebMvcTest`, `MockMvc`, `jsonPath`, success case, not-found JSON error, empty list, non-empty list.
- `C:\Users\Armour\Documents\HOGENT\EWD\Notes\08-05-26-REST.md`
  - Notes that when validator advice is used in controllers, tests must fix/import that advice.

## Tests Audited

MVC controller tests:

- `src/test/java/com/example/callthematch/controller/AccountControllerTests.java`
- `src/test/java/com/example/callthematch/controller/CompetitionControllerTests.java`
- `src/test/java/com/example/callthematch/controller/HomeControllerTests.java`
- `src/test/java/com/example/callthematch/controller/LocaleControllerTests.java`
- `src/test/java/com/example/callthematch/controller/PredictionControllerTests.java`
- `src/test/java/com/example/callthematch/controller/TeamControllerTests.java`

REST controller tests also checked:

- `src/test/java/com/example/callthematch/restcontroller/CompetitionRestControllerTests.java`
- `src/test/java/com/example/callthematch/restcontroller/StadiumRestControllerTests.java`

Related security tests:

- `src/test/java/com/example/callthematch/security/AccessSecurityTests.java`

## Verification Done

MVC controller tests were run:

```powershell
.\mvnw.cmd -q "-Dtest=AccountControllerTests,CompetitionControllerTests,PredictionControllerTests,TeamControllerTests,HomeControllerTests,LocaleControllerTests" test
```

Result:

- `AccountControllerTests`: 5 tests, 0 failures
- `CompetitionControllerTests`: 24 tests, 0 failures
- `HomeControllerTests`: 3 tests, 0 failures
- `LocaleControllerTests`: 1 test, 0 failures
- `PredictionControllerTests`: 8 tests, 0 failures
- `TeamControllerTests`: 20 tests, 0 failures
- Total MVC controller tests: 61 passing

REST controller tests were run:

```powershell
.\mvnw.cmd -q "-Dtest=CompetitionRestControllerTests,StadiumRestControllerTests" test
```

Result:

- `CompetitionRestControllerTests`: 3 tests, 0 failures
- `StadiumRestControllerTests`: 2 tests, 0 failures
- Total REST controller tests: 5 passing

## Overall Verdict

The controller tests are mostly school-conform.

They correctly use:

- `@WebMvcTest`
- `MockMvc`
- mocked services with `@MockitoBean`
- status/view/model/redirect assertions
- `csrf()` for protected POST routes
- `user(...).roles(...)` for role-specific controller behavior
- `jsonPath` for REST controller JSON assertions

The main cleanup is not because the tests are wrong, but because some are too broad or brittle. Several tests check facts owned by services, repositories, validators, or templates instead of controller behavior.

## Keep

Keep these patterns:

- Account controller tests for register/login views, valid registration redirect, invalid registration field errors, and CSRF rejection.
- Competition controller tests for public detail, admin add/edit/result forms, invalid form redisplay, service calls, not-found/type-mismatch error views, and role-specific visibility when it directly affects the rendered controller outcome.
- Prediction controller tests for list/form/save flows, invalid score redisplay, cutoff handling, guest redirect, and admin forbidden.
- Team controller tests for dashboard model, create/join valid and invalid flows, private detail/scoreboard access, owner actions, non-owner forbidden behavior, and guest/admin route behavior.
- REST controller tests for JSON success, empty list, bad request/not found error JSON, and service verification.

## Cut Or Reduce

These are overdone or testing the wrong layer:

1. `TeamControllerTests.rankingShowsPublicTopTenInScoreOrder`
   - Cut the AssertJ checks that the fixture has at most 10 teams and is sorted.
   - Reason: the controller only passes through `teamService.getTop10Teams()`. Top-10 limiting and sorting belong in `TeamService` or repository tests.
   - Keep only status, view, model attribute, and service verify.

2. `TeamControllerTests.dashboardShowsOnlyCurrentUsersTeamsAndFormModels`
   - Cut assertions that inspect the owner email inside the fixture.
   - Reason: the controller should pass `principal.getName()` to the service and render the returned list. Ownership filtering belongs in `TeamService` tests.

3. `HomeControllerTests.homePageRendersLocaleSwitcherFragment`
   - This is more template-fragment testing than controller testing.
   - The real controller behavior is already covered by `LocaleControllerTests.changeLocaleRedirectsToReferer`.
   - Cut unless the locale switcher is a project acceptance requirement.

4. `PredictionControllerTests`
   - Remove unrelated mocked validators (`CompetitionValidator`, `InputTeamValidator`, `InputTeamJoinValidator`) if possible.
   - Better pattern: exclude unrelated validator advice the same way `HomeControllerTests` and `AccountControllerTests` do.

5. `CompetitionControllerTests.validAdminAddSubmissionCallsServiceAndRedirectsToHome` and `successfulAddRedirectCarriesVisibleFlashMessageToHome`
   - Combine into one test: successful add should call service, redirect to `/home`, and carry `successMessage`.

6. `CompetitionControllerTests.validAdminEditSubmissionCallsServiceAndRedirectsToCompetition` and `successfulEditRedirectCarriesVisibleFlashMessageToDetail`
   - Combine into one test: successful edit should call service, redirect to `/competition/3`, and carry `successMessage`.

7. `CompetitionControllerTests.adminAddFormExposesModelAndRendersStadiumsCorrectly`
   - Reduce HTML/JS assertions.
   - Keep model assertions and maybe one meaningful rendered `data-code` check.
   - Cut brittle checks for exact text order, JS path, or too many HTML strings unless they are explicitly required by a PRD.

8. `TeamControllerTests.memberCanSeeInviteCodeSharePanel`
   - Reduce clipboard/JS implementation assertions.
   - Keep that the invite code appears for members and regenerate is not shown for non-owners.

9. `CompetitionControllerTests.adminDoesNotReceivePredictionStatusOnCompetitionDetail`
   - Current test verifies `predictionService.findPredictionStatusByCompetitionIdAndEmail(1L, "admin@example.com")`.
   - This locks in questionable behavior. Admins should not participate in prediction flows.
   - Prefer cutting that verify or later refactoring the controller so admins do not request prediction status.

## Separation Of Responsibilities

Use this split when deciding whether a test belongs in controller tests:

- Controller tests: route, status, view, model attributes, redirect, flash attributes, service delegation, basic security behavior when it changes controller access.
- Service tests: top-10 limiting, sorting, current-user filtering, membership/ownership decisions, score calculations.
- Validator tests: field-specific validation rules, duplicate team name, invite code existence, stadium checksum, date bounds, time conflict.
- Security tests: broad guest/user/admin route matrix and login/logout behavior. The project already has `AccessSecurityTests`, so avoid duplicating all security coverage inside each controller test.
- Template/view tests: exact JavaScript snippets, exact copy, exact HTML order. Avoid these in controller tests unless a project requirement depends on that rendered string.

## Next Refactor Shape

Suggested safe sequence:

1. Remove fixture-only AssertJ checks from `TeamControllerTests`.
2. Combine duplicate add/edit success tests in `CompetitionControllerTests`.
3. Trim brittle HTML/JS assertions in competition/team tests.
4. Add the missing model/flash cases listed above.
5. Clean the `PredictionControllerTests` setup by excluding unrelated validator advice or removing unrelated validator mocks.
6. Re-run:

```powershell
.\mvnw.cmd -q "-Dtest=AccountControllerTests,CompetitionControllerTests,PredictionControllerTests,TeamControllerTests,HomeControllerTests,LocaleControllerTests,CompetitionRestControllerTests,StadiumRestControllerTests" test
```

## Final Position

Do not throw away the controller test suite. It is useful and mostly aligned with the exercises. The right move is to make it more school-shaped: focused MockMvc tests around observable controller behavior, with service and validator rules moved to their own targeted tests.
