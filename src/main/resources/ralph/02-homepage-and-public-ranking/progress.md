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

### 2026-05-22 - Clean Public Match Detail And Add MVC 404 Advice

- **Tasks**:
  - `complete-public-match-detail-surface` - Complete Public Match Detail Surface
  - `handle-public-match-detail-errors` - Handle Public Match Detail Errors
- **What changed**: removed the guest-visible edit control and private prediction placeholder from public competition detail, kept nullable score rendering honest on the detail page, and added shared MVC controller advice that maps missing competitions and malformed path variables to the existing 404 view with HTTP 404 status.
- **Decisions**: kept this AFK pass on public MVC detail only. The 404 template already existed, so the error slice adds advice rather than duplicating error markup or introducing REST error handling.
- **Files changed**:
  - `src/main/resources/templates/competition/show.html`
  - `src/main/java/com/example/callthematch/controller/MVCExceptionHandler.java`
  - `src/main/resources/plan/02-homepage-and-public-ranking/plan.json`
  - `src/main/resources/ralph/02-homepage-and-public-ranking/progress.md`
- **Verification**:
  - The first sandboxed direct Maven run could not resolve the Spring Boot parent because Maven Central access was denied.
  - The first approved direct Maven run exposed the wrong type-mismatch import before tests could run; that import was corrected to Spring Web's method-annotation package.
  - Approved direct local Maven 3.9.14 rerun completed with `BUILD SUCCESS`: `mvn.cmd test`.
  - Final test result: 8 tests run, 0 failures and 0 errors.
  - The public detail template was inspected after the run to confirm it retains only fixture/result context and nullable score output. The shared MVC advice was inspected to confirm both `CompetitionNotFound` and `MethodArgumentTypeMismatchException` return `error/404` with not-found status.
  - Focused MVC error assertions remain scheduled for the public closure-test task.
- **Status**: acceptance criteria verified for both tasks and their `"passes"` flags set to `true`.
