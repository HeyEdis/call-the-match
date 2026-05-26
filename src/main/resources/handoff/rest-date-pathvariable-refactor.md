# Handoff: REST Date PathVariable Refactor

## Context

The REST matches endpoint was changed during the audit from a query-parameter route to a path-variable route.

Current controller:

- `src/main/java/com/example/callthematch/controller/CompetitionRestController.java`
- Class mapping: `@RequestMapping("/api")`
- Method mapping: `@GetMapping("{date}/matches")`
- Effective route: `GET /api/{date}/matches`

Current method:

```java
@GetMapping("{date}/matches")
public List<MatchRestDTO> getMatchByDate(@PathVariable("date") String date, Locale locale) {
    return competitionService.findRestMatchesByDate(dateFormatter.parse(date, locale));
}
```

The old route was `GET /api/matches?date=yyyy-MM-dd`.

## Why This Exists

The user intentionally changed the endpoint shape to use `@PathVariable`, matching the style of the school REST examples that use routes like:

- `GET /fruits/{id}`
- `GET /schools/{schoolId}/students`
- `GET /api/stadiums/{id}/capacity`

Do not silently revert this to `@RequestParam`.

## Required Follow-Up

Refactor the WebClient caller:

- File: `src/main/java/com/example/callthematch/client/RestClient.java`
- Current code still uses `.path("/api/matches")` plus `.queryParam("date", date)`.
- Update it to call `/api/{date}/matches`, for example with `uriBuilder.path("/api/{date}/matches").build(date)`.

Refactor REST controller tests:

- File: `src/test/java/com/example/callthematch/restcontroller/CompetitionRestControllerTests.java`
- Current tests still call `get("/api/matches").param("date", "2026-05-20")`.
- Update happy path, empty-list path, and invalid-date path to call:

```java
get("/api/2026-05-20/matches")
get("/api/invalid/matches")
```

Keep the existing assertions around:

- JSON array response.
- Empty array response.
- Bad request JSON response for invalid date.
- `dateFormatter.parse(...)` verification.
- `competitionService.findRestMatchesByDate(...)` verification.

## Verification

After refactoring, run at least:

```powershell
.\mvnw.cmd "-Dtest=CompetitionRestControllerTests,StadiumRestControllerTests" test
```

If time allows, run:

```powershell
.\mvnw.cmd test
```

## Suggested Skills

- `project-guidelines`: Use this first. REST choices must stay aligned with the EWD local examples and the `Richtlijnen` folder.
- `diagnose`: Use only if the route refactor causes unexpected MockMvc/WebClient failures.
