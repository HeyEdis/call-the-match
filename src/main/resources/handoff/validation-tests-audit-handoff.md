# Validation Tests Audit Handoff

Date: 2026-05-27  
Project: `call-the-match`

## Suggested Skills

- `project-guidelines`: use first. Validation test decisions must be checked against the local EWD school conventions, notes, and exercise projects.
- `diagnose`: use only if a validation test fails or a validator behaves unexpectedly.
- `handoff`: use again after validation tests are refactored or new decisions are made.

## Purpose

This handoff captures the audit of the validation test package. The user wanted to know whether the validation tests follow the school guidelines and exercises, whether test cases are missing, and which tests are overdone enough to cut.

## Evidence Sources

Primary project guideline:

- `.agents/skills/project-guidelines/references/testing.md`
  - Annotation validation should use a Jakarta `Validator`.
  - Spring `Validator` classes should be tested with `BeanPropertyBindingResult`.
  - Tests should be focused and small.

Validation guideline:

- `.agents/skills/project-guidelines/references/validation-i18n-exceptions.md`
  - Put validation annotations on request DTO/record fields.
  - Use `@Valid` in controllers.
  - Put `BindingResult` immediately after the validated DTO.
  - Use validator classes/advice for cross-field checks or repository-backed checks.
  - Use a custom annotation for project-required custom validation, preferably `@ValidStadiumChecksum`.
  - Validation messages belong in `src/main/resources/i18n/messages.properties`.

Lesson notes:

- `C:\Users\Armour\Documents\HOGENT\EWD\Notes\13-03-26-Validation.md`
  - "Zet de validatie in de Record -> DTO."
  - Do not hardcode validation messages.
  - Validation flow is annotations in DTO, `@Valid` + `BindingResult` in controller, then field errors in the view.
  - Validator classes are for validation that spans multiple fields.

Exercise evidence:

- `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Spring\Spring_validatie\Spring_Boot_Validation\src\test\java\com\example\spring_boot_validation\dto\request\InputRegistrationDTOTest.java`
  - Uses `Validation.buildDefaultValidatorFactory().getValidator()` for DTO annotation tests.
- `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Spring\Spring_validatie\Spring_Boot_Validation\src\test\java\com\example\spring_boot_validation\validator\RegistrationValidatorTest.java`
  - Uses `BeanPropertyBindingResult` for a Spring `Validator` class.
- `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Spring\Spring_validatie\Spring_Boot_Validation\src\main\java\com\example\spring_boot_validation\validator\ValidPasswords.java`
  - Shows the custom annotation pattern with `@Constraint`, `message`, `groups`, and `payload`.

## Tests Audited

Validation tests:

- `src/test/java/com/example/callthematch/validation/InputCompetitionDTOValidationTests.java`
- `src/test/java/com/example/callthematch/validation/InputPredictionDTOValidationTests.java`
- `src/test/java/com/example/callthematch/validation/InputRegistrationDTOValidationTests.java`
- `src/test/java/com/example/callthematch/validation/InputTeamDTOValidationTests.java`
- `src/test/java/com/example/callthematch/validation/InputTeamJoinDTOValidationTests.java`
- `src/test/java/com/example/callthematch/validation/CompetitionValidatorTests.java`
- `src/test/java/com/example/callthematch/validation/InputTeamValidatorTests.java`
- `src/test/java/com/example/callthematch/validation/InputTeamJoinValidatorTests.java`
- `src/test/java/com/example/callthematch/validation/StadiumChecksumValidatorTests.java`

Related implementation files:

- `src/main/java/com/example/callthematch/dto/request/InputCompetitionDTO.java`
- `src/main/java/com/example/callthematch/dto/request/InputCompetitionResultDTO.java`
- `src/main/java/com/example/callthematch/dto/request/InputPredictionDTO.java`
- `src/main/java/com/example/callthematch/dto/request/InputRegistrationDTO.java`
- `src/main/java/com/example/callthematch/dto/request/InputTeamDTO.java`
- `src/main/java/com/example/callthematch/dto/request/InputTeamJoinDTO.java`
- `src/main/java/com/example/callthematch/validator/CompetitionValidator.java`
- `src/main/java/com/example/callthematch/validator/InputTeamValidator.java`
- `src/main/java/com/example/callthematch/validator/InputTeamJoinValidator.java`
- `src/main/java/com/example/callthematch/validator/StadiumChecksumValidator.java`
- `src/main/java/com/example/callthematch/validator/ValidStadiumChecksum.java`

