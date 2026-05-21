## Problem Statement

The project is a Spring Boot web application for a school assignment about FIFA World Cup 2026 team predictions. Users must be able to create an account, form private teams with friends, submit match predictions, and compare scores. Admins must manage matches and official results. Guests must only access public information.

The current codebase already contains a useful JPA domain foundation, seed data, a public home page, team pages, invite-code behavior, match listing, and a partial match creation flow. However, the application is not yet safe to complete because authentication and authorization are missing. The current implementation also contains temporary user handling, incomplete validation, missing prediction/scoring behavior, missing REST/WebClient requirements, incomplete i18n, and almost no tests.

The goal is not to polish the application first. The goal is to implement everything needed to pass safely by the real project deadline of 27 May 2026. The deadline in the original FIFA PDF is ignored for planning purposes.

## Solution

Build a minimum-complete, school-compliant version of the FIFA World Cup prediction application. The implementation must prioritize required functionality and required technical evidence over visual polish.

The application will support three separate actors:

1. Guest: public information only.
2. User: registration, login, teams, invite codes, predictions, and private team scoreboards.
3. Admin: match management and official result entry only.

Admin is not treated as a normal user for team and prediction workflows. Admins can add and edit matches, enter official scores after matches, and manage match data. Users handle team and prediction workflows.

The implementation will follow the school patterns from the provided notes and example projects:

- Spring MVC controllers with Thymeleaf views.
- Service layer as the only layer that directly coordinates repository operations.
- JPA repositories for persistence.
- Request DTOs for form input and validation.
- Jakarta Validation with existing annotations, at least one custom annotation, and at least one validator class.
- Spring Security with database-backed users.
- Resource bundles for validation messages, labels, score constants, date formatting, and one complete screen.
- Global MVC error handling and REST error handling.
- REST API plus Reactive WebClient as a final feature block.
- Minimum required tests after feature completion, with validator and scoring code designed to be testable from the start.

## User Stories

