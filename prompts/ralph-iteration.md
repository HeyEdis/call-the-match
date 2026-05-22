# Ralph Iteration: call-the-match

You are Codex working inside the `call-the-match` Spring Boot EWD project.

## Context To Read First

1. Use the `ewd-schoolrichtlijnen` skill.
2. Read the selected feature PRD, feature `plan.md`, `plan.json`, `progress.md`, and `TODO.md`.
3. Check `git status --short`.
4. Inspect the relevant current code before editing.

## Task Selection

Pick the highest-priority incomplete task from `plan.json` where `"passes": false`.
Work on one task only unless the user explicitly requested multiple tasks.

## Project Decisions

- Safe passing first, polish later.
- Deadline is 27 May 2026.
- Login uses email.
- Registered accounts get role `USER` by default.
- Admin is not a normal user and must not join teams or submit predictions.
- Admin manages matches and official results only.
- Guest, user and admin access must remain explicit.
- Match date validation uses 20 May 2026 through 6 June 2026.
- Scoring constants belong in resource bundles.
- REST/WebClient is a separate late block unless the selected task belongs to it.
- Tests are a late closure block, but new validators, scoring logic and services should remain easy to test.

## Implementation Rules

- Keep changes small and focused on the selected task.
- Follow current codebase style and school exercise patterns.
- Keep controllers thin and delegate to services.
- Keep repository access out of controllers.
- Use DTO validation and `BindingResult` for form flows.
- Use resource bundles for validation and user-facing messages.
- Preserve guest, user and admin boundaries.
- Do not implement REST/WebClient early unless this task belongs to that late block.
- Do not revert unrelated user changes.

## Verification

Run the smallest meaningful verification first.
Preferred commands:

- `.\mvnw.cmd test`
- `.\mvnw.cmd verify`
- `.\mvnw.cmd -Dtest=NaamTest test`
- `.\mvnw.cmd spring-boot:run` when manual MVC verification is useful

Only set `"passes": true` when the task acceptance criteria are concretely verified.

## Logging

Append to `progress.md`:

- task id and title;
- what changed;
- decisions made;
- files changed;
- verification commands and results;
- blockers or TODOs.

If verification fails, leave `"passes": false`, record the blocker and add a concrete TODO when human input is needed.

Stop with a concise status summary.
