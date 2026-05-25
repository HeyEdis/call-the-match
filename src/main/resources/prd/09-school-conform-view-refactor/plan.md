# Plan: School-Conform View Refactor

> Source PRD: `src/main/resources/prd/09-school-conform-view-refactor-prd.md`

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, especially the feature overview, role descriptions, team/match/prediction/ranking screens, and technical requirements.
2. School guidelines from `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen`, especially `Slides_Spring_Boot.pdf`, `Slides_Spring_Web_Flow.pdf`, `Slides_Spring_Web_MVC_i18n.pdf`, `Slides_Spring_Security.pdf`, `Slides_Spring_Exceptions.pdf`, and `Slides_Spring_MultipleRow.pdf`.
3. Lesson notes from `C:\Users\Armour\Documents\HOGENT\EWD\Notes`, especially `13-03-26-Validation.md`, `03-04-2026-ErrorMessageEnI18n.md`, `24-04-26-Security.md`, and `Project.md`.
4. Exercise projects from `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij`, especially Multirow CRUD views, validation form views, i18n fragment/language-switch examples, and security login/error views.
5. Existing `call-the-match` codebase, especially `src/main/resources/templates`, `src/main/resources/i18n/messages.properties`, MVC controllers, security config, and MVC/security/validation tests.
6. Project guidelines: `.agents/skills/project-guidelines/SKILL.md` and related references for MVC layering and validation/i18n/exceptions.
7. Git repository: `https://github.com/HeyEdis/call-the-match.git`.
8. User decision from the current conversation: create a PRD for reviewing all views against `WorkspacesIntelij` school examples and redoing current views according to those guidelines.

## Architectural Decisions

Durable decisions that apply across all phases:

- **Routes**: public `/home`, `/team/ranking`, `/competition/{id}`, `/login`, `/register`, static resources, error pages, and public REST GET endpoints; user-only `/team/**` and `/predictions/**`; admin-only match management routes under `/competition`, `/competition/add`, `/competition/edit/{id}`, and `/competition/{id}/result`.
- **Schema**: no schema changes are planned. Existing entities, repositories, DTOs, and services remain the data source for views.
- **Key models**: `InputRegistrationDTO`, `InputTeamDTO`, `InputTeamJoinDTO`, `InputCompetitionDTO`, `InputCompetitionResultDTO`, `InputPredictionDTO`, competition/team/prediction response data, and authenticated user context remain the important view inputs.
- **Security**: login uses email; logout is POST with CSRF; admin is not a normal user and must not enter team or prediction flows; user must not enter admin match-management flows; view-level `sec:authorize` only hides/shows UI and does not replace security config or service checks.
- **Validation/i18n**: DTO-backed forms use `th:object`, `th:field`, and `th:errors`; validation, flash, label, navigation, footer, and error-page text belongs in `src/main/resources/i18n/messages.properties`; `typeMismatch` messages remain resource-bundle based.
- **REST/WebClient**: REST/WebClient implementation is out of scope. Public REST GET endpoints must remain permitted and unaffected.
- **Testing**: tests are a closure step. Targeted MVC, security, and validation tests verify that route behavior, form binding, role visibility, and validation display survived the refactor.

---

## Phase 1: Baseline View Audit And Route Map

**User stories**: 6, 7, 8

### What To Build

Create the safety map for the refactor before changing templates. Inventory every template, fragment, route link, form action, POST form, CSRF token, `th:object`, `th:field`, `th:errors`, hardcoded visible string, inline JavaScript action, and existing dirty worktree file. Compare the current view structure with the school examples from Multirow, validation, i18n fragments, and security login/error views.

### Acceptance Criteria

- [ ] All templates under `src/main/resources/templates` are listed and grouped by feature.
- [ ] Every navbar, footer, table action, detail link, and form action is checked against an existing controller route.
- [ ] Every POST form is checked for CSRF behavior.
- [ ] Every validated form is checked for `th:object`, `th:field`, and nearby `th:errors`.
- [ ] Hardcoded user-facing text is listed for later movement to `messages.properties`.
- [ ] Inline `onclick` behavior is listed and classified as either removable, acceptable, or movable to static JavaScript.
- [ ] Dirty worktree files are noted so useful existing changes are preserved.
- [ ] No application behavior is changed in this phase.

