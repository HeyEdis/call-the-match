# PRD: School-Conform View Refactor

## Problem Statement

The current `call-the-match` application has the required Thymeleaf screens for the FIFA World Cup 2026 prediction project, but the views have grown unevenly across feature work. Some templates already follow the HOGENT examples with `th:object`, `th:field`, `th:errors`, fragments, resource-bundle labels, CSRF tokens, and role-aware navigation. Other parts still need a school-conform pass: route links, fragment structure, hardcoded text, inline JavaScript, commented-out fragments, repeated view structure, and CSS that reads more like prototype polish than course-style MVC support.

This PRD defines one refactor feature: audit all current views against the school examples in `WorkspacesIntelij` and rewrite them so they remain functional, simple to defend, and aligned with the course guidance.

Safe passing first: preserve the existing user, admin, and guest flows before improving visual polish.

## Solution

Refactor all application views under `src/main/resources/templates` and supporting view resources so they follow the school's Thymeleaf MVC conventions.

The refactor should make the view layer boring, consistent, and explainable:

1. Every form view must bind to a request DTO with `th:object`, use `th:field`, and show field errors with `th:errors`.
2. POST forms must include CSRF tokens when Spring Security does not inject them automatically for the chosen form shape.
3. User-facing labels, headings, validation summaries, flash messages, and repeated navigation/footer text must come from `i18n/messages.properties`.
4. Views must not contain business logic, repository assumptions, or access decisions that belong in services, controllers, or security config.
5. Reusable navigation/footer/language parts must be expressed as valid Thymeleaf fragments.
6. Links must use Thymeleaf URL expressions and must point to real controller routes.
7. Dates and formatted values must use the course style: converters/formatters and double-brace display where appropriate.
8. Error pages must remain present and connected to MVC exception handling.

The outcome is not a redesign from scratch. It is a school-conform cleanup of all current screens.

## Current Codebase State

The application currently has 20 Thymeleaf templates:

- public/home and ranking views;
- account login and registration views;
- competition list, detail, add, edit, and result views;
- prediction list and form views;
- team dashboard, detail, edit, ranking, and private scoreboard views;
- 403, 404, and 500 error views;
- navbar and footer fragments.

The current controller layer returns these view names through `HomeController`, `AccountController`, `CompetitionController`, `PredictionController`, and `TeamController`. REST controllers are separate and do not render views.

The security configuration already separates admin match-management routes from user team/prediction routes. Login uses the custom `/login` page and `email` as username parameter.

The current views already include several school-conform pieces:

- `th:object` and `th:field` are used on registration, competition, prediction, and team forms.
- field-level errors are shown with `#fields.hasErrors(...)` and `th:errors`.
- `messages.properties` contains many labels and messages.
- navbar and footer fragments exist.
- logout and protected POST forms include CSRF tokens in key places.
- templates use `sec:authorize` for role-aware navigation and screen actions.

Known issues to address during the refactor:

- `fragments/navbar.html` has an invalid document shape with `<head>` before `<html>`.
- `fragments/footer.html` links to routes that do not match the current controllers, such as `/competitions` and `/templates/ranking`.
- `competition/list.html` contains a commented-out footer fragment instead of a consistent fragment decision.
- Some templates still include hardcoded visible text, for example ranking text and match separator text.
- Some views use inline `onclick` behavior for cancel/copy actions; this should be minimized or moved to static JavaScript when behavior is still needed.
- The CSS contains many feature-specific/prototype class names and should be simplified only where that helps school-conform readability.
- Several view files are currently modified in the working tree. This PRD must not assume those changes are disposable.

Known dirty worktree at PRD creation time:

- `src/main/resources/i18n/messages.properties`
- `src/main/resources/static/css/main.css`
- `src/main/resources/templates/account/login.html`
- `src/main/resources/templates/account/register.html`
- `src/main/resources/templates/competition/list.html`
- `src/main/resources/templates/competition/show.html`
- `src/main/resources/templates/fragments/footer.html`
- `src/main/resources/templates/fragments/navbar.html`
- `src/main/resources/templates/home.html`
- `src/main/resources/templates/prediction/list.html`
- `src/main/resources/templates/team/show.html`

These changes should be inspected and preserved where they are useful. Do not revert user or previous-agent work as part of the view refactor.

## School Requirements

