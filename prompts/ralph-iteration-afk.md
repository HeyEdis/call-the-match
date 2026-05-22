# Ralph Iteration AFK: call-the-match

You are Codex working inside the `call-the-match` Spring Boot EWD project in AFK mode.

## Context To Read First

1. Use the `ewd-schoolrichtlijnen` skill.
2. Read the selected feature PRD, feature `plan.md`, `plan.json`, `progress.md`, and `TODO.md`.
3. Check `git status --short`.
4. Inspect the relevant current code before editing.

## Task Selection

Work the highest-priority safe incomplete tasks from `plan.json` where `"passes": false`.
Stop after the requested task count or when no safe incomplete task remains.

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

- Keep changes small and focused per selected task.
- Follow current codebase style and school exercise patterns.
- Keep controllers thin and delegate to services.
- Keep repository access out of controllers.
- Use DTO validation and `BindingResult` for form flows.
- Use resource bundles for validation and user-facing messages.
- Preserve guest, user and admin boundaries.
- Do not implement REST/WebClient early unless the selected task belongs to that late block.
- Do not revert unrelated user changes.

## AFK Mode

Do not ask questions for low-risk choices that can be inferred from:

- current codebase;
- PRD;
- `plan.md`;
- `plan.json` acceptance criteria;
- `progress.md` and `TODO.md`;
- `ewd-schoolrichtlijnen`;
- local notes, richtlijnen and exercise patterns already available.

Do not guess on:

- role or security behavior;
- schema-breaking changes;
- scoring rules;
- match date validation rules;
- admin, user and guest boundaries;
- school-required validation or test obligations.

When blocked:

1. Leave the task `"passes": false`.
2. Append the blocker to `progress.md`.
3. Add a concrete item to `TODO.md`.
4. Move to the next safe incomplete task when possible.

## Verification

Run the smallest meaningful verification first.
Preferred commands:

- `.\mvnw.cmd test`
- `.\mvnw.cmd verify`
- `.\mvnw.cmd -Dtest=NaamTest test`
- `.\mvnw.cmd spring-boot:run` when manual MVC verification is useful

Only set `"passes": true` when task acceptance criteria are concretely verified.

## Logging

Append to `progress.md` for every attempted task:

- task id and title;
- what changed;
- decisions made;
- files changed;
- verification commands and results;
- blockers or TODOs.

Stop with a concise status summary and name any skipped risky tasks.
