# School And FIFA Conformance Audit

## Audit Contract

This audit checks whether `call-the-match` is defendable to a HOGENT EWD evaluator and conforms to the FIFA World Cup 2026 Team Prediction assignment.

The goal is not only "the application works". The goal is: during the oral exam, every important requirement can be pointed to in the codebase, explained in school terminology, and defended against the official assignment and course examples.

## User Decisions

- The target is evaluator-defendable conformance.
- Documented deviations are allowed when they are intentional and defensible.
- `.idea` files are not authoritative guideline sources. They may only be used as historical clues.
- `WorkspacesIntelij` examples are checked selectively by feature area, not exhaustively.
- Tests are considered already school-conform. This audit uses tests as supporting evidence only and does not reopen the test-refactor scope.
- Missing FIFA or school functionality must become a PRD before implementation.
- Refactor-shaped gaps may use the `request-refactor-plan` workflow after the audit identifies them.

## Source Order

1. Current `call-the-match` codebase.
2. Official guidelines in `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen`.
3. User notes in `C:\Users\Armour\Documents\HOGENT\EWD\Notes`.
4. Concrete exercise patterns in `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij`.
5. FIFA assignment PDF: `C:\Users\Armour\Documents\HOGENT\EWD\FIFA_World_Cup_2026_-_Team_Prediction.pdf`.

When sources conflict, prefer official school convention and concrete local exercise style over generic Spring Boot patterns.

## Status Legend

- `PASS`: requirement is implemented and defendable.
- `DOCUMENTED DEVIATION`: implementation differs from the most literal source pattern, but the deviation is intentional and defendable.
- `GAP`: missing, risky, or not defendable enough. Requires a PRD before implementation.
- `QUESTION`: needs source interpretation before it can be classified.
- `NOT AUDITED`: not checked yet.

## Audit Slices

1. FIFA functional requirements.
2. Role and access model.
3. MVC, JPA, and layering.
4. DTO validation, i18n, and error handling.
5. Thymeleaf form handling.
6. JPA/domain model.
7. REST and WebClient.
8. Defence summary.

## First-Pass Matrix