This PRD supports these EWD requirements:

- Spring Boot MVC controllers returning Thymeleaf views.
- Thymeleaf templates with `th:text`, `th:href`, `th:each`, `th:if`, and `th:unless`.
- DTO-backed form views using `th:object` and `th:field`.
- Validation messages displayed with `th:errors`.
- i18n/resource-bundle labels and messages.
- reusable Thymeleaf fragments for common page parts.
- Spring Security login/logout and role-aware screen behavior.
- CSRF on POST forms.
- custom error pages for 403, 404, and 500.
- MVC controller tests later verifying views, model attributes, validation errors, redirects, and role access.

REST and WebClient are not part of this view refactor, except that public links and security rules must not break existing REST GET endpoints.

## Role And Access Decisions

- **Guest**: may view public home, public ranking, public competition detail, login, register, static resources, error pages, and public REST GET endpoints.
- **User**: may use team dashboard, team detail for teams they belong to, private team scoreboard, prediction list, and prediction form.
- **Admin**: may manage matches and official results only.
- **Forbidden**: admin must not see or use team membership and prediction flows; user must not see or use admin match-management actions; guest must not submit protected forms.

Views may hide or show links with `sec:authorize`, but the real access rule must remain in Spring Security and controller/service checks where needed.

## User Stories

1. As a guest, I want public pages to use consistent navigation and resource-bundle text, so that the app looks coherent without requiring login.
2. As a guest, I want login and registration forms to follow the school security and validation examples, so that authentication is easy to defend.
3. As a user, I want team forms and prediction forms to show field errors next to the fields, so that validation behavior matches the course pattern.
4. As a user, I want team lists, scoreboards, and match data to be displayed through simple Thymeleaf loops and formatted values, so that the view does not contain business logic.
5. As an admin, I want match add/edit/result screens to use the same form structure and error display, so that match management is consistent.
6. As a developer, I want fragments to be valid Thymeleaf fragments, so that navbar/footer reuse can be explained from the i18n and fragment exercises.
7. As a developer, I want every link in the views to match an existing controller route, so that the UI does not contain dead navigation.
8. As a student, I want the final screens to visibly match the school examples, so that the defence can point to known patterns instead of custom web-app complexity.

## Implementation Decisions

Audit and refactor these template groups:

- `home.html`
- `account/login.html`
- `account/register.html`
- `competition/add.html`
- `competition/edit.html`
- `competition/list.html`
- `competition/result.html`
- `competition/show.html`
- `prediction/form.html`
- `prediction/list.html`
- `team/dashboard.html`
- `team/edit.html`
- `team/ranking.html`
- `team/scoreboard.html`
- `team/show.html`
- `error/403.html`
- `error/404.html`
- `error/500.html`
- `fragments/navbar.html`
- `fragments/footer.html`

View structure decisions:

- Keep one standard HTML skeleton per full page: doctype, Thymeleaf namespace, optional security namespace, head with charset/title/CSS, body with fragments and page content.
- Keep fragments as reusable Thymeleaf blocks and avoid full-page markup inside fragment-only files unless it is needed by Thymeleaf parsing.
- Use `th:replace="~{fragments/navbar :: navbar}"` consistently where a page should show navigation.
- Decide consistently whether the footer appears on all public and authenticated pages. If included, fix its routes and render it through the fragment instead of leaving commented-out includes.
- Keep error pages simple and resource-bundle backed.

Form decisions:

- Use DTO-backed forms for registration, team create/join/edit, competition add/edit/result, and prediction submit.
- Keep `BindingResult` behavior in controllers and return the same view on validation errors.
- Every validated field should have a nearby `th:errors` element.
- Reload dropdown data in controllers when returning a form after validation errors.
- Keep select fields bound through DTOs/converters/formatters, not through manual view logic.
- Use static JavaScript only for small client-side convenience, such as stadium checksum prefill or invite-code copy. Server-side validation remains the source of truth.

i18n decisions:

- Move hardcoded headings, table headers, button labels, empty-state text, flash messages, and error text into `messages.properties`.
- Keep validation messages and `typeMismatch` messages in the resource bundle.
- Use message keys consistently by feature, for example `competition.*`, `team.*`, `prediction.*`, `nav.*`, `footer.*`, and `error.*`.
- Do not store school scoring constants in templates. They remain in the resource bundle and service/domain layer as already decided.

