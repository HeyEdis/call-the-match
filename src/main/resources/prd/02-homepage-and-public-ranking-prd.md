## Problem Statement

Guests and authenticated users need public entry points into the application. The home page must show the World Cup schedule and link to login, registration, the public top-10 teams, and authenticated user areas. The public ranking must remain available to everyone.

## Solution

Provide a public home page with sorted matches and role-aware navigation. Provide a public top-10 team ranking sorted by total team score with member count. Logged-in users see links to their teams and predictions. Admins additionally see match management entry points.

## User Stories

1. As a guest, I want to see all matches sorted by date, so that I can understand the tournament schedule.
2. As a guest, I want each match row to show both countries, date, time, stadium, and city, so that I have complete match context.
3. As a guest, I want to open a match detail page, so that I can inspect a specific match.
4. As a guest, I want links to login and registration, so that I can become a user.
5. As a guest, I want a link to the public top-10 ranking, so that I can compare teams.
6. As a user, I want links to my teams and predictions, so that I can continue my private workflow.
7. As an admin, I want a visible match management link, so that I can add and edit matches.
8. As anyone, I want the top-10 team list to show rank, team name, score, and member count, so that the ranking is understandable.
9. As anyone, I want the top-10 team list sorted by total score descending, so that the best teams are shown first.
10. As anyone, I want pages to render with shared navigation and styling, so that the app feels coherent.
11. As the application, I want null official scores to display cleanly, so that unplayed matches do not show confusing values.
12. As the application, I want dates and labels to use resource bundles where required, so that i18n requirements are satisfied.

## Implementation Decisions

- Keep the home page public.
- Keep the public ranking page public.
- Sort matches by date and time.
- Calculate team score from team members or a consistent scoring source.
- Make navigation role-aware instead of showing every link to every user.
- Use resource bundle entries for at least one complete public screen.
- Display official scores only when both scores are known.
- Ensure not-found and type-mismatch cases route to friendly error pages.

## Testing Decisions

- Controller tests should verify the home page returns the expected view and exposes the match list.
- Controller tests should verify the ranking page exposes only the top 10 teams in score order.
- Security tests should verify guests can access home and ranking.
- View-level behavior can be covered indirectly through model attributes and security visibility tests.

## Out of Scope

- Advanced filtering by country, group, or stadium.
- Pagination of match schedules.
- Live updates from official FIFA data.
- Visual redesign beyond what is needed to complete the school requirements.

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, pages 3 and 5: home page and public top-10 requirements.
2. Notes in `Project.md`: resource bundles, type mismatch messages, and global exception handling reminders.
3. Notes in `03-04-2026-ErrorMessageEnI18n.md`: language switching and resource bundle guidance.
4. Current repository implementation of home and ranking flows.
5. Git repository: https://github.com/HeyEdis/call-the-match.git.

## Further Notes

This functionality is mostly present already. The remaining work is role-aware navigation, cleaner score display, stronger ranking correctness, and i18n/error polish.
