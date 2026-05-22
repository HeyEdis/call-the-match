---
name: prd-to-plan
description: Turn a call-the-match PRD into a school-conform multi-phase Markdown implementation plan using tracer-bullet vertical slices. Use when the user wants to break down PRDs from src/main/resources/prd into feature plans, create or revise plan.md files, plan phases, or mentions tracer bullets for this Spring Boot EWD project.
---

# PRD to Plan

Break a `call-the-match` PRD into a phased Markdown implementation plan using vertical slices (tracer bullets). This skill is tailored to the local Spring Boot school project and must follow `project-guidelines`.

Generated plans are written to:

`src/main/resources/prd/<feature-name>/plan.md`

Do not use `docs/features` for this project.

## Required Context

Before drafting or updating a plan, use the `project-guidelines` skill. Its rules are mandatory for this project.

Carry these durable project decisions into plans when relevant:

- Safe passing first, polish later.
- Real planning deadline: 27 May 2026.
- Login uses email.
- Admin is not a normal user and must not use team or prediction flows.
- Admin manages matches and official results only.
- Guest, user, and admin route access must remain explicit.
- Match validation period is 20 May 2026 through 6 June 2026.
- REST/WebClient is a separate late block.
- Tests are late closure blocks, but validators and scoring services should be designed testably from the start.

## Process

### 1. Confirm the PRD or Feature Source

Search in this order:

1. User-provided PRD or plan path.
2. `src/main/resources/prd/<feature-name>/plan.md` if revising an existing feature plan.
3. `src/main/resources/prd/*-prd.md` for feature PRDs.
4. `src/main/resources/prd/world-cup-team-prediction-prd.md` as master context only.

Do not convert the master PRD to one big master plan unless the user explicitly asks for a master plan.

If multiple feature PRDs or plans match and the user did not specify one, list them and ask which feature to plan.

### 2. Explore the Codebase

Inspect enough of the current repo to understand what already exists before slicing.

For this project, check relevant files/directories:

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

Also check `git status --short` before writing. The worktree may be dirty; do not alter application code while running this skill.

### 3. Identify Durable Architectural Decisions

Before slicing, identify decisions that should remain stable across phases:

- **Routes**: public, user-only, and admin-only URL patterns.
- **Schema**: key tables/entities and relationship shape.
- **Key models**: domain models and DTO concepts.
- **Security**: guest/user/admin access, email login, CSRF, 403 handling.
- **Validation**: DTO annotations, custom annotations, validator classes, resource bundles.
- **REST/WebClient boundaries**: which REST endpoints and client/demo behavior belong to this feature or to the separate late REST block.
- **Testing boundary**: which school-required test category will later verify the behavior.

These decisions go in the plan header.

### 4. Draft Vertical Slices

Break the PRD into tracer-bullet phases. A phase is a thin, complete path through the relevant layers for a behavior. Do not create horizontal phases like "make all repositories" or "make all controllers".

<vertical-slice-rules>
- Each slice delivers a narrow but complete, demoable behavior.
- Each slice should mention every relevant school layer: MVC/Thymeleaf, service, repository/JPA, DTO/validation, security, resource bundles, tests, REST/WebClient when applicable.
- Prefer many thin phases over a few large phases.
- Include durable route paths, schema shapes, and model names when useful.
- Avoid fragile implementation detail such as exact method names, line numbers, or overly specific file edits.
- If a phase includes a data-driven UI control such as a select, list, or table, its acceptance criteria must verify actual data flow from the backend/database.
- If a phase touches validation, acceptance criteria must state whether it uses existing Jakarta annotations, a custom annotation, or a validator class.
- If a phase touches security, acceptance criteria must state expected guest/user/admin behavior.
- If a phase touches tests, acceptance criteria must map to the required school categories: MVC controller, REST controller, security, validation for existing annotations, custom annotation, and validator class.
- Keep REST/WebClient as a separate late phase unless the user explicitly asks to interleave it.
- Keep tests as late closure phases unless the feature is a small validator/scoring module where testability must be designed immediately.
- For good/bad examples, refer to `project-guidelines/references/good-bad-examples.md` instead of duplicating large examples here.
- If the PRD contains a Sources section, carry those references into the plan.
- If sources lack the Git repository URL, identify it with `git remote -v` and add it.
</vertical-slice-rules>

