---
name: project-guidelines
description: Apply the EWD school conventions for the call-the-match Spring Boot project. Use when planning, implementing, reviewing, testing, or generating Ralph tasks for this project, especially for MVC/Thymeleaf, JPA/MySQL, login and Spring Security, validation/i18n, exceptions, REST/WebClient, and JUnit/MockMvc tests.
---

# Project Guidelines for call-the-match

## Purpose

Use this skill whenever you work on the `call-the-match` Spring Boot project. It keeps the implementation aligned with the HOGENT EWD school conventions, the user's notes, and the exercise projects in `WorkspacesIntelij`.

Main goal: safe passing first, polish later. For this project, treat 27 May 2026 as the real deadline.

## Required Source Order

Before making implementation or planning decisions, use this order:

1. Check the current `call-the-match` codebase.
2. Check `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen` for the official convention.
3. Check `C:\Users\Armour\Documents\HOGENT\EWD\Notes` for the user's lesson notes and project reminders.
4. Check `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij` for the concrete school-style implementation pattern.

If there is a conflict, prefer the school convention and the local exercise style over generic internet Spring Boot advice.

## Quick Workflow

1. Identify the feature area: MVC/JPA, login/security, validation/i18n, exceptions, REST/WebClient, or tests.
2. Read the relevant reference file below.
3. Inspect one matching exercise project before coding if the implementation pattern is unclear.
4. Keep the change minimal and school-conform.
5. Verify with targeted tests or, if tests cannot run, clearly state why.

## Accountability Audit Workflow

When the user asks why code was written a certain way, do not answer from memory alone. Use the audit shape from `references/audit-playbook.md`:

1. Give the verdict first.
2. Cite evidence from the current codebase, `Richtlijnen`, `Notes`, or `WorkspacesIntelij`.
3. Explain how the code works.
4. Name the tradeoff or risk.
5. Give the school-style recommendation or code shape.

If a pattern is generic Spring but not visible in the school material, say so plainly.

## Project Rules For call-the-match

- Admin is not a normal user for team or prediction flows.
- Users log in with email.
- Registered accounts get role `USER` by default.
- Admins manage matches and official results only.
- Guests can see public home, public ranking, public match detail, login/register, static resources, error pages, and public REST GET endpoints.
- Users can manage teams, predictions, and private scoreboards.
- Match date validation uses 20 May 2026 through 6 June 2026 for this project.
- Scoring constants are `exactScore=5`, `correctOutcome=2`, `uniqueExactBonus=3`, `uniqueOutcomeBonus=1` and must be in resource bundles.
- REST/WebClient is a separate late block. Tests are a late block, but validators and scoring services should be easy to unit test from the start.

## Hard Rules

- Do not put business logic in Thymeleaf views.
- Do not put repository calls directly in controllers.
- Do not use hardcoded user ids; use the authenticated principal/current user.
- Do not store plain-text passwords once security is added.
- Do not hardcode validation or user-facing form error messages in controllers/views.
- Do not make `contextLoads` the only test coverage.
- Do not skip `BindingResult` immediately after `@Valid` form DTOs.
- Do not add web patterns that were not used in the course unless the school requirements force it.
- Do not implement admin as a user who can also join teams or predict matches.

## References

Load only the reference needed for the current task:

- `references/source-map.md`: where to look in Richtlijnen, Notes, and WorkspacesIntelij.
- `references/audit-playbook.md`: answer shape and cross-cutting audit verdicts from the accountability audit.
- `references/mvc-jpa-layering.md`: controller/service/repository/model/DTO conventions.
- `references/security-login.md`: login, registration, roles, CSRF, current user, and 403.
- `references/validation-i18n-exceptions.md`: DTO validation, custom annotations, validator classes, resource bundles, type mismatch, and error pages.
- `references/rest-webclient.md`: REST controllers, REST advice, ErrorResponse, WebClient, and JSON loop notes.
- `references/testing.md`: MVC, REST, security, and validation tests in the school style.
- `references/good-bad-examples.md`: short good/bad implementation examples from the exercises and this project.

## Final Checklist

Before finishing a code task, check:

- Feature follows the correct role access for guest/user/admin.
- Controllers delegate work to services.
- Services coordinate repositories.
- Form input uses DTOs with validation.
- Field errors render in Thymeleaf with `th:errors`.
- Messages and labels that belong in bundles are in `i18n/messages.properties`.
- Security forms include CSRF tokens when needed.
- Exceptions produce school-style error pages or REST JSON errors.
- Tests cover the required category when the task is in the test phase.
- Any unfinished verification is stated clearly.
