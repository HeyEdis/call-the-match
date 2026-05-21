# Plan: Predictions Scoring And Scoreboards

> Source PRD: `src/main/resources/prd/05-predictions-scoring-and-scoreboards-prd.md`

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, page 5: predictions, scoring, private scoreboard, and public ranking requirements.
2. `Project.md`: scoring constants in resource bundles and redirect guidance.
3. `08-05-26-REST.md`: JSON loop reminders for later REST exposure.
4. Current prediction, competition, team, and team member models in the repository.
5. Git repository: https://github.com/HeyEdis/call-the-match.git.

## Architectural Decisions

- **Dependency**: access/accounts, team membership, and match detail must exist first.
- **Admin scope**: admins do not submit predictions.
- **Routes**: user-only prediction create/update actions and member-only team scoreboard routes.
- **Schema**: one prediction per user and match.
- **Deadline**: predictions can be changed until one hour before kick-off.
- **Scoring constants**: `exactScore=5`, `correctOutcome=2`, `uniqueExactBonus=3`, `uniqueOutcomeBonus=1`.
- **Source of truth**: member prediction points drive member and team scores.
- **Trigger**: official result save triggers recalculation.
- **Testing timing**: scoring should be implemented as an isolated deep module and tested in the project test block.

---

## Phase 1: User Prediction Form

**User stories**: 1, 2, 5, 6, 17

### What To Build

Add the user prediction flow on match detail. Users can create or update one prediction per match with non-negative scores. Guests and admins cannot submit predictions.

### Acceptance Criteria

- [ ] User can see their prediction form on match detail.
- [ ] User can create a prediction for a match.
- [ ] User can update an existing prediction instead of creating duplicates.
- [ ] Prediction scores must be non-negative.
- [ ] Guest cannot submit predictions.
- [ ] Admin cannot submit predictions.

---

## Phase 2: Prediction Deadline

**User stories**: 3, 4

### What To Build

Enforce the one-hour-before-kick-off deadline in the service layer and reflect it in the UI.

### Acceptance Criteria

- [ ] Prediction can be changed more than one hour before kick-off.
- [ ] Prediction cannot be changed within one hour before kick-off.
- [ ] Prediction cannot be changed after kick-off.
- [ ] Blocked update shows a clear message.
- [ ] Deadline logic is not only enforced in the view.

---

## Phase 3: Scoring Service

**User stories**: 7, 8, 9, 10, 11, 12

### What To Build

Implement a scoring service that calculates exact score points, correct outcome points, and unique team bonuses using resource-bundle scoring constants.

### Acceptance Criteria

- [ ] Exact score earns 5 points.
- [ ] Correct winner or draw earns 2 points.
- [ ] Unique exact score within team earns bonus 3.
- [ ] Unique correct outcome within team earns bonus 1.
- [ ] No premature points are assigned before official result exists.
- [ ] Scoring values come from resource bundle entries.
- [ ] Scoring logic is isolated enough to test directly.

---

## Phase 4: Result-Triggered Recalculation

**User stories**: 7, 12, 19

### What To Build

Connect official result entry to scoring recalculation so prediction points, member scores, team totals, and public ranking update automatically.

### Acceptance Criteria

- [ ] Saving official result recalculates predictions for that match.
- [ ] Member scores update from prediction points.
- [ ] Team totals update from member scores.
- [ ] Public top-10 ranking reflects updated team totals.
- [ ] Re-saving a changed official result recalculates rather than double-counting.

---

## Phase 5: Private Team Scoreboard

**User stories**: 13, 14, 15, 16, 17, 18

### What To Build

Create a private team scoreboard for members with sorted member scores. Keep match-level details optional unless required work is complete.

### Acceptance Criteria

- [ ] Team members can view private scoreboard.
- [ ] Guest cannot view private scoreboard.
- [ ] Non-member cannot view another team's scoreboard.
- [ ] Scoreboard shows member names and total scores.
- [ ] Scoreboard sorts members by score descending.
- [ ] Optional detail per match is deferred unless time remains.

---

## Phase 6: Prediction And Scoring Test Closure

**User stories**: 1-19

### What To Build

Add late-stage service, MVC, security, and validation tests for predictions and scoring.

### Acceptance Criteria

- [ ] Scoring tests cover exact score, correct outcome, no points, exact bonus, outcome bonus, and no bonus when tied.
- [ ] Prediction service tests cover create, update, and deadline rejection.
- [ ] MVC tests cover prediction form display and submit behavior.
- [ ] Security tests cover user-only prediction actions and member-only scoreboard access.
- [ ] Ranking test confirms team totals update after official result.