### 5. Quiz the User

Before writing or replacing any plan file, present the proposed breakdown as a numbered list.

For each phase show:

- **Title**: short descriptive name.
- **User stories covered**: which stories from the PRD this addresses.
- **What it covers**: one or two sentences.

Ask:

- Does the granularity feel right?
- Should any phases be merged or split?
- Is the order correct?

Iterate until the user approves.

### 6. Write or Update plan.md

Determine the feature directory:

- If the source is `src/main/resources/prd/<feature-name>/plan.md`, update that file only after user approval.
- If the source is `src/main/resources/prd/<feature-name>-prd.md`, write to `src/main/resources/prd/<feature-name>/plan.md`.
- If the source path is ambiguous, ask the user to confirm the feature name.

If `plan.md` already exists, do not overwrite automatically. Ask whether to:

- leave it as-is,
- overwrite it,
- or update it with a revised version.

Use this template.

<plan-template>
# Plan: {Feature Name}

> Source PRD: `{source-prd-path}`

## Sources

Carry over the full Sources section from the PRD. Include Git repository URL from `git remote -v` if missing.

## Architectural Decisions

Durable decisions that apply across all phases:

- **Routes**: ...
- **Schema**: ...
- **Key models**: ...
- **Security**: ...
- **Validation/i18n**: ...
- **REST/WebClient**: ...
- **Testing**: ...

---

## Phase 1: {Phase Title}

**User stories**: `{user-story-numbers-or-summary}`

### What To Build

A concise description of this vertical slice. Describe the end-to-end behavior, not only one layer.

### Acceptance Criteria

- [ ] Criterion 1
- [ ] Criterion 2
- [ ] Criterion 3

---

## Phase 2: {Phase Title}

**User stories**: `{user-story-numbers-or-summary}`

### What To Build

...

### Acceptance Criteria

- [ ] ...

<!-- Repeat for each phase -->
</plan-template>

## Filled Example For This Project

Use this as a shape example. Do not copy it blindly; adapt it to the selected feature PRD.

```markdown
# Plan: Access Accounts And Roles

> Source PRD: `src/main/resources/prd/01-access-accounts-and-roles-prd.md`

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, pages 2-5: role and access requirements.
2. `24-04-26-Security.md`: custom login, CSRF, principal usage, role display, and 403 handling.
3. `EWDJ_Security/Spring_Boot_security_JPA`: JPA-backed security example.
4. Git repository: https://github.com/HeyEdis/call-the-match.git.

## Architectural Decisions

Durable decisions that apply across all phases:

- **Routes**: public `/home`, `/ranking`, `/competition/{id}`, `/login`, `/register`; user-only `/team/**`, `/predictions/**`; admin-only match management routes.
- **Schema**: `User` keeps email, encoded password, and `Role`; team/prediction flows resolve the current authenticated user.
- **Key models**: `User`, `Role`, and later `TeamMember` for user-owned flows.
- **Security**: email login, custom login page, CSRF, logout, 403 page, database-backed `UserDetailsService`.
- **Validation/i18n**: registration DTO validates email/password/confirm password and uses resource bundle messages.
- **REST/WebClient**: public REST GET endpoints must remain permitted; REST implementation itself is a later block.
- **Testing**: late security tests cover guest/user/admin route access and login/logout behavior.

---

## Phase 1: Security Dependency And Encoded Users

**User stories**: 3, 4, 6, 12, 14

### What To Build

Add Spring Security and make existing users usable for authentication with email lookup, encoded passwords, and role-to-authority conversion.

### Acceptance Criteria

- [ ] Spring Security dependency is present.
- [ ] Seeded passwords are encoded with BCrypt.
- [ ] Users can be loaded by email.
- [ ] Domain roles convert to `ROLE_USER` or `ROLE_ADMIN` authorities.
```
