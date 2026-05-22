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

### 2026-05-22 - Attempt Admin Add Match Validation Slice

- **Tasks attempted**:
  - `fix-admin-add-match-form-binding` - Fix Admin Add Match Form Binding
  - `complete-admin-add-match-validation-and-persistence` - Complete Admin Add Match Validation And Persistence
  - `add-fixture-cross-field-validator` - Add Fixture Cross-Field Validator
  - `reject-stadium-time-conflicts` - Reject Stadium Time Conflicts
  - `add-stadium-checksum-custom-annotation` - Add Stadium Checksum Custom Annotation
- **What changed**: replaced the half-bound add-match form path with a request DTO form model, corrected Team A/Team B/Stadium field and error binding, bound select ids on the request DTO and resolved their entities inside `CompetitionService`, removed official score input from fixture creation, kept competition persistence in the service, added fixture validator advice for different-country/date-period/stadium-time rules and added the assignment-required stadium-code checksum custom annotation using the `stadiumCode % 97` rule from the local FIFA assignment PDF.
- **Decisions**: kept official scores out of fixture creation because the result flow is a later task and `Competition` scores are nullable. Used the school validator-class pattern through `@ControllerAdvice` plus `@InitBinder` for cross-field and repository-backed fixture rules, and kept checksum validation as a separate Jakarta custom annotation on `InputCompetitionDTO`.
- **Files changed**:
  - `src/main/java/com/example/callthematch/controller/CompetitionController.java`
  - `src/main/java/com/example/callthematch/dto/request/InputCompetitionDTO.java`
  - `src/main/java/com/example/callthematch/service/CompetitionService.java`
  - `src/main/java/com/example/callthematch/repository/CompetitionRepository.java`
  - `src/main/java/com/example/callthematch/advice/CompetitionValidatorAdvice.java`
  - `src/main/java/com/example/callthematch/validator/CompetitionValidator.java`
  - `src/main/java/com/example/callthematch/validator/ValidStadiumChecksum.java`
  - `src/main/java/com/example/callthematch/validator/StadiumChecksumValidator.java`
  - `src/main/resources/templates/competition/add.html`
  - `src/main/resources/i18n/messages.properties`
  - `src/main/resources/ralph/04-match-screen-and-admin-management/progress.md`
  - `src/main/resources/ralph/04-match-screen-and-admin-management/TODO.md`
- **Verification**:
  - Extracted the local FIFA assignment PDF page 4 to confirm the checksum rule is the remainder of the four-digit stadium code divided by 97.
  - `.\mvnw.cmd test` did not start Maven because the wrapper PowerShell path failed with `Cannot index into a null array`.
  - The required direct Maven verification fallback was not approved in this run, so compilation and MVC behavior for this add slice could not be confirmed automatically.
  - Ran `git diff --check` and a source usage scan to catch whitespace problems and old `InputCompetitionDTO` score accessor use; no old DTO score accessor use remained.
- **Blocker**: direct Maven verification must run successfully before these five acceptance-criteria flags can safely be changed to `true`.
- **Status**: implementation attempted, but all five `"passes"` flags stay `false` until Maven verification succeeds.

### 2026-05-22 - Complete Admin Add Match Validation Slice

- **Tasks completed**:
  - `fix-admin-add-match-form-binding` - Fix Admin Add Match Form Binding
  - `complete-admin-add-match-validation-and-persistence` - Complete Admin Add Match Validation And Persistence
  - `add-fixture-cross-field-validator` - Add Fixture Cross-Field Validator
  - `reject-stadium-time-conflicts` - Reject Stadium Time Conflicts
  - `add-stadium-checksum-custom-annotation` - Add Stadium Checksum Custom Annotation
- **What changed**: closed the five add-fixture tasks after review-driven adjustments. Fixture select controls bind id fields on the DTO and service code resolves persisted countries and stadiums, invalid form posts reload backend-backed select options explicitly, successful saves log and report the saved competition id, fixture cross-field checks remain in the school-style validator advice path, and checksum feedback stays a separate custom Jakarta constraint.
- **Decisions**: removed the local form-data helper and entity converters after comparing the school examples and project guidelines. Fixed-value validation message data now flows through message arguments or annotation attributes instead of being embedded in `messages.properties`.
- **Verification**:
  - Checked school validator examples under `WorkspacesIntelij` before keeping `@ControllerAdvice`, `@InitBinder` and `binder.addValidators(...)`.
  - Ran `.\mvnw.cmd test` successfully.
  - Maven result: 28 tests run, 0 failures, 0 errors and 0 skipped. Existing Hibernate create-drop missing-table DDL warnings and Mockito dynamic-agent warnings remain in the passing output.
- **Status**: acceptance criteria verified for the five add-fixture tasks and their `"passes"` flags set to `true`.

### 2026-05-22 - Complete Admin Fixture Edit Slice

- **Tasks completed**:
  - `expose-admin-edit-fixture-entry-and-form` - Expose Admin Edit Fixture Entry And Form
  - `persist-admin-fixture-edits` - Persist Admin Fixture Edits
