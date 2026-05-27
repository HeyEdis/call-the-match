# Service Tests Audit Handoff

Date: 2026-05-27  
Project: `call-the-match`

## Suggested Skills

- `project-guidelines`: use first. Service test decisions must stay aligned with the local EWD school conventions, notes, and exercise projects.
- `diagnose`: use only if a service test fails or service behavior becomes unclear during refactor.
- `handoff`: use again after service tests are expanded or cleaned up.

## Purpose

This handoff captures the audit of the service test package. The user wanted to know whether the service tests follow the school guidelines and exercises, whether cases are missing, and which tests are overdone enough to cut.

## Evidence Sources

Primary project guideline:

- `.agents/skills/project-guidelines/references/testing.md`
  - Service tests should own sorting, limiting, current-user filtering, membership/owner checks, scoring, and repository coordination.
  - Tests should focus on observable behavior and required school outcomes.
  - Private methods should not be tested directly.

Layering guideline:

- `.agents/skills/project-guidelines/references/mvc-jpa-layering.md`
  - Services are processing classes.
  - Services coordinate repositories and domain decisions.
  - Services throw domain exceptions when data is missing.
  - Helper methods for lookup, mapping, and repeated calculations should normally be private.

Lesson notes:

- `C:\Users\Armour\Documents\HOGENT\EWD\Notes\03-04-2026-MySQL.md`
  - "Service klasse is de enigste die in contact is met de repository!!!"
- `C:\Users\Armour\Documents\HOGENT\EWD\Notes\27-02-26-Spring-Boot.md`
  - A service class is a processing class.
  - `spring-boot-starter-test` includes JUnit and Mockito.

Exercise evidence:

- `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Spring\Spring_structuur\Spring_Boot_Beer\src\test\java\com\example\spring_boot_beer\service\BeerServiceTest.java`
  - Shows pure service tests with setup, fixtures, and assertions.
- `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_JPA\EWDJ_mvn_JPA_applicationframe\src\test\java\domain\DomeinTest.java`
  - Shows Mockito-based tests with `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`, `when(...)`, and `verify(...)`.

## Tests Audited

Service tests:

- `src/test/java/com/example/callthematch/service/UserServiceTests.java`
- `src/test/java/com/example/callthematch/service/TeamServiceTests.java`
- `src/test/java/com/example/callthematch/service/TeamMemberServiceTests.java`
- `src/test/java/com/example/callthematch/service/ScoringServiceTests.java`
- `src/test/java/com/example/callthematch/service/PredictionServiceTests.java`

Related service classes:

- `src/main/java/com/example/callthematch/service/CompetitionService.java`
- `src/main/java/com/example/callthematch/service/CountryService.java`
- `src/main/java/com/example/callthematch/service/MyUserDetailsService.java`
- `src/main/java/com/example/callthematch/service/PredictionService.java`
- `src/main/java/com/example/callthematch/service/ScoringService.java`
- `src/main/java/com/example/callthematch/service/StadiumService.java`
- `src/main/java/com/example/callthematch/service/TeamMemberService.java`
- `src/main/java/com/example/callthematch/service/TeamService.java`
- `src/main/java/com/example/callthematch/service/UserService.java`

## Verification Done

Service tests were run:

```powershell
.\mvnw.cmd -q "-Dtest=*ServiceTests" test
```

Result:

- `PredictionServiceTests`: 5 tests, 0 failures
- `ScoringServiceTests`: 7 tests, 0 failures
- `TeamMemberServiceTests`: 1 test, 0 failures
- `TeamServiceTests`: 4 tests, 0 failures
- `UserServiceTests`: 1 test, 0 failures
- Total service tests: 18 passing

The run produced Mockito dynamic-agent warnings from the current JDK/Mockito setup, but the tests passed.

## Overall Verdict

The service tests are mostly school-conform, but incomplete.

The test style is good:

- Plain JUnit and Mockito unit tests.
- Repository/service dependencies are mocked.
- Service behavior is asserted through public methods.
- Repository coordination is verified where useful.
- No private helper methods are tested directly.

The biggest issue is coverage imbalance. Scoring and prediction have decent service coverage, but `CompetitionService`, `StadiumService`, `MyUserDetailsService`, and most of `TeamService` behavior still need targeted tests.

## What Is Good And Should Stay

Keep `UserServiceTests`.

- `registrationStoresEncodedPasswordAndUserRole` proves registration encodes the password, stores a user role of `USER`, and saves timestamps.
- This belongs in service tests because registration is service-owned domain coordination, not controller behavior.

Keep `ScoringServiceTests`.

- Scoring is core business logic.
- The tests cover exact score, non-unique exact score, correct outcome, wrong outcome, unique outcome bonus, draw outcome, and duplicate prediction behavior.
- This is not overdone. These tests protect the game rules.

Keep `PredictionServiceTests`.

- Covers creating a new prediction.
- Covers updating an existing prediction.
- Covers cutoff rejection.
- Covers current user's existing prediction status.
- Covers empty prediction status.
- This follows the school split because cutoff and prediction lookup are service-owned.

Keep `TeamServiceTests`.

- Covers team creation, duplicate team name, duplicate membership, and unknown invite code.
- Duplicate name and unknown invite code are also validator concerns, but keeping service tests is still correct because the service must protect the rule if another caller reaches it.

Keep `TeamMemberServiceTests`.

- The single test is dense, but it covers an important coordination flow: result entered -> prediction base points -> member scores -> team total -> repository saves.
- This is service-owned logic and should not be moved to controller tests.

## Definitely Add

