---
name: ralph-init
description: Scaffold a Codex-native Ralph workflow for the call-the-match Spring Boot EWD project. Use when the user wants to initialize Ralph files, prompts, progress logs, or a task-loop launcher for an existing feature plan.json in `src/main/resources/plan/{feature}/plan.json`.
---

# Ralph Init

Scaffold a Codex-native Ralph workflow into the current `call-the-match` repository.

This skill is tailored to the HOGENT EWD Spring Boot project. It must follow `project-guidelines`.

Ralph here means: a repeatable Codex workflow that works through `plan.json` tasks, keeps a `progress.md` log, respects the PRD and school guidelines, and supports human-in-the-loop or AFK-style runs.

Do not copy Claude-specific behavior into this project.

## Pipeline

Use this skill as the final step:

1. `write-a-prd` creates or revises `src/main/resources/prd/{feature}-prd.md`.
2. `prd-to-plan` creates or revises `src/main/resources/prd/{feature}/plan.md`.
3. `prd-to-plan-json` creates `src/main/resources/plan/{feature}/plan.json`.
4. `ralph-init` creates the Codex Ralph runner, prompts, and progress files.

If `src/main/resources/plan/{feature}/plan.json` is missing, stop and tell the user to run `prd-to-plan-json` first. Do not create a starter plan.json.

## Artifact Layout

Create these repo files when missing:

- `scripts/ralph.ps1`
- `prompts/ralph-iteration.md`
- `prompts/ralph-iteration-hitl.md`
- `prompts/ralph-iteration-afk.md`
- `src/main/resources/ralph/{feature}/progress.md`
- `src/main/resources/ralph/{feature}/TODO.md`

Read these existing inputs:

- PRD: `src/main/resources/prd/{feature}-prd.md`
- Markdown plan: `src/main/resources/prd/{feature}/plan.md`
- JSON task plan: `src/main/resources/plan/{feature}/plan.json`
- School rules: `project-guidelines`

Do not use `docs/features` for this project.

## Modes And Flags

The generated workflow should support these concepts:

- `--feature {name}`: required when multiple feature plans exist.
- `--hitl`: one task only, then stop for user review.
- `--afk`: attempt multiple tasks without asking, but only for safe choices.
- `--tasks 3`: maximum number of incomplete tasks to attempt.
- `--commit`: optional; only commit when the user explicitly enables it.

Do not add a `--from` flag. Ralph should always choose the highest-priority incomplete task.

Default behavior without `--hitl` or `--afk`: preview only. Show feature paths, open task count, and recommended commands.

## Safety Rules

- Never overwrite existing Ralph files without asking.
- Never create or edit `plan.json` from scratch. It must come from `prd-to-plan-json`.
- Never mark `"passes": true` unless the task acceptance criteria were concretely verified.
- If verification fails, leave `"passes": false`, log the blocker in `progress.md`, and add a TODO when human input is needed.
- In `--afk`, do not guess on decisions that affect security, schema, scoring, roles, validation rules, or school requirements.
- In `--afk`, if a risky decision is blocked, skip the task, record the blocker, and move to a safer task.
- Do not automatically commit unless `--commit` is passed.
- Always check `git status --short` before code changes. The worktree may contain user changes.
- Do not revert unrelated user changes.

## Project Rules To Carry Into Prompts

Every generated prompt must instruct Codex to use:

- `project-guidelines`
- the feature PRD
- the feature `plan.md`
- the feature `plan.json`
- `progress.md`
- `TODO.md`
- current codebase context
- `git status --short`

Carry these project decisions into the prompts:

- Safe passing first, polish later.
- Deadline is 27 May 2026.
- Login uses email.
- Registered accounts get role `USER` by default.
- Admin is not a normal user and must not join teams or submit predictions.
- Admin manages matches and official results only.
- Guest, user, and admin access must remain explicit.
- Match date validation uses 20 May 2026 through 6 June 2026.
- Scoring constants belong in resource bundles.
- REST/WebClient is a separate late block unless the task explicitly belongs to that block.
- Tests are a late closure block, but new validators/scoring/services should remain easy to test.

## Verification Commands

Detect the project as Maven/Spring Boot when `pom.xml` and `mvnw.cmd` exist.

Use these commands in generated prompts:

