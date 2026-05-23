# Ralph Progress Log: 05-predictions-scoring-and-scoreboards

Each iteration appends what was done, decisions made, files changed, verification results, and blockers.

## Entries

### 2026-05-23 - Prediction create/update slice

- `prediction-dto-validation` - Added `InputPredictionDTO` with Jakarta `@NotNull` and `@Min(0)` score validation and bundle-backed score messages.
- `prediction-repository-query` - Added the user+competition repository lookup and a database unique constraint for one prediction per user and match.
- `prediction-service-upsert` - Added `PredictionService.savePrediction(...)` with create/update behavior, authenticated-user helpers and `createdAt` for new predictions.
- `prediction-controller-form` - Added the USER-only prediction form route, prefill flow, score inputs, match-context link and invalid-form redisplay.
- Decisions: Kept repository access in `PredictionService`; reused the existing `CompetitionService` response DTO for the form model; left cutoff and scoring work for their own follow-up tasks.
- Files changed: `Prediction`, `PredictionRepository`, `InputPredictionDTO`, `PredictionService`, `PredictionController`, `templates/prediction/form.html`, `templates/competition/show.html`, `messages.properties`, prediction/security/validation/service tests and this plan/progress state.
- Verification: `.\mvnw.cmd test` passed with 38 existing tests before focused coverage was added. `.\mvnw.cmd '-Dtest=InputPredictionDTOValidationTests,PredictionServiceTests,PredictionMvcTests,AccessSecurityMvcTests' test` passed with 12 focused tests after the slice was covered.
- Blockers/TODOs: none for these four tasks.

### 2026-05-23 - Cutoff and scoring core

- `cutoff-enforcement` - Added server-side one-hour prediction cutoff enforcement, a form error for late saves and a disabled submit state after cutoff.
- `scoring-constants-bundle` - Added exact score, correct outcome and unique bonus constants to the message bundle.
- `scoring-service` - Added a bundle-backed `ScoringService` for exact score, correct outcome and per-team unique bonus calculations.
- `result-triggers-recalculation` - Inspected the current model and skipped implementation in AFK mode. One user can belong to multiple teams, unique bonuses are per team, but the current model only has one `Prediction.pointsEarned` value for that user's match prediction.
- Decisions: Kept cutoff enforcement in `PredictionService`; kept cutoff message rendering in the prediction controller/form; used `MessageSource` so scoring constants stay in the resource bundle.
- Files changed: prediction controller/service/form/messages, `PredictionCutoffPassed`, `ScoringService`, prediction/scoring tests and this Ralph state. Also included the pending review cleanup that moved repeated competition model data into `@ModelAttribute` and removed unused save return values.
- Verification: `.\mvnw.cmd '-Dtest=PredictionMvcTests,PredictionServiceTests,ScoringServiceTests' test` passed after cutoff and scoring coverage was added.
- Blockers/TODOs: result recalculation decision added to `TODO.md`.

### 2026-05-23 - Scoring constant review correction

- Review result: moved fixed scoring values out of `messages.properties` into `model/ScoringPoints`.
- Source decision: FIFA assignment page 5 says X/Y/B/C are mentioned in resource bundles, while page 6 says resource bundles contain no fixed values. Applied the review instruction and the stricter fixed-value rule from page 6.
- Follow-up state: updated the feature PRD, plan and plan.json wording so later Ralph iterations do not re-add numeric score values to the message bundle.

### 2026-05-23 - Result recalculation decision

- Decision: use Option A for the required implementation. Keep one prediction per user and match; `Prediction.pointsEarned` means base points only and does not include team-specific unique bonuses.
- Recalculation guidance for Ralph: when an official result is saved, update each prediction's base points, then recalculate each affected `TeamMember.score` per team using `ScoringService` with that team's member predictions so unique bonuses are computed within the team context.
- Rejected for required implementation: adding `bonusPoints` to `Prediction`, because the same prediction can have different unique bonuses in different teams.
- Deferred optional feature: Option C, a `TeamPredictionScore` detail entity for per-team/per-match base and bonus detail. Handoff saved outside the workspace at `C:\Users\Armour\AppData\Local\Temp\call-the-match-option-c-team-prediction-score-handoff.md`.

### 2026-05-23 - Official result recalculation

- `result-triggers-recalculation` - Added automatic score recalculation after admin saves an official competition result.
- What changed: `CompetitionService.updateResult(...)` now saves the official result and calls `TeamMemberService.recalculateScoresAfterResult(...)`. The recalculation updates each prediction's base `pointsEarned`, recalculates affected team-member totals with team-specific unique bonuses, and saves affected team totals.
- Decisions: Kept `Prediction.pointsEarned` as base points only; unique bonuses are applied only while recalculating each member score inside that team context. Recalculated member totals from all completed predictions so changing an old result remains idempotent.
- Review correction: moved recalculation into the existing `TeamMemberService` layer, removed the separate recalculation service file, and moved `MatchOutcome` out of `ScoringService` into the model folder.
- Files changed: `CompetitionService`, `TeamMemberService`, `ScoringService`, `MatchOutcome`, `PredictionRepository`, `TeamMemberRepository`, `TeamMemberServiceTests`, `plan.json`, `progress.md`, `TODO.md`.
- Verification: `.\mvnw.cmd '-Dtest=TeamMemberServiceTests,ScoringServiceTests,PredictionServiceTests' test` passed. `.\mvnw.cmd test` passed with 50 tests.
- Blockers/TODOs: none for the required Option A recalculation task. Optional per-team/per-match score detail remains deferred in `TODO.md`.

### 2026-05-23 - Private scoreboard and access rules

- `private-scoreboard-page` - Added a member-only `/team/{id}/scoreboard` page that shows the team total and team members sorted by score descending.
- `security-hardening` - Explicitly restricted scoreboard and prediction routes to `ROLE_USER`; guests are redirected to login and admins receive 403 for the private participation routes.
- What changed: `TeamController` now exposes the scoreboard route, `TeamService` returns DTO-backed scoreboard data after enforcing current-user team membership, and `TeamMemberRepository` loads team members ordered by score.
- Decisions: Used response DTOs for the Thymeleaf model so the view does not depend directly on entities; kept membership enforcement in `TeamService` because it matches the existing team privacy behavior; kept admin blocked through the `ROLE_USER` security rule.
- Files changed: `TeamController`, `TeamService`, `TeamMemberRepository`, `SecurityConfig`, `TeamScoreboardDTO`, `TeamMemberScoreDTO`, `templates/team/scoreboard.html`, `templates/team/show.html`, `messages.properties`, `TeamManagementMvcTests`, `AccessSecurityMvcTests`, `plan.json`, `progress.md`.
- Verification: `.\mvnw.cmd '-Dtest=TeamManagementMvcTests,AccessSecurityMvcTests' test` passed. `.\mvnw.cmd test` passed with 52 tests.
- Blockers/TODOs: none.
