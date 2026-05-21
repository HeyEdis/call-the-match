## Problem Statement

The core application promise is that users predict World Cup match scores with friends and compare results. The data model already contains predictions, team members, and scores, but the user prediction flow, deadline rule, point calculation, bonus logic, private scoreboard, and score updates are not complete.

## Solution

Allow authenticated users to create or update one prediction per match until one hour before kick-off. When an admin enters an official result, calculate user points and team/member scores using configurable values from resource bundles. Show a private team scoreboard to team members and keep the public top-10 ranking based on team totals.

## User Stories

1. As a user, I want to predict goals for team A and team B, so that I can participate in the game.
2. As a user, I want one prediction per match, so that my latest valid prediction counts.
3. As a user, I want to update my prediction until one hour before kick-off, so that I can revise my choice.
4. As a user, I want prediction editing to be blocked after the deadline, so that the game is fair.
5. As a user, I want to see my prediction on the match detail screen, so that I know what I submitted.
6. As a user, I want validation for non-negative goal values, so that invalid predictions are rejected.
7. As an admin, I want official results to trigger point calculation, so that scores become available after matches.
8. As a user, I want exact score predictions to receive X points, so that precision is rewarded.
9. As a user, I want correct winner or draw predictions to receive Y points, so that partly correct predictions are rewarded.
10. As a user, I want bonus B when I am the only team member with the exact score, so that unique precision is rewarded.
11. As a user, I want bonus C when I am the only team member with the correct winner or draw, so that unique outcome insight is rewarded.
12. As the application, I want X, Y, B, and C defined in resource bundles, so that point values are configurable and documented.
13. As a team member, I want a private scoreboard, so that I can compare scores within my team.
14. As a team member, I want the scoreboard to show total score per member, so that rankings are clear.
15. As a team member, I want the scoreboard sorted by score, so that the current leader is visible.
16. As a team member, I want optional match-level details, so that I can understand where points came from if time permits.
17. As a guest, I should not access private team scoreboards, so that team data remains protected.
18. As a non-member, I should not access another team's scoreboard, so that private competition data is protected.
19. As the public ranking, I want team totals to reflect member prediction points, so that top-10 rankings are meaningful.

## Implementation Decisions

- Store one prediction per user and match.
- Use authenticated user identity for creating and updating predictions.
- Keep prediction validation in request DTOs.
- Check the one-hour deadline in the service layer before saving.
- Treat official scores as the trigger for recalculation.
- Encapsulate score calculation in a dedicated scoring service with a simple public interface.
- Read X, Y, B, and C from resource bundles or configuration-backed message values as required by the assignment.
- Recalculate affected predictions and member/team scores after an official result changes.
- Restrict private scoreboards to team members.
- Keep public top-10 based on team total score.
- Handle matches without official scores as pending and avoid assigning points prematurely.

## Testing Decisions

- Unit tests should cover exact score points, winner/draw points, no points, exact-score bonus, winner bonus, and ties where no bonus is given.
- Service tests should cover prediction creation, update before deadline, rejection after deadline, and recalculation after official result entry.
- Controller tests should cover prediction form rendering, invalid prediction input, valid submit, and deadline errors.
- Security tests should verify prediction pages are user-only and private scoreboards are member-only.
- Ranking tests should verify public top-10 reflects recalculated team scores.

## Out of Scope

- Prediction comments or explanations.
- Live match state.
- Advanced tiebreaker rules for public rankings.
- Detailed per-match scoreboard unless the core required functionality is already complete.

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, page 5: predictions, scoring, team scoreboard, and public ranking requirements.
2. REST notes from `08-05-26-REST.md`: avoiding infinite JSON loops and handling REST error flow.
3. Project notes in `Project.md`: point constants in resource bundles and redirect guidance.
4. Current repository implementation of prediction model, team members, scores, and top-10 ranking.
5. Git repository: https://github.com/HeyEdis/call-the-match.git.

## Further Notes

This PRD should be implemented after authentication and team membership are reliable. The scoring service is a good deep module because it can be tested heavily without depending on the UI.