1. As a guest, I want to view the home page, so that I can see public World Cup match information.
2. As a guest, I want to see matches sorted by date and time, so that the tournament schedule is clear.
3. As a guest, I want to see each match's countries, date, time, stadium, and city, so that I can understand the fixture.
4. As a guest, I want to open a public match detail page, so that I can inspect one match.
5. As a guest, I want to view the public top-10 teams, so that I can see the best-performing teams.
6. As a guest, I want to register an account, so that I can become a user.
7. As a guest, I want to log in using my email address, so that I can access user functionality.
8. As a guest, I want protected user pages to redirect me to login, so that access rules are clear.
9. As a user, I want to log out from every screen, so that I can safely end my session.
10. As a user, I want to see my username or role in the navigation, so that I know which account is active.
11. As a user, I want to create a team, so that I can compete with friends.
12. As a user, I want my team name to be unique, so that teams are identifiable.
13. As a user, I want a team invite code to be generated automatically, so that friends can join.
14. As a user, I want the invite code to contain at least eight characters, so that it satisfies the assignment requirement.
15. As a user, I want to become the owner of the team I create, so that I can manage membership.
16. As a user, I want to join a team with an invite code, so that I can participate in a friend's team.
17. As a user, I want invalid invite codes to show a clear message, so that I know why joining failed.
18. As a user, I want duplicate team membership to be prevented, so that I do not appear twice in the same team.
19. As a user, I want to see only teams I belong to, so that private team data is protected.
20. As a team member, I want to view my private team page, so that I can see team information.
21. As a team member, I want to see team name, member list, owner marker, personal scores, and total team score, so that team ranking is understandable.
22. As a team owner, I want to regenerate the invite code, so that I can invalidate older shared codes.
23. As a team owner, I want to remove members from my team, so that I can manage membership.
24. As a non-owner team member, I should not be able to perform owner-only actions, so that team control remains with the owner.
25. As a non-member, I should not access a private team page, so that team information remains private.
26. As a user, I want to submit a score prediction for a match, so that I can participate in the prediction game.
27. As a user, I want one active prediction per match, so that my latest valid prediction counts.
28. As a user, I want to update my prediction until one hour before kick-off, so that I can revise it before the deadline.
29. As a user, I want prediction editing to be blocked after the deadline, so that the game stays fair.
30. As a user, I want to see my prediction on the match detail page, so that I know what I submitted.
31. As a user, I want non-negative prediction score validation, so that invalid scores are rejected.
32. As a team member, I want a private team scoreboard, so that I can compare member scores.
33. As a team member, I want the scoreboard sorted by member score, so that the current leader is clear.
34. As a guest, I should not access private team scoreboards, so that team data is protected.
35. As a non-member, I should not access another team's scoreboard, so that private competition data remains private.
36. As an admin, I want to access match management screens, so that I can maintain tournament data.
37. As an admin, I want to add matches, so that the application has a schedule.
38. As an admin, I want to edit matches, so that incorrect match data can be fixed.
39. As an admin, I want to receive a redirect success message after adding or editing a match, so that I know the action succeeded.
40. As an admin, I want to enter official results after a match, so that predictions can be scored.
41. As an admin, I want official scores to be nullable before result entry, so that future matches can exist without results.
42. As an admin, I want match input validation, so that invalid match data cannot be saved.
43. As an admin, I want land A and land B to be different, so that impossible matches are rejected.
44. As an admin, I want match dates to be between 20 May 2026 and 6 June 2026, so that the schedule fits the chosen project demo period.
45. As an admin, I want stadium code and checksum validation, so that the custom validation requirement is met.
46. As an admin, I want matches at the same stadium and time to be rejected, so that scheduling conflicts are prevented.
47. As the application, I want exact score predictions to receive X points, so that precision is rewarded.
48. As the application, I want correct winner or draw predictions to receive Y points, so that partially correct predictions are rewarded.
49. As the application, I want a unique exact-score bonus B within a team, so that unique precision receives extra points.
50. As the application, I want a unique outcome bonus C within a team, so that unique outcome predictions receive extra points.
51. As the application, I want X, Y, B, and C stored in resource bundles, so that scoring constants satisfy the assignment requirement.
52. As the application, I want score calculation to run automatically when official results are saved, so that rankings remain up to date.
53. As anyone, I want the public top-10 ranking to reflect recalculated team totals, so that public rankings are meaningful.
54. As a REST client, I want to retrieve matches for a specific date, so that match data is available through an API.
55. As a REST client, I want to retrieve stadium capacity, so that stadium information is available through an API.
56. As the application, I want REST errors returned as structured JSON, so that API errors are consistent.
57. As the application, I want a WebClient demo/client to call the REST endpoints, so that the Reactive WebClient requirement is satisfied.
58. As the application, I want custom error pages for not found, forbidden, and server errors, so that failures are handled cleanly.
59. As the application, I want type mismatch errors handled, so that invalid path variables do not show raw error pages.
60. As the application, I want one full screen translated through resource bundles, so that the i18n requirement is demonstrable.

## Implementation Decisions

- The master PRD is the source of truth for implementation planning.
- Existing older PRD files in the PRD folder are left in place as earlier iterations and are not deleted.
- The real planning deadline is 27 May 2026.
- The implementation optimizes for safe passing first and polish later.
- User login uses email, not username.
- Registration is required and should be implemented minimally using the school security and registration examples referenced in the notes.
- Registered accounts receive the `USER` role by default.
- Admin users are seeded or managed separately and have only admin responsibilities.
- Admin is not considered a normal user in team or prediction workflows.
- Public routes include home, public ranking, public match detail, login, registration, static resources, error pages, and public REST GET endpoints.
- User-only routes include team management, team scoreboards, and prediction actions.
- Admin-only routes include match add, match edit, official result entry, and match management.
- Use Spring Security with a security filter chain, custom login page, logout, CSRF protection, access denied handling, and database-backed user details.
- Use a dedicated security user details service for authentication.
- Keep non-security user operations in a separate user service.
- Convert domain roles to Spring authorities with the `ROLE_` prefix.
- Use the authenticated principal instead of hardcoded temporary user ids.
- Keep service classes as the coordinating layer between controllers and repositories.
- Keep request validation in DTOs and validator classes, not in Thymeleaf views.
- Official match scores are nullable until entered by an admin.
- The match validity period is 20 May 2026 through 6 June 2026.
- Use scoring values `exactScore=5`, `correctOutcome=2`, `uniqueExactBonus=3`, and `uniqueOutcomeBonus=1`.
- Store scoring constants in resource bundles.
- Treat member prediction points as the source of truth for team score; stored team totals may be recalculated but should not be manually edited.
- Automatically recalculate prediction, member, and team scores after official result entry.
- Implement `@ValidStadiumChecksum` as the custom validation annotation.
- Implement a `CompetitionValidator` for cross-field and repository-backed checks, including different countries, valid date range, and no same stadium/time conflict.
- Implement converters or formatters for country and stadium form binding.
- Implement REST and Reactive WebClient as a separate final functional block after core MVC features.
- Implement the required test suite near the end, after the main features are stable, while keeping validators and scoring logic isolated and easy to test from the start.
- Use the competition add/edit screen as the full resource-bundle-backed screen.
- Out-of-scope items for the passing version include email invitations, password reset, OAuth, live FIFA integrations, team logos, advanced dashboards, and optional detailed per-match score breakdowns.

