---
name: write-a-prd
description: Create or revise a school-conform PRD for the call-the-match Spring Boot EWD project, using the codebase, FIFA assignment, school guidelines, notes, and exercise projects. Store feature PRDs as `src/main/resources/prd/{feature-name}-prd.md`. Use when the user wants to write, regenerate, or improve a PRD for this project.
---

# Write A PRD

Create a Product Requirements Document for the `call-the-match` Spring Boot school project.

This skill is tailored to the HOGENT EWD project workflow. It must follow `project-guidelines` and prioritize safe passing before polish.

Generated feature PRDs are written to:

`src/main/resources/prd/{feature-name}-prd.md`

Do not use `docs/features` for this project.

Create or revise one feature PRD at a time. Only create a master PRD when the user explicitly asks for a master PRD.

## Required Context

Before writing or revising a PRD, use the `project-guidelines` skill. Its rules are mandatory for this project.

Carry these durable project decisions into PRDs when relevant:

- Safe passing first, polish later.
- Real project deadline: 27 May 2026. Ignore the 23 May deadline in the FIFA PDF.
- Defence period/context: 20 May 2026 through 6 June 2026 may be used for planning context.
- Login uses email.
- Registered accounts get role `USER` by default.
- Admin is not a normal user and must not join teams or submit predictions.
- Admin manages matches and official results only.
- Guests can access public home, public ranking, public competition/match detail, login/register, static resources, error pages, and public REST GET endpoints.
- Users can manage teams, predictions, and private scoreboards.
- Match date validation uses 20 May 2026 through 6 June 2026.
- Scoring constants are `exactScore=5`, `correctOutcome=2`, `uniqueExactBonus=3`, `uniqueOutcomeBonus=1` and must be in resource bundles.
- REST/WebClient is a separate late block unless the user explicitly asks to include it in the current feature.
- Tests are a late closure block, but validators and scoring services should be designed so they are easy to test.
- Required final test categories are MVC controllers, REST controllers, security, validation annotations, custom annotations, and validator classes.

## Source Order

Use this source order before asking the user to guess:

1. Existing conversation context and user answers.
2. Current `call-the-match` codebase.
3. `C:\Users\Armour\Documents\HOGENT\EWD\FIFA_World_Cup_2026_-_Team_Prediction.pdf`.
4. `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen`.
5. `C:\Users\Armour\Documents\HOGENT\EWD\Notes`.
6. `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij`.
7. Git repository metadata from `git remote -v`.

If there is a conflict, prefer the school guidelines and local exercise style over generic Spring Boot advice.

## Process

### 1. Confirm Feature Scope

Identify which feature PRD the user wants.

Default to one feature PRD per project part, for example:

- `01-access-accounts-and-roles-prd.md`
- `02-homepage-and-public-ranking-prd.md`
- `03-team-management-prd.md`
- `04-match-screen-and-admin-management-prd.md`
- `05-predictions-scoring-and-scoreboards-prd.md`

Do not create one large master PRD unless the user explicitly asks for it.

If the feature name is unclear, propose a kebab-case feature name and ask the user to confirm.

### 2. Explore Existing State

Inspect enough of the current repo to avoid writing a PRD as if the project starts from zero.

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

Capture the result in the PRD under `Current Codebase State`.

### 3. Interview Only For Missing Decisions

Use `grill-me` style questioning, but do not re-ask known decisions.

Ask one focused question at a time only when:

- the answer cannot be derived from the codebase, guidelines, notes, exercises, or previous user answers;
- a wrong assumption would change security, domain behavior, schema, validation, grading risk, or planning order.

Do not ask about decisions already settled in this project, such as email login, admin restrictions, REST/WebClient late block, tests late block, or the 27 May deadline.

### 4. Design The Feature

For the selected feature, identify:

