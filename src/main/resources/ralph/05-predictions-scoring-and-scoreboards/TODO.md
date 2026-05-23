# Ralph TODO: 05-predictions-scoring-and-scoreboards

Human review items, blockers, and deferred decisions.

## Open Items

- Decision made for result recalculation: implement Option A. `Prediction.pointsEarned` stores only team-independent base points for the user's prediction: exact score, correct outcome, or zero. Unique bonuses are team-specific and must be included only while recalculating `TeamMember.score` for each team. Do not assume a user belongs to only one team.
- Optional later feature if time remains: implement Option C with a per-team, per-match score detail entity such as `TeamPredictionScore`. Handoff document: `C:\Users\Armour\AppData\Local\Temp\call-the-match-option-c-team-prediction-score-handoff.md`.