Security decisions:

- Keep login form fields aligned with security config: email field name for username and password field name for password.
- Keep logout as a POST form with CSRF.
- Use `sec:authorize` only for view visibility. Do not rely on hidden links as the only security mechanism.
- Navbar must not offer admin team/prediction flows.
- Navbar must not offer user admin match-management flows.

Link and route decisions:

- Use `@{...}` expressions for normal links and form actions.
- Use path-variable expressions consistently, such as `@{/competition/{id}(id=${competition.id})}`.
- Avoid string-built URLs where Thymeleaf URL expressions are clearer.
- Confirm every navbar, footer, table action, form action, and detail link matches a real controller route.

CSS decisions:

- Keep CSS sufficient for readable pages, forms, tables, and messages.
- Prefer simple reusable classes for form fields, error/success messages, tables, and page sections.
- Remove or rename prototype-only classes only when doing so does not create risky churn.
- Do not let CSS changes alter route behavior, validation, or security behavior.

## Testing Decisions

Tests are not the main implementation body for this PRD, but the refactor must be verified.

Useful verification:

- Run MVC controller tests after route/link/form changes.
- Run security tests after navbar/login/logout changes.
- Run validation tests after changing form field names or DTO bindings.
- Run full `mvn test` after the refactor if time allows.
- Manually open the main flows if a local server is already being used: guest home/ranking/detail, login/register, user team dashboard, user prediction form, admin competition list/add/edit/result, and error pages.

Final required test categories remain covered by the broader testing PRDs:

- MVC controller tests should assert view names, model attributes, field errors, redirects, and flash messages.
- Security tests should assert guest/user/admin access and login/logout behavior.
- Validation tests should assert DTO annotations and validator classes.

This view refactor should not be marked complete if templates render but controller/security tests fail.

## REST And WebClient Decisions

REST and WebClient implementation is out of scope.

This PRD may touch navigation or security only to ensure existing public REST GET endpoints remain permitted and unaffected. Do not add WebClient screens, REST mutation forms, or REST client UI in this refactor.

## Out Of Scope

- Changing domain rules, scoring rules, or persistence mappings.
- Redesigning the whole visual identity for polish.
- Adding new user journeys.
- Adding REST or WebClient behavior.
- Replacing server-side validation with JavaScript.
- Moving business decisions into Thymeleaf templates.
- Reverting current dirty worktree changes.
- Creating a master PRD.

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, especially the feature overview, role descriptions, team/match/prediction/ranking screens, and technical requirements.
2. School guidelines from `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen`, especially `Slides_Spring_Boot.pdf`, `Slides_Spring_Web_Flow.pdf`, `Slides_Spring_Web_MVC_i18n.pdf`, `Slides_Spring_Security.pdf`, `Slides_Spring_Exceptions.pdf`, and `Slides_Spring_MultipleRow.pdf`.
3. Lesson notes from `C:\Users\Armour\Documents\HOGENT\EWD\Notes`, especially `13-03-26-Validation.md`, `03-04-2026-ErrorMessageEnI18n.md`, `24-04-26-Security.md`, and `Project.md`.
4. Exercise projects from `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij`, especially Multirow CRUD views, validation form views, i18n fragment/language-switch examples, and security login/error views.
5. Existing `call-the-match` codebase, especially `src/main/resources/templates`, `src/main/resources/i18n/messages.properties`, MVC controllers, security config, and MVC/security/validation tests.
6. Project guidelines: `.agents/skills/project-guidelines/SKILL.md` and related references for MVC layering and validation/i18n/exceptions.
7. Git repository: `https://github.com/HeyEdis/call-the-match.git`.
8. User decision from the current conversation: create a PRD for reviewing all views against `WorkspacesIntelij` school examples and redoing current views according to those guidelines.

## Further Notes

- The highest-risk files are the shared fragments because a broken navbar/footer can affect every screen.
- The second-highest risk is form binding: changing field names, DTO names, or object names can break validation redisplay.
- Convert views in small groups: fragments first, account forms, competition forms, team forms, prediction forms, then listing/detail/error pages.
- Keep each step easy to test with one or two MVC/security test classes before moving on.
- If a design choice conflicts with school clarity, prefer the simpler school-style view.