- **What changed**: added an admin-only edit link on the public match context, replaced the read-only edit page with a prefilled fixture edit form, loaded backend country and stadium choices for the GET and invalid POST flows, and added the service/controller update path that saves fixture corrections and redirects with bundle-backed feedback.
- **Decisions**: followed the local `Spring_Boot_list_crud-opl` edit pattern with a service `findInputById(...)` mapper plus service `update(...)`. The edit form reuses `InputCompetitionDTO`, the school validator advice path and `@ValidStadiumChecksum`, so fixture update validation stays aligned with fixture creation.
- **Files changed**:
  - `src/main/java/com/example/callthematch/controller/CompetitionController.java`
  - `src/main/java/com/example/callthematch/service/CompetitionService.java`
  - `src/main/resources/templates/competition/show.html`
  - `src/main/resources/templates/competition/edit.html`
  - `src/main/resources/i18n/messages.properties`
  - `src/main/resources/plan/04-match-screen-and-admin-management/plan.json`
  - `src/main/resources/ralph/04-match-screen-and-admin-management/progress.md`
- **Verification**:
  - Inspected `SecurityConfig` to confirm `/competition/edit/**` remains ADMIN-only for both GET and POST requests.
  - Ran `.\mvnw.cmd test` successfully.
  - Maven result: 28 tests run, 0 failures, 0 errors and 0 skipped. Existing Hibernate create-drop missing-table DDL warnings and Mockito dynamic-agent warnings remain in the passing output.
  - Ran `git diff --check`; no whitespace errors were reported.
- **Status**: acceptance criteria verified for the fixture edit form and persistence tasks and both `"passes"` flags set to `true`.

### 2026-05-22 - Complete Official Result And Mutation Boundary Slice

- **Tasks completed**:
  - `build-official-result-form-boundary` - Build Official Result Form Boundary
  - `persist-official-match-results` - Persist Official Match Results
  - `harden-match-mutation-security-and-errors` - Harden Match Mutation Security And Errors
- **What changed**: added a dedicated official-result request DTO and admin result form, exposed an admin result link from match detail, saved official score values through the competition service, and aligned the protected result route pattern with the MVC `{id}` route. The existing shared MVC exception advice continues to handle missing competition ids and malformed path ids for competition read and admin mutation paths.
- **Decisions**: kept official result validation separate from fixture add/edit validation so future fixtures can still keep nullable scores. Carried the user-requested message-bundle review fix into this commit by consolidating duplicate values and updating the affected Thymeleaf keys.
- **Files changed**:
  - `src/main/java/com/example/callthematch/config/SecurityConfig.java`
  - `src/main/java/com/example/callthematch/controller/CompetitionController.java`
  - `src/main/java/com/example/callthematch/dto/request/InputCompetitionResultDTO.java`
  - `src/main/java/com/example/callthematch/service/CompetitionService.java`
  - `src/main/resources/templates/competition/result.html`
  - `src/main/resources/templates/competition/show.html`
  - `src/main/resources/i18n/messages.properties`
  - existing Thymeleaf templates touched by duplicate message-key consolidation
  - `src/main/resources/plan/04-match-screen-and-admin-management/plan.json`
  - `src/main/resources/ralph/04-match-screen-and-admin-management/progress.md`
- **Verification**:
  - Inspected `SecurityConfig` to confirm add, edit and result mutation routes are ADMIN-only while competition detail remains public.
  - Inspected `GlobalExceptionAdvice` and the competition lookup paths to confirm missing competition ids and malformed competition ids still resolve through shared MVC 404 handling.
  - Ran `.\mvnw.cmd test` successfully.
  - Maven result: 28 tests run, 0 failures, 0 errors and 0 skipped. Existing Hibernate create-drop missing-table DDL warnings and Mockito dynamic-agent warnings remain in the passing output.
  - Ran the duplicate message-value scan after the new result keys; no duplicate `messages.properties` values remain.
  - Ran `git diff --check`; no whitespace errors were reported.
- **Status**: acceptance criteria verified for the official-result form, official-result persistence and mutation security/error boundary tasks and their `"passes"` flags set to `true`.

### 2026-05-22 - Close Match MVC Security And Validation Tests

- **Tasks completed**:
  - `add-match-mvc-and-security-closure-tests` - Add Match MVC And Security Closure Tests
  - `add-match-validation-closure-tests` - Add Match Validation Closure Tests
- **What changed**: added match-management MVC coverage for admin add, edit and result forms plus invalid submissions and admin not-found/type-mismatch paths. Extended security coverage so guests and USER actors cannot submit match writes while ADMIN actors can open match-management forms. Added focused validation coverage for fixture annotations, official result score annotations, the checksum custom annotation and the repository-backed competition validator rules.
- **Decisions**: kept MVC closure focused on observable invalid-submission outcomes because persistence behavior already lives behind the form flow and this school test slice needs model, view, binding and security evidence. Included the pending redirect review fix by replacing competition redirect string concatenation with the existing path-variable redirect style.
- **Files changed**:
  - `src/main/java/com/example/callthematch/controller/CompetitionController.java`
  - `src/test/java/com/example/callthematch/AccessSecurityMvcTests.java`
  - `src/test/java/com/example/callthematch/MatchManagementMvcTests.java`
  - `src/test/java/com/example/callthematch/dto/InputCompetitionDTOValidationTests.java`
  - `src/test/java/com/example/callthematch/validator/CompetitionValidatorTests.java`
  - `src/main/resources/plan/04-match-screen-and-admin-management/plan.json`
  - `src/main/resources/ralph/04-match-screen-and-admin-management/progress.md`
- **Verification**:
  - Ran `.\mvnw.cmd test` successfully after adding the closure tests.
  - Maven result: 37 tests run, 0 failures, 0 errors and 0 skipped.
  - Ran `git diff --check`; no whitespace errors were reported.
- **Status**: acceptance criteria verified for the two match closure test tasks and both `"passes"` flags set to `true`.
