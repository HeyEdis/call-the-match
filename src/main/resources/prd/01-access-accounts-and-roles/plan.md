# Plan: Access, Accounts And Roles

> Source PRD: `src/main/resources/prd/01-access-accounts-and-roles-prd.md`

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, pages 2 and 6: roles and security requirements.
2. School guidelines: `Slides_Spring_Security.pdf` and `Slides_Spring_Security_JDBC.pdf`.
3. Lesson notes: `24-04-26-Security.md` and `Project.md`.
4. Exercise projects identified for security patterns: `Spring_Boot_security_JPA`, `Spring_Boot_security_Form`, and `Spring_Boot_security_roles`.
5. Existing `call-the-match` codebase: current user model, role model, build dependencies, seeded users and team controller state.
6. Git repository URL: `https://github.com/HeyEdis/call-the-match.git`.
7. User/project decisions from this conversation and local skills: email login, `USER` registration default, admin separation and real deadline 27 May 2026.

## Architectural Decisions

Durable decisions that apply across all phases:

- **Routes**: public MVC routes include `/home`, `/ranking`, public competition detail, `/login`, `/register`, static resources and error pages. User-only routes include `/team/**` and later `/predictions/**`. Admin-only routes include match creation, match editing and official result management.
- **Schema**: `User` remains the authentication anchor with email, encoded password hash and `Role`. User-owned team and prediction actions resolve the authenticated user rather than accepting a hardcoded or caller-chosen user id.
- **Key models**: `User`, `Role`, a registration request DTO and a current-user/security lookup boundary. Existing `Team` and `TeamMember` flows are the first consumer of authenticated user ownership.
- **Security**: use email login, a database-backed user details service, role-to-authority mapping, CSRF protection, visible logout, a custom login page and school-style 403 handling. Admin remains separate from normal user team and prediction flows.
- **Validation/i18n**: registration form input uses Jakarta Validation on a DTO and resource bundle messages. Login/registration/access feedback should not rely on hardcoded controller text where bundle messages belong.
- **REST/WebClient**: REST implementation is deferred. The security route matrix must leave room for later public REST GET endpoints without making this feature depend on WebClient work.
- **Testing**: tests close the feature late with required security coverage and relevant MVC behavior for public access, protected access, login/registration boundaries and forbidden actor combinations.

---

## Phase 1: Email Authentication Foundation

**User stories**: `2`, `8`

### What To Build

Make the existing `User` and `Role` model usable as the identity source for Spring Security. This slice connects JPA-backed email lookup, password encoding and authority mapping so later login and route protection rest on real project data instead of framework defaults.

### Acceptance Criteria

- [ ] Existing user data can be loaded for authentication by email through the repository/service path used by Spring Security.
- [ ] Passwords used for authentication are encoded with a school-style password encoder and are not stored or compared as plain text.
- [ ] Domain roles map consistently to user and admin authorities so later route rules can distinguish both actors.
- [ ] This slice keeps repository access out of MVC controllers and exposes a reusable authentication boundary for later phases.

---

## Phase 2: Public And Protected Route Matrix

**User stories**: `3`, `4`, `9`, `10`, `11`, `12`

### What To Build

Define the first complete access matrix in the security filter chain. Public browsing remains open, user flows and admin match-management flows are protected explicitly, and forbidden access gets a school-style MVC path instead of accidental default behavior.

### Acceptance Criteria

- [ ] Guest access remains available for public home, public ranking, public competition detail, login, registration, static resources and error pages.
- [ ] Team and prediction route groups are user-only and do not become public by accident.
- [ ] Match-management route groups are admin-only, while normal users cannot mutate official match data.
- [ ] Admin access does not grant team or prediction participation in the route matrix.
- [ ] Forbidden MVC access resolves to the chosen school-style 403 behavior and not a raw default error.
- [ ] The route decision leaves public REST GET endpoints as an explicit later security addition rather than interleaving REST implementation now.

---

## Phase 3: Login, Logout And Role-Aware Navigation

**User stories**: `2`, `6`, `7`

### What To Build

Expose the security baseline through the UI. Add the custom login path, logout action and shared Thymeleaf navigation that reflects authentication state and actor role while keeping the public shell usable.

### Acceptance Criteria

- [ ] The app provides a custom login screen and the security flow accepts email credentials.
- [ ] Logout is reachable from shared authenticated navigation and mutating logout behavior respects CSRF/security form expectations.
- [ ] Shared navigation distinguishes guest, user and admin entry points instead of showing every private and admin action to everyone.
- [ ] The authenticated account or role context is available to Thymeleaf through a school-style model/security mechanism where the UI needs it.
- [ ] User-facing login/logout copy and shared labels that belong in bundles are prepared for resource-bundle use.

---

## Phase 4: Registration With Default USER Role

**User stories**: `1`, `5`

### What To Build

Add the minimal registration vertical slice. A guest submits validated form data, the service persists a normal account with an encoded password and role `USER`, and the MVC flow returns clear validation and success behavior.

### Acceptance Criteria

- [ ] Registration has guest-facing MVC and Thymeleaf form behavior backed by a request DTO rather than direct entity binding.
- [ ] Existing Jakarta Validation annotations cover required registration input and field errors can render beside the form inputs.
- [ ] Validation messages and user-facing registration feedback use resource bundle keys where school conventions require them.
- [ ] A successfully registered account is persisted with an encoded password and default role `USER`.
- [ ] Registration does not create or expose admin privileges and keeps normal user/account concerns separate from authentication lookup concerns.

---

## Phase 5: Authenticated Current User For User-Owned Actions

**User stories**: `8`

### What To Build

Connect the new identity boundary to an existing user-owned action. Replace the temporary join-user assumption in the team path with current-user lookup so the access work immediately proves value for the next team-management feature.

### Acceptance Criteria

- [ ] User-owned team behavior no longer relies on a hardcoded temporary user id.
- [ ] The current authenticated user boundary can be reused by later team and prediction features without controllers querying repositories directly.
- [ ] Guest, user and admin behavior remains explicit for the touched team action: guest cannot use it, user ownership resolves from authentication and admin is not admitted into team participation.
- [ ] Error and redirect behavior for the touched MVC path remains compatible with later team-management validation and resource-bundle work.

---

## Phase 6: Security Closure Tests

**User stories**: `3`, `4`, `5`, `9`, `10`, `11`, `12`

### What To Build

Close the feature with focused automated verification for the security boundary and the MVC behavior introduced by this plan. Keep this as the late closure block so the route matrix and form surfaces are stable before tests are broadened.

### Acceptance Criteria

- [ ] Required security tests cover guest access to public screens and denial or redirect behavior for protected flows.
- [ ] Security tests cover user access to user routes, user denial from admin routes and admin access to match-management routes.
- [ ] Security tests cover that admin is not allowed into team or prediction participation routes for this project.
- [ ] MVC-oriented tests cover the relevant login/registration form surface or controller behavior introduced by this feature where it is observable without brittle UI assertions.
- [ ] Validation test coverage for registration documents existing Jakarta annotation behavior if registration validation is introduced in this plan.
- [ ] The feature no longer relies on `contextLoads` as its only verification evidence.
