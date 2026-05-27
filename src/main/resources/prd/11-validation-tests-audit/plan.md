# Plan: Validation Tests Audit

> Source PRD: `src/main/resources/prd/11-validation-tests-audit-prd.md`

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
11. User request from the current conversation: create a PRD from the `validation-tets-audit` handoff in the handoff folder, then turn `11-validation-tests-audit-prd.md` into a plan.

## Architectural Decisions

Durable decisions that apply across all phases:

- **Routes**: validation behavior is reached through existing account, team, prediction, and admin competition MVC routes. This plan does not add or change routes.
- **Schema**: no schema changes. Tests may use existing DTOs, repositories, and test fixtures only.
- **Key models**: `InputCompetitionDTO`, `InputCompetitionResultDTO`, `InputPredictionDTO`, `InputRegistrationDTO`, `InputTeamDTO`, `InputTeamJoinDTO`, `CompetitionValidator`, `InputTeamValidator`, `InputTeamJoinValidator`, `ValidStadiumChecksum`, and `StadiumChecksumValidator`.
- **Security**: role access remains owned by controller and security tests. Validation tests must not duplicate the full guest/user/admin route matrix.
- **Validation/i18n**: DTO annotation tests use a Jakarta `Validator`; Spring validator tests use `BeanPropertyBindingResult`; the custom checksum message key is `{validator.stadiumChecksum}`; validation messages remain in resource bundles.
- **REST/WebClient**: out of scope. This plan must not add REST endpoints, WebClient behavior, or REST-specific validation tests.
- **Testing**: this plan contributes to the school-required validation categories: existing annotations, custom annotation, and validator classes. Keep tests focused and small.

---

## Phase 1: Checksum Message Key Cleanup

**User stories**: 5, 6, 7

### What To Build

Clean up the direct custom checksum validator test so its mocked default message template matches the real annotation message key. This is a narrow test-evidence fix: the validator behavior should stay unchanged, and the test should still prove the custom validator accepts valid checksums, rejects invalid checksums, and skips incomplete input.

### Acceptance Criteria

- [ ] `StadiumChecksumValidatorTests` uses `{validator.stadiumChecksum}` as the default constraint message template.
- [ ] Existing checksum validator behavior tests still pass.
- [ ] No production validator or annotation behavior changes are required.
- [ ] The test remains direct custom validator coverage for the required validation category.

---

## Phase 2: Competition Validator Edge Cases

**User stories**: 1, 3, 4, 7

### What To Build

Extend `CompetitionValidatorTests` with the missing validator-class edge cases. Keep this at the Spring validator layer with `BeanPropertyBindingResult`, mocked repositories, and existing competition DTO fixtures where practical.

### Acceptance Criteria

- [ ] A date after 6 June 2026 rejects field `date`.
- [ ] A null date does not throw and leaves required-field handling to DTO annotations.
- [ ] Null stadium, date, or time skips the stadium/time conflict repository lookup.
- [ ] Existing-match validation with a DTO id uses the update-aware conflict lookup instead of the create lookup.
- [ ] A valid competition input produces no custom validator field errors.
- [ ] Tests assert field-level behavior and repository interactions only where that proves validator responsibility.

---

## Phase 3: Competition DTO Annotation Gaps

**User stories**: 1, 2, 5, 7

### What To Build

Add the missing `InputCompetitionDTOValidationTests` cases for annotation-owned numeric constraints. Keep using the Jakarta `Validator` so these tests remain separate from `CompetitionValidatorTests`.

### Acceptance Criteria

- [ ] Negative `stadiumCode` rejects through annotation validation.
- [ ] Negative `checksum` rejects through annotation validation.
- [ ] The existing class-level stadium checksum validation remains covered through normal Jakarta validation.
- [ ] Tests assert the failing field or violation path clearly enough to defend the behavior.
- [ ] No Spring `Validator` or repository mocking is introduced in DTO annotation tests.

---

## Phase 4: Registration DTO Field Coverage

**User stories**: 1, 2, 7

### What To Build

Make registration DTO annotation coverage more explicit for required login data. Keep it as DTO-level Jakarta validation and avoid controller or security assertions in this phase.

### Acceptance Criteria

- [ ] Blank password is rejected as its own clear validation case.
- [ ] Blank email is rejected as its own clear validation case.
- [ ] Invalid email is rejected if it is not already covered clearly enough.
- [ ] Existing valid registration coverage remains.
- [ ] Tests use resource-bundle-backed annotation behavior without hardcoding user-facing validation text.

---

## Phase 5: Team And Invite DTO Null/Whitespace Coverage

**User stories**: 1, 2, 7

### What To Build

Add missing null and whitespace cases for team creation and team joining DTOs. Use parameterized tests only where they make repeated blank/null cases easier to scan.

### Acceptance Criteria

- [ ] `InputTeamDTOValidationTests` rejects null team name.
- [ ] `InputTeamDTOValidationTests` rejects whitespace-only team name.
- [ ] `InputTeamJoinDTOValidationTests` rejects null invite code.
- [ ] `InputTeamJoinDTOValidationTests` rejects whitespace-only invite code.
- [ ] Existing valid DTO cases remain.
- [ ] Parameterization, if used, keeps the expected failing field obvious.

---

## Phase 6: Validation Suite Verification

**User stories**: 1, 7

### What To Build

Run the narrow validation test command and confirm the suite remains green and school-conform after the cleanup. Treat failures as validation-test or validator regressions and fix within the same narrow scope.

### Acceptance Criteria

- [ ] Run:

```powershell
.\mvnw.cmd -q "-Dtest=*ValidationTests,*ValidatorTests" test
```

- [ ] All DTO annotation validation tests pass.
- [ ] All Spring validator class tests pass.
- [ ] Custom annotation and custom validator coverage remains present.
- [ ] No controller, security, REST, WebClient, service, or unrelated test cleanup is introduced in this plan.
- [ ] Any remaining risk or intentionally skipped optional test is documented before closing the task.
