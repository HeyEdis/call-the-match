# PRD: Validation Tests Audit

## Problem Statement

The `call-the-match` validation test package already gives useful evidence for the HOGENT EWD validation requirements, but it needs a small cleanup and a few targeted additions before it is fully defensible.

The current suite proves the important split between DTO annotation validation, Spring validator classes, repository-backed validator behavior, and the custom stadium checksum annotation. The remaining risk is not that the suite is wrong overall, but that a few edge cases are missing and one checksum test still refers to a stale message key.

## Solution

Refine the existing validation tests without rewriting the package.

Safe passing first:

- Keep the current DTO validation tests that use a Jakarta `Validator`.
- Keep the current Spring validator tests that use `BeanPropertyBindingResult`.
- Keep direct coverage for `@ValidStadiumChecksum` and `StadiumChecksumValidator`.
- Fix the stale checksum message key in the direct checksum validator test.
- Add a small number of missing edge cases around competition dates, null input, update conflict behavior, and blank or whitespace DTO fields.
- Use parameterized tests only where they reduce repetition and keep the failing field obvious.

The desired end state is a validation test package that is small, green, school-conform, and easy to explain during defence.

## Current Codebase State

The project is a Spring Boot 4.0.5 Java 21 application with Spring MVC, Thymeleaf, Validation, JPA, Security, WebFlux/WebClient dependencies, MySQL runtime support, and Spring test dependencies.

The current codebase already has the production validation structure expected by the school conventions:

- request DTOs under `src/main/java/com/example/callthematch/dto/request`;
- custom Spring validators under `src/main/java/com/example/callthematch/validator`;
- controller advice classes that bind validators to MVC form DTOs;
- validation and user-facing messages in `src/main/resources/i18n/messages.properties` and `messages_nl.properties`;
- MVC form templates for account, team, competition, and prediction flows;
- existing controller, service, security, REST controller, and validation test packages.

Existing validation tests:

- `InputCompetitionDTOValidationTests`
- `InputPredictionDTOValidationTests`
- `InputRegistrationDTOValidationTests`
- `InputTeamDTOValidationTests`
- `InputTeamJoinDTOValidationTests`
- `CompetitionValidatorTests`
- `InputTeamValidatorTests`
- `InputTeamJoinValidatorTests`
- `StadiumChecksumValidatorTests`

The audit handoff reports that the validation command already passed:

```powershell
.\mvnw.cmd -q "-Dtest=*ValidationTests,*ValidatorTests" test
```

Reported result:

- 30 validation tests passing.
- DTO annotation tests use Jakarta validation.
- Spring validator class tests use `BeanPropertyBindingResult`.
- Repository-backed validators use mocked repositories.
- The custom stadium checksum annotation and validator are covered.

Known issue:

- `StadiumChecksumValidatorTests` mocks the default message template as `{competition.checksum.valid}`, while the custom annotation default is `{validator.stadiumChecksum}`.

Known worktree state at PRD creation:

- There are unrelated modified controller-test files and an untracked Ralph folder for `10-controller-tests-audit`.
- This PRD must not modify or restore those unrelated changes.

## School Requirements

This PRD supports the required validation test category in the EWD project.

Relevant school requirements:

- validation annotations belong on request DTO or record fields;
- controllers use `@Valid`;
- `BindingResult` must immediately follow the validated DTO;
- field errors are rendered through Thymeleaf with `th:errors`;
- validation messages belong in resource bundles, not hardcoded controller or view logic;
- custom validators are used for cross-field checks or repository-backed checks;
- the project must include evidence for existing validation annotations, a custom annotation, and validator classes;
- tests should be focused and small;
- DTO annotation tests should use a Jakarta `Validator`;
- Spring `Validator` classes should be tested with `BeanPropertyBindingResult`.

This PRD is about test coverage and test shape. It should not introduce new application behavior except where a missing test exposes a real bug.

## Role And Access Decisions

- **Guest**: may trigger public validation only through public forms such as registration; controller tests own the guest route behavior.
- **User**: may trigger team, team join, and prediction validation through user flows; controller tests own route access and redisplay behavior.
- **Admin**: may trigger competition and official-result validation through admin match-management flows only.
- **Forbidden**: admin must not use team or prediction validation flows; user must not access admin competition-management validation paths; guest must not reach protected form submissions except through login/register.

Validation tests do not need to duplicate the full role matrix. Security and controller tests own access outcomes. Validation tests own field, cross-field, repository-backed, and custom annotation behavior.

## User Stories

1. As a student, I want validation tests to match the school examples, so that the required validation category is easy to defend.
2. As a developer, I want DTO annotation tests to use a Jakarta `Validator`, so that `@NotBlank`, `@NotNull`, `@Min`, `@PositiveOrZero`, `@Email`, `@Size`, and class-level constraints are tested in the right layer.
3. As a developer, I want Spring validator tests to use `BeanPropertyBindingResult`, so that cross-field and repository-backed checks are tested without MVC noise.
4. As a developer, I want competition validation edge cases covered, so that date bounds, null values, stadium/time conflicts, and update behavior do not regress.
5. As a developer, I want the custom stadium checksum validator covered directly and through DTO validation, so that the custom annotation requirement has clear evidence.
6. As a developer, I want stale or misleading message-key expectations fixed, so that the tests point to the real resource bundle key.
7. As a student, I want the suite to stay small and readable, so that it looks intentional instead of over-engineered.

## Implementation Decisions

### Keep The Existing Validation Package

Do not delete any validation test class wholesale.

The current split is correct:

- DTO annotation tests use Jakarta validation.
- Spring validator class tests use `BeanPropertyBindingResult`.
- repository-backed validators mock repositories.
- custom checksum logic has direct validator coverage.

### Fix The Checksum Message Key

