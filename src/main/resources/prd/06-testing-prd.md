# PRD: Testing

## Problem Statement

Het HOGENT EWD-project vereist unit tests in meerdere categorieën om te slagen voor de verdediging. De schoolrichtlijnen eisen tests voor MVC controllers, REST controllers, security, bestaande validatie-annotaties, custom annotatie(s) en validator-klasse(n). Er bestaan al veel tests maar ze staan in de verkeerde mappenstructuur (root package en `dto/`, `service/`, `validator/` submappen) in plaats van de school-conforme structuur (`controller/`, `security/`, `validation/`, `restcontroller/`). Daarnaast is de dekking onvolledig en ontbreken custom validatie-annotaties/validators in de codebase.

## Solution

Reorganiseer de bestaande testsuite naar de juiste mappenstructuur en vul ontbrekende dekking aan. De aanpak is:

1. **Verplaats bestaande tests** naar de juiste packages volgens schoolconventie.
2. **MVC Controller tests** – Hernoem/verplaats bestaande MVC tests naar `controller/` package en vul aan waar nodig.
3. **REST Controller tests** – `@WebMvcTest` of `@SpringBootTest` + MockMvc tests voor REST endpoints (zodra REST controllers bestaan).
4. **Security tests** – Verplaats `AccessSecurityMvcTests` naar `security/` package en breid uit.
5. **Validatie tests** – Verplaats DTO-validatietests van `dto/` naar `validation/` en verplaats `CompetitionValidatorTests` van `validator/` naar `validation/`.

Safe passing first: focus op correctheid, juiste structuur en brede dekking van de verplichte categorieën boven geavanceerde edge cases.

## Current Codebase State

### Bestaande tests (HUIDIGE locatie → GEWENSTE locatie)

| Testklasse | Huidige locatie | Categorie | Gewenste locatie |
|---|---|---|---|
| `AccessSecurityMvcTests` | root package | Security | `security/AccessSecurityTests` |
| `PublicBrowseMvcTests` | root package | MVC | `controller/HomeControllerTests` of `controller/PublicBrowseControllerTests` |
| `TeamManagementMvcTests` | root package | MVC + Security | `controller/TeamControllerTests` |
| `MatchManagementMvcTests` | root package | MVC | `controller/CompetitionControllerTests` |
| `PredictionMvcTests` | root package | MVC | `controller/PredictionControllerTests` |
| `InputRegistrationDTOValidationTests` | `dto/` | Validatie (bestaande annotaties) | `validation/InputRegistrationDTOValidationTests` |
| `InputTeamDTOValidationTests` | `dto/` | Validatie (bestaande annotaties) | `validation/InputTeamDTOValidationTests` |
| `InputCompetitionDTOValidationTests` | `dto/` | Validatie (bestaande annotaties) | `validation/InputCompetitionDTOValidationTests` |
| `InputPredictionDTOValidationTests` | `dto/` | Validatie (bestaande annotaties) | `validation/InputPredictionDTOValidationTests` |
| `CompetitionValidatorTests` | `validator/` | Validatie (validator klasse) | `validation/CompetitionValidatorTests` |
| `TeamServiceTests` | `service/` | Service unit (geen schoolcategorie) | `service/` (mag blijven) |
| `UserServiceTests` | `service/` | Service unit (geen schoolcategorie) | `service/` (mag blijven) |
| `PredictionServiceTests` | `service/` | Service unit (geen schoolcategorie) | `service/` (mag blijven) |
| `ScoringServiceTests` | `service/` | Service unit (geen schoolcategorie) | `service/` (mag blijven) |
| `TeamMemberServiceTests` | `service/` | Service unit (geen schoolcategorie) | `service/` (mag blijven) |

### Ontbreekt

- REST controller tests (REST controllers bestaan nog niet).
- Custom annotatie tests (custom annotaties en validators bestaan nog niet).
- Tests in de juiste mappenstructuur (`controller/`, `security/`, `validation/`, `restcontroller/`).
- Eventuele uitbreiding van MVC controller tests voor AccountController (register/login flows).

## School Requirements

- MVC controller tests met MockMvc.
- REST controller tests met MockMvc.
- Security tests (rolgebaseerd, form-login, CSRF).
- Validatie tests met bestaande annotaties (`@NotBlank`, `@Email`, `@Size`, `@NotNull`, `@Future`, `@Past`, etc.).
- Minstens één custom annotatie met bijbehorende `ConstraintValidator` klasse, getest via unit test.
- Validator-klasse tests (direct testen van de `isValid` methode).

