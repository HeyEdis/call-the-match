# PRD: REST Controllers & React WebClient

## Problem Statement

De schoolopdracht vereist minstens één REST controller en het consumeren van een externe (of eigen) REST API via WebClient/React. Deze PRD beperkt zich tot twee concrete vereisten: het ophalen van wedstrijden op een bepaalde datum, en het ophalen van de capaciteit van een stadion.

## Solution

Bouw twee publieke REST GET endpoints in een Spring Boot REST controller. Bouw daarnaast een React-frontend (of standalone React-pagina) die via fetch/axios deze endpoints consumeert en de data toont. Houd het minimaal en gericht op veilig slagen.

## Current Codebase State

- `Competition` entity bevat `date` (LocalDate), `time`, `teamA`, `teamB`, `stadium`, `scoreA`, `scoreB`.
- `Stadium` entity bevat `id`, `name`, `code`, `capacity`, `location`.
- `CompetitionRepository` en `StadiumRepository` bestaan maar hebben nog geen custom query methods.
- Er is nog geen REST controller in het project.
- Er is nog geen React/WebClient frontend aanwezig.

## School Requirements

- Minstens één REST controller met JSON responses.
- Minstens één WebClient- of React-consumer die de REST API aanroept.
- REST endpoints moeten publiek toegankelijk zijn voor guests (GET only).
- Tests voor REST controllers zijn vereist (kan in het late test block).

## Role And Access Decisions

- **Guest**: mag beide REST GET endpoints aanroepen.
- **User**: mag beide REST GET endpoints aanroepen.
- **Admin**: mag beide REST GET endpoints aanroepen.
- **Forbidden**: geen mutaties via REST in deze scope.

## User Stories

1. As anyone, I want to retrieve a list of matches for a specific date via REST, so that I can see the schedule for that day.
2. As anyone, I want to retrieve the capacity of a stadium by its id via REST, so that I can see how many spectators the stadium holds.
3. As anyone, I want a React page that shows matches for a chosen date, so that the WebClient/frontend requirement is fulfilled.
4. As anyone, I want a React page that shows a stadium's capacity, so that both REST endpoints are consumed by the frontend.

## Implementation Decisions

### REST Endpoints

| Method | Path | Parameters | Response |
|--------|------|------------|----------|
| GET | `/api/matches` | `date` (query, format `yyyy-MM-dd`) | JSON array of match objects for that date |
| GET | `/api/stadiums/{id}/capacity` | `id` (path) | JSON object with stadium name and capacity |

### Response DTOs

- `MatchRestDTO`: id, teamAName, teamBName, date, time, stadiumName, scoreA, scoreB.
- `StadiumCapacityDTO`: id, name, capacity.

### REST Controller

- Eén `@RestController` class, bijv. `CompetitionRestController`.
- Of twee aparte: `CompetitionRestController` en `StadiumRestController`.
- Gebruik `@RequestParam` voor datum, `@PathVariable` voor stadium id.
- Return 200 met lege lijst als er geen wedstrijden zijn op die datum.
- Return 404 als stadium id niet bestaat.

### Repository Additions

- `CompetitionRepository`: voeg `List<Competition> findByDate(LocalDate date)` toe.
- `StadiumRepository`: bestaande `findById` volstaat.

### Security

- Voeg `/api/**` toe aan de publieke permit-list in de security config.

### React Frontend

- Minimale React-app (kan als standalone HTML+JS met CDN React, of als apart project).
- Aanbevolen: plaats in `src/main/resources/static/react/` als standalone HTML die React via CDN laadt.
- Twee simpele componenten: één voor matches-by-date, één voor stadium capacity.
- Gebruik `fetch()` om de eigen REST endpoints aan te roepen.

### i18n

- Geen i18n nodig voor REST JSON responses (Engels is fine).

### Exception/Error Behavior

- Stadium niet gevonden: return 404 met een error JSON body `{ "error": "Stadium not found" }`.
- Ongeldige datum format: return 400 met error JSON body.

## Testing Decisions

- REST controller tests met `@WebMvcTest` en `MockMvc` voor beide endpoints.
- Test 200 happy path, 404 voor onbekend stadium, lege lijst voor datum zonder wedstrijden.
- Tests zijn onderdeel van het late test block maar de controller moet testbaar zijn opgezet.

## REST And WebClient Decisions

- Volledig in scope: dit IS de REST/WebClient feature PRD.
- React frontend fungeert als de "WebClient" consumer die de school vereist.

## Out Of Scope

- Mutatie-endpoints (POST/PUT/DELETE) voor wedstrijden of stadions.
- Authenticatie/autorisatie op REST endpoints.
- Complexe React routing of state management.
- Server-side WebClient die een externe derde-partij API aanroept (tenzij de school dit expliciet vereist boven een React consumer).

## Sources

1. FIFA World Cup 2026 Team Prediction PDF (wedstrijd- en stadioncontext).
2. School guidelines uit `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen` (REST + WebClient vereiste).
3. Bestaande `call-the-match` codebase (Competition, Stadium entities en repositories).
4. User beslissing: scope beperkt tot twee GET endpoints + React consumer.

## Further Notes

- Dit is bewust minimaal gehouden: twee endpoints, twee React-componenten. Genoeg om de REST + WebClient rubric items af te vinken.
- Als de school een server-side `WebClient` (Spring WebClient bean) vereist i.p.v. een browser fetch, kan één van de twee calls omgebouwd worden naar een Thymeleaf-page die via server-side WebClient de eigen REST API aanroept. Vraag dit na bij de docent als er twijfel is.
- De React bestanden in `static/react/` worden automatisch geserveerd door Spring Boot zonder extra config.