## Verification Done

Validation tests were run:

```powershell
.\mvnw.cmd -q "-Dtest=*ValidationTests,*ValidatorTests" test
```

Result:

- `CompetitionValidatorTests`: 4 tests, 0 failures
- `InputCompetitionDTOValidationTests`: 4 tests, 0 failures
- `InputPredictionDTOValidationTests`: 5 tests, 0 failures
- `InputRegistrationDTOValidationTests`: 2 tests, 0 failures
- `InputTeamDTOValidationTests`: 2 tests, 0 failures
- `InputTeamJoinDTOValidationTests`: 2 tests, 0 failures
- `InputTeamJoinValidatorTests`: 3 tests, 0 failures
- `InputTeamValidatorTests`: 3 tests, 0 failures
- `StadiumChecksumValidatorTests`: 5 tests, 0 failures
- Total validation tests: 30 passing

## Overall Verdict

The validation tests are mostly school-conform.

The important split is correct:

- DTO annotation tests use a Jakarta `Validator`.
- Spring validator class tests use `BeanPropertyBindingResult`.
- Repository-backed validators use mocked repositories.
- The custom checksum annotation/validator is covered.

No validation test class needs to be deleted wholesale.

## What Is Good And Should Stay

Keep:

- `InputCompetitionDTOValidationTests`
  - Covers missing required match fields.
  - Covers official result required/negative score validation through `InputCompetitionResultDTO`.
  - Covers class-level stadium checksum through normal Jakarta validation.
  - Covers valid match input.

- `InputPredictionDTOValidationTests`
  - Covers null score A and B.
  - Covers negative score A and B.
  - Covers valid non-negative scores.

- `InputRegistrationDTOValidationTests`
  - Covers missing/invalid registration fields.
  - Covers a valid registration DTO.

- `InputTeamDTOValidationTests`
  - Covers blank team name.
  - Covers valid team name.

- `InputTeamJoinDTOValidationTests`
  - Covers blank invite code.
  - Covers valid invite code.

- `CompetitionValidatorTests`
  - Uses `BeanPropertyBindingResult`, matching the school validator test pattern.
  - Covers same-team check.
  - Covers date-before-project-period check.
  - Covers stadium/time conflict.
  - Covers selected stadium code mismatch.

- `InputTeamValidatorTests`
  - Covers duplicate team name.
  - Covers unique team name.
  - Covers blank name skipping repository lookup, leaving `@NotBlank` to own required input.

- `InputTeamJoinValidatorTests`
  - Covers unknown invite code.
  - Covers existing invite code.
  - Covers blank invite code skipping repository lookup, leaving `@NotBlank` to own required input.

- `StadiumChecksumValidatorTests`
  - Directly covers custom validator behavior.
  - Keeps evidence for the required custom validator.

## Definitely Fix

Fix the stale mocked message template in `StadiumChecksumValidatorTests`.

Current test setup uses:

```java
lenient().when(context.getDefaultConstraintMessageTemplate()).thenReturn("{competition.checksum.valid}");
```

But the annotation default is:

```java
String message() default "{validator.stadiumChecksum}";
```

Refactor to:

```java
lenient().when(context.getDefaultConstraintMessageTemplate()).thenReturn("{validator.stadiumChecksum}");
```

Reason:

- The test currently passes because it only asserts boolean behavior.
- But as evidence, it points to the wrong message key.
- The actual class-level checksum message is `validator.stadiumChecksum`, while `competition.checksum.valid` is the positive-number field message.

## Could Cut Or Reduce

Do not cut the whole `StadiumChecksumValidatorTests` class.

Reason:

- The project needs evidence for a custom annotation/validator.
- The DTO validation test proves integration, but the direct validator test proves the custom validator logic.