Update the direct checksum validator test setup so the mocked default constraint message template matches the annotation default:

- current stale key: `{competition.checksum.valid}`;
- expected key: `{validator.stadiumChecksum}`.

The stale key currently does not fail the suite because the test asserts boolean behavior, but it is misleading as evidence. `competition.checksum.valid` belongs to positive-number field validation, while `validator.stadiumChecksum` belongs to the custom class-level checksum validator.

### Add Missing Competition Validator Coverage

Add focused `CompetitionValidatorTests` cases for:

- date after 6 June 2026 rejects field `date`;
- null date does not throw;
- null stadium, date, or time skips stadium/time conflict repository lookup;
- edit/update conflict uses the update-aware repository method when the DTO has an id;
- valid competition input produces no custom validator errors.

These tests should stay at the validator layer. They should not involve MockMvc, Thymeleaf, or controller setup.

### Add Missing DTO Annotation Coverage

Add focused DTO validation cases where the current tests are too thin:

- negative `stadiumCode` rejects through `PositiveOrZero`;
- negative `checksum` rejects through `PositiveOrZero`;
- blank password is rejected as its own registration case;
- blank email is rejected as its own registration case;
- invalid email is rejected if not already clearly covered;
- null team name is rejected;
- whitespace-only team name is rejected;
- null invite code is rejected;
- whitespace-only invite code is rejected.

Prefer asserting the failing field over broad counts when that keeps the test clearer.

### Keep Direct Custom Validator Coverage

Keep `StadiumChecksumValidatorTests`.

The DTO validation test proves integration, but the direct validator test proves the custom validator logic. If this class is reduced, preserve these core cases:

- valid checksum returns true;
- invalid checksum returns false;
- null input or null relevant fields return true.

Optional coverage may verify that invalid checksum adds the violation to property node `checksum`, but only if the mocking stays readable.

### Use Parameterization Sparingly

Good candidates for `@ParameterizedTest`:

- null and negative prediction score cases;
- null, empty, and whitespace team names;
- null, empty, and whitespace invite codes;
- invalid registration field combinations.

Do not parameterize tests only for style. If a normal test makes the failing field easier to see, keep it simple.

### Preserve Responsibility Boundaries

Use this split:

- DTO annotation tests: field-level and class-level annotation behavior.
- Spring validator tests: repository-backed checks, cross-field rules, date rules, skip logic for missing required fields.
- Controller tests: `@Valid` plus `BindingResult` flow, form redisplay, model reload, and service not called on validation failure.
- Message bundle checks: only targeted checks when message-key regressions are a real risk.

Do not turn validation tests into full i18n integration tests.

## Testing Decisions

After the validation test cleanup, run:

```powershell
.\mvnw.cmd -q "-Dtest=*ValidationTests,*ValidatorTests" test
```

Expected result:

- all DTO annotation validation tests pass;
- all Spring validator tests pass;
- the custom checksum annotation and validator remain covered;
- the suite stays focused and small.

This feature contributes directly to the school-required validation test category:

- existing annotation tests;
- custom annotation tests;
- validator class tests.

Useful local exercise prior art:

- `InputRegistrationDTOTest` uses `Validation.buildDefaultValidatorFactory().getValidator()` for DTO annotation validation.
- `RegistrationValidatorTest` uses `BeanPropertyBindingResult` for a Spring `Validator`.
- `ValidPasswords` shows the custom annotation pattern with `@Constraint`, message, groups, and payload.

These validation-test changes are part of the current cleanup scope. Broader controller, security, REST, service, and WebClient tests remain separate scopes.

## REST And WebClient Decisions

REST and WebClient are out of scope for this PRD.

The validation behavior defined here may support later REST error handling if request DTOs or validators are reused, but this PRD must not add REST endpoints, WebClient calls, or REST-specific validation tests.

## Out Of Scope

- Rewriting the entire test suite.
- Removing the validation test package.
- Adding new application features.
- Changing controller route access.
- Moving validation responsibilities into controllers.
- Adding broad i18n integration tests.
- Adding browser or end-to-end tests.
- Adding REST or WebClient coverage.
- Modifying unrelated dirty controller-test or Ralph files.

## Sources

1. Validation tests audit handoff: `src/main/resources/handoff/validation-tests-audit-handoff.md`.
2. Existing `call-the-match` codebase, especially request DTOs, validators, validator advice, resource bundles, templates, and validation tests.
3. Project guidelines: `.agents/skills/project-guidelines/SKILL.md`.
4. Validation/i18n/exception guideline reference: `.agents/skills/project-guidelines/references/validation-i18n-exceptions.md`.
5. Testing guideline reference: `.agents/skills/project-guidelines/references/testing.md`.
6. Lesson notes cited by the handoff: `C:\Users\Armour\Documents\HOGENT\EWD\Notes\13-03-26-Validation.md`.
7. Exercise projects cited by the handoff under `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Spring\Spring_validatie\Spring_Boot_Validation`.
8. Existing testing PRD: `src/main/resources/prd/06-testing-prd.md`.
9. Existing school-conform test refactor PRD: `src/main/resources/prd/08-school-conform-test-refactor-prd.md`.
10. Git repository: `https://github.com/HeyEdis/call-the-match.git`.
11. User request from the current conversation: create a PRD from the `validation-tets-audit` handoff in the handoff folder.

## Further Notes

- The best implementation sequence is: fix the checksum message key, add missing `CompetitionValidatorTests`, add DTO null/whitespace cases, then parameterize only where it makes the tests cleaner.
- The date boundary must remain 20 May 2026 through 6 June 2026.
- Keep the command narrow while working on this PRD, then run broader test categories only when later PRDs require them.
- This PRD is narrower than the global testing PRD and the school-conform test refactor PRD. It is only about the audited validation tests.
