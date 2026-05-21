# Plan: Access Accounts And Roles

> Source PRD: `src/main/resources/prd/01-access-accounts-and-roles-prd.md`

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, pages 2-5: guest, user, and admin role requirements.
2. `24-04-26-Security.md`: custom login, CSRF, principal usage, role display, 403 handling, and registration/login project guidance.
3. `Project.md`: REST endpoints that must be permitted and project-level exception handling reminders.
4. WorkspacesIntelij security examples: Spring Security JPA, custom login, password encoding, and security tests.
5. Git repository: https://github.com/HeyEdis/call-the-match.git.

## Architectural Decisions

- **Deadline**: plan for safe completion by 27 May 2026.
- **Actors**: guest, user, and admin remain separate.
- **Admin scope**: admin does not act as a normal user for teams or predictions.
- **Login identifier**: users log in with email.
- **Routes**: public routes include `/`, `/home`, `/ranking`, `/competition/{id}`, `/login`, `/register`, static resources, error pages, and public REST GET endpoints.
- **User routes**: `/team/**`, `/predictions/**`, and private scoreboards.
- **Admin routes**: match add/edit/result management and match management screens.
- **Security**: Spring Security with a security filter chain, custom login, logout, CSRF, access denied page, and database-backed user details.
- **Key models**: `User`, `Role`.
- **Testing timing**: implement the feature first, then cover with security tests in the project test block.

---

## Phase 1: Security Dependency And Encoded Users

**User stories**: 3, 4, 6, 12, 14

### What To Build

Add the required security dependencies and make the existing user model usable for authentication with encoded passwords and email lookup. Seed at least one admin and one user with valid encoded passwords for development and demo.

### Acceptance Criteria

- [ ] Security dependency is present.
- [ ] Passwords used by seeded accounts are encoded.
- [ ] Users can be looked up by email.
- [ ] User roles map to Spring Security authorities.
- [ ] Existing application still starts with seed data.

---

## Phase 2: Login Logout And Public Access

**User stories**: 1, 2, 4, 5, 7, 13, 14

### What To Build

Add a custom login page, logout behavior, public route access, and 403 handling. Guests must still reach home, ranking, public match detail, login, registration, static resources, error pages, and public REST GET endpoints.

### Acceptance Criteria

- [ ] Guest can open public pages.
- [ ] Guest is redirected to login for user-only routes.
- [ ] Login works with email and password.
- [ ] Logout is available and works from shared navigation.
- [ ] Forbidden access renders the custom 403 page.
- [ ] CSRF is supported in forms.

---

## Phase 3: Registration And Current User

**User stories**: 3, 6, 8, 12

### What To Build

Implement minimal registration for normal users and expose the authenticated user to controllers and Thymeleaf. Replace temporary user-id usage with the logged-in user.

### Acceptance Criteria

- [ ] Guest can register a user account.
- [ ] New accounts receive role `USER`.
- [ ] Registration validates required fields and matching passwords.
- [ ] Logged-in username or role is visible in navigation.
- [ ] User-owned actions can resolve the current user without hardcoded ids.

---

## Phase 4: Role Boundaries

**User stories**: 5, 9, 10, 11, 13

### What To Build

Enforce the final route boundaries: users cannot access admin match management, admins do not use team/prediction flows, and guests remain public-only.

### Acceptance Criteria

- [ ] Guest cannot access user-only or admin-only flows.
- [ ] User can access team and prediction routes.
- [ ] User cannot access admin match management.
- [ ] Admin can access match management.
- [ ] Admin is blocked from user team/prediction workflows unless explicitly public.
- [ ] Navigation only shows relevant actions for the current actor.

---

## Phase 5: Security Test Closure

**User stories**: 1-14

### What To Build

Add focused security tests after the main feature behavior is stable.

### Acceptance Criteria

- [ ] Anonymous/public route tests exist.
- [ ] Anonymous protected route redirect tests exist.
- [ ] User access and forbidden admin route tests exist.
- [ ] Admin access and blocked user-flow tests exist.
- [ ] Login/logout behavior is covered.
