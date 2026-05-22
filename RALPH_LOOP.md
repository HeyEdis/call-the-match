# Ralph Loop Usage

This project uses a Codex-native Ralph workflow to work through feature tasks from `plan.json`.

Ralph does not replace the PRD and planning steps. It starts after the feature has a reviewed PRD, a Markdown plan, and a JSON task plan.

## Project Workflow

Use the skills in this order:

1. `write-a-prd`
2. `prd-to-plan`
3. `prd-to-plan-json`
4. `ralph-init`

The input files for a feature are:

```text
src/main/resources/prd/{feature}-prd.md
src/main/resources/prd/{feature}/plan.md
src/main/resources/plan/{feature}/plan.json
```

The Ralph runtime files are created by `ralph-init`:

```text
scripts/ralph.ps1
prompts/ralph-iteration.md
prompts/ralph-iteration-hitl.md
prompts/ralph-iteration-afk.md
src/main/resources/ralph/{feature}/progress.md
src/main/resources/ralph/{feature}/TODO.md
```

If `plan.json` does not exist yet, do not start Ralph. Run `prd-to-plan-json` first.

## Current Feature Example

The first feature plan currently available in this repo is:

```text
src/main/resources/plan/01-access-accounts-and-roles/plan.json
```

Its feature name is:

```text
01-access-accounts-and-roles
```

## Initialize Ralph

Ask Codex to run the `ralph-init` skill for the selected feature.

Example prompt in Codex:

```text
Gebruik ralph-init voor feature 01-access-accounts-and-roles.
Maak de Ralph scaffold en volg ewd-schoolrichtlijnen.
```

After initialization, check that the script, prompts, progress log, and TODO file exist.

## Flags

The generated PowerShell script uses PowerShell parameters.

| Parameter | Meaning |
| --- | --- |
| `-Feature {name}` | Select the feature/story whose `plan.json` should be used. |
| `-Hitl` | Human-in-the-loop mode. Work one task and stop for review. |
| `-Afk` | AFK mode. Attempt several safe tasks without stopping for every decision. |
| `-Tasks {count}` | Maximum number of incomplete tasks to attempt in AFK mode. |
| `-Commit` | Allow commits during the Ralph run. Without it, do not commit automatically. |

There is deliberately no `-From` flag. Ralph should always choose the highest-priority incomplete task where `"passes": false`.

## Preview The Feature

Run the script without HITL or AFK mode to preview the selected feature and task state:

```powershell
.\scripts\ralph.ps1 -Feature 01-access-accounts-and-roles
```

Preview mode should show:

- the feature name;
- PRD path;
- Markdown plan path;
- JSON plan path;
- number of open tasks;
- progress and TODO paths;
- recommended next command.

## Recommended First Run: HITL

Start with one supervised task:

```powershell
.\scripts\ralph.ps1 -Feature 01-access-accounts-and-roles -Hitl
```

HITL mode should:

1. use `ewd-schoolrichtlijnen`;
2. read the feature PRD, `plan.md`, `plan.json`, `progress.md`, and `TODO.md`;
3. check the current codebase and `git status`;
4. pick the highest-priority incomplete task;
5. implement only that task;
6. run meaningful verification;
7. update `progress.md`;
8. set `"passes": true` only when the acceptance criteria are verified;
9. stop for your review.

Use HITL while the Ralph prompts and task sizing are still being proven.

## AFK Run

Once the first HITL run behaves well, allow Ralph to attempt several safe tasks:

```powershell
.\scripts\ralph.ps1 -Feature 01-access-accounts-and-roles -Afk -Tasks 3
```

AFK mode may continue through low-risk decisions that can be derived from:

- the codebase;
- the PRD;
- `plan.md`;
- `plan.json`;
- `progress.md`;
- `TODO.md`;
- `ewd-schoolrichtlijnen`;
- the school notes, richtlijnen, and exercise patterns.

AFK mode must not guess on:

- guest/user/admin security rules;
- schema-breaking changes;
- scoring rules;
- match date validation rules;
- school requirements;
- unclear validation behavior.

When AFK mode is blocked, it should keep the task incomplete, log the blocker in `progress.md`, add a concrete item to `TODO.md`, and move to another safe task if possible.

## Commit Behavior

Ralph should not commit by default.

Allow commits only when you explicitly pass `-Commit`:

```powershell
.\scripts\ralph.ps1 -Feature 01-access-accounts-and-roles -Hitl -Commit
```

AFK example with commit permission:

```powershell
.\scripts\ralph.ps1 -Feature 01-access-accounts-and-roles -Afk -Tasks 2 -Commit
```

Do not use commit mode while you are still validating whether a Ralph task was sliced correctly.

## Verification

Ralph should use the Maven/Spring Boot feedback loop that fits the task.

Common commands:

```powershell
.\mvnw.cmd test
.\mvnw.cmd verify
.\mvnw.cmd -Dtest=NaamTest test
.\mvnw.cmd spring-boot:run
```

`"passes": true` in `plan.json` means the task acceptance criteria were actually verified. It should not be used as a guess or progress estimate.

## Progress Files

Read these after each run:

```text
src/main/resources/ralph/{feature}/progress.md
src/main/resources/ralph/{feature}/TODO.md
```

`progress.md` should record:

- task id and title;
- what changed;
- decisions made;
- files changed;
- verification commands and results;
- blockers.

`TODO.md` should record:

- human review items;
- deferred decisions;
- blockers that Ralph must not guess through.

## Concrete Session Examples

### 1. First security/auth task

```powershell
.\scripts\ralph.ps1 -Feature 01-access-accounts-and-roles -Hitl
```

Use this when you want to watch the first task closely.

### 2. Continue two safe access tasks

```powershell
.\scripts\ralph.ps1 -Feature 01-access-accounts-and-roles -Afk -Tasks 2
```

Use this only after the HITL run showed that the prompt and plan are behaving well.

### 3. Work a future feature

```powershell
.\scripts\ralph.ps1 -Feature 03-team-management -Hitl
```

This only works after this file exists:

```text
src/main/resources/plan/03-team-management/plan.json
```

### 4. Ask Codex directly when you do not want to run PowerShell yet

```text
Use the Ralph HITL workflow for feature 01-access-accounts-and-roles.
Read the PRD, plan.md, plan.json, progress.md, TODO.md and ewd-schoolrichtlijnen.
Work only the highest-priority incomplete task and stop for review.
```

## School Project Rules

Ralph runs must keep these rules visible:

- safe passing first, polish later;
- deadline is 27 May 2026;
- login uses email;
- registered accounts get role `USER` by default;
- admin does not join teams and does not submit predictions;
- admin manages matches and official results only;
- guest/user/admin route access stays explicit;
- REST/WebClient remains a late separate block unless the selected task belongs to it;
- tests are a late closure block, but validators and scoring logic should remain testable.