| Area | Requirement | Source | Evidence | Status | Notes / Next Step |
| --- | --- | --- | --- | --- | --- |
| Source control | Do not treat IDE/Copilot state as a guideline source. | User decision; `.agents/skills/project-guidelines/SKILL.md` source order. | `.idea/workspace.xml` and `.idea/copilotDiffState.xml` exist, but are excluded from authority. | PASS | Keep `.idea` out of conformance decisions except for historical clues. |
| FIFA roles | Guest can only see public information such as top-10 teams. | FIFA PDF, roles section. | `SecurityConfig` permits `/home`, `/ranking`, `/competition`, `/competition/{id}`, login/register, static resources, `/api/**`, and errors. | DOCUMENTED DEVIATION | Public match list/detail are also intentionally public because the FIFA home page requires public match overview. Defend as assignment-compatible. |
| FIFA roles | User can create account, create/join team, predict, and view team scores. | FIFA PDF, roles section. | `AccountController`, `TeamController`, `PredictionController`; `/team/**` and `/predictions/**` require `USER`. | PASS | Continue detailed feature audit for each flow. |
| FIFA roles | Admin manages matches and official results. | FIFA PDF, roles section. | `CompetitionController` has add/edit/result routes; `SecurityConfig` restricts those routes to `ADMIN`. | PASS | Need detail audit of all required match fields and validation. |
| Security | Admin must not enter team or prediction flows. | Project guidelines role rules. | `/predictions/**`, `/team/*/scoreboard`, and `/team/**` require `USER`; admin links only show manage matches. | PASS | This assumes admins have only `ADMIN`, not both roles. Verify seed data and registration roles in domain audit. |
| Security | Login uses email. | Project guidelines; security notes. | `SecurityConfig` sets `.usernameParameter("email")`; navbar displays authenticated name. | PASS | Verify `MyUserDetailsService` resolves by email in domain/security audit. |
| Security | Security lookup service is split from normal domain user service. | `security-login.md`; `24-04-26-Security.md`. | `MyUserDetailsService` implements `UserDetailsService` and only resolves email/password/authority data; `UserService` handles registration and current domain user lookup. | PASS | Good defence point: this follows the user's security notes. |
| Security | New registrations get role `USER` and passwords are encoded. | Project guidelines; security slides. | `UserService.register()` encodes password with `PasswordEncoder` and sets `Role.USER`; `SecurityBeansConfig` provides `BCryptPasswordEncoder`. | PASS | Seed data also uses `passwordEncoder.encode("password")`. |
| Security | Logout visible on all screens. | FIFA technical requirements. | `templates/fragments/navbar.html` has authenticated logout form with CSRF token; all non-error page templates found by `rg` include the navbar fragment. | PASS | Error templates were not checked for navbar inclusion; likely not required for "all screens" in oral defence, but can be noted if asked. |
| Security | Role display visible. | FIFA technical requirements. | `templates/fragments/navbar.html` displays `ADMIN` or `USER` with `sec:authorize`; all non-error page templates found by `rg` include the navbar fragment. | PASS | Clear and easy to demonstrate during evaluation. |
| MVC layering | Controllers should call services, not repositories. | `mvc-jpa-layering.md`. | Initial `rg` found repository imports in `config/InitDataConfig`, not controllers. Controllers use services. | PASS | Keep full controller scan in final audit. |
| MVC validation | `BindingResult` immediately follows `@Valid` DTO. | `validation-i18n-exceptions.md`. | `AccountController`, `CompetitionController`, `TeamController`, and `PredictionController` follow this order in scanned methods. | PASS | Continue template-level error display audit. |
| Team management | Team has unique name, generated invite code, and owner. | FIFA PDF, team management section. | `Team.name` is unique; `Team.inviteCode` is unique; `Team.generateInviteCode()` creates 4 letters + 4 digits; `TeamService.createTeam()` checks duplicate name, generates a unique invite code, saves owner membership. | PASS | Invite code minimum 8 characters is met exactly. |
| Team management | Owner can regenerate invite code and remove members. | FIFA PDF, team management section. | `TeamController` exposes `/{id}/invite-code` and `/{id}/members/{memberId}/remove`; `TeamService` has owner checks. | PASS | Need verify owner cannot remove self and access errors are handled cleanly. |
| Match management | Admin add form includes country A/B, datetime, stadium/location, stadium code, and checksum. | FIFA PDF, match management section. | `CompetitionController` and `InputCompetitionDTO`; templates include teamA/teamB/stadium/stadiumCode/checksum/date/time fields. | PASS | Need verify location is available through selected stadium, which is likely defendable. |
| Match validation | Country A differs from country B. | FIFA PDF; validation reference. | `CompetitionValidator` rejects `teamB` with `competition.teams.different`. | PASS | Confirm message key exists and field errors render. |
| Match validation | Date falls within official World Cup period chosen by student. | FIFA PDF; project guideline says 20 May 2026 through 6 June 2026. | `CompetitionValidator` rejects dates before `2026-05-20` or after `2026-06-06` with `competition.date.period`. | PASS | Date range matches project guideline. |
| Match validation | Checksum equals stadium code modulo 97. | FIFA PDF; custom annotation requirement. | `@ValidStadiumChecksum` and `StadiumChecksumValidator` exist; message key exists. | PASS | Also satisfies minimum custom annotation + validator requirement. |
| Match validation | No two matches at the same time in the same location. | FIFA PDF. | `CompetitionValidator` checks `existsByStadiumIdAndDateAndTime`. | PASS | It checks stadium rather than location. Defensible because stadium implies location, but note if multiple stadiums per city matter. |
| Predictions | User can create/change prediction until 1 hour before kickoff. | FIFA PDF. | `PredictionService.savePrediction()` reuses an existing prediction or creates one, and throws `PredictionCutoffPassed` when current time is at or after kickoff minus one hour. | PASS | Exact cutoff is strict: changes are allowed only while now is before the one-hour-before moment. |
| Scoring | Exact score, correct outcome, unique exact bonus, and unique outcome bonus. | FIFA PDF, scoring section. | `ScoringService` implements base points and unique bonuses. | PASS | Need verify constants are in resource bundle and not duplicated as magic numbers. |
| Resource bundles | Score constants must be mentioned in resource bundles. | FIFA PDF; project guidelines. | `ScoringPoints` defines `EXACT_SCORE=5`, `CORRECT_OUTCOME=2`, `UNIQUE_EXACT_BONUS=3`, and `UNIQUE_OUTCOME_BONUS=1`, but `messages.properties` does not currently mention these constants. | GAP | Create a PRD or small PRD-backed cleanup to add defendable resource bundle entries and optionally show/explain them in UI/documentation. |
| Public ranking | Guests can see top 10 teams sorted by total score, team name, score, member count. | FIFA PDF. | `RankingController` delegates to `TeamService.getTop10Teams()`, which sorts by score descending and limits to 10; `ranking/list.html` displays rank, team name, total score, and member count. | PASS | Sorting happens in service memory rather than repository. Acceptable for this project size, but mention if asked. |
| REST | REST API exposes matches by date and stadium capacity. | FIFA technical requirements; `rest-webclient.md`. | `CompetitionRestController` has `GET /api/matches`; `StadiumRestController` has `GET /api/stadiums/{id}/capacity`; `/api/**` is public. | PASS | Need inspect response DTOs and error advice. |
| WebClient | Reactive WebClient demo is present. | FIFA technical requirements; REST notes; `rest-webclient.md`. | `ClientRunner` creates `RestClient`; `RestClient` uses `WebClient`, calls matches-by-date and stadium-capacity endpoints, prints results, and demonstrates not-found error handling with `doOnError`, `onErrorResume`, and `Mono.empty()`. | PASS | Shape matches the local REST guidance closely enough for defence. |
| Error handling | At least one exception maps to error page. | FIFA technical requirements; `validation-i18n-exceptions.md`; notes. | `GlobalExceptionAdvice` maps not-found exceptions and type mismatches to `error/404`, access-denied exceptions to `error/403`, and generic exceptions to `error/500`. | PASS | Unknown URL handling still needs a separate runtime/static check. |
| Error handling | Unknown URLs are handled correctly. | FIFA technical requirements; school error handling guidance. | `templates/error/404.html` exists. | NOT AUDITED | Needs runtime check because unknown URL handling may be handled by Spring Boot's error controller rather than `GlobalExceptionAdvice`. |
| REST errors | REST errors return JSON, not Thymeleaf views. | `rest-webclient.md`; REST notes. | `RestErrorAdvice` is scoped to REST controllers and returns `ErrorResponse` with `status`, `message`, and `timestamp` for bad request and missing stadium paths. | PASS | Need check if missing competition/match date paths require additional REST exception mapping. |

## Open Questions

1. Does the evaluator expect `/competition` match list to be public because the home page lists matches, or should only `/home` and `/competition/{id}` be public? Current implementation makes the list public; this is likely defendable.
2. Does "Locatie (stad + stadion)" require admin to enter city separately during match creation, or is selecting a stadium with attached location sufficient? Current implementation appears to use stadium selection as the location boundary.
3. Does "Geen twee wedstrijden op hetzelfde moment op dezelfde locatie" mean same stadium or same city/location entity? Current implementation checks same stadium/date/time.
4. Resolved: score constants are currently only in Java (`ScoringPoints`) and are not mentioned in `messages.properties`.

## PRD Queue

No PRDs have been written yet. Confirmed `GAP` findings enter this queue before implementation.

Confirmed PRD candidates:

- Resource bundle cleanup for scoring constants: add exact-score, correct-outcome, unique-exact-bonus, and unique-outcome-bonus values to `messages.properties`, then make the values defendable without changing scoring behavior.

Potential PRD candidates if confirmed:

- Location conflict clarification/fix if the source interpretation requires city-level conflict instead of stadium-level conflict.
