# Ralph Progress Log: 07-rest-and-webclient

Each iteration appends what was done, decisions made, files changed, verification results, and blockers.

## Entries

### 2026-05-23 - matches-rest-endpoint

- Task: `matches-rest-endpoint` - Matches REST GET Endpoint.
- Changed: added `CompetitionRepository.findByDate(LocalDate date)`, `MatchRestDTO`, `CompetitionRestController`, REST bad-request advice, and `/api/**` public security access.
- Decisions: used REST response DTOs to avoid JPA JSON graph loops; followed the project-guidelines REST reference and fruit exercise error-response shape with `ErrorResponse(status, message, timestamp)`.
- Files changed:
  - `src/main/java/com/example/callthematch/repository/CompetitionRepository.java`
  - `src/main/java/com/example/callthematch/dto/response/MatchRestDTO.java`
  - `src/main/java/com/example/callthematch/dto/response/ErrorResponse.java`
  - `src/main/java/com/example/callthematch/service/CompetitionService.java`
  - `src/main/java/com/example/callthematch/controller/CompetitionRestController.java`
  - `src/main/java/com/example/callthematch/advice/RestErrorAdvice.java`
  - `src/main/java/com/example/callthematch/config/SecurityConfig.java`
  - `src/main/resources/plan/07-rest-and-webclient/plan.json`
- Verification: `.\mvnw.cmd test` passed twice after implementation and after aligning the error response with project guidelines. Final run: 61 tests, 0 failures, 0 errors.
- Result: all acceptance criteria for this task are verified; `passes` set to `true`.

### 2026-05-23 - review comment on REST date parsing

- Task: review feedback for `matches-rest-endpoint`.
- Changed: removed `@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)` from `CompetitionRestController` and reused the existing project `DateFormatter` for the `date` query parameter.
- Decisions: the REST guidelines and exercise projects prefer local formatter/utility patterns over introducing a new controller annotation here. The endpoint contract remains `GET /api/matches?date=yyyy-MM-dd`.
- Files changed:
  - `src/main/java/com/example/callthematch/controller/CompetitionRestController.java`
  - `src/main/java/com/example/callthematch/advice/RestErrorAdvice.java`
  - `src/main/resources/ralph/07-rest-and-webclient/progress.md`
- Verification: `.\mvnw.cmd -DskipTests compile` passed. A later `.\mvnw.cmd test` attempt failed during application startup because the local MySQL schema was missing `users`; this was after earlier full test runs had passed and is unrelated to the date parsing compile path.

### 2026-05-23 - stadium-capacity-rest-endpoint

- Task: `stadium-capacity-rest-endpoint` - Stadium Capacity REST GET Endpoint.
- Changed: added `StadiumCapacityDTO`, `StadiumRestController`, `StadiumService.findCapacityById(Long id)`, and REST 404 handling for `StadiumNotFound`.
- Decisions: used a dedicated `/api/stadiums` REST controller and the same school-style `ErrorResponse(status, message, timestamp)` used by the existing REST advice.
- Files changed:
  - `src/main/java/com/example/callthematch/dto/response/StadiumCapacityDTO.java`
  - `src/main/java/com/example/callthematch/controller/StadiumRestController.java`
  - `src/main/java/com/example/callthematch/service/StadiumService.java`
  - `src/main/java/com/example/callthematch/exception/StadiumNotFound.java`
  - `src/main/java/com/example/callthematch/advice/RestErrorAdvice.java`
  - `src/main/resources/plan/07-rest-and-webclient/plan.json`
  - `src/main/resources/ralph/07-rest-and-webclient/progress.md`
- Verification: `.\mvnw.cmd test` passed with 61 tests, 0 failures, 0 errors.
- Result: all acceptance criteria for this task are verified; `passes` set to `true`.
