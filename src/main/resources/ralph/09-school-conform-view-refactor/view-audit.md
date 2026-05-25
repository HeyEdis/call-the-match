# View Audit And Route Map: 09-school-conform-view-refactor

## Template Groups

- Public: `home.html`, `team/ranking.html`, `competition/show.html`
- Account: `account/login.html`, `account/register.html`
- Competition admin: `competition/list.html`, `competition/add.html`, `competition/edit.html`, `competition/result.html`
- Prediction user flow: `prediction/list.html`, `prediction/form.html`
- Team user flow: `team/dashboard.html`, `team/show.html`, `team/scoreboard.html`, `team/edit.html`
- Error pages: `error/403.html`, `error/404.html`, `error/500.html`
- Fragments: `fragments/navbar.html`

## Controller Route Map

- Public routes: `GET /home`, `GET /team/ranking`, `GET /competition/{id}`, `GET /login`, `GET /register`, `POST /register`
- Admin routes: `GET /competition`, `GET /competition/add`, `POST /competition/add`, `GET /competition/edit/{id}`, `POST /competition/edit/{id}`, `GET /competition/{id}/result`, `POST /competition/{id}/result`
- User routes: `GET /team/dashboard`, `POST /team/create`, `POST /team/join`, `GET /team/{id}`, `GET /team/{id}/scoreboard`, `POST /team/{id}/invite-code`, `POST /team/{id}/members/{memberId}/remove`, `GET /predictions`, `GET /predictions/{competitionId}`, `POST /predictions/{competitionId}`
- Public REST routes remain outside the view refactor: `GET /api/matches`, `GET /api/stadiums/{id}/capacity`

## Link And Form Checks

- Navbar links map to existing routes: `/home`, `/team/ranking`, `/team/dashboard`, `/predictions`, `/competition`, `/login`, `/register`.
- Public match links in `home.html`, `competition/list.html`, and `competition/show.html` map to `GET /competition/{id}`.
- Admin match links map to `GET /competition/add`, `GET /competition/edit/{id}`, and `GET /competition/{id}/result`.
- Prediction links map to `GET /predictions/{competitionId}`.
- Team links map to `GET /team/{id}` and `GET /team/{id}/scoreboard`.
- POST forms found: login, register, competition add/edit/result, team create/join, team invite-code regeneration, team member removal, prediction save, and logout.
- Explicit CSRF hidden inputs found on login, logout, team invite-code regeneration, and team member removal. Other Thymeleaf `th:action` POST forms rely on Spring's request-data-value processor; verify during MVC/security closure.

## Form Binding And Validation

- `account/register.html` uses `inputRegistrationDto`, `th:field`, and nearby `th:errors`.
- `competition/add.html` and `competition/edit.html` use `inputCompetitionDto`, `th:field`, and field-level `th:errors`.
- `competition/result.html` uses `inputCompetitionResultDto`, `th:field`, and field-level `th:errors`.
- `team/dashboard.html` uses `inputTeamJoinDto` and `inputTeamDto`, `th:field`, and field-level `th:errors`.
- `prediction/form.html` uses `inputPredictionDto`, `th:field`, and field-level `th:errors`.
- `account/login.html` is not DTO-backed, uses `name="email"` and `name="password"` as expected by Spring Security.

## Hardcoded User-Facing Text To Move Later

- `home.html`: literal `vs`.
- `competition/show.html`: literal `vs`.
- `prediction/form.html`: literal `vs`.
- `team/show.html`: literal `Rank: #`.
- `messages.properties`: footer keys contain placeholder characters, but footer rendering is intentionally out of scope for this run.

## Inline Actions

- `competition/add.html`, `competition/edit.html`, `competition/result.html`, and `prediction/form.html`: `onclick="history.back()"`; removable if a safe route-specific cancel link exists, otherwise isolate as small static JavaScript.
- `team/show.html`: clipboard copy `onclick`; acceptable as small convenience behavior, or movable to static JavaScript later.

## Footer Decision

- `fragments/footer.html` does not currently exist. Per user direction during this Ralph run, do not add the footer back.
- No footer includes are present in current templates.

## Dirty Worktree Notes

- At audit time, only this feature's new PRD/plan/Ralph files were untracked.
- No current tracked template, stylesheet, or message-bundle modifications were present in `git status --short`.

## Behavior

This audit task only recorded current routes and view risks. No application behavior was changed by the audit itself.
