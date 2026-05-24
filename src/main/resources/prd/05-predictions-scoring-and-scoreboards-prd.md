# PRD: Predictions, Scoring And Scoreboards

## Problem Statement

De kern van de opdracht is dat users wedstrijden voorspellen en punten vergelijken binnen teams and in the public ranking. De repo heeft al prediction records, team member scores, ranking building blocks and a user prediction form, but the match detail page still needs to show the signed-in user's existing prediction when one exists.

## Solution

Maak een user-only prediction flow with one current prediction per user and match. Laat users predictions wijzigen tot one hour before kick-off. Op `/competition/{id}` ziet een signed-in user zijn bestaande prediction als een rustige statusregel/card, bijvoorbeeld label `Your prediction` plus `2 - 1`, met daarnaast de bestaande predict/edit actie. Wanneer admin een official result saves, berekent a dedicated scoring service the prediction base points, team-member totals and team totals with model constants and unique bonuses inside each team. Team members krijgen a private scoreboard; the public ranking reuses team totals without exposing private detail.

## Current Codebase State

- Prediction records already link users and competitions and store predicted scores plus team-independent base points.
- Team members already have score fields and teams already compute a team total from members.
- Seed data already creates sample predictions.
- Prediction controller, prediction form DTO, user-only routes and cutoff enforcement are present.
- The predictions list can show a user's predictions, and the form pre-fills an existing prediction.
- The competition detail page links to the prediction form for users, but it does not yet show the current user's existing prediction inline on that match page.
- Public ranking already exists but still depends on score values being recalculated correctly.
- REST controllers and WebClient are present or planned separately; private prediction data remains out of public REST scope.

## School Requirements

- MVC and Thymeleaf for prediction input and private scoreboard views.
- Service/repository/JPA layering for prediction storage and score recalculation.
- Spring Security for user-only predictions and member-only scoreboards.
- Jakarta Validation for prediction score input.
- Resource bundles for user-facing errors/messages; fixed scoring values remain code constants.
- Match detail integration that shows the authenticated user's existing prediction when present.
- Error behavior for missing matches or forbidden private scoreboard access.
- MVC, security and validation tests later; scoring deserves focused service tests.
- REST/WebClient remains late unless reused read-only later.

## Role And Access Decisions

- **Guest**: mag geen predictions maken and ziet geen private scoreboard.
- **User**: mag eigen predictions beheren and scoreboards of teams where they are a member view.
- **Admin**: enters official results through the admin match feature and does not submit predictions.
- **Forbidden**: updates after cutoff, non-member scoreboard access and admin prediction participation.

## User Stories

1. As a user, I want to predict goals for both countries in a match, so that I can play the game.
2. As a user, I want one active prediction per match, so that my current choice is unambiguous.
3. As a user, I want to change my prediction until one hour before kick-off, so that I can revise it in time.
4. As a user, I want prediction changes blocked after the cutoff, so that the game stays fair.
5. As a user, I want non-negative score input validation, so that invalid predictions are rejected.
6. As a user, I want my own prediction visible on the match detail page, so that I can check what I entered without opening the form first.
7. As the application, I want exact-score predictions rewarded, so that precision matters.
8. As the application, I want correct outcome predictions rewarded, so that winner or draw insight matters.
9. As the application, I want unique exact-score bonuses within a team, so that unique precision earns extra points.
10. As the application, I want unique outcome bonuses within a team, so that unique match insight earns extra points.
11. As the application, I want fixed score constants kept out of resource bundles, so that bundles do not store fixed values.
12. As a team member, I want a private scoreboard sorted by member score, so that team standing is clear.
13. As a team member, I want member totals and team total visible, so that public and private scores connect.
14. As a guest, I want private scoreboards blocked, so that team data stays private.
15. As a non-member, I want another team's scoreboard blocked, so that membership has meaning.
16. As anyone viewing the public ranking, I want team totals updated after official results, so that the top-10 is credible.

## Implementation Decisions

- Store or enforce one prediction per authenticated user and competition.
- Put prediction input in a request DTO with score validation.
- Check the one-hour cutoff in service/domain logic, not only in the UI.
- Add a service/controller read path for the current user's prediction by competition id that returns no value safely when the user has not predicted yet.
- On `/competition/{id}`, render the current user's prediction only for role `USER` and only when one exists; guests see no private prediction context and admins do not participate.
- Use existing resource-bundle labels such as `prediction.own.score` or add a focused key for the match-page status text.
- Treat official result save as the recalculation trigger.
- Use a dedicated scoring service with a small testable interface.
- Use scoring constants `exactScore=5`, `correctOutcome=2`, `uniqueExactBonus=3` and `uniqueOutcomeBonus=1`.
- Store those constants in a model constants class; do not store fixed score values in resource bundles.
- Calculate unique bonuses per team, not globally.
- Required implementation choice: use Option A. `Prediction.pointsEarned` stores only team-independent base points, because one user can belong to multiple teams and unique bonuses can differ per team.
- Do not add a `bonusPoints` column to `Prediction` for the required implementation; it would be ambiguous for users in multiple teams.
- Include unique bonuses while recalculating each `TeamMember.score` for each team context.
- Optional later extension: add a per-team, per-match score detail entity such as `TeamPredictionScore` if detailed bonus history is needed.
- Keep pending matches without official results at zero or unscored state as defined by the service boundary.
- Recalculate affected prediction points, member totals and team totals consistently when a result changes.
- Restrict scoreboards to members and keep public ranking summary-only.

## Testing Decisions

- Verify exact score, correct outcome, no-point and draw scenarios.
- Verify unique exact and unique outcome bonus behavior, including non-unique ties.
- Verify create/update before cutoff and rejection after cutoff.
- Verify match detail shows the signed-in user's existing prediction and does not show another user's prediction.
- Verify match detail omits the prediction status for guests, admins and users without a prediction.
- Verify result save recalculation updates the relevant totals.
- Verify prediction pages are user-only and scoreboards are member-only.
- Verify prediction DTO validation rejects invalid score values.
- This PRD contributes to MVC, security and validation test categories; scoring service tests add high-value domain confidence.
- Tests are deferred to the late closure block, but scoring code should remain easy to test from the start.

## REST And WebClient Decisions

REST and WebClient are out of scope for the current feature implementation. A later REST block may read public match/stadium data, not private prediction data, unless the scope is explicitly expanded.

## Out Of Scope

- Prediction comments, confidence levels and per-user analytics.
- Live match progress.
- Advanced public ranking tie-breakers.
- Optional per-match private scoreboard detail until the minimum required scoreboard is stable.

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, page 5 and page 6: prognoses, score calculation, private scoreboard, public ranking and the conflicting resource-bundle note resolved by keeping fixed values out of bundles.
2. School guidelines: MVC/JPA, validation, security and testing guidance from the EWD Richtlijnen folder.
3. Lesson notes: `13-03-26-Validation.md`, `Project.md` and `08-05-26-REST.md` for late REST boundaries and JSON loop caution.
4. Exercise projects identified for service/repository, validation and security patterns in the school reference map.
5. Existing `call-the-match` codebase: current prediction model, seeded predictions, member scores, team total calculation and public ranking foundation.
6. Git repository URL: `https://github.com/HeyEdis/call-the-match.git`.
7. User/project decisions from this conversation and local skills: fixed score constants, admin exclusion from predictions and late test/REST blocks.

## Further Notes

Deze PRD komt na access, teams and admin result entry. The scoring service is the most useful deep module here: keep it compact, deterministic and easy to prove in tests.
