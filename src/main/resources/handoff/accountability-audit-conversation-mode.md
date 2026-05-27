# Accountability Audit Conversation Mode

## Purpose

This handoff explains the working mode for continuing the `call-the-match` accountability audit in a fresh chat.

The user is auditing the Spring Boot project against the school style and expects concrete evidence for every explanation. The conversation is not a generic code review; it is a guided interrogation of why certain choices were made, whether they match the guidelines, and how to rewrite them in the style used in the exercises.

## What We Are Doing

The user is going controller by controller, validator by validator, model by model, and through security/advice/client code. For every questioned choice, the assistant should explain:

- why it was done that way
- whether it follows the school guidelines and exercise patterns
- where the evidence is in the workspace
- what the school-conform alternative would look like
- what tradeoff exists if the code is kept or changed

The user often asks only for understanding, not an immediate fix. Do not blindly refactor unless the user explicitly asks for implementation.

## Evidence Sources

Use local evidence first:

- `C:\Users\Armour\Documents\HOGENT\EWD\call-the-match`
- `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen`
- `C:\Users\Armour\Documents\HOGENT\EWD\Notes`
- `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij`

When answering, cite concrete files and, when useful, line references.

## Expected Answer Style

The preferred answer shape is:

1. Verdict first.
2. Evidence from the current codebase and school examples.
3. Explanation of how it works.
4. Tradeoff or risk.
5. School-style recommendation or code shape.

The tone should be direct, accountable, and collaborative. The user wants to challenge decisions and expects the assistant to defend, correct, or retract them with evidence.

## Covered Topics So Far

The previous audit covered:

- `@ModelAttribute` naming and DTO binding
- REST request parameters versus path variables
- REST client and test changes after changing `/api/matches?date=...` to `/api/{date}/matches`
- controller model attribute repetition
- validator advice versus controller `try/catch`
- `TeamValidatorAdvice`, direct validator injection, and avoiding `ObjectProvider`
- MVC and REST exception advice
- Spring Security config choices
- exception messages versus resource bundle keys
- REST DTOs versus domain entities
- entity setters, constructors, builders, and protected no-args constructors
- behavior in the `Team` model
- simplified validator patterns
- class-level Bean Validation annotations such as stadium checksum validation

A fuller summary exists at:

`C:\Users\Armour\Documents\HOGENT\EWD\call-the-match\src\main\resources\audit\conversation-accountability-summary-2026-05-27.md`

## Suggested Skills

- `project-guidelines` if available in `.agents/skills/project-guidelines`
- `diagnose` when the user reports a runtime error, failed test, stack trace, or broken behavior
- `handoff` when the user asks to preserve decisions or create a reusable continuation document

## Important Reminder

The goal is not to make the code look impressive. The goal is to make the reasoning traceable to the school guidelines, exercises, and current project constraints.