- Targeted/full tests: `.\mvnw.cmd test`
- Full verification when needed: `.\mvnw.cmd verify`
- Specific test class: `.\mvnw.cmd -Dtest=NaamTest test`
- Manual app run when useful: `.\mvnw.cmd spring-boot:run`

Do not add TypeScript, lint, `tsc`, npm, or Claude TDD instructions unless the repo actually contains such tooling and the user asks for it.

## Process

### 1. Confirm Feature

Find existing feature plans in:

`src/main/resources/plan/*/plan.json`

If exactly one exists, use it. If multiple exist and the user did not provide `--feature`, list them and ask which feature to initialize.

If no `plan.json` exists, stop and say to run `prd-to-plan-json` first.

### 2. Inspect Existing Files

Check whether these already exist:

- `scripts/ralph.ps1`
- `prompts/ralph-iteration.md`
- `prompts/ralph-iteration-hitl.md`
- `prompts/ralph-iteration-afk.md`
- `src/main/resources/ralph/{feature}/progress.md`
- `src/main/resources/ralph/{feature}/TODO.md`

Skip valid existing files. Ask before replacing any existing file.

### 3. Create `scripts/ralph.ps1`

Create a Windows-friendly PowerShell launcher.

It should:

- accept `-Feature`, `-Hitl`, `-Afk`, `-Tasks`, and `-Commit`;
- resolve paths for PRD, plan.md, plan.json, progress.md, TODO.md;
- validate that `plan.json` exists;
- count incomplete tasks where `passes` is `false`;
- create `progress.md` and `TODO.md` if missing;
- preview the selected feature and recommended next command;
- print a ready-to-paste Codex prompt for the selected mode;
- optionally open or display the relevant prompt file path;
- not silently run destructive commands.

Prefer PowerShell parameter names over Bash-style parsing inside the script, but the user-facing examples may mention equivalent conceptual flags like `--hitl`.

Suggested PowerShell usage:

```powershell
.\scripts\ralph.ps1 -Feature 01-access-accounts-and-roles
.\scripts\ralph.ps1 -Feature 01-access-accounts-and-roles -Hitl
.\scripts\ralph.ps1 -Feature 01-access-accounts-and-roles -Afk -Tasks 3
```

Example structure:

```powershell
param(
    [string]$Feature = "",
    [switch]$Hitl,
    [switch]$Afk,
    [int]$Tasks = 1,
    [switch]$Commit
)

$ErrorActionPreference = "Stop"

if (-not $Feature) {
    $plans = Get-ChildItem -Path "src/main/resources/plan" -Filter "plan.json" -Recurse -ErrorAction SilentlyContinue
    if ($plans.Count -eq 1) {
        $Feature = $plans[0].Directory.Name
    } elseif ($plans.Count -gt 1) {
        Write-Host "Multiple feature plans found. Re-run with -Feature {name}."
        $plans | ForEach-Object { Write-Host " - $($_.Directory.Name)" }
        exit 1
    } else {
        Write-Host "No plan.json found. Run prd-to-plan-json first."
        exit 1
    }
}

$prdPath = "src/main/resources/prd/$Feature-prd.md"
$planMdPath = "src/main/resources/prd/$Feature/plan.md"
$planJsonPath = "src/main/resources/plan/$Feature/plan.json"
$ralphDir = "src/main/resources/ralph/$Feature"
$progressPath = "$ralphDir/progress.md"
$todoPath = "$ralphDir/TODO.md"

if (-not (Test-Path $planJsonPath)) {
    Write-Host "Missing plan.json: $planJsonPath"
    Write-Host "Run prd-to-plan-json before ralph-init/ralph."
    exit 1
}

New-Item -ItemType Directory -Force -Path $ralphDir | Out-Null
if (-not (Test-Path $progressPath)) {
    "# Ralph Progress Log: $Feature`n`nEach iteration appends work done, decisions, files changed, verification results, and blockers.`n`n## Entries`n" | Set-Content $progressPath
}
if (-not (Test-Path $todoPath)) {
    "# Ralph TODO: $Feature`n`nHuman review items, blockers, and deferred decisions.`n`n## Open Items`n" | Set-Content $todoPath
}

$plan = Get-Content $planJsonPath -Raw | ConvertFrom-Json
$openTasks = @($plan.tasks | Where-Object { $_.passes -eq $false } | Sort-Object priority)
$taskLimit = if ($Afk) { $Tasks } else { 1 }
$promptFile = if ($Hitl) { "prompts/ralph-iteration-hitl.md" } elseif ($Afk) { "prompts/ralph-iteration-afk.md" } else { "prompts/ralph-iteration.md" }

