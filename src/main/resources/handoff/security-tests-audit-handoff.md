# Security Tests Audit Handoff

Date: 2026-05-27  
Project: `call-the-match`

## Suggested Skills

- `project-guidelines`: use first. Security test decisions must be checked against the local EWD school conventions, security notes, and exercise projects.
- `diagnose`: use only if a security test starts failing or MockMvc authentication/redirect behavior becomes unclear.
- `handoff`: use again after security tests are refactored or new decisions are made.

## Purpose

This handoff captures the audit of `AccessSecurityTests`. The user wanted to know whether the security tests follow the guidelines and exercises, what test cases are missing, what is overdone, and what can definitely be cut.

## Evidence Sources

Primary project guideline:

- `.agents/skills/project-guidelines/references/testing.md`
  - Security tests should use `@WithMockUser`, `@WithAnonymousUser`, and form login helpers where appropriate.
  - Required coverage:
    - guest can access public pages
    - guest is redirected for user/admin routes
    - user can access user routes
    - user cannot access admin routes
    - admin can access admin routes
    - admin cannot use team/prediction flows in this project

Security guideline:

- `.agents/skills/project-guidelines/references/security-login.md`
  - Users log in with email.
  - Admin is not a normal user for team or prediction flows.
  - Public routes include home, ranking, public match detail, login/register, static assets, error pages, and public REST GET endpoints.
  - User routes include teams, predictions, and private scoreboards.
  - Admin routes include match add/edit/result management.

Exercise evidence:

- `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Security\Spring_Boot_security_roles\src\test\java\com\example\spring_boot_security_roles\controller\SecurityTest.java`
  - Uses `@WebMvcTest`, `@Import(SecurityConfig.class)`, `@WithMockUser`, `formLogin`, forbidden checks, and redirect checks.
- `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Security\Spring_Boot_security_JPA\src\test\java\com\example\spring_boot_security_jpa\controller\SecurityTest.java`
  - Uses `@SpringBootTest` and `@AutoConfigureMockMvc` for security tests involving the real app/security context.
- `C:\Users\Armour\Documents\HOGENT\EWD\Notes\24-04-26-Security.md`
  - Notes that `Principal` gives the name of the user.
  - Notes that login/register/static resources and chosen public endpoints must be permitted.
  - Notes that custom login field names require `.usernameParameter("email")`.
  - Notes that logout is handled by Spring Security and `login?logout` can be used for feedback.

## Files Audited

- `src/test/java/com/example/callthematch/security/AccessSecurityTests.java`
- `src/main/java/com/example/callthematch/config/SecurityConfig.java`

Related coverage already exists in:

- `src/test/java/com/example/callthematch/controller/AccountControllerTests.java`
- `src/test/java/com/example/callthematch/controller/CompetitionControllerTests.java`
- `src/test/java/com/example/callthematch/controller/PredictionControllerTests.java`
- `src/test/java/com/example/callthematch/controller/TeamControllerTests.java`
- `src/test/java/com/example/callthematch/validation/InputRegistrationDTOValidationTests.java`

## Verification Done

Security tests were run:

```powershell
.\mvnw.cmd -q "-Dtest=AccessSecurityTests" test
```

Result:

- `AccessSecurityTests`: 14 tests, 0 failures, 0 errors, 0 skipped.

## Overall Verdict

`AccessSecurityTests` is mostly school-conform.

It correctly uses:

- `@SpringBootTest`
- `@AutoConfigureMockMvc`
- `@WithMockUser`
- `@WithAnonymousUser`
- `formLogin`
- `logout`
- `authenticated()`
- `unauthenticated()`
- redirect checks
- forbidden checks

Using `@SpringBootTest` is heavier than the smallest security exercise, but defensible here because this project needs the real security config, real seeded users, real email login, and the full role matrix.

## What Is Good And Should Stay

Keep tests proving the project security matrix:

- Guest can open public screens:
  - `/home`
  - `/team/ranking`
  - `/competition/1`
  - `/login`
  - `/register`
- Guest is redirected to login for protected routes:
  - `/team/dashboard`
  - `/predictions/3`
  - `/team/1/scoreboard`
- Guest is redirected to login for admin write routes:
  - `POST /competition/add`
  - `POST /competition/edit/3`
  - `POST /competition/3/result`
- `USER` can open user routes:
  - `/team/dashboard`
  - `/predictions/3`
- `USER` cannot open admin match management routes.
- `ADMIN` can open admin match management routes.
- `ADMIN` cannot open team and prediction routes.
- Email login uses `user1@example.com` as the authenticated principal.
- Wrong password redirects to `/login?error`.
- Logout redirects to `/login?logout`.
- POST without CSRF returns forbidden.

