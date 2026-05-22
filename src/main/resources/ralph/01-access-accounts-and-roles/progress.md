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
