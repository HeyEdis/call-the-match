# PRD: Accountability Audit Conversation Mode

## Problem Statement

The `call-the-match` project has reached a late audit phase where the biggest risk is no longer only whether features exist, but whether each implementation choice can be defended against the HOGENT EWD guidelines, lesson notes, and exercise projects.

The handoff file `src/main/resources/handoff/accountability-audit-conversation-mode.md` describes a specific working mode: future audit conversations must be concrete, evidence-based, and school-conform. The user is not asking for a generic code review or automatic refactor. The user is interrogating the codebase one controller, validator, model, advice class, security rule, REST client, and test category at a time.

This PRD turns that handoff into a durable project requirement so future plans and Ralph tasks can preserve the same audit behavior.

Safe passing first: the audit should improve traceability and defensibility without introducing unnecessary churn or refactoring code before the user asks for it.

## Solution

Create an accountability audit workflow for continuing the project review. The workflow must make every answer and follow-up decision traceable to local evidence:

1. Start from the current `call-the-match` codebase.
2. Compare the code to the project guideline skill, school slides, lesson notes, and exercise projects.
3. Answer with a verdict first.
4. Cite concrete files and, when useful, line references.
5. Explain how the current implementation works.
6. State the tradeoff or risk of keeping the current code.
7. Recommend the school-conform shape or the smallest safe follow-up.

The workflow should distinguish between three different outcomes:

- explanation only, when the user wants to understand why code is shaped a certain way;
- documentation/handoff, when the user wants decisions preserved for another session;
- implementation, only when the user explicitly asks to change code or when a diagnosed bug requires a fix.

## Current Codebase State

The project already contains a broad Spring Boot MVC application with the major school-required areas present:

- MVC controllers for account, home, competition, prediction, team, and locale flows.
- REST controllers for match lookup and stadium capacity.
- service, repository, model, request DTO, response DTO, formatter, validator, advice, client, template, i18n, and test packages.
- Thymeleaf views for account, competition, prediction, team, public pages, fragments, and error pages.
- custom validation through Spring `Validator` classes and the `@ValidStadiumChecksum` Bean Validation annotation.
- MVC exception advice and REST error advice.
- Spring Security with email login, role-based access, password encoding, CSRF-backed forms, and error handling.
- tests for MVC controllers, REST controllers, security, validation annotations, custom annotations, validator classes, and services.

The accountability audit context already exists in two places:

- `src/main/resources/handoff/accountability-audit-conversation-mode.md`
- `src/main/resources/audit/conversation-accountability-summary-2026-05-27.md`

The current audit summary records decisions about:

- explicit `@ModelAttribute` names and DTO binding;
- REST request parameters versus path variables;
- controller helper methods and repeated model attributes;
- moving team create/join errors from controller `try/catch` blocks into validators;
- keeping prediction cutoff handling in controller/service flow because it depends on path and user context;
- direct validator injection in advice instead of `ObjectProvider`;
- MVC and REST exception advice shapes;
- REST DTOs versus entity serialization;
- Spring Security fallback routes, 403 behavior, success handlers, and email login;
- exception messages versus resource-bundle keys;
- protected constructors, entity setters, builders, and model behavior;
- team score calculation versus mutation naming;
- guard-first custom validator style.

Known current observations from the codebase:

- `SecurityConfig` now contains the `PasswordEncoder` bean and uses email as the login username parameter.
- `SecurityConfig` still imports `AuthenticationSuccessHandler`, although no such bean is present in the file.
- `TeamValidatorAdvice` directly injects `InputTeamValidator` and `InputTeamJoinValidator`, but still has an unused `ObjectProvider` import.
- `CompetitionRestController` exposes match lookup as `/api/{date}/matches` through `@GetMapping("{date}/matches")`.
- `TeamController` create and join POST handlers now use `@Valid`, `BindingResult`, model reloads, service delegation, and flash messages instead of normal-flow service exception handling.
- `CompetitionValidator` is split into smaller guard-first validation methods for teams, date scope, stadium/time conflicts, and selected stadium code.
- `Team` exposes separate score calculation and score recalculation methods.