Write-Host "Ralph context ready"
Write-Host "Feature: $Feature"
Write-Host "PRD: $prdPath"
Write-Host "Plan.md: $planMdPath"
Write-Host "Plan.json: $planJsonPath"
Write-Host "Open tasks: $($openTasks.Count)"
Write-Host "Progress: $progressPath"
Write-Host "TODO: $todoPath"
Write-Host "Prompt: $promptFile"

Write-Host ""
Write-Host "Paste this into Codex:"
Write-Host "Use ralph-init workflow for feature '$Feature'. Mode: $(if ($Hitl) { 'HITL' } elseif ($Afk) { 'AFK' } else { 'standard preview' }). Task limit: $taskLimit. Read @$prdPath @$planMdPath @$planJsonPath @$progressPath @$todoPath and @$promptFile, follow project-guidelines, then work the highest-priority incomplete task(s). Commit only if Commit=$Commit."
```

### 4. Create Standard Prompt

Create `prompts/ralph-iteration.md`.

It must instruct Codex to:

1. Read `project-guidelines`.
2. Read the feature PRD, `plan.md`, `plan.json`, `progress.md`, and `TODO.md`.
3. Check `git status --short`.
4. Pick the highest-priority incomplete task where `passes` is `false`.
5. Explore relevant code before editing.
6. Implement only one task per iteration unless the user explicitly requested more.
7. Keep changes school-conform and minimal.
8. Run relevant verification commands.
9. Update `progress.md` with work done, decisions, files changed, and verification result.
10. Set `"passes": true` only when acceptance criteria are verified.
11. Stop with a concise status.

Quality guidance:

- Keep changes small and focused.
- Prefer existing codebase patterns and exercise-project patterns.
- Controllers delegate to services.
- Services coordinate repositories.
- Form input uses DTOs and validation.
- User-facing messages belong in resource bundles.
- Security decisions must preserve guest/user/admin boundaries.
- Do not implement REST/WebClient early unless the task is part of that late block.

Example prompt shape:

```markdown
# Ralph Iteration: call-the-match

You are Codex working inside the `call-the-match` Spring Boot EWD project.

## Context To Read First

1. Use the `project-guidelines` skill.
2. Read the feature PRD, feature `plan.md`, `plan.json`, `progress.md`, and `TODO.md`.
3. Check `git status --short`.
4. Inspect the relevant code before editing.

## Task Selection

Pick the highest-priority incomplete task from `plan.json` where `"passes": false`.
Work on one task only unless the user explicitly requested multiple tasks.

## Implementation Rules

- Follow the current codebase style and school exercise patterns.
- Keep controllers thin and delegate to services.
- Keep repository access out of controllers.
- Use DTO validation and `BindingResult` for form flows.
- Use resource bundles for validation/user-facing messages.
- Preserve guest/user/admin boundaries.
- Do not implement REST/WebClient early unless this task belongs to that late block.

## Verification

Run the smallest meaningful verification first.
Preferred commands:

- `.\mvnw.cmd test`
- `.\mvnw.cmd verify`
- `.\mvnw.cmd -Dtest=NaamTest test`
- `.\mvnw.cmd spring-boot:run` when manual MVC verification is useful

Only set `"passes": true` when the acceptance criteria are concretely verified.

## Logging

Append to `progress.md`:

- task id/title;
- what changed;
- decisions made;
- files changed;
- verification commands and results;
- blockers or TODOs.

Stop with a concise status summary.
```

### 5. Create HITL Prompt

Create `prompts/ralph-iteration-hitl.md`.

It should be the standard prompt plus:

- perform one task only;
- after verification and progress updates, stop for user review;
- do not commit unless `-Commit` is enabled;
- clearly state what the user should review.

Example HITL addition:

```markdown
## HITL Review Phase

After completing one task:

1. Update `progress.md`.
2. Update `plan.json` only if all acceptance criteria were verified.
3. Do not continue to the next task.
4. Do not commit unless commit mode was explicitly enabled.
5. Tell the user:

Task complete. Ready for review.

Include:

- changed files;
- verification performed;
- remaining risks;
- whether `passes` was updated.

