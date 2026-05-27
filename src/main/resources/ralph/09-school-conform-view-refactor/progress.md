# Ralph Progress Log: 09-school-conform-view-refactor

Each iteration appends what was done, decisions made, files changed, verification results, and blockers.

## Entries

### 2026-05-25 - baseline-view-audit-route-map

- Task: Baseline View Audit And Route Map
- What changed: added `view-audit.md` with template groups, controller route map, link/form checks, CSRF notes, DTO binding notes, hardcoded text findings, inline action findings, footer decision, and dirty worktree notes.
- Decisions made: kept this as an audit-only task with no application behavior changes.
- Files changed: `src/main/resources/ralph/09-school-conform-view-refactor/view-audit.md`, `src/main/resources/plan/09-school-conform-view-refactor/plan.json`, `src/main/resources/ralph/09-school-conform-view-refactor/progress.md`.
- Verification: audit criteria were checked against templates, controllers, security config, and `git status --short`; no runtime verification needed because behavior was not changed.
- Result: acceptance criteria verified; marked `passes` true.

### 2026-05-25 - shared-fragments-navigation

- Task: Shared Fragments And Navigation
- What changed: fixed `fragments/navbar.html` so the Thymeleaf fragment no longer has a `<head>` before `<html>` shape.
- Decisions made: did not add `fragments/footer.html` or footer includes because the user explicitly said not to add the footer back.
- Files changed: `src/main/resources/templates/fragments/navbar.html`, `src/main/resources/ralph/09-school-conform-view-refactor/TODO.md`, `src/main/resources/ralph/09-school-conform-view-refactor/progress.md`.
- Verification: `.\mvnw.cmd '-Dtest=AccountControllerTests,AccessSecurityTests' test` passed with 18 tests.
- Blocker: footer-related acceptance criteria cannot be completed without contradicting the user's instruction.
- Result: left `passes` false.

### 2026-05-25 - account-views

- Task: Account Views
- What changed: no template changes needed; existing login and registration views already match the task requirements.
- Decisions made: avoided cosmetic churn after verifying that login uses `name="email"`, login includes CSRF, register binds to `inputRegistrationDTO`, registration fields use `th:field`, field errors use `th:errors`, and account text uses resource-bundle keys.
- Files changed: `src/main/resources/plan/09-school-conform-view-refactor/plan.json`, `src/main/resources/ralph/09-school-conform-view-refactor/progress.md`.
- Verification: `.\mvnw.cmd '-Dtest=AccountControllerTests,AccessSecurityTests' test` passed with 18 tests.
- Result: acceptance criteria verified; marked `passes` true.

### 2026-05-25 - competition-admin-public-match-views

- Task: Competition Admin And Public Match Views
- What changed: replaced string-built detail links with Thymeleaf path-variable URL expressions, added the missing result action link to the admin competition list, moved visible `vs` text to the resource bundle, and replaced `history.back()` cancel buttons with route links to the competition list or match detail.
- Decisions made: kept server-side stadium checksum validation authoritative and left the existing static checksum helper in place as client convenience only.
- Files changed: `src/main/resources/templates/competition/list.html`, `src/main/resources/templates/competition/show.html`, `src/main/resources/templates/competition/add.html`, `src/main/resources/templates/competition/edit.html`, `src/main/resources/templates/competition/result.html`, `src/main/resources/i18n/messages.properties`, `src/main/resources/plan/09-school-conform-view-refactor/plan.json`, `src/main/resources/ralph/09-school-conform-view-refactor/progress.md`.
- Verification: `.\mvnw.cmd '-Dtest=CompetitionControllerTests,TeamControllerTests,PredictionControllerTests' test` passed with 52 tests; after adding the result action link, `.\mvnw.cmd '-Dtest=CompetitionControllerTests' test` passed with 24 tests.
- Result: acceptance criteria verified; marked `passes` true.

### 2026-05-25 - team-views

