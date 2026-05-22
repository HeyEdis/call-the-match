# Plan: REST Controllers & Reactive WebClient

> Source PRD: `src/main/resources/prd/07-rest-and-webclient-prd.md`

## Sources

1. FIFA World Cup 2026 Team Prediction PDF: match and stadium context for the public REST data.
2. `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen\webservices_REST.pdf`: REST controllers, REST advice, ErrorResponse, WebClient and REST tests.
3. `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen\Oneindige_lus_vermijden.pdf`: JSON serialization guidance for avoiding entity relationship loops.
4. Lesson notes `08-05-26-REST.md` and `Project.md`: project-specific REST, WebClient and JSON-loop reminders.
5. `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_REST\Spring_Boot_rest_fruit_start\src\main\java\com\example\spring_boot_rest_fruit_start\controller\FruitRestController.java`: primary REST controller shape for GET endpoints.
6. `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_REST\Spring_Boot_rest_fruit_start\src\main\java\com\example\spring_boot_rest_fruit_start\advice\FruitErrorAdvice.java` and `dto\response\ErrorResponse.java`: REST error-response pattern.
7. `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_REST\Spring_Boot_rest_fruit_start\src\main\java\com\example\spring_boot_rest_fruit_start\client\ClientRunner.java` and `client\RestClientDemo.java`: WebClient demo structure.
8. `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_REST\Spring_Boot_rest_fruit_start\src\test\java\com\example\spring_boot_rest_fruit_start\controller\FruitRestControllerTest.java`: REST controller test pattern with MockMvc and JSON assertions.
9. Existing `call-the-match` codebase: `Competition`, `Stadium`, repositories and security configuration reused by the REST slice.
10. User decision: scope remains limited to two public GET endpoints plus a Spring WebClient consumer.
11. Git repository: https://github.com/HeyEdis/call-the-match.git.

## Architectural Decisions

Durable decisions that apply across all phases:

- **Routes**: publiek `/api/matches` en `/api/stadiums/{id}/capacity`; geen authenticatie vereist.
- **Schema**: bestaande `Competition` (met `date`, `teamA`, `teamB`, `stadium`, `scoreA`, `scoreB`) en `Stadium` (met `capacity`) entities.
- **Key models**: `MatchRestDTO`, `StadiumCapacityDTO` als response objecten; `Competition` en `Stadium` als domain entities.
- **Security**: `/api/**` wordt toegevoegd aan de publieke permit-list. Geen mutatie-endpoints.
- **Validation/i18n**: geen i18n voor REST responses. Bad request voor ongeldig datumformaat, 404 voor onbekend stadium.
- **REST/WebClient**: beide volledig in scope. REST endpoints + Spring Reactive WebClient consumer service.
- **Testing**: REST controller tests met `@WebMvcTest` in het late test block (Phase 4).

---

## Phase 1: Matches REST Endpoint

**User stories**: 1

### What To Build

Een complete verticale slice voor het ophalen van wedstrijden op een datum: repository query method, response DTO, REST controller endpoint, security permit voor `/api/**`, en error handling voor ongeldig datumformaat.

### Acceptance Criteria

- [ ] `CompetitionRepository` heeft een `findByDate(LocalDate date)` query method.
- [ ] `MatchRestDTO` record/class bevat id, teamAName, teamBName, date, time, stadiumName, scoreA, scoreB.
- [ ] `GET /api/matches?date=2026-05-20` retourneert 200 met JSON array.
- [ ] Lege lijst wordt geretourneerd als er geen wedstrijden zijn op die datum.
- [ ] Ongeldig datumformaat retourneert 400 met error JSON.
- [ ] `/api/**` is toegevoegd aan de publieke security permit-list.
- [ ] Guest, user en admin kunnen het endpoint aanroepen zonder authenticatie.

---

## Phase 2: Stadium Capacity REST Endpoint

**User stories**: 2

### What To Build

Een complete verticale slice voor het ophalen van stadioncapaciteit: response DTO, REST controller endpoint met `@PathVariable`, en 404 error handling voor onbekend stadium id.

### Acceptance Criteria

- [ ] `StadiumCapacityDTO` record/class bevat id, name, capacity.
- [ ] `GET /api/stadiums/1/capacity` retourneert 200 met JSON object.
- [ ] Onbekend stadium id retourneert 404 met `{ "error": "Stadium not found" }`.
- [ ] Guest, user en admin kunnen het endpoint aanroepen zonder authenticatie.

---

## Phase 3: WebClient Consumer Service

**User stories**: 3, 4

### What To Build

Voeg `spring-boot-starter-webflux` toe, configureer een `WebClient` bean, en bouw een service die via WebClient de eigen `/api/matches` en `/api/stadiums/{id}/capacity` endpoints aanroept. Dit bewijst dat de applicatie een REST API consumeert via Spring Reactive WebClient.

### Acceptance Criteria

- [ ] `spring-boot-starter-webflux` dependency staat in `pom.xml`.
- [ ] Een `@Configuration` class definieert een `WebClient` bean met de juiste base URL.
- [ ] Een service class (bijv. `MatchWebClientService`) roept beide endpoints aan via WebClient.
- [ ] De service retourneert `Mono` of `Flux` types (reactive).
- [ ] De WebClient consumer is aanroepbaar (via test, controller, of logging) als bewijs.

---

## Phase 4: REST Controller Tests

**User stories**: alle (late test block)

### What To Build

`@WebMvcTest` tests voor beide REST endpoints. Dekt de school-vereiste categorie "REST controller tests".

### Acceptance Criteria

- [ ] Test class gebruikt `@WebMvcTest` met de REST controller(s).
- [ ] Test: `GET /api/matches?date=2026-05-20` retourneert 200 met verwachte JSON.
- [ ] Test: `GET /api/matches?date=2026-05-20` retourneert 200 met lege lijst als er geen matches zijn.
- [ ] Test: `GET /api/stadiums/1/capacity` retourneert 200 met verwachte JSON.
- [ ] Test: `GET /api/stadiums/999/capacity` retourneert 404.
- [ ] Tests gebruiken `MockMvc` en gemockte service/repository lagen.

