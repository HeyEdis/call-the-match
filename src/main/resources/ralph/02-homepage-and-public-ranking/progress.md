# Ralph Progress Log: 02-homepage-and-public-ranking

Each iteration appends what was done, decisions made, files changed, verification results, and blockers.

## Entries

### 2026-05-22 - Tighten Public Home Schedule Data

- **Task**: `tighten-public-home-schedule-data` - Tighten Public Home Schedule Data
- **What changed**: tightened the existing public home schedule slice by ordering competition rows by match date and time in the service layer and changing the home score cell to show an official score only when both score values exist.
- **Decisions**: kept this HITL pass limited to existing home data flow. Public-detail cleanup, ranking hardening, i18n coverage and closure tests remain later feature tasks.
- **Files changed**:
  - `src/main/java/com/example/callthematch/service/CompetitionService.java`
  - `src/main/resources/templates/home.html`
  - `src/main/resources/plan/02-homepage-and-public-ranking/plan.json`
  - `src/main/resources/ralph/02-homepage-and-public-ranking/progress.md`
- **Verification**:
  - The first sandboxed direct Maven run could not resolve the Spring Boot parent because Maven Central access was denied.
  - Approved direct local Maven 3.9.14 run completed with `BUILD SUCCESS`: `mvn.cmd test`.
  - Final test result: 8 tests run, 0 failures and 0 errors.
  - The home route remains explicitly public in the existing Spring Security route matrix and the view still receives persisted competition DTO data from `HomeController` through `CompetitionService`.
  - Existing Hibernate `create-drop` missing-table DDL warnings and Mockito agent warnings remain noisy in the passing build output.
- **Status**: acceptance criteria verified for this task and `"passes"` set to `true`.