- actor behavior for guest, user, and admin;
- route/access expectations;
- domain models and DTOs involved;
- MVC/Thymeleaf behavior;
- service/repository responsibilities;
- validation and i18n messages;
- exception/error behavior;
- REST/WebClient boundary, if relevant;
- final test category that should later verify the behavior.

Look for deep modules with stable, testable interfaces, especially for scoring, validation, current-user lookup, access decisions, and match/prediction rules.

Do not include fragile method names, line numbers, or detailed code snippets in the PRD.

### 5. Write Or Revise The PRD

Write feature PRDs to:

`src/main/resources/prd/{feature-name}-prd.md`

If that file already exists, do not overwrite automatically. Ask whether to:

- leave it as-is,
- overwrite it,
- or update it with a revised version.

Use the template below.

```markdown
# PRD: {Feature Name}

## Problem Statement

Describe the project problem from the user's/school assignment perspective.

## Solution

Describe the intended feature behavior in school-project terms. Keep safe passing first and polish later.

## Current Codebase State

Summarize what already exists, what is partially implemented, and what is still missing.

## School Requirements

List the school requirements this PRD must support, such as:

- MVC controllers
- Thymeleaf views
- service/repository/JPA layering
- Spring Security
- validation with existing annotations
- custom annotation and validator class
- i18n/resource bundle messages
- exception handling and error pages
- REST controller coverage later
- WebClient later
- required tests later

Only include the items relevant to this feature, but be explicit about deferred REST/WebClient and test obligations.

## Role And Access Decisions

Describe what each actor may do:

- **Guest**: ...
- **User**: ...
- **Admin**: ...
- **Forbidden**: ...

For this project, admin must not use team or prediction flows unless the user explicitly changes that rule.

## User Stories

Use a complete but exam-focused numbered list. Prefer sharp coverage over a very long generic list.

Format:

1. As a {actor}, I want {feature}, so that {benefit}.

Include guest/user/admin stories and edge cases when relevant.

## Implementation Decisions

List durable decisions that planning and implementation should preserve:

- routes and access rules;
- schema/entity shape;
- key DTOs and validation rules;
- service boundaries;
- resource bundle/i18n decisions;
- exception/error behavior;
- REST/WebClient boundary;
- testability decisions.

Do not include fragile file paths, method names, line numbers, or code snippets.

## Testing Decisions

State what should eventually be tested for this feature.

Include:

- the external behavior to verify;
- the school-required test category this feature contributes to;
- useful prior art from the local exercise projects when known;
- whether tests are part of the current implementation phase or deferred to the late test block.

## REST And WebClient Decisions

State whether REST/WebClient is:

- out of scope for the current feature implementation and deferred to the late REST block;
- partially relevant now because the feature defines data or behavior that a later REST endpoint must expose;
- fully in scope only if the user explicitly requested it.

## Out Of Scope

List what this PRD intentionally does not cover.

## Sources

Number all source materials that informed this PRD.

Always include, when relevant:

1. FIFA World Cup 2026 Team Prediction PDF and page range.
2. School guidelines from `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen`.
3. Lesson notes from `C:\Users\Armour\Documents\HOGENT\EWD\Notes`.
4. Exercise projects from `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij`.
5. Existing `call-the-match` codebase.
6. Git repository URL from `git remote -v`.
7. User decisions from the current conversation.

## Further Notes

Add risks, assumptions, open questions, or sequencing notes that matter for planning.
```

## Output Rules

- Keep the PRD in Markdown.
- Use visible placeholders with `{placeholder}` syntax when documenting a template. Do not use angle-bracket placeholders, because Markdown renderers may hide them as HTML tags.
- Use project-specific actor names: guest, user, admin.
- Keep wording practical and school-conform.
- Include enough detail for `prd-to-plan` and `prd-to-plan-json` to create implementation plans without re-interviewing the user.
- Do not add generic SaaS/product-management sections unless the user asks for them.
- Do not mention Figma, Jira, Slack, Teams, Confluence, or Notion as default source types. Include them only if the user actually provides such a source.