## Role And Access Decisions

Niet direct van toepassing op deze PRD. Tests verifiëren de bestaande access-regels:

- **Guest**: mag public routes (home, ranking, competition detail, login, register).
- **User**: mag team/predictions routes, niet admin-routes.
- **Admin**: mag competition/add, edit, result; niet team/predictions routes.
- **Forbidden**: 403 bij ongeautoriseerde toegang.

## User Stories

1. As a developer, I want MVC controller tests for every controller, so that form submissions, model attributes, and view names are verified.
2. As a developer, I want REST controller tests for all REST endpoints, so that JSON responses and HTTP status codes are verified.
3. As a developer, I want security tests that verify role-based access for every protected route, so that the SecurityConfig is proven correct.
4. As a developer, I want validation tests for DTOs using existing annotations, so that invalid input is proven to be rejected.
5. As a developer, I want at least one custom annotation with a ConstraintValidator tested in isolation, so that custom validation logic is proven correct.
6. As a developer, I want validator class tests that directly call `isValid`, so that the validator logic is tested without Spring context.

## Implementation Decisions

### Reorganisatie bestaande tests

De eerste prioriteit is het verplaatsen van bestaande tests naar de juiste mappenstructuur. Dit betekent:

- Verplaats testklassen naar het juiste subpackage (`controller/`, `security/`, `validation/`).
- Pas de `package` declaratie bovenaan de bestanden aan.
- Hernoem klassen waar nodig om het naampatroon `{Feature}ControllerTests`, `{Feature}SecurityTests`, `{Dto}ValidationTests` te volgen.
- Verwijder de oude bestanden na succesvolle verplaatsing.
- Service tests (`service/`) hoeven NIET verplaatst te worden – die zijn geen schoolcategorie maar mogen blijven.

### MVC Controller Tests

- Gebruik `@SpringBootTest` + `@AutoConfigureMockMvc` (integration style, zoals de bestaande tests).
- Verplaats `PublicBrowseMvcTests` → `controller/` (eventueel splitsen in HomeControllerTests en RankingControllerTests).
- Verplaats `TeamManagementMvcTests` → `controller/TeamControllerTests`.
- Verplaats `MatchManagementMvcTests` → `controller/CompetitionControllerTests`.
- Verplaats `PredictionMvcTests` → `controller/PredictionControllerTests`.
- Voeg `controller/AccountControllerTests` toe (nieuw, voor register/login flows).
- Test per controller: happy path, validatie-fouten terugkeer naar view, redirect na succes, model-attributen.
- Gebruik `.with(user(...).roles(...))` en `.with(csrf())` voor authenticated requests.

### REST Controller Tests

- REST controllers bestaan nog niet. Zodra die worden aangemaakt (bijv. `RestTeamController`, `RestCompetitionController`), test met MockMvc op JSON responses.
- Verificeer HTTP status codes: 200, 201, 400, 403, 404.
- Verificeer JSON-body structuur met `jsonPath`.
- Guest mag public GET endpoints; mutaties vereisen authenticatie.

### Security Tests

- Verplaats `AccessSecurityMvcTests` → `security/AccessSecurityTests`.
- Uitbreiden met:
  - Alle admin-routes (add/edit/result).
  - Alle user-routes incl. predictions.
  - CSRF-verificatie op POST endpoints.
  - Logout-flow.
- Test form-login met juiste en onjuiste credentials.

### Validatie – Bestaande Annotaties

- Verplaats alle tests uit `dto/` naar `validation/` package:
  - `dto/InputRegistrationDTOValidationTests` → `validation/InputRegistrationDTOValidationTests`
  - `dto/InputTeamDTOValidationTests` → `validation/InputTeamDTOValidationTests`
  - `dto/InputCompetitionDTOValidationTests` → `validation/InputCompetitionDTOValidationTests`
  - `dto/InputPredictionDTOValidationTests` → `validation/InputPredictionDTOValidationTests`
- Verplaats `validator/CompetitionValidatorTests` → `validation/CompetitionValidatorTests`.
- Voor elke request-DTO een testklasse met `jakarta.validation.Validator`.
- Test zowel ongeldige als geldige input.
- Vul aan waar nodig: `InputTeamJoinDTO` en eventuele future DTOs.

### Validatie – Custom Annotatie(s)

De custom annotatie(s) bestaan nog niet maar worden aangemaakt. Verwachte voorbeelden:

