# Service Layer Audit And Ralph Handoff

## Purpose

This document is a reusable handoff for creating a PRD and/or Ralph loop that audits and refactors the `call-the-match` service layer.

The goal is not to rewrite the project into a generic enterprise Spring style. The goal is to make the service layer clean, school-conform, and easy to defend with evidence from the project guidelines, notes, and exercise projects.

## Scope

Audit all service classes in:

`C:\Users\Armour\Documents\HOGENT\EWD\call-the-match\src\main\java\com\example\callthematch\service`

Current service files:

- `CompetitionService.java`
- `CountryService.java`
- `MyUserDetailsService.java`
- `PredictionService.java`
- `ScoringService.java`
- `StadiumService.java`
- `TeamMemberService.java`
- `TeamService.java`
- `UserService.java`

## Required Evidence Sources

Use this source order:

1. Current codebase: `C:\Users\Armour\Documents\HOGENT\EWD\call-the-match`
2. Project guideline skill: `C:\Users\Armour\Documents\HOGENT\EWD\call-the-match\.agents\skills\project-guidelines`
3. Official guidelines: `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen`
4. Lesson notes: `C:\Users\Armour\Documents\HOGENT\EWD\Notes`
5. Exercise projects: `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij`

Important local references:

- `.agents/skills/project-guidelines/references/mvc-jpa-layering.md`
- `.agents/skills/project-guidelines/references/good-bad-examples.md`
- `Notes/03-04-2026-MySQL.md`
- `WorkspacesIntelij/EWDJ_Multirow/Spring_Boot_list_crud-opl/.../ContactService.java`
- `WorkspacesIntelij/EWDJ_REST/Spring_Boot_rest_example/.../EmployeeService.java`

## School-Conform Service Rules

Use these rules when auditing:

- Controllers call services, not repositories.
- Services are processing classes.
- Services coordinate repositories and domain decisions.
- Services throw domain exceptions when data is missing.
- Services may map entities to DTOs when controllers/views/REST should not receive entities directly.
- Repositories should handle database retrieval and query ordering where Spring Data method names can express it cleanly.
- Do not move business logic into Thymeleaf views.
- Do not introduce patterns that were not used in the course unless the project requirements force it.

## Audit Checklist Per Service

For each service, answer:

- Is this method public because a controller/other service needs it, or is it only a helper?
- Are helper methods private?
- Are repository calls only in service classes?
- Is there repeated lookup code that should be a small private helper?
- Is there repeated mapping code that should be a private `toDTO` or `toInputDTO` helper?
- Is sorting/filtering better expressed as a repository query method?
- Are exceptions domain-specific instead of generic `RuntimeException` where possible?
- Are user-facing messages kept out of services unless they are true technical exception messages?
- Is the method name clear about the use case, for example `updateOfficialResult` instead of vague `updateResult`?
- Is the service doing too much that belongs in another service or in the entity model?

## Current Starting Point

The audit started with `CompetitionService`.

Accepted direction:

- Keep separate methods for editing match setup and editing official results.
- Do not merge `update(...)` and `updateOfficialResult(...)`.
- `update(...)` changes teams, stadium, date, and time.
- `updateOfficialResult(...)` changes scores and triggers score recalculation.
- Use private lookup helpers such as `findCompetitionById`, `findCountryById`, and `findStadiumById`.
- Use repository ordering methods:
  - `findAllByOrderByDateAscTimeAsc()`
  - `findByDateOrderByTimeAsc(LocalDate date)`
- Remove service-level `Comparator` sorting when the repository can return ordered data.

## Suggested Ralph Task Breakdown

Use tiny, reviewable tasks.

1. Audit one service at a time and write findings before editing.
2. Refactor only one service per task unless two services are tightly coupled.
3. Keep public methods only when they are called outside the class.
4. Convert repeated service lookup logic to private helpers.
5. Move obvious database ordering into repository query methods.
6. Rename vague service methods only when all call sites are updated in the same task.
7. Run a targeted compile/test after each service refactor.
8. Record any unresolved guideline question in the PRD or Ralph progress log instead of guessing.

## Suggested Acceptance Criteria

A service refactor is done when:

- Controllers still call services, not repositories.
- No unrelated behavior was changed.
- Helper methods are private.
- Public service methods map to real application use cases.
- DTO conversion remains in service where needed.
- Repository query methods are used for simple database ordering/filtering.
- Existing form validation and redirect behavior still works.
- Relevant tests pass, or the failure is clearly identified as unrelated.

## Non-Goals

Do not:

- Replace the school-style service structure with a mapper framework.
- Add unnecessary abstraction layers.
- Move all DTO mapping into separate mapper classes unless the project already establishes that pattern.
- Rewrite controllers while auditing services unless a service method signature change requires a small call-site update.
- Combine different use cases just because they set fields on the same entity.

## Suggested Skills

- `project-guidelines`: Always use first for school-conform evidence.
- `diagnose`: Use when a service refactor creates a failing test, compile error, stack trace, or runtime issue.
- `write-a-prd`: Use when turning this handoff into a formal PRD.
- `ralph-init`: Use when initializing Ralph files from the final PRD/plan.
- `handoff`: Use again when preserving decisions after a service or batch of services is audited.

## Related Existing Handoffs

- `src/main/resources/handoff/accountability-audit-conversation-mode.md`
- `src/main/resources/handoff/rest-date-pathvariable-refactor.md`
- `src/main/resources/handoff/team-validator-advice-refactor.md`

## Reminder For Future Agents

Give verdicts with evidence. If a pattern is not in the guidelines or exercises, say so plainly. The user is auditing for accountability, not asking for a generic Spring cleanup.
