# Ralph Progress Log: 01-access-accounts-and-roles

Each iteration appends what was done, decisions made, files changed, verification results, and blockers.

## Entries

### 2026-05-22 - Add Email-Based Security Identity

- **Task**: `add-email-based-security-identity` - Add Email-Based Security Identity
- **What changed**: added email lookup to the user repository, introduced a dedicated Spring Security `UserDetailsService`, added a BCrypt `PasswordEncoder` bean and encoded seeded dev passwords before persistence.
- **Decisions**: kept this HITL iteration limited to authentication identity plumbing. Route matchers, login UI and current-user team wiring stay in later access tasks.
- **Files changed**:
  - `src/main/java/com/example/callthematch/repository/UserRepository.java`
  - `src/main/java/com/example/callthematch/service/SecurityUserDetailsService.java`
  - `src/main/java/com/example/callthematch/config/SecurityBeansConfig.java`
  - `src/main/java/com/example/callthematch/config/InitDataConfig.java`
  - `src/main/resources/plan/01-access-accounts-and-roles/plan.json`
- **Verification**:
  - `.\mvnw.cmd test` did not start Maven because the current wrapper script failed in its embedded PowerShell bootstrap with `Cannot index into a null array`.
  - A direct local Maven 3.9.14 wrapper-cache binary was used instead: `mvn.cmd test`.
  - The first sandboxed direct Maven run was blocked while fetching missing dependencies; the approved rerun completed successfully with `BUILD SUCCESS`.
  - The passing test run started the Spring context and logged the global authentication manager using the `securityUserDetailsService` bean.
- **Status**: acceptance criteria verified for this task and `"passes"` set to `true`.

### 2026-05-22 - Protect Public And User Routes

- **Task**: `protect-public-and-user-routes` - Protect Public And User Routes
- **What changed**: added the first `SecurityFilterChain` route matrix. Public MVC entry points, login/register placeholders, static CSS and error paths remain permitted while `/team/**` and future `/predictions/**` paths require role `USER`.
- **Decisions**: kept admin match-route restrictions and custom email login UI out of this task because they belong to later Ralph tasks. Default Spring Security form login stays in place until the custom login task.
- **Files changed**:
  - `src/main/java/com/example/callthematch/config/SecurityConfig.java`
  - `src/main/resources/plan/01-access-accounts-and-roles/plan.json`
  - `src/main/resources/ralph/01-access-accounts-and-roles/progress.md`
- **Verification**:
  - A sandboxed direct Maven run was blocked while Maven attempted central access.
  - Approved direct local Maven 3.9.14 run completed with `BUILD SUCCESS`: `mvn.cmd test`.
  - The route matrix was inspected after the run: public matchers are explicit and the USER-only matchers reserve both team and prediction paths.
- **Status**: acceptance criteria verified for this task and `"passes"` set to `true`.
