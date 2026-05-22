# Plan: Predictions, Scoring And Scoreboards

> Source PRD: `src/main/resources/prd/05-predictions-scoring-and-scoreboards-prd.md`

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, pages 5-6: prognoses, score calculation, private scoreboard, public ranking and the fixed-value bundle conflict.
2. `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen\Slides_Spring&JPA_mySql.pdf`: repository, entity and service boundaries for persisted prediction and score updates.
3. `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen\Slides_Spring_Web_Flow.pdf`: MVC form DTOs, `@Valid`, `BindingResult` and server-side validation flow.
4. `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen\Slides_Spring_Web_MVC_i18n.pdf`: Thymeleaf form errors, validation messages and resource bundles.
5. `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen\Slides_Spring_Security.pdf`: authenticated and role-restricted MVC behavior for user-only flows.
6. Lesson notes `13-03-26-Validation.md`, `Project.md` and `08-05-26-REST.md`: validation reminders and the late REST/JSON-loop boundary.
7. `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_SpringAndJPA`: school-style JPA repository and service-layer patterns.
8. `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Spring\Spring_validatie\Spring_Boot_Validation`: DTO-backed MVC validation example.
9. `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Security\Spring_Boot_security_JPA`: role-aware Spring Security example.
10. Existing `call-the-match` codebase: current prediction model, seeded predictions, member scores, team total calculation and public ranking foundation.
11. Git repository: https://github.com/HeyEdis/call-the-match.git
12. User/project decisions from this conversation and local skills: fixed score constants, admin exclusion from predictions and late test/REST blocks.

## Architectural Decisions

Durable decisions that apply across all phases:

- **Routes**: user-only `/predictions/**` for create/edit; user-only `/team/{id}/scoreboard` for private scoreboard; admin match result save triggers recalculation (existing admin route).
- **Schema**: `Prediction` already links user + competition with predicted scores and points earned. `TeamMember.score` holds member total. `Team.score` holds team total. Unique constraint on (user, competition) enforces one prediction per match.
- **Key models**: `Prediction`, `Competition`, `TeamMember`, `Team`, `MyUser`; new `InputPredictionDTO` for form input; new `ScoringService` for point calculation.
- **Security**: prediction routes are user-only; admin does not predict; scoreboard is member-only (403 for guest, non-member, admin); cutoff is enforced server-side.
- **Validation/i18n**: prediction DTO uses `@Min(0)` and `@NotNull` Jakarta annotations; scoring constants (`exactScore=5`, `correctOutcome=2`, `uniqueExactBonus=3`, `uniqueOutcomeBonus=1`) live in a model constants class; error messages in bundle.
- **REST/WebClient**: out of scope; separate late block.
- **Testing**: late closure block; scoring service designed testably from the start with a small deterministic interface.

---

## Phase 1: Prediction Create/Update Flow

**User stories**: 1, 2, 3, 5, 6

### What To Build

A user-only prediction form where an authenticated user submits two non-negative integer scores for a match. The system enforces one active prediction per user and competition (upsert semantics). The form is accessible from match context and shows the user's current prediction if one exists. An `InputPredictionDTO` validates scores with Jakarta `@NotNull` and `@Min(0)`. A `PredictionService` handles create/update logic. A `PredictionController` serves the Thymeleaf form and processes submissions.

### Acceptance Criteria

- [ ] `InputPredictionDTO` exists with `@NotNull @Min(0)` on both score fields.
- [ ] Validation error messages are in the resource bundle.
- [ ] `PredictionRepository` has a query to find a prediction by user and competition.
- [ ] `PredictionService` creates a new prediction or updates the existing one (upsert).
- [ ] `PredictionController` serves a form at a user-only route (e.g. `/predictions/{competitionId}`).
- [ ] The form pre-fills existing prediction values when editing.
- [ ] Only authenticated users with role USER can access the prediction route.
- [ ] Invalid scores (null, negative) are rejected with bundle messages shown on the form.

---

## Phase 2: One-Hour Cutoff Enforcement

**User stories**: 3, 4

### What To Build

The prediction service checks whether the current time is before the match kickoff minus one hour. If the cutoff has passed, create and update operations are rejected with a clear error. The cutoff is enforced server-side in the service layer, not only in the UI.

### Acceptance Criteria

- [ ] Service computes cutoff as `competition.date + competition.time - 1 hour`.
- [ ] Prediction create/update throws or returns an error when current time ≥ cutoff.
- [ ] The error message is loaded from the resource bundle.
- [ ] The controller displays the cutoff error on the form or redirects with a flash message.
- [ ] The form/UI disables or hides the submit button when cutoff has passed (progressive enhancement, not sole enforcement).

---

## Phase 3: Scoring Service And Constants

**User stories**: 7, 8, 9, 10, 11

### What To Build