Add `CompetitionServiceTests`. This is the biggest missing service test class.

Suggested cases:

- `getAllCompetitions()` uses `competitionRepository.findAllByOrderByDateAscTimeAsc()`.
- `findRestMatchesByDate(date)` uses `competitionRepository.findByDateOrderByTimeAsc(date)`.
- `findById(id)` maps a competition to `CompetitionDTO`.
- `findInputById(id)` maps a competition to `InputCompetitionDTO`, including stadium code and checksum.
- `findInputResultById(id)` maps official scores to `InputCompetitionResultDTO`.
- `add(dto)` loads both countries and the stadium, builds a competition, saves it, and returns the saved id.
- `update(dto)` loads existing competition, updates teams/stadium/date/time, saves it, and returns the id.
- `updateOfficialResult(id, dto)` updates score fields, saves the competition, and calls `teamMemberService.recalculateScoresAfterResult(savedCompetition)`.
- Missing competition throws `CompetitionNotFound`.
- Missing country throws `CountryNotFound`.
- Missing stadium throws `StadiumNotFound`.

Add more `TeamServiceTests`.

Suggested cases:

- `getCurrentUserTeams(email)` looks up the user by email and returns only teams from that user's memberships.
- `getTop10Teams()` delegates to `findTop10ByOrderByScoreDesc()` and maps to public ranking DTOs.
- `findDetailById(id, email)` denies a non-member with `AccessDeniedException`.
- `findDetailById(id, email)` returns team DTO, owner flag, and rank for a member.
- `findScoreboardById(id, email)` requires membership and returns members ordered by score.
- `regenerateInviteCode(id, email)` requires owner and saves a new unique invite code.
- `regenerateInviteCode(id, email)` denies non-owner.
- `removeMember(teamId, memberId, email)` requires owner and deletes the member.
- `removeMember(teamId, memberId, email)` denies non-owner.
- `removeMember(teamId, memberId, email)` rejects removing the owner.
- `joinTeamWithInviteCode(inviteCode, email)` saves a new member on the happy path.

Add more `PredictionServiceTests`.

Suggested cases:

- `getCurrentUserPredictions(email)` looks up the user by email and uses `findAllByUserOrderByCompetitionDateAscCompetitionTimeAsc(user)`.
- `getCurrentUserPredictions(email)` maps competitions and predictions to `PredictionOverviewDTO`.
- `saveCurrentUserPrediction(competitionId, dto, email)` looks up the user and competition, then saves the prediction.
- Missing competition in status/save flows throws `CompetitionNotFound`.

Add `StadiumServiceTests`.

Suggested cases:

- `getAllStadiums()` uses `findAllByOrderByNameAsc()` and maps to `StadiumDTO`.
- `findCapacityById(id)` maps stadium name and capacity to `StadiumCapacityDTO`.
- Missing stadium throws `StadiumNotFound`.

Add `MyUserDetailsServiceTests`.

Suggested cases:

- Existing user returns Spring Security `UserDetails` with email as username, password hash, and `ROLE_USER` or `ROLE_ADMIN`.
- Missing user throws `UsernameNotFoundException`.

Optional: add `CountryServiceTests`.

- One tiny test for `getAllCountries()` using `findAllByOrderByNameAsc()` and mapping to `CountryDTO` is enough.
- Do not overbuild this class because it is only simple repository ordering plus DTO mapping.

## Could Cut Or Reduce

No existing service test class should be removed wholesale.

Do not cut `ScoringServiceTests`.

- The scoring test count looks high compared to other services, but scoring is the most rule-heavy pure service.
- These are valuable, readable tests.

Do not cut duplicate team name or unknown invite code from `TeamServiceTests`.

- Validator tests protect form error behavior.
- Service tests protect the actual domain rule.
- Having both is defensible.

Do not add tests for private helpers.

- Do not directly test helpers such as `toDTO`, `rankFor`, `isOwner`, `findTeamById`, or `calculateTeamScore`.
- Test those through public service use cases.

Avoid low-value tests that only assert Lombok/builders/entities work.

- Service tests should prove service coordination and decisions, not generated getters/setters.

## Responsibility Split

Use this split when deciding where a case belongs:

- Controller tests:
  - route, status, view, model, redirect, flash, service delegation.
- Service tests:
  - repository coordination, DTO mapping, sorting/limiting delegation, current-user filtering, membership checks, owner checks, cutoff checks, scoring triggers.
- Validator tests:
  - form field errors, duplicate checks, invite-code checks, checksum, date/time conflict, null guards.
- Security tests:
  - guest/user/admin access matrix, login/logout, CSRF, public routes.
- Repository tests:
  - not currently required unless a custom query becomes complex. Spring Data method names such as `findTop10ByOrderByScoreDesc` are better exercised indirectly through service tests or trusted as framework behavior.

## Suggested Refactor Sequence

1. Add `CompetitionServiceTests` first.
2. Expand `TeamServiceTests` for membership/owner/ranking/scoreboard flows.
3. Expand `PredictionServiceTests` for overview list and `saveCurrentUserPrediction`.
4. Add small `StadiumServiceTests`.
5. Add small `MyUserDetailsServiceTests`.
6. Optionally add one `CountryServiceTests`.
7. Re-run:

```powershell
.\mvnw.cmd -q "-Dtest=*ServiceTests" test
```

## Final Position

The service test package is a good start, not a finished suite. It follows the school style well enough: small tests, mocked repositories, public service behavior, and no private-method testing. The next move is not cutting, but filling the missing service-owned behavior, especially `CompetitionService` and the access/ranking branches in `TeamService`.