- Task: Team Views
- What changed: replaced string-built team links and POST form actions with Thymeleaf path-variable URL expressions, moved the hardcoded rank label to an existing resource-bundle key, and removed prototype comments from team views.
- Decisions made: kept the invite-code clipboard handler inline for now because the task explicitly allows small static/convenience JavaScript and existing tests verify that behavior.
- Files changed: `src/main/resources/templates/team/dashboard.html`, `src/main/resources/templates/team/show.html`, `src/main/resources/templates/team/scoreboard.html`, `src/main/resources/plan/09-school-conform-view-refactor/plan.json`, `src/main/resources/ralph/09-school-conform-view-refactor/progress.md`.
- Verification: `.\mvnw.cmd '-Dtest=CompetitionControllerTests,TeamControllerTests,PredictionControllerTests' test` passed with 52 tests.
- Result: acceptance criteria verified; marked `passes` true.

### 2026-05-25 - prediction-views

- Task: Prediction Views
- What changed: moved visible `vs` text to the resource bundle, removed the prototype comment from the prediction list, and replaced the prediction form `history.back()` cancel button with a normal route link to the match detail.
- Decisions made: kept prediction form DTO binding, field errors, cutoff handling, and controller-supplied model data unchanged.
- Files changed: `src/main/resources/templates/prediction/list.html`, `src/main/resources/templates/prediction/form.html`, `src/main/resources/i18n/messages.properties`, `src/main/resources/plan/09-school-conform-view-refactor/plan.json`, `src/main/resources/ralph/09-school-conform-view-refactor/progress.md`.
- Verification: `.\mvnw.cmd '-Dtest=CompetitionControllerTests,TeamControllerTests,PredictionControllerTests' test` passed with 52 tests.
- Result: acceptance criteria verified; marked `passes` true.

### 2026-05-25 - error-pages-resource-bundles

- Task: Error Pages And Resource Bundles
- What changed: no template changes needed; `error/403.html`, `error/404.html`, and `error/500.html` already use resource-bundle titles, headings, messages, and return-home navigation text.
- Decisions made: kept `styleNotFound.css` for the error pages because it exists and changing stylesheet wiring is not required for the resource-bundle task.
- Files changed: `src/main/resources/plan/09-school-conform-view-refactor/plan.json`, `src/main/resources/ralph/09-school-conform-view-refactor/progress.md`.
- Verification: checked `GlobalExceptionAdvice` returns the expected error views, scanned template message keys against `messages.properties` with no missing keys found, and ran `.\mvnw.cmd test` successfully with 120 tests.
- Result: acceptance criteria verified; marked `passes` true.

### 2026-05-25 - css-simplification-without-behavior-churn

- Task: CSS Simplification Without Behavior Churn
- What changed: removed stale prototype comments from `main.css`.
- Decisions made: avoided class renames and visual churn because the existing form, table, message, navbar, and page-section styles are already covered by the templates and tests; footer styles were not introduced because the footer should not be restored.
- Files changed: `src/main/resources/static/css/main.css`, `src/main/resources/plan/09-school-conform-view-refactor/plan.json`, `src/main/resources/ralph/09-school-conform-view-refactor/progress.md`.
- Verification: `.\mvnw.cmd test` passed with 120 tests.
- Result: acceptance criteria verified; marked `passes` true.

### 2026-05-25 - mvc-security-validation-verification

- Task: MVC/Security/Validation Verification
- What changed: ran the full automated verification suite.
- Decisions made: did not mark this task complete because the acceptance criteria also ask for a manual smoke check across guest, user, admin, and error-page flows. AFK mode should not guess credentials or session setup for that manual pass.
- Files changed: `src/main/resources/ralph/09-school-conform-view-refactor/TODO.md`, `src/main/resources/ralph/09-school-conform-view-refactor/progress.md`.
- Verification: `.\mvnw.cmd test` passed with 120 tests, covering MVC, security, validation, service, and REST GET tests.
- Blocker: manual browser smoke remains.
- Result: left `passes` false.
