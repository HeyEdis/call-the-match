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

### 2026-05-22 - Protect Admin Routes And Forbidden Access

- **Task**: `protect-admin-routes-and-forbidden-access` - Protect Admin Routes And Forbidden Access
- **What changed**: added ADMIN-only match-management matchers for competition add/edit and future result paths, wired Spring Security forbidden handling to `/403`, and added a minimal MVC route that renders the existing `error/403` template.
- **Decisions**: admin matchers are placed before the public single-segment competition detail matcher so `/competition/add` cannot be swallowed by `/competition/{id}`. Team and prediction paths continue to require role `USER`, which keeps ADMIN out of participation flows while roles remain single-role authorities.
- **Files changed**:
  - `src/main/java/com/example/callthematch/config/SecurityConfig.java`
  - `src/main/java/com/example/callthematch/controller/ErrorPageController.java`
  - `src/main/resources/plan/01-access-accounts-and-roles/plan.json`
  - `src/main/resources/ralph/01-access-accounts-and-roles/progress.md`
- **Verification**:
  - Approved direct local Maven 3.9.14 run completed with `BUILD SUCCESS`: `mvn.cmd test`.
  - The security config was inspected to confirm ADMIN matchers run before public competition detail, USER-only `/team/**` and `/predictions/**` matchers remain intact, and forbidden access targets `/403`.
  - Route-specific security tests remain scheduled for the later security closure task.
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

### 2026-05-22 - Add Account Entry UI And Registration Validation

- **Tasks**:
  - `build-custom-email-login-and-logout-ui` - Build Custom Email Login And Logout UI
  - `make-shared-navigation-role-aware` - Make Shared Navigation Role-Aware
  - `build-registration-form-with-validation` - Build Registration Form With Validation
- **What changed**: configured the custom email login page and logout redirect, added Thymeleaf login and registration screens, changed the shared navbar to show guest, USER and ADMIN actions through Spring Security view attributes, and introduced a registration request DTO/controller slice with Jakarta Validation field errors and bundle-backed copy.
- **Decisions**: this AFK iteration keeps registration at validated form behavior only. Persisting a registered account, encoding its password and assigning default role USER stay in the next planned persistence task.
- **Files changed**:
  - `src/main/java/com/example/callthematch/config/SecurityConfig.java`
  - `src/main/java/com/example/callthematch/controller/AccountController.java`
  - `src/main/java/com/example/callthematch/dto/request/InputRegistrationDTO.java`
  - `src/main/resources/templates/account/login.html`
  - `src/main/resources/templates/account/register.html`
  - `src/main/resources/templates/fragments/navbar.html`
  - `src/main/resources/i18n/messages.properties`
  - `src/main/resources/plan/01-access-accounts-and-roles/plan.json`
  - `src/main/resources/ralph/01-access-accounts-and-roles/progress.md`
- **Verification**:
  - The first sandboxed direct Maven run could not resolve the Spring Boot parent because network access to Maven Central was denied.
  - Approved direct local Maven 3.9.14 run completed with `BUILD SUCCESS`: `mvn.cmd test`.
  - The run compiled the new MVC controller, DTO and templates through Spring context startup. Existing Hibernate `create-drop` missing-table DDL warnings remain noisy but did not fail the run.
  - Focused security and registration MVC tests remain scheduled for the later closure-test task.
- **Status**: acceptance criteria verified for these three tasks and their `"passes"` flags set to `true`.