## Definitely Cut

Cut `invalidRegistrationReturnsFieldErrors` from `AccessSecurityTests`.

Reason:

- This is registration validation/controller behavior, not security behavior.
- It belongs in `AccountControllerTests` and DTO validation tests.
- The project already has registration validation coverage elsewhere.

## Duplicate Mock-User Setup Explanation

The issue is not that there are separate tests for a mocked admin and a mocked user. That is correct and should stay.

The issue is setting the authenticated user twice in the same test:

```java
@Test
@WithMockUser(username = "user1@example.com", roles = "USER")
void userCanOpenUserRoutes() throws Exception {
    mockMvc.perform(get("/team/dashboard")
            .with(user("user1@example.com").roles("USER")))
            .andExpect(status().isOk());
}
```

This uses two mechanisms at once:

1. `@WithMockUser(...)` sets the security context for the full test method.
2. `.with(user(...))` sets/overrides the user for that specific MockMvc request.

Use one style per test.

Recommended for `AccessSecurityTests`:

```java
@Test
@WithMockUser(username = "user1@example.com", roles = "USER")
void userCanOpenUserRoutes() throws Exception {
    mockMvc.perform(get("/team/dashboard"))
            .andExpect(status().isOk());
}
```

Alternative, also valid:

```java
@Test
void userCanOpenUserRoutes() throws Exception {
    mockMvc.perform(get("/team/dashboard")
            .with(user("user1@example.com").roles("USER")))
            .andExpect(status().isOk());
}
```

Audit verdict:

- In the security matrix class, prefer `@WithMockUser`.
- In controller tests, `.with(user(...))` is often fine because individual requests may need different principals.

## Maybe Reduce

`guestCanLoadStaticJavaScriptAssets` can be merged into the public-access test if the suite feels too long.

However, keeping one static asset test is defensible because the security notes explicitly say static JS/CSS must be permitted.

## Missing Or Useful Test Cases To Add

1. Correct login redirect
   - Current test checks that login authenticates and username is email.
   - Add/assert redirect to `/home`, because `SecurityConfig` uses `.defaultSuccessUrl("/home", true)`.
   - This follows the security exercise style where correct login checks redirect.

2. Public REST GET endpoint
   - `SecurityConfig` permits `/api/**`.
   - Add a guest test for a public REST endpoint, for example:
     - `GET /api/2026-05-20/matches`
   - This proves REST remains public as required by the project guidelines.

3. Public error pages
   - `SecurityConfig` permits:
     - `/403**`
     - `/404**`
     - `/500**`
   - Add checks that guests can open these pages.

4. Unknown URL behavior
   - This is important because the user previously observed that `.anyRequest().hasRole("USER")` can redirect invalid guest URLs to login instead of showing a 404.
   - Add a test for a nonsense URL.
   - Expected school/project behavior should be decided, but likely:
     - guest `GET /does-not-exist` should return 404/error page, not redirect to login.

5. Admin POST-block on team/prediction flows
   - Current admin tests cover GET blocking for team/prediction routes.
   - Add one mutating route to prove admin cannot actually use the flows:
     - `POST /team/create` with CSRF as admin should be forbidden.
     - or `POST /predictions/3` with CSRF as admin should be forbidden.

## Separation Of Responsibilities

Use this split:

- Security tests:
  - route access
  - role matrix
  - login/logout behavior
  - CSRF enforcement
  - public route permission
- Controller tests:
  - status/view/model/redirect for one controller
  - controller service delegation
  - controller-specific form redisplay
- Validation tests:
  - DTO field errors
  - custom validators
  - validator advice behavior if needed
- Service tests:
  - current-user lookup by email
  - membership/owner checks
  - sorting/limiting
  - scoring

## Suggested Refactor Sequence

1. Delete `invalidRegistrationReturnsFieldErrors` from `AccessSecurityTests`.
2. Remove unused imports from `AccessSecurityTests`, especially `model` and `redirectedUrlPattern` if no longer used.
3. Pick one authentication style per test.
   - Prefer `@WithMockUser` in `AccessSecurityTests`.
   - Remove `.with(user(...))` where `@WithMockUser` already exists.
4. Add correct-login redirect assertion.
5. Add guest public REST and guest public error-page tests.
6. Add unknown URL behavior test.
7. Add one admin POST-block test for team or prediction mutation.
8. Re-run:

```powershell
.\mvnw.cmd -q "-Dtest=AccessSecurityTests" test
```

## Final Position

Keep `AccessSecurityTests`. It is useful and mostly follows the school pattern. The main cleanup is to make it a pure security matrix: remove validation behavior, avoid setting the mocked user twice, and add the few public/edge-route checks that directly protect the project-specific security decisions.