- `@ValidMatchDate` – valideert dat de wedstrijddatum binnen 20 mei 2026 – 6 juni 2026 valt.
- `@UniqueTeamName` – valideert dat de teamnaam nog niet bestaat (database-check).
- Of een andere domein-specifieke constraint.

Test-aanpak:
- Unit test die de annotatie en validator-klasse direct test zonder Spring context.
- Gebruik `Validation.buildDefaultValidatorFactory().getValidator()` of instantieer de validator-klasse direct.
- Test `isValid(...)` met geldige en ongeldige waarden.
- Test null-handling (conventie: null = valid, laat `@NotNull` de null-check doen).

### Validatie – Validator Klasse(n)

- Instantieer de `ConstraintValidator` implementatie direct.
- Roep `initialize(...)` aan indien nodig.
- Roep `isValid(value, context)` aan met een mock `ConstraintValidatorContext`.
- Verifieer true/false return waarden.

### Structuur en naamgeving

Testklassen verplaatsen/aanmaken in `src/test/java/com/example/callthematch/`:

| Categorie | Pakket/Locatie | Naampatroon | Actie |
|---|---|---|---|
| MVC controllers | `controller/` | `{Feature}ControllerTests` | Verplaats bestaande + nieuwe AccountController |
| REST controllers | `restcontroller/` | `Rest{Feature}Tests` | Nieuw (zodra REST bestaat) |
| Security | `security/` | `{Feature}SecurityTests` | Verplaats AccessSecurityMvcTests |
| Validatie (bestaande + custom + validator) | `validation/` | `{DtoOrValidator}ValidationTests` | Verplaats uit dto/ en validator/ |
| Service (geen schoolcategorie) | `service/` | `{Service}Tests` | Blijft op huidige locatie |

### Dependencies

Bestaande test-dependencies in pom.xml volstaan (spring-boot-starter-test, spring-security-test). Geen extra dependencies nodig.

## Testing Decisions

- Dit IS de test-PRD; tests zijn het primaire deliverable.
- Alle testcategorieën zijn nu in scope (niet uitgesteld).
- Minimum per categorie voor een veilige pass:
  - MVC: minstens 3 controllers getest.
  - REST: minstens 1 REST controller getest (zodra die bestaat).
  - Security: alle routes per rol getest.
  - Validatie bestaande: minstens 3 DTOs getest.
  - Custom annotatie: minstens 1 custom annotatie + validator getest.
- Prior art: bestaande tests in dit project volgen het `@SpringBootTest` + `@AutoConfigureMockMvc` patroon met AssertJ en MockMvc matchers.

## REST And WebClient Decisions

- REST controllers bestaan nog niet; REST controller tests worden geschreven zodra de REST controllers zijn geïmplementeerd.
- WebClient tests zijn out of scope voor deze PRD.
- Deze PRD reserveert de categorie zodat de tests direct geschreven kunnen worden wanneer de REST layer klaar is.

## Out Of Scope

- WebClient / externe API-integratie tests.
- Performance tests.
- E2E/UI tests (Selenium, Playwright).
- Service-layer unit tests (bestaan al deels, geen schoolvereiste categorie).
- Repository-layer tests.
- Implementatie van de custom annotaties/validators zelf (die worden in een andere taak aangemaakt; deze PRD beschrijft alleen hoe ze getest moeten worden).

## Sources

1. Existing `call-the-match` codebase – bestaande testklassen als referentiepatroon.
2. School guidelines uit `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen` – verplichte testcategorieën.
3. Exercise projects uit `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij` – testpatronen.
4. Git repository: https://github.com/HeyEdis/call-the-match.git.
5. User decisions uit huidige conversatie – custom annotaties bestaan nog niet maar moeten wel in PRD.

## Further Notes

- **Risico**: Custom annotaties en validators bestaan nog niet. Zodra die worden geïmplementeerd, moeten de tests direct worden geschreven volgens het patroon in deze PRD.
- **Volgorde**: Schrijf eerst de validatie-tests voor bestaande annotaties en de security-tests (die kunnen nu al), dan MVC-controller tests voor admin-flows, dan custom validator tests en REST tests zodra die lagen bestaan.
- **Deadline**: 27 mei 2026. Verdediging start 20 mei 2026. Alle testcategorieën moeten voor de verdediging klaar zijn.
- **Tip**: Houd tests deterministisch – gebruik `InitDataConfig` seed data die in bestaande tests al wordt gebruikt.