If the user gives review feedback, fix that feedback first before selecting another task.
```

### 6. Create AFK Prompt

Create `prompts/ralph-iteration-afk.md`.

It should be the standard prompt plus:

- never block on low-risk decisions that can be inferred from codebase, PRD, plan, or school guidelines;
- do not guess on security, schema, scoring, roles, validation rules, or school requirements;
- when risky information is missing, log a blocker in `progress.md`, add a TODO, skip that task, and choose another safe incomplete task;
- stop after the requested task count or when no safe incomplete tasks remain.

Example AFK addition:

```markdown
## AFK Mode

You are running unattended.

Do not ask questions for low-risk choices that can be inferred from:

- current codebase;
- PRD;
- plan.md;
- plan.json acceptance criteria;
- progress.md/TODO.md;
- project-guidelines;
- local notes/richtlijnen/exercise patterns when already available.

Do not guess on:

- role/security behavior;
- schema-breaking changes;
- scoring rules;
- match date validation rules;
- admin/user/guest boundaries;
- school-required validation or test obligations.

When blocked:

1. Leave the task `"passes": false`.
2. Append the blocker to `progress.md`.
3. Add a concrete item to `TODO.md`.
4. Move to the next safe incomplete task.

Stop after the requested task count or when no safe task remains.
```

### 7. Create Progress Files

Create `src/main/resources/ralph/{feature}/progress.md`:

```markdown
# Ralph Progress Log: {feature}

Each iteration appends what was done, decisions made, files changed, verification results, and blockers.

## Entries
```

Create `src/main/resources/ralph/{feature}/TODO.md`:

```markdown
# Ralph TODO: {feature}

Human review items, blockers, and deferred decisions.

## Open Items
```

### 8. Optional Loop Prompts

Only create these when the user asks for them. Keep them Spring Boot/EWD specific.

Test coverage loop: `prompts/ralph-coverage.md`

```markdown
# Ralph Coverage Loop

Find missing or weak tests for the selected feature.

Prioritize school-required categories:

- MVC controller tests
- REST controller tests
- security tests
- validation annotation tests
- custom validator class tests

Write one focused test slice at a time.
Run `.\mvnw.cmd test` after each slice.
Update `progress.md` with what was covered and what remains.
```

Validation loop: `prompts/ralph-validation.md`

```markdown
# Ralph Validation Loop

Review DTO validation, custom annotations, validator classes, and i18n messages for the selected feature.

Fix one validation gap at a time.
Verify with targeted tests when available, otherwise with `.\mvnw.cmd test`.
Update `progress.md` and leave TODOs for any ambiguous school-rule decisions.
```

Security loop: `prompts/ralph-security.md`

```markdown
# Ralph Security Loop

Review guest/user/admin route access for the selected feature.

Do not change role semantics without explicit user approval.
Admin must not enter user team/prediction flows.

Add or update security tests where this belongs to the test phase.
Run `.\mvnw.cmd test`.
Update `progress.md`.
```

### 9. Final Summary

After scaffolding, summarize:

- selected feature;
- PRD path;
- plan.md path;
- plan.json path and incomplete task count;
- progress/TODO paths;
- prompt paths;
- exact recommended commands.

Always recommend first run as HITL:

```powershell
.\scripts\ralph.ps1 -Feature {feature} -Hitl
```

Then AFK only when the user is confident:

```powershell
.\scripts\ralph.ps1 -Feature {feature} -Afk -Tasks {count}
```

Do not start the first Ralph run automatically. Ask the user before running it.

Example summary:

```text
Ralph workflow is ready.

Feature: {feature}
PRD: src/main/resources/prd/{feature}-prd.md
Plan: src/main/resources/prd/{feature}/plan.md
Plan JSON: src/main/resources/plan/{feature}/plan.json ({open-task-count} open tasks)
Progress: src/main/resources/ralph/{feature}/progress.md
TODO: src/main/resources/ralph/{feature}/TODO.md

Recommended first run:

.\scripts\ralph.ps1 -Feature {feature} -Hitl

When you trust the setup:

.\scripts\ralph.ps1 -Feature {feature} -Afk -Tasks {suggested-count}

Notes:

- AFK mode skips risky decisions and logs blockers instead of guessing.
- Commits happen only with -Commit.
- Check TODO.md after AFK runs.
- Delete or archive progress.md when the sprint is done if you want a clean history.
```
