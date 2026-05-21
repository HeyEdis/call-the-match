## Problem Statement

The application needs a clear access model for three actors: guest, user, and admin. At the moment, domain roles exist, but the application does not yet enforce authentication, authorization, login, logout, or a reliable current-user flow. This blocks team membership, private scoreboards, predictions, and admin-only match management.

## Solution

Implement account access with Spring Security so guests can only use public screens, users can manage teams and predictions, and admins can manage matches and official results. The login/logout experience must be visible and consistent across screens, and the application must expose the current user's role where needed.

## User Stories

1. As a guest, I want to view public World Cup information, so that I can explore the application without an account.
2. As a guest, I want to view the public top-10 teams, so that I can see the current ranking.
3. As a guest, I want to register an account, so that I can join teams and submit predictions.
4. As a guest, I want to log in, so that I can access user-only functionality.
5. As a guest, I want protected pages to redirect me to login, so that I understand I need an account.
6. As a user, I want to stay authenticated during my session, so that team and prediction actions are linked to me.
7. As a user, I want to log out from every screen, so that I can safely end my session.
8. As a user, I want to see my username or role in the UI, so that I know which account is active.
9. As a user, I want admin-only pages to be blocked for me, so that I cannot manage matches.
10. As an admin, I want access to match management screens, so that I can maintain official match data.
11. As an admin, I want user-only screens to remain available when relevant, so that I can inspect the public and authenticated experience.
12. As the application, I want to use the authenticated principal instead of temporary user ids, so that data is connected to the real logged-in user.
13. As the application, I want a custom access denied page, so that forbidden actions are handled cleanly.
14. As the application, I want static resources and public endpoints to remain accessible, so that pages render correctly before login.

## Implementation Decisions

- Use Spring Security with a security filter chain.
- Use form login with a custom login screen and a default post-login redirect.
- Use logout with a visible logout control in shared navigation.
- Use database-backed user details through a user details service.
- Store encoded passwords instead of plain text passwords.
- Convert the domain role enum to Spring authorities with the `ROLE_` prefix.
- Keep user account logic separate from security-specific user lookup.
- Use a global model attribute or advice to expose username and role to Thymeleaf views.
- Permit public screens, login, registration, static resources, error pages, and REST endpoints that must remain public.
- Restrict user flows to authenticated users with the user role.
- Restrict match management and official result management to admins.
- Replace temporary user ids with the authenticated user in all user-owned actions.

## Testing Decisions

- Security tests should verify external behavior: redirects, allowed pages, forbidden pages, and successful login/logout.
- Test anonymous access to public pages.
- Test anonymous users are redirected away from user-only and admin-only pages.
- Test users can access team and prediction pages but not admin match management.
- Test admins can access match management.
- Use the security testing patterns from the school security examples with mocked users and MockMvc.

## Out of Scope

- Email verification.
- Password reset flow.
- OAuth or external identity providers.
- Multi-role users.
- Account profile management beyond what is required for registration and login.

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, pages 2-5: roles and required feature access.
2. Security notes from `24-04-26-Security.md`: custom login, CSRF, role display, 403 handling, and user details guidance.
3. School security examples in WorkspacesIntelij: Spring Security JPA patterns, user details service, password encoding, and security tests.
4. Git repository: https://github.com/HeyEdis/call-the-match.git.

## Further Notes

This is the first implementation priority because team ownership, membership, predictions, and admin match management all depend on reliable authentication and authorization.
