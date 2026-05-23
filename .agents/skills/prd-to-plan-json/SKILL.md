---
name: prd-to-plan-json
description: Turn a project PRD or feature plan into a plan.json file for the Ralph autonomous coding loop in this Spring Boot school project. Breaks work into prioritized, verifiable vertical-slice tasks. Outputs all generated JSON plans under src/main/resources/plan/<feature>/plan.json.
allowed_tools: Read, Write, Edit, Glob, Grep, Bash
---

# PRD to plan.json

Convert a PRD or an existing feature `plan.md` into a Ralph-compatible `plan.json` for this Spring Boot school project. The skill is tailored to the `call-the-match` codebase and its planning convention.

Generated `plan.json` files are **not** colocated with the PRD. They are written under:

`src/main/resources/plan/<feature-name>/plan.json`

The PRD and Markdown plans remain under:

`src/main/resources/prd/`

## Project Context

Before drafting tasks for call-the-match, use the project-guidelines skill. Its school conventions are mandatory context for task descriptions and acceptance criteria.

This skill is meant for a Java Spring Boot application using:

- Spring MVC controllers and Thymeleaf views
- JPA repositories and MySQL
- Service classes as the coordination layer between controllers and repositories
- Request/response DTOs
- Jakarta Validation
- Spring Security
- Resource bundles under `src/main/resources/i18n`
- JUnit, MockMvc, REST controller tests, security tests, and validation tests

Do not assume frontend conventions such as Figma, `.dtl.ts`, data-test-label files, or Playwright e2e task files unless the user explicitly asks for them in a later request.

## Process

### 1. Confirm the Source Document Is in Context

Prefer existing feature `plan.md` files when available, because they already contain the user-approved tracer-bullet breakdown.

Search in this order:

1. `src/main/resources/prd/<feature-name>/plan.md`
2. `src/main/resources/prd/*-prd.md`
3. `src/main/resources/prd/world-cup-team-prediction-prd.md`
4. Any PRD or plan path explicitly provided by the user

If multiple feature plans exist and the user did not specify one, list them and ask which one to convert.

Do **not** generate a `plan.json` for the master PRD unless the user explicitly asks for a master JSON plan. The master PRD is context and source-of-truth, while feature folders are the executable planning units.

### 2. Explore the Codebase

Before drafting tasks, inspect enough of the codebase to understand the current architecture and existing implementation state.

For this repo, check relevant areas such as:

- `pom.xml`
- `src/main/java/**/controller`
- `src/main/java/**/service`
- `src/main/java/**/repository`
- `src/main/java/**/model`
- `src/main/java/**/dto`
- `src/main/resources/templates`
- `src/main/resources/i18n`
- `src/test/java`
- `src/main/resources/prd`

Also run `git status --short` before writing. The worktree may be dirty. Do not modify application code while running this skill; only create or append planning JSON files under `src/main/resources/plan` unless the user explicitly asks otherwise.

### 3. Draft Ralph Tasks

Break the source plan or PRD into concrete, implementable Ralph tasks. Each task should be a narrow vertical slice that is independently verifiable.

<task-rules>
- Each task delivers a narrow but complete path through every relevant layer for that behavior.
- A completed task must be demoable or verifiable on its own through acceptance criteria.
- Prefer many small tasks over few large ones.
- A task should usually be about 1 to 3 hours of focused work for one autonomous coding pass.
- Tasks should be ordered so foundational work comes first.
- Later tasks can depend on earlier tasks, but should still be as independent as practical.
- Acceptance criteria must be concrete and testable.
- Avoid vague criteria such as "works correctly" or "UI looks good".
- Use project language and school concepts: Spring Security, MVC controller, service layer, repository, DTO, Thymeleaf view, resource bundle, validator, custom annotation, MockMvc, REST controller, WebClient.
- For UI tasks with data controls such as selects, tables, lists, or dropdowns, acceptance criteria must prove real data flow. Example: "The stadium select renders stadium options loaded from the database" is acceptable; "The dropdown is visible" is not enough.
- If a task touches validation, mention whether it uses existing Jakarta annotations, a custom annotation, or a validator class.
- If a task touches security, mention the expected guest/user/admin behavior.
- If a task touches tests, map it to the school-required test categories: MVC controllers, REST controllers, security, validation for existing annotations, custom annotations, and validator classes.
- If the source contains a Sources section, carry it into the top-level `sources` array.
- If sources are missing the Git repository URL, identify it with `git remote -v` and add it.
</task-rules>

### 4. Quiz the User Before Writing

Present the proposed task breakdown as a numbered list before writing or updating `plan.json`.

For each task show:

- **Title**: short descriptive name
- **What it covers**: one or two sentences
- **Priority**: suggested order

Ask the user:

- Does the granularity feel right?
- Should any tasks be merged or split?
- Is the priority order correct?

Iterate until the user approves.

### 5. Write plan.json

Determine the feature name from the source:

- If using `src/main/resources/prd/<feature-name>/plan.md`, use that folder name as `<feature-name>`.
- If using `src/main/resources/prd/<feature-name>-prd.md`, derive a kebab-case feature name from the file name.
- If the source path is ambiguous, ask the user to confirm the feature name.

Write the JSON to:

`src/main/resources/plan/<feature-name>/plan.json`

Create the directory if it does not exist.

Use this exact structure:

```json
{
  "sources": [
    {
      "url": "https://example.com/thread/123",
      "description": "Original feature request thread"
    }
  ],
  "tasks": [
    {
      "id": "short-kebab-case-id",
      "title": "Short descriptive title",
      "description": "What needs to be done and why. Be specific enough that an AI agent can implement this without further clarification.",
      "acceptance_criteria": [
        "Specific, testable criterion 1",
        "Specific, testable criterion 2"
      ],
      "priority": 1,
      "passes": false
    }
  ]
}
```

Rules for the JSON:

- `sources`: array of source objects copied from the PRD/plan context.
- `tasks`: ordered array of tasks.
- `id`: unique kebab-case identifier.
- `title`: short descriptive title.
- `description`: detailed enough for Ralph or another autonomous agent to implement without ambiguity.
- `acceptance_criteria`: concrete and verifiable.
- `priority`: integer; `1` is highest priority and should be done first.
- `passes`: always `false` for new tasks. Ralph marks tasks true when complete.
- Tasks must be ordered by priority.
- Do not add extra fields unless the user explicitly confirms Ralph supports them.

### 6. Existing plan.json Rule

When `src/main/resources/plan/<feature-name>/plan.json` already exists:

- Never edit existing tasks.
- Never change existing `passes` values.
- Never rewrite task descriptions or acceptance criteria.
- Never remove tasks.
- Append new tasks only.
- Set new task priorities to continue from the highest existing priority number.
- If a task marked `passes: true` is later found incomplete, create a new follow-up task for the gap instead of flipping it back to `false`.

### 7. No e2e Sync

Do not create, update, or inspect `e2e.json` as part of this skill.

This project's required verification is based on school-required Java/Spring tests:

- MVC controller tests
- REST controller tests
- Security tests
- Validation tests for existing annotations, custom annotations, and validator classes

Only discuss e2e testing if the user explicitly asks for it in a separate request.