## Testing Decisions

- Required tests must cover MVC controllers, REST controllers, security, and validation.
- Validation tests must include existing annotations, the custom annotation, and validator classes.
- Security tests must cover guest access, user-only access, admin-only access, redirects to login, and forbidden access.
- MVC controller tests must cover public home/ranking behavior and key form flows for teams, matches, and predictions.
- REST controller tests must cover successful JSON responses and structured error responses.
- Scoring service tests should be added because scoring is high risk and easy to test in isolation.
- Tests should verify external behavior: status codes, redirects, view names, model attributes, JSON fields, validation errors, and visible authorization outcomes.
- Tests should follow the school examples using MockMvc, Spring Security test support, WebMvcTest where appropriate, and full context tests only where security or integration setup requires it.
- Most tests can be written as the penultimate implementation block, but validator and scoring code should be designed in small isolated modules so the test phase remains fast.

## Out of Scope

- Visual polish beyond usable Thymeleaf pages and clear navigation.
- Email service for invite codes.
- Password reset.
- OAuth or external identity providers.
- Live FIFA data import.
- Full tournament bracket generation.
- Advanced per-match scoreboard details unless all required work is complete early.
- Team logos, avatars, or extensive profile management.
- Admin acting as a normal user in team or prediction flows.
- Production deployment hardening.

## Sources

1. FIFA World Cup 2026 Team Prediction PDF: functional requirements for roles, home page, team management, match screen, admin match management, predictions, scoring, private scoreboard, public top-10, and technical requirements.
2. `27-02-26-Spring-Boot.md`: Spring Boot MVC basics, service classes, DTO folders, and controller/view flow.
3. `06-03-26-SpringBoot-Les2.md`: controller simplification and Thymeleaf key usage.
4. `13-03-26-Validation.md`: DTO validation, BindingResult, field errors, custom validation, and resource bundle messages.
5. `03-04-2026-ErrorMessageEnI18n.md`: validation message keys, type mismatch messages, fragments, and language switching.
6. `03-04-2026-MySQL.md`: JPA repository guidance, service-repository separation, named query conventions, and MySQL setup.
7. `24-04-26-Security.md`: security config, custom login, CSRF, principal usage, role display, 403 handling, and registration/login project references.
8. `08-05-26-REST.md`: REST requirements, dummy/user endpoint note, RestControllerAdvice, WebClient client runner, and JSON infinite loop handling.
9. `Project.md`: project-specific reminders for setup, validations, resource bundles, REST, client runner, typeMismatch, and global exception advice.
10. Richtlijnen folder PDFs: Spring Boot, JPA/MySQL, validation, i18n, exceptions, multiple row, security, security JDBC/JPA, REST/WebClient, and testing guidance.
11. WorkspacesIntelij examples: school implementation patterns for security JPA, registration/login, validators, i18n, REST, WebClient, MVC tests, REST tests, and security tests.
12. Current codebase: existing JPA models, repositories, services, controllers, Thymeleaf views, resource bundle, seed data, and partial team/match flows.
13. Git repository: https://github.com/HeyEdis/call-the-match.git.

## Further Notes

The implementation plan should not follow the PDF order blindly. It should follow dependency order:

1. Authentication and authorization first.
2. Public shell and role-aware navigation.
3. User team flows.
4. Admin match flows and validation.
5. User predictions.
6. Score calculation and private/public rankings.
7. Tests.
8. REST API and WebClient.

REST/WebClient is intentionally kept as a separate late block because the user prefers not to interleave it with MVC feature development. Tests are also a late block, but validators and scoring should be designed as deep, isolated modules so they can be tested quickly when the test phase starts.
