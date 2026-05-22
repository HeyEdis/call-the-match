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