If the suite feels too detailed, reduce the direct checksum validator test to core cases:

- valid checksum returns true
- invalid checksum returns false
- null input/null relevant fields return true

No other validation tests are definite cuts. They are small and defensible.

## Missing Or Useful Test Cases To Add

Add these for stronger school-style coverage:

1. `CompetitionValidatorTests`
   - Date after latest allowed date should reject field `date`.
   - Current tests only cover date before earliest allowed date.

2. `CompetitionValidatorTests`
   - Null date should not throw.
   - This protects the earlier `NullPointerException` issue when creating an empty match.

3. `CompetitionValidatorTests`
   - Null stadium, date, or time should skip stadium/time conflict repository lookup.
   - This proves required-field annotations own missing field validation.

4. `CompetitionValidatorTests`
   - Edit/update conflict should use `existsByStadiumIdAndDateAndTimeAndIdNot` when `input.id()` is not null.
   - This protects the update case from falsely conflicting with itself.

5. `CompetitionValidatorTests`
   - Valid competition should produce no custom validator errors.

6. `InputCompetitionDTOValidationTests`
   - Negative `stadiumCode` should trigger `PositiveOrZero`.
   - Negative `checksum` should trigger `PositiveOrZero`.

7. `InputRegistrationDTOValidationTests`
   - Blank password as a separate case.
   - Blank email as a separate case.
   - Optionally invalid email as a separate case.

8. `InputTeamDTOValidationTests`
   - `null` name.
   - whitespace-only name.

9. `InputTeamJoinDTOValidationTests`
   - `null` invite code.
   - whitespace-only invite code.

10. `InputTeamValidatorTests`
    - Optionally assert exact error code `team.name.duplicate`, not only field presence.

11. `InputTeamJoinValidatorTests`
    - Optionally assert exact error code `team.inviteCode.invalid`, not only field presence.

12. `StadiumChecksumValidatorTests`
    - Optionally verify invalid checksum adds the violation to property node `checksum`.
    - Keep this only if it stays readable; do not over-mock just for ceremony.

## Parameterization Guidance

The school exercises often use `@ParameterizedTest` and `@MethodSource` for repeated valid/invalid DTO cases.

Good candidates for parameterization:

- `InputPredictionDTOValidationTests`
  - negative/null score cases
- `InputTeamDTOValidationTests`
  - null, empty, and whitespace names
- `InputTeamJoinDTOValidationTests`
  - null, empty, and whitespace invite codes
- `InputRegistrationDTOValidationTests`
  - invalid field combinations

Do not parameterize just to look fancy. Use it only where it reduces repetition and keeps the expected failing field obvious.

## Separation Of Responsibilities

Use this split:

- DTO annotation tests:
  - `@NotNull`, `@NotBlank`, `@Min`, `@PositiveOrZero`, `@Email`, `@Size`
  - Use Jakarta `Validator`.

- Spring validator class tests:
  - repository-backed checks
  - cross-field rules
  - skip logic for blank/null values
  - Use `BeanPropertyBindingResult`.

- Controller tests:
  - `@Valid` + `BindingResult` flow
  - form redisplay
  - model reload after validation failure
  - service not called when validation fails

- Message bundle checks:
  - only add targeted checks if message-key regressions become a real risk.
  - Do not turn validator tests into full i18n integration tests.

## Suggested Refactor Sequence

1. Fix the stale checksum message key in `StadiumChecksumValidatorTests`.
2. Add missing `CompetitionValidatorTests` for:
   - date after latest allowed date
   - null date does not throw
   - update conflict uses `existsByStadiumIdAndDateAndTimeAndIdNot`
3. Add null/whitespace DTO validation cases for team and join DTOs.
4. Optionally parameterize DTO tests where it simplifies repeated checks.
5. Re-run:

```powershell
.\mvnw.cmd -q "-Dtest=*ValidationTests,*ValidatorTests" test
```

## Final Position

Keep the validation test package. It is mostly aligned with the school examples and project guidelines. The main accountability item is the stale checksum message key. The strongest useful additions are the missing `CompetitionValidator` edge cases, especially null-date protection and update-conflict behavior.