A dedicated `ScoringService` that calculates points for a single prediction given the official result and team context. Fixed scoring constants live in the model folder rather than the resource bundle. The service awards exact-score points, correct-outcome points, and unique bonuses within a team. The interface is small and deterministic so it is easy to test later.

### Acceptance Criteria

- [ ] Model constants class contains exact-score, correct-outcome and unique bonus score values.
- [ ] `ScoringService` reads fixed score values from the model constants class.
- [ ] Exact-score match awards 5 points.
- [ ] Correct outcome (win/loss/draw) without exact score awards 2 points.
- [ ] No match awards 0 points.
- [ ] Unique exact-score bonus (+3) awarded when only one member in a team predicted that exact score.
- [ ] Unique outcome bonus (+1) awarded when only one member in a team predicted that outcome.
- [ ] Bonuses are computed per team, not globally.
- [ ] The service has a clear method signature that accepts prediction, result and team-member context.

---

## Phase 4: Official Result Triggers Recalculation

**User stories**: 16

### What To Build

When admin saves an official result for a competition (scoreA and scoreB), the system triggers the scoring service to recalculate points for all predictions on that competition. After scoring individual predictions, the system updates each affected `TeamMember.score` (sum of that member's prediction points) and each affected `Team.score` (sum of member scores). This ensures the public ranking stays up to date.

### Acceptance Criteria

- [ ] Saving an official result (admin action) triggers recalculation automatically.
- [ ] All predictions for the competition are rescored using `ScoringService`.
- [ ] Each prediction's `pointsEarned` field is updated and persisted.
- [ ] Each affected team member's `score` field is recalculated as the sum of their prediction points.
- [ ] Each affected team's `score` field is recalculated via `calculateTeamScore()` or equivalent.
- [ ] If admin changes an already-entered result, points are recalculated (idempotent).
- [ ] Public ranking reflects updated team totals without additional manual steps.

---

## Phase 5: Private Team Scoreboard

**User stories**: 12, 13, 14, 15

### What To Build

A member-only scoreboard page at `/team/{id}/scoreboard` displaying all team members sorted by score descending, each member's total points, and the team total. Access is restricted to authenticated users who are members of that specific team. Guests, non-members, and admins receive 403.

### Acceptance Criteria

- [ ] Scoreboard page exists at a user-only route (e.g. `/team/{id}/scoreboard`).
- [ ] Page lists team members sorted by score descending.
- [ ] Each member row shows username/name and total score.
- [ ] Team total score is displayed.
- [ ] Only members of the team can access the page (checked in controller or service).
- [ ] Non-members receive 403 Forbidden.
- [ ] Guests (unauthenticated) are redirected to login or receive 403.
- [ ] Admin accounts receive 403 (admin does not participate in team flows).

---

## Phase 6: Security And Access Hardening

**User stories**: 14, 15

### What To Build

Ensure Spring Security configuration explicitly protects prediction and scoreboard routes. Prediction routes require `ROLE_USER`. Scoreboard membership check is enforced at the application level (controller/service). Verify that admin cannot reach prediction or scoreboard pages.

### Acceptance Criteria

- [ ] Security config restricts `/predictions/**` to `ROLE_USER`.
- [ ] Security config restricts `/team/*/scoreboard` to `ROLE_USER`.
- [ ] Admin role cannot access prediction or scoreboard routes (403).
- [ ] Guest access to prediction or scoreboard routes redirects to login.
- [ ] Membership check for scoreboard is enforced server-side, not only by URL obscurity.

---

## Phase 7: Tests (Late Closure)

**User stories**: All

### What To Build

Test suite covering the predictions and scoring feature across required school categories: MVC controller tests for prediction flow, security tests for route access (guest/user/admin/non-member), validation tests for `InputPredictionDTO` annotations, and service tests for `ScoringService` logic including exact score, correct outcome, zero-point, draw, and unique bonus scenarios.

### Acceptance Criteria

- [ ] MVC test: prediction form submission succeeds for valid input.
- [ ] MVC test: prediction form rejects invalid input with validation errors.
- [ ] Security test: guest cannot access prediction routes.
- [ ] Security test: admin cannot access prediction or scoreboard routes.
- [ ] Security test: non-member cannot access another team's scoreboard.
- [ ] Validation test: DTO rejects null and negative score values.
- [ ] Service test: exact-score prediction earns 5 points.
- [ ] Service test: correct-outcome-only prediction earns 2 points.
- [ ] Service test: wrong prediction earns 0 points.
- [ ] Service test: unique exact bonus awards +3 when prediction is unique in team.
- [ ] Service test: unique outcome bonus awards +1 when outcome is unique in team.
- [ ] Service test: no bonus when multiple members share the same prediction.
- [ ] Service test: cutoff enforcement rejects late predictions.

