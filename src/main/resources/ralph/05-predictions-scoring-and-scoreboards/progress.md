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
