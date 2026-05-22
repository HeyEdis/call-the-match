# Plan: Homepage And Public Ranking

> Source PRD: `src/main/resources/prd/02-homepage-and-public-ranking-prd.md`

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, pages 3 and 5: homepagina and publieke top-10 requirements.
2. School guidelines: `Slides_Spring_Boot.pdf`, `Slides_Spring_Web_MVC_i18n.pdf`, `Slides_Spring_Exceptions.pdf` and `Slides_Spring_MultipleRow.pdf`.
3. Lesson notes: `03-04-2026-ErrorMessageEnI18n.md` and `Project.md`.
4. Exercise projects identified for MVC, i18n and exception patterns in the school reference map.
5. Existing `call-the-match` codebase: current home, competition, ranking, message bundle and error template state.
6. Git repository URL: `https://github.com/HeyEdis/call-the-match.git`.
7. User/project decisions from this conversation and local skills: guest public access and safe passing before polish.

## Architectural Decisions

Durable decisions that apply across all phases:

- **Routes**: keep `/home`, `/ranking` and public `/competition/{id}` reachable for guests. Public screens link into login and registration; user-only team/prediction routes and admin-only match-management routes stay role-aware in shared navigation.
- **Schema**: reuse existing `Competition`, `Country`, `Stadium`, `Location`, `Team` and `TeamMember` data. Public ranking exposes only summary data needed for top-10 output and does not introduce a private scoreboard shape.
- **Key models**: `CompetitionDTO` shapes schedule/detail rows and `TeamDTO` shapes public ranking rows. Services remain responsible for ordering and summary shaping before Thymeleaf renders lists.
- **Security**: public screens remain read-only guest routes. Logged-in users and admins see the same public content while role-specific navigation entry points stay explicit and do not add public mutation actions.
- **Validation/i18n**: public screen copy, labels and date display selected for full-screen i18n coverage use `src/main/resources/i18n/messages.properties`. Shared MVC exception handling covers not-found and path-variable type mismatch behavior for public match detail URLs.
- **REST/WebClient**: this feature stays MVC/Thymeleaf only. A later public match-by-date REST block may reuse the public competition boundary without being implemented here.
- **Testing**: close the feature with MVC controller/model-view assertions, public-route security checks, ranking order/limit evidence, and friendly error behavior for invalid public detail requests.

---

## Phase 1: Public Home Schedule Slice

**User stories**: `1`, `2`, `3`, `11`

### What To Build

Make the existing home flow a reliable public schedule slice. The home controller remains thin, competition data flows from the service/repository boundary into Thymeleaf, the schedule reads in date-and-time order, and unplayed fixtures do not show fake official results.

### Acceptance Criteria

- [ ] Guests can open the public home schedule through MVC without crossing into private team, prediction or admin actions.
- [ ] Home schedule rows are shaped from persisted competition data through the service layer and include countries, date, time, stadium and location context needed by the view.
- [ ] Matches are rendered in natural chronological order using date and time rather than arbitrary repository order.
- [ ] Official result output only appears when the result values are available; unplayed matches remain visibly honest in the schedule.

---

## Phase 2: Public Match Detail And Friendly Errors

**User stories**: `4`, `12`

### What To Build

Complete the public match-detail path as a browseable slice. One competition can be inspected directly through the MVC/service/repository path, while missing competitions and malformed path variables resolve through shared school-style MVC error handling.

### Acceptance Criteria

- [ ] Guests, users and admins can open public competition detail through `/competition/{id}` and see only public fixture/result context for that match.
- [ ] The detail view receives its competition data through the service boundary and does not query repositories or leak private prediction content to guests.
- [ ] A missing competition id is handled as friendly not-found MVC behavior using the existing error page direction.
- [ ] An invalid public detail identifier uses shared type-mismatch handling and resolves to friendly public error behavior instead of a raw framework page.

---

## Phase 3: Public Top-10 Ranking Summary

**User stories**: `6`, `7`, `8`

### What To Build

Make the ranking route a public summary slice. The ranking controller asks the team service for score-ordered top teams and Thymeleaf renders the assignment-required summary columns without exposing private team scoreboards.

### Acceptance Criteria

- [ ] Guests can open the public ranking and receive ranking rows backed by real team data from the service/repository path.
- [ ] Public ranking contains at most 10 teams sorted from highest to lowest total team score.
- [ ] Each ranking row renders team name, total score and member count from the backend data available for that team.
- [ ] The ranking output keeps private team scoreboard details, invite codes and user-owned workflows out of the public summary.
- [ ] Ranking data flow keeps future scoring corrections behind the team score source instead of duplicating score calculations in Thymeleaf.

---

## Phase 4: Public Shell, Navigation And i18n Coverage

**User stories**: `5`, `6`, `9`, `10`

### What To Build

Round out the public shell around home, match detail and ranking. Public navigation makes login, registration and ranking discoverable for guests, keeps user/admin workflow entry points role-aware, and gives at least one public screen complete bundle-backed labels and copy.

### Acceptance Criteria

- [ ] Guest navigation makes public home, ranking, login and registration entry points reachable without showing private or admin-only mutations.
- [ ] Authenticated user navigation still leads from the public shell into team and prediction flows, while admin navigation still leads into match management only.
- [ ] Visible copy and field/table labels selected for the full-screen public i18n slice come from `i18n/messages.properties` rather than hardcoded controller text.
- [ ] Public date presentation uses the project message/date-format direction where it belongs for this i18n slice.
- [ ] Shared navigation and public screen work remain coherent with the access-and-roles feature instead of reintroducing guest access regressions.

---

## Phase 5: MVC, Security And Error Closure Tests

**User stories**: `1`-`12`

### What To Build

Close the public feature with focused automated evidence after the screen behavior and error boundary are stable. Use MVC and security-oriented checks for public routes, model/view output and invalid detail behavior without mixing REST or pixel-level UI assertions into this phase.

### Acceptance Criteria

- [ ] MVC controller tests verify home returns the public schedule view and competition model data expected by Thymeleaf.
- [ ] MVC controller tests verify ranking output is limited to top-10 score order and exposes only public summary fields needed by the view.
- [ ] MVC controller tests verify public match detail success behavior and friendly not-found or type-mismatch behavior for invalid detail URLs.
- [ ] Security tests verify guests can access home, ranking and public match detail while role-aware navigation and protected private/admin routes stay governed by the access feature.
- [ ] Test coverage uses model, view, status and redirect/error assertions rather than brittle visual assertions.
- [ ] This closure phase does not add REST controller, WebClient or e2e test work to the public MVC feature.