---

## Phase 2: Shared Fragments And Navigation

**User stories**: 1, 6, 7

### What To Build

Refactor shared fragments first because they affect every screen. Make `navbar.html` and `footer.html` valid Thymeleaf fragments, fix route links, preserve role-aware navigation, keep logout as a CSRF-protected POST form, and decide consistently where the footer is rendered.

### Acceptance Criteria

- [ ] `fragments/navbar.html` has valid Thymeleaf fragment structure and no invalid `<head>` before `<html>` shape.
- [ ] Navbar links point only to existing routes.
- [ ] Guest navigation shows public routes, login, and register.
- [ ] USER navigation shows team and prediction routes, but no admin match-management actions.
- [ ] ADMIN navigation shows match-management routes, but no team or prediction actions.
- [ ] Logout remains a POST form with CSRF.
- [ ] `fragments/footer.html` links are corrected to existing routes.
- [ ] Footer use is consistent; commented-out footer includes are removed or replaced by the real fragment decision.
- [ ] Navigation/footer text uses resource-bundle keys.

---

## Phase 3: Account Views

**User stories**: 2, 8

### What To Build

Bring login and registration views in line with the school security and validation examples. Preserve the custom email login flow, registration DTO binding, CSRF behavior, field-level validation display, and login/logout/registration feedback messages.

### Acceptance Criteria

- [ ] `account/login.html` uses the email field name expected by Spring Security.
- [ ] Login form includes CSRF.
- [ ] Login error and logout messages use resource-bundle keys.
- [ ] `account/register.html` binds to `inputRegistrationDto` with `th:object`.
- [ ] Registration fields use `th:field`.
- [ ] Registration validation errors are shown with `th:errors` near each field.
- [ ] Register headings, labels, button text, legal/help text, and links use resource-bundle keys.
- [ ] Account views keep the shared navbar/fragment behavior without exposing forbidden routes.

---

## Phase 4: Competition Admin And Public Match Views

**User stories**: 1, 5, 7, 8

### What To Build

Refactor competition list, detail, add, edit, and result templates. Preserve public match browsing and admin match management while making form binding, dropdowns, validation errors, i18n, route links, and official-result display match the school Thymeleaf examples.

### Acceptance Criteria

- [ ] `competition/list.html` uses valid route links for detail, add, edit, and result actions.
- [ ] Public match detail remains available to guests through `competition/show.html`.
- [ ] Admin-only controls in match detail use `sec:authorize` for visibility but remain protected by security config.
- [ ] Add/edit forms bind to `inputCompetitionDto` with `th:object`.
- [ ] Country and stadium select fields use `th:field` and backend-provided model data.
- [ ] Add/edit validation errors are shown with `th:errors` near each field.
- [ ] Result form binds to `inputCompetitionResultDto`.
- [ ] Result form validation errors are shown with `th:errors`.
- [ ] Match dates/times and scores are displayed using Thymeleaf formatting appropriate to the model values.
- [ ] Visible competition text, empty states, table headers, buttons, and flash/error messages use resource-bundle keys.
- [ ] Static JavaScript for stadium checksum remains a convenience only; server-side validation remains authoritative.

---

## Phase 5: Team Views

**User stories**: 3, 4, 7, 8

### What To Build

Refactor team dashboard, detail, edit, ranking, and private scoreboard templates. Preserve team create, join, owner actions, invite codes, member lists, public ranking, private scoreboards, and role/access visibility while making forms, loops, errors, links, and text school-conform.

### Acceptance Criteria

- [ ] `team/dashboard.html` binds create and join forms to their request DTOs.
- [ ] Team create and join fields use `th:field`.
- [ ] Team create and join validation errors are shown with `th:errors`.
- [ ] Dashboard team table uses backend-provided team data and contains no business calculations.
- [ ] `team/show.html` displays team name, rank, members, owner marker, scores, and invite code from model data.
- [ ] Owner-only remove/regenerate controls are visible only when allowed by the model/security rules.
- [ ] Team member removal and invite-code regeneration forms include CSRF.
- [ ] Invite-code copy behavior is either kept as small static JavaScript or simplified without affecting server behavior.
- [ ] `team/ranking.html` remains public and displays top teams from backend data.
- [ ] `team/scoreboard.html` remains private and displays team member scores from backend data.
- [ ] Team links use Thymeleaf URL expressions and existing controller routes.
- [ ] Visible team text, table headers, empty states, buttons, and messages use resource-bundle keys.

