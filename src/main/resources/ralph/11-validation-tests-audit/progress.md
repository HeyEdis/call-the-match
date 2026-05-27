# Ralph Progress Log: 11-validation-tests-audit

Each iteration appends what was done, decisions made, files changed, verification results, and blockers.

## Entries

### 2026-05-27 - AFK validation test audit cleanup

Tasks:

- `fix-checksum-message-key`
- `add-competition-validator-edge-cases`
- `add-competition-dto-numeric-validation`
- `expand-registration-dto-validation`
- `expand-team-invite-dto-validation`
- `verify-validation-suite`

What changed:

- Ran the validation-test baseline before edits; it passed.
- Updated `StadiumChecksumValidatorTests` to use the real custom annotation message key `{validator.stadiumChecksum}`.
- Added `CompetitionValidatorTests` coverage for the upper date bound, null date skip behavior, missing stadium/date/time conflict lookup skip behavior, update-aware conflict lookup, and valid no-error input.
- Added `InputCompetitionDTOValidationTests` coverage for negative `stadiumCode` and negative `checksum` annotation validation.
- Added focused `InputRegistrationDTOValidationTests` cases for blank password, blank email, and invalid email.
- Added `InputTeamDTOValidationTests` cases for null and whitespace-only team names.
- Added `InputTeamJoinDTOValidationTests` cases for null and whitespace-only invite codes.
- Marked all six plan tasks as passing after concrete verification.

Files changed:

- `src/test/java/com/example/callthematch/validation/StadiumChecksumValidatorTests.java`
- `src/test/java/com/example/callthematch/validation/CompetitionValidatorTests.java`
- `src/test/java/com/example/callthematch/validation/InputCompetitionDTOValidationTests.java`
- `src/test/java/com/example/callthematch/validation/InputRegistrationDTOValidationTests.java`
- `src/test/java/com/example/callthematch/validation/InputTeamDTOValidationTests.java`
- `src/test/java/com/example/callthematch/validation/InputTeamJoinDTOValidationTests.java`
- `src/main/resources/plan/11-validation-tests-audit/plan.json`
- `src/main/resources/ralph/11-validation-tests-audit/progress.md`
- `src/main/resources/ralph/11-validation-tests-audit/TODO.md`

Verification:

- `.\mvnw.cmd -q "-Dtest=*ValidationTests,*ValidatorTests" test` passed before edits.
- `.\mvnw.cmd -q "-Dtest=*ValidationTests,*ValidatorTests" test` passed after edits.

Blockers:

- None.