At final verification time, `git status --short` reported only this PRD as modified.

## School Requirements

This PRD supports the project-wide EWD requirements indirectly by defining how audit answers and follow-up tasks must judge the code:

- MVC controllers should stay thin and delegate to services.
- Thymeleaf views should not contain business logic.
- services should coordinate repositories and domain decisions.
- repositories should not be called directly from controllers or views.
- request DTOs should own form input and validation annotations.
- `BindingResult` should immediately follow the validated DTO.
- custom validators should be attached through school-style `@ControllerAdvice` and `@InitBinder` where appropriate.
- user-facing validation, form, flash, and view text should use resource bundles.
- exception handling should use MVC error pages for page flows and JSON error DTOs for REST flows.
- Spring Security should preserve the settled guest, user, and admin access rules.
- REST/WebClient work should follow the local REST exercise style and stay in its separate late block unless explicitly requested.
- tests should cover MVC controllers, REST controllers, security, validation annotations, custom annotations, and validator classes.

The audit must prefer the local school examples and project guidelines over generic Spring Boot advice.

## Role And Access Decisions

- **Guest**: may access public home, public ranking, public competition detail, login/register, static resources, error pages, and public REST GET endpoints.
- **User**: may manage teams, predictions, and private scoreboards.
- **Admin**: may manage matches and official results only.
- **Forbidden**: admin must not join teams, manage user teams, view private team scoreboards, or submit predictions; users must not manage official match data; guests must not access protected user/admin flows.

The accountability audit must keep these role decisions as fixed project constraints unless the user explicitly changes them.

## User Stories

1. As a student, I want each audit answer to start with a clear verdict, so that I know whether the current code is defensible before reading the details.
2. As a student, I want audit answers to cite files from the codebase and school materials, so that I can trace every claim during defence preparation.
3. As a student, I want the assistant to explain why code was written a certain way, so that I can defend or reject that choice myself.
4. As a student, I want the assistant to separate school-conform issues from harmless style preferences, so that I do not waste time polishing low-risk code.
5. As a developer, I want suggested rewrites to match the exercise projects, so that implementation plans can be executed without introducing unfamiliar patterns.
6. As a developer, I want the audit to identify tradeoffs before changing code, so that safe passing remains more important than clever refactoring.
7. As a developer, I want normal form-validation problems, service exceptions, security decisions, REST boundaries, and DTO/model boundaries to be judged consistently, so that follow-up plans do not contradict earlier decisions.
8. As a future assistant, I want a reusable answer format and source order, so that a fresh chat can continue the audit without losing accountability.

## Implementation Decisions

Use this answer shape for accountability audit conversations:

1. Verdict first.
2. Evidence from current project files.
3. Evidence from school guidelines, notes, or exercise projects when needed.
4. Explanation of the current behavior.
5. Tradeoff or risk.
6. School-style recommendation or minimal code shape.
7. Follow-up task only when the user wants implementation or preservation.

Use this source order:

- current `call-the-match` codebase;
- `.agents/skills/project-guidelines` and its focused references;
- `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen`;
- `C:\Users\Armour\Documents\HOGENT\EWD\Notes`;
- `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij`;
- existing PRDs, plans, Ralph progress logs, handoffs, and audit summaries;
- git repository metadata.

Use this decision policy:

- Explain first when the user asks "why", "is this conform", "what is the tradeoff", or similar audit questions.
- Diagnose when the user reports a runtime error, failed test, stack trace, or broken behavior.
- Implement only when the user asks for a fix, a refactor, a plan execution step, or when the diagnosis requires a concrete correction.
- Preserve handoffs when the user asks to carry context into another chat.
- Do not revert user or previous-agent work while auditing.

Use this school-conform judgment policy:

