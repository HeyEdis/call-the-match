# Ralph Progress Log: 03-team-management

Each iteration appends what was done, decisions made, files changed, verification results, and blockers.

## Entries

### 2026-05-22 - Scope Team Dashboard To Current User

- **Task**: `scope-team-dashboard-to-current-user` - Scope Team Dashboard To Current User
- **What changed**: switched `/team/dashboard` from the all-teams service path to a current-user membership path. `TeamService` resolves the authenticated user, asks `TeamMemberRepository` for that user's persisted memberships and maps only those teams into dashboard DTO rows.
- **Decisions**: kept this HITL slice limited to the existing dashboard route and security boundary. Team create, join validation, detail membership checks and owner-only controls remain later plan tasks.
- **Files changed**:
  - `src/main/java/com/example/callthematch/controller/TeamController.java`
  - `src/main/java/com/example/callthematch/service/TeamService.java`
  - `src/main/java/com/example/callthematch/repository/TeamMemberRepository.java`
  - `src/test/java/com/example/callthematch/AccessSecurityMvcTests.java`
  - `src/main/resources/plan/03-team-management/plan.json`
  - `src/main/resources/ralph/03-team-management/progress.md`
- **Verification**:
  - `.\mvnw.cmd test` did not start Maven because the wrapper PowerShell startup path failed with `Cannot index into a null array`.
  - The first approved direct Maven 3.9.14 run reached tests and exposed that the existing USER route test used non-seeded `user@example.com`; the test now uses seeded member `user1@example.com` so current-user membership lookup is exercised.
  - Approved direct local Maven 3.9.14 rerun completed with `BUILD SUCCESS`: `mvn.cmd test`.
  - Final test result: 11 tests run, 0 failures and 0 errors.
  - The Spring Security route matrix still restricts `/team/**` to role `USER`, which keeps guests redirected and ADMIN actors forbidden from the dashboard.
  - Existing Hibernate missing-table DDL warnings and Mockito agent warnings remain noisy in the passing build output.
- **Status**: acceptance criteria verified for this dashboard task and `"passes"` set to `true`.

### 2026-05-22 - Build Team Create And Invite Join Slices

- **Tasks**:
  - `build-team-create-form-validation` - Build Team Create Form Validation
  - `persist-created-team-with-owner-membership` - Persist Created Team With Owner Membership
  - `build-invite-code-join-form-validation` - Build Invite Code Join Form Validation
  - `join-team-from-authenticated-invite-code` - Join Team From Authenticated Invite Code
- **What changed**: converted the dashboard create and invite-join areas into DTO-backed Thymeleaf forms with Jakarta required-input validation and field feedback; added controller validation flows that keep duplicate team names and invalid invitecodes on the dashboard; added bundle-backed team labels, validation messages and flash feedback; and added service persistence for authenticated team creation with unique invitecode generation plus owner membership creation.
- **Decisions**: kept the actor boundary on existing `/team/**` role security and current-user service lookup. Duplicate joins still stay idempotent through the existing membership existence check, while user-facing join success/failure strings now come from the message bundle.
- **Files changed**:
  - `src/main/java/com/example/callthematch/controller/TeamController.java`
  - `src/main/java/com/example/callthematch/dto/request/InputTeamDTO.java`
  - `src/main/java/com/example/callthematch/dto/request/InputTeamJoinDTO.java`
  - `src/main/java/com/example/callthematch/exception/TeamNameAlreadyExists.java`
  - `src/main/java/com/example/callthematch/repository/TeamRepository.java`
  - `src/main/java/com/example/callthematch/service/TeamService.java`
  - `src/main/resources/i18n/messages.properties`
  - `src/main/resources/templates/team/dashboard.html`
  - `src/main/resources/plan/03-team-management/plan.json`
  - `src/main/resources/ralph/03-team-management/progress.md`
- **Verification**:
  - Approved direct local Maven 3.9.14 run completed with `BUILD SUCCESS`: `mvn.cmd test`.
  - Final test result: 11 tests run, 0 failures and 0 errors.
  - The dashboard template and controller validation flow were inspected to confirm both forms bind request DTOs, render `th:errors`, keep `BindingResult` immediately after `@Valid`, and return dashboard model data on field and service-rule feedback.
  - The create service path was inspected to confirm the owner comes from `UserService.getCurrentUser()`, invitecodes are generated until repository uniqueness passes, and an OWNER `TeamMember` is persisted with the new team.
  - The join service path was inspected to confirm invitecode lookup stays repository-backed, actor lookup stays authenticated-current-user based, and duplicate membership remains blocked before saving a new `TeamMember`.
  - The existing Spring Security route matrix still excludes guests and ADMIN actors from all `/team/**` create and join paths.
  - Existing Hibernate missing-table DDL warnings and Mockito agent warnings remain noisy in the passing build output.
- **Status**: acceptance criteria verified for these four tasks and their `"passes"` flags set to `true`.
