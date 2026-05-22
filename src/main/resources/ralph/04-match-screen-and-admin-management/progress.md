# Ralph Progress Log: 04-match-screen-and-admin-management

Each iteration appends what was done, decisions made, files changed, verification results, and blockers.

## Entries

### 2026-05-22 - Finish Public Match Detail Baseline

- **Task**: `finish-public-match-detail-baseline` - Finish Public Match Detail Baseline
- **What changed**: finished the public competition detail surface by making the rendered match context explicit on the existing `CompetitionDTO` detail page. The page now labels the fixture date, time, stadium, location and official-result area through the resource bundle and keeps a clear no-result message for future fixtures with nullable official scores.
- **Decisions**: kept this HITL task read-only and on the existing MVC controller-to-service detail path. Admin add/edit/result work and prediction write UI remain later plan tasks; the detail screen only preserves space for those flows by showing persisted fixture context cleanly.
- **Files changed**:
  - `src/main/resources/templates/competition/show.html`
  - `src/main/resources/i18n/messages.properties`
  - `src/main/resources/plan/04-match-screen-and-admin-management/plan.json`
  - `src/main/resources/ralph/04-match-screen-and-admin-management/progress.md`
- **Verification**:
  - Inspected `CompetitionController.show` and `CompetitionService.findById` to confirm the detail page receives `CompetitionDTO` data through the service boundary.
  - Inspected `SecurityConfig` to confirm public competition detail remains permitted to guest, USER and ADMIN actors without entering mutation routes.
  - `.\mvnw.cmd -Dtest=PublicBrowseMvcTests test` did not start Maven because the wrapper PowerShell path failed with `Cannot index into a null array`.
  - Approved focused direct Maven 3.9.14 run completed with `BUILD SUCCESS`: `mvn.cmd -Dtest=PublicBrowseMvcTests test`.
  - Focused test result: 3 tests run, 0 failures and 0 errors. Existing Hibernate missing-table DDL and Mockito dynamic-agent warnings remain noisy in the passing output.
- **Status**: acceptance criteria verified for this public detail baseline and `"passes"` set to `true`.