- Prefer explicit controller model setup over hidden helpers unless reuse is clear.
- Prefer DTO annotation validation for simple field requirements.
- Prefer Spring `Validator` classes for form rules that need repositories or cross-field checks.
- Prefer custom Bean Validation annotations for reusable DTO-level or field-level validation.
- Prefer service exceptions for route, authenticated-user, or context-dependent rules that do not attach cleanly to one form field.
- Prefer REST response DTOs when entities would expose internals or create JSON loops.
- Prefer resource-bundle keys for form/view/validation text, while keeping exception messages simple and technical.
- Prefer role access that keeps admin separate from normal user team and prediction flows.

The audit should produce small follow-up items that can later become PRDs, plans, or Ralph tasks. It should not silently expand into a master refactor.

## Testing Decisions

This PRD does not require new application tests by itself because it defines an audit and documentation workflow.

When the audit leads to code changes, the relevant existing test category must verify the behavior:

- MVC controller changes should be checked with MockMvc controller tests.
- REST route or error changes should be checked with REST controller tests using `jsonPath` and mocked services.
- security access changes should be checked with security tests for guest, user, admin, login, logout, and CSRF.
- DTO annotation changes should be checked with Jakarta `Validator` tests.
- Spring `Validator` changes should be checked with `BeanPropertyBindingResult`.
- custom Bean Validation annotation changes should be checked with direct `ConstraintValidator` tests.
- broad refactors should end with full `mvn test` when time allows.

For explanation-only audit turns, verification means citing the code and school source accurately, not running tests unnecessarily.

## REST And WebClient Decisions

REST and WebClient implementation remains out of scope for this PRD.

The accountability audit may evaluate REST and WebClient choices, especially:

- route shape such as query parameter versus path variable;
- DTO versus entity response boundaries;
- REST error advice and `ErrorResponseDTO`;
- WebClient runner behavior and server availability;
- REST controller test shape.

Any REST/WebClient implementation work should remain in the separate REST/WebClient block unless the user explicitly asks to apply a specific audit finding immediately.

## Out Of Scope

- Creating a master PRD.
- Refactoring application code as part of this PRD.
- Changing role/access decisions.
- Adding new user-facing features.
- Adding new REST or WebClient endpoints.
- Rewriting tests unless a later implementation task targets them.
- Replacing the existing PRDs, plans, Ralph logs, audit summaries, or handoff files.
- Generic code review that ignores the HOGENT EWD source order.

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, project feature and role context.
2. School guidelines from `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen`, especially Spring Boot, MVC flow, i18n, validation, exceptions, security, multirow, and REST slides.
3. Lesson notes from `C:\Users\Armour\Documents\HOGENT\EWD\Notes`, especially validation, i18n/error messages, security, REST, and project notes.
4. Exercise projects from `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij`, especially EWDJ_Multirow, EWDJ_Security, EWDJ_REST, EWDJ_Spring, EWDJ_ErrorMessages, and EWDJ_SpringAndJPA.
5. Existing `call-the-match` codebase, especially controllers, services, repositories, models, DTOs, validators, advice classes, security config, REST client, templates, i18n bundles, tests, PRDs, Ralph logs, handoffs, and audit summaries.
6. Project guidelines: `.agents/skills/project-guidelines/SKILL.md` and focused references for MVC/JPA layering, validation/i18n/exceptions, security/login, REST/WebClient, and testing.
7. Handoff source: `src/main/resources/handoff/accountability-audit-conversation-mode.md`.
8. Audit summary source: `src/main/resources/audit/conversation-accountability-summary-2026-05-27.md`.
9. Git repository: `https://github.com/HeyEdis/call-the-match.git`.
10. User request from the current conversation: create a PRD from the accountability-audit markdown file in the handoff folder.

## Further Notes

- This PRD should guide conversation behavior and follow-up planning, not production behavior.
- The highest risk is premature refactoring: the audit mode must preserve the distinction between "explain this" and "change this".
- The second-highest risk is unsupported certainty: every verdict should be traceable to local code, school materials, or an explicit user decision.
- Keep future audit outputs compact enough to be useful, but concrete enough that a defence answer can be reconstructed from them.