---

## Phase 6: Prediction Views

**User stories**: 3, 4, 7, 8

### What To Build

Refactor prediction list and prediction form templates. Preserve user-only prediction browsing and score submission while keeping DTO-backed input, validation display, match data display, route links, and closed/cutoff behavior clear and school-conform.

### Acceptance Criteria

- [ ] `prediction/list.html` displays predictions from backend-provided model data.
- [ ] Prediction list links point to existing prediction form routes.
- [ ] Empty prediction list state uses a resource-bundle key.
- [ ] `prediction/form.html` binds to `inputPredictionDto` with `th:object`.
- [ ] Predicted score fields use `th:field`.
- [ ] Field errors for predicted scores are shown with `th:errors`.
- [ ] Match data on the form is display-only and contains no scoring/business logic.
- [ ] Closed/cutoff messaging remains visible when supplied by the controller.
- [ ] Cancel/back behavior avoids inline JavaScript if a normal route link can safely replace it; otherwise the behavior is isolated as small static JavaScript.
- [ ] Visible prediction text, labels, buttons, and error messages use resource-bundle keys.

---

## Phase 7: Error Pages And Resource Bundles

**User stories**: 1, 6, 8

### What To Build

Normalize the MVC error pages and resource-bundle coverage after feature templates are cleaned up. Keep 403, 404, and 500 pages simple and connected to existing exception handling. Move remaining user-facing hardcoded strings into `messages.properties` without removing existing validation, type mismatch, flash, and scoring keys.

### Acceptance Criteria

- [ ] `error/403.html` uses resource-bundle title, heading, message, and navigation text.
- [ ] `error/404.html` uses resource-bundle title, heading, message, and navigation text.
- [ ] `error/500.html` uses resource-bundle title, heading, message, and navigation text.
- [ ] Existing `GlobalExceptionAdvice` behavior still returns the expected error views.
- [ ] `messages.properties` contains keys for every visible label/message introduced by this refactor.
- [ ] Existing validation and `typeMismatch` keys remain intact.
- [ ] Existing scoring constants in the resource bundle remain intact.
- [ ] No template relies on a missing message key.

---

## Phase 8: CSS Simplification Without Behavior Churn

**User stories**: 1, 8

### What To Build

Clean the stylesheet only where it supports the school-conform view refactor. Keep readable pages, tables, forms, field errors, success messages, navbar/footer, and error pages. Avoid a broad redesign or risky class churn that would distract from the MVC/Thymeleaf requirements.

### Acceptance Criteria

- [ ] Shared form, table, message, navbar, footer, and page-section styles remain readable.
- [ ] Prototype-only class names or comments are removed or renamed only when the template changes already make that safe.
- [ ] Error and success messages remain visually distinct.
- [ ] CSS changes do not require controller, service, repository, or DTO changes.
- [ ] CSS changes do not alter security, validation, or route behavior.
- [ ] Screens remain usable on normal desktop and narrow browser widths.

---

## Phase 9: MVC/Security/Validation Verification

**User stories**: 8

### What To Build

Run the final verification pass for the refactor. Confirm templates render through the MVC layer, role visibility and protected routes still behave correctly, form validation still redisplays errors, and resource bundles resolve. Keep REST/WebClient untouched.

### Acceptance Criteria

- [ ] Targeted MVC controller tests pass after route/form/view changes.
- [ ] Security tests pass after navbar/login/logout changes.
- [ ] Validation tests pass after form field or DTO-binding changes.
- [ ] REST GET tests still pass if security/static route changes could affect public endpoints.
- [ ] Full suite passes with `.\mvnw.cmd test` if time allows.
- [ ] Manual smoke check covers guest home/ranking/match detail, login/register, user team dashboard, user prediction form, admin competition list/add/edit/result, and error pages.
- [ ] Any unverified item is recorded clearly before the refactor is considered complete.
