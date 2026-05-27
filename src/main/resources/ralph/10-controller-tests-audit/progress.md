# Ralph Progress Log: 10-controller-tests-audit

Each iteration appends what was done, decisions made, files changed, verification results, and blockers.

## Entries

### 2026-05-27 - AFK controller test audit cleanup

Tasks:

- `baseline-controller-test-boundary`
- `trim-team-controller-assertions`
- `combine-competition-success-tests`
- `reduce-brittle-rendered-markup-checks`
- `clean-prediction-controller-slice`
- `resolve-admin-prediction-status-assertion`
- `final-controller-rest-verification`

What changed:

- Ran the controller-focused baseline before edits; it passed.
- Confirmed the in-scope MVC controller tests are `AccountControllerTests`, `CompetitionControllerTests`, `HomeControllerTests`, `LocaleControllerTests`, `PredictionControllerTests`, and `TeamControllerTests`.
- Confirmed the in-scope REST controller tests are `CompetitionRestControllerTests` and `StadiumRestControllerTests`.
- Kept broad guest/user/admin route-matrix coverage in `AccessSecurityTests`.
- Trimmed `TeamControllerTests` assertions that proved service-owned top-10 ranking, score ordering, and current-user filtering.
- Combined duplicate competition add/edit happy-path tests so each verifies the service call, redirect, and `successMessage` flash attribute in one place.
- Reduced brittle rendered markup checks in `CompetitionControllerTests`, `HomeControllerTests`, and `TeamControllerTests`.
- Cleaned `PredictionControllerTests` by excluding unrelated validator advice instead of mocking unrelated validators.
- Chose the admin prediction-status task as test-only; removed the brittle verification that admin detail view calls prediction-status lookup while preserving the rendered admin outcome assertions.
- Marked all seven plan tasks as passing after concrete verification.

Files changed:

- `src/test/java/com/example/callthematch/controller/TeamControllerTests.java`
- `src/test/java/com/example/callthematch/controller/CompetitionControllerTests.java`
- `src/test/java/com/example/callthematch/controller/HomeControllerTests.java`
- `src/test/java/com/example/callthematch/controller/PredictionControllerTests.java`
- `src/main/resources/plan/10-controller-tests-audit/plan.json`
- `src/main/resources/ralph/10-controller-tests-audit/progress.md`
- `src/main/resources/ralph/10-controller-tests-audit/TODO.md`

Verification:

- `.\mvnw.cmd -q "-Dtest=AccountControllerTests,CompetitionControllerTests,PredictionControllerTests,TeamControllerTests,HomeControllerTests,LocaleControllerTests,CompetitionRestControllerTests,StadiumRestControllerTests" test` passed before edits.
- `.\mvnw.cmd -q "-Dtest=TeamControllerTests" test` passed.
- `.\mvnw.cmd -q "-Dtest=CompetitionControllerTests" test` passed.
- `.\mvnw.cmd -q "-Dtest=PredictionControllerTests" test` passed.
- `.\mvnw.cmd -q "-Dtest=HomeControllerTests,LocaleControllerTests,CompetitionControllerTests,TeamControllerTests" test` passed.
- `.\mvnw.cmd -q "-Dtest=AccountControllerTests,CompetitionControllerTests,PredictionControllerTests,TeamControllerTests,HomeControllerTests,LocaleControllerTests,CompetitionRestControllerTests,StadiumRestControllerTests" test` passed after edits.

Blockers:

- None.
