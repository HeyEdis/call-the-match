# Plan: Testing

> Source PRD: `src/main/resources/prd/06-testing-prd.md`

## Sources

1. Existing `call-the-match` codebase: existing controllers, validators, security configuration and test classes that must be reorganized.
2. `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen\Slides_Spring_Web_Flow_JUnit.pdf`: school JUnit and MockMvc guidance for Spring MVC controller tests.
3. `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen\Slides_Spring_MultipleRow.pdf`: controller, path-variable, not-found and MVC test patterns.
4. `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen\Slides_Spring_Security_JDBC.pdf`: form login, role access and Spring Security test expectations.
5. `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen\webservices_REST.pdf`: REST controller behavior and JSON test expectations.
6. `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Multirow\Spring_Boot_list_crud-opl\src\test\java\com\example\spring_boot_list_crudopl\controller\ContactControllerTest.java`: MVC MockMvc example.
7. `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_Security\Spring_Boot_security_JPA\src\test\java\com\example\spring_boot_security_jpa\controller\SecurityTest.java`: role-aware security test example.
8. `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_ErrorMessages\Spring_Boot_i18n_Product2`: validator, i18n error-message and MVC validation examples.
9. `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij\EWDJ_REST\Spring_Boot_rest_fruit_start\src\test\java\com\example\spring_boot_rest_fruit_start\controller\FruitRestControllerTest.java`: primary REST MockMvc and `jsonPath` test example.
10. Git repository: https://github.com/HeyEdis/call-the-match.git.

## Architectural Decisions

- **Routes**: public `/home`, `/ranking`, `/competition/{id}`, `/login`, `/register`; user-only `/team/**`, `/predictions/**`; admin-only `/competition/add`, `/competition/edit/**`, `/competition/*/result`.
- **Schema**: bestaande entities (`MyUser`, `Team`, `Competition`, `Country`, `Stadium`, `Prediction`, `Ranking`, `TeamMember`).
- **Key models**: request DTOs met Jakarta validation, response DTOs voor views.
- **Security**: email login, CSRF, role-based access (USER, ADMIN), 403 handler, form-login page.
- **Validation/i18n**: bestaande annotaties op DTOs, custom annotatie(s) en validator-klasse(n) worden later toegevoegd.
- **REST/WebClient**: REST controllers bestaan nog niet; tests worden geschreven zodra die bestaan.
- **Testing**: tests reorganiseren naar aparte folders: `controller/`, `restcontroller/`, `security/`, `validation/`. Service tests blijven in `service/`.

---

## Phase 1: Reorganisatie – Verplaats Bestaande Tests Naar Juiste Packages

**User stories**: 1, 3, 4

### What To Build

Verplaats alle bestaande testklassen naar de school-conforme mappenstructuur. Geen nieuwe testlogica schrijven, alleen verplaatsen, package declaratie aanpassen en hernoemen waar nodig.

Verplaatsingen:
- `AccessSecurityMvcTests` → `security/AccessSecurityTests`
- `PublicBrowseMvcTests` → `controller/PublicBrowseControllerTests`
- `TeamManagementMvcTests` → `controller/TeamControllerTests`
- `MatchManagementMvcTests` → `controller/CompetitionControllerTests`
- `PredictionMvcTests` → `controller/PredictionControllerTests`
- `dto/InputRegistrationDTOValidationTests` → `validation/InputRegistrationDTOValidationTests`
- `dto/InputTeamDTOValidationTests` → `validation/InputTeamDTOValidationTests`
- `dto/InputCompetitionDTOValidationTests` → `validation/InputCompetitionDTOValidationTests`
- `dto/InputPredictionDTOValidationTests` → `validation/InputPredictionDTOValidationTests`
- `validator/CompetitionValidatorTests` → `validation/CompetitionValidatorTests`

### Acceptance Criteria

- [ ] Alle verplaatste tests compileren en slagen na verplaatsing.
- [ ] Package declaraties zijn correct aangepast.
- [ ] Oude bestanden in root package en `dto/`/`validator/` zijn verwijderd.
- [ ] `service/` tests blijven onaangeroerd op hun huidige locatie.
- [ ] `CallTheMatchApplicationTests` blijft in root package.
- [ ] `mvn test` slaagt volledig na reorganisatie.

---

## Phase 2: MVC Controller Tests – AccountController (nieuw)

**User stories**: 1

### What To Build

Nieuwe test `src/test/java/com/example/callthematch/controller/AccountControllerTests.java` voor register/login MVC flows.

### Acceptance Criteria

- [ ] GET `/register` retourneert status 200, view `account/register`, model bevat `inputRegistrationDTO`.
- [ ] GET `/login` retourneert status 200, view `account/login`.
- [ ] POST `/register` met geldige data redirectt naar `/login`.
- [ ] POST `/register` met ongeldige data retourneert view `account/register` met field errors.
- [ ] POST `/register` zonder CSRF token geeft 403.

---

## Phase 3: MVC Controller Tests – Aanvullingen

**User stories**: 1

### What To Build

Beoordeel de verplaatste MVC controller tests en vul ontbrekende scenario's aan. Check of elke controller minstens happy path, validatiefout-terugkeer, redirect en model-attributen dekt.

### Acceptance Criteria

- [ ] `CompetitionControllerTests` dekt GET list, GET detail, GET not-found (404), admin add flow (valid + invalid), USER access denied.
- [ ] `TeamControllerTests` dekt dashboard, create (valid + invalid + duplicate), join (valid + invalid), detail (member vs non-member), owner-only acties.
- [ ] `PredictionControllerTests` dekt prediction submit flows.
- [ ] Alle tests slagen met `mvn test`.

---

## Phase 4: Security Tests – Uitbreiden

**User stories**: 3

### What To Build

Breid de verplaatste `security/AccessSecurityTests` uit met ontbrekende access-verificaties.

### Acceptance Criteria

- [ ] Guest kan public routes openen: `/home`, `/ranking`, `/competition/{id}`, `/login`, `/register`.
- [ ] Guest wordt geredirect naar `/login` bij user-only routes.
- [ ] USER kan `/team/dashboard` en `/predictions` openen.
- [ ] USER krijgt 403 op admin-routes.
- [ ] ADMIN kan `/competition/add` openen.
- [ ] ADMIN krijgt 403 op user-only routes.
- [ ] Form-login met correcte/onjuiste credentials werkt correct.
- [ ] Logout redirect naar `/login?logout`.
- [ ] POST zonder CSRF geeft 403.

---

## Phase 5: Validatie Tests – Aanvullingen Bestaande Annotaties

**User stories**: 4

### What To Build

Beoordeel de verplaatste validatie tests in `validation/` en vul ontbrekende positieve/negatieve cases aan. Voeg `InputTeamJoinDTOValidationTests` toe als die ontbreekt.

### Acceptance Criteria

- [ ] Elke DTO-validatietest heeft zowel positief (geldige input = 0 violations) als negatief (ongeldige input = verwachte violations) cases.
- [ ] `InputTeamJoinDTOValidationTests` bestaat en test blank inviteCode.
- [ ] Alle tests gebruiken `jakarta.validation.Validator` zonder Spring context.

---

## Phase 6: Validatie Tests – Custom Annotatie(s) & Validator Klasse(n)

**User stories**: 5, 6

### What To Build

Tests voor custom annotatie(s) en hun validator-klassen zodra die bestaan. Direct instantiëren zonder Spring context.

### Acceptance Criteria

- [ ] Validator-klasse wordt direct geïnstantieerd.
- [ ] `isValid(geldigeWaarde, context)` retourneert `true`.
- [ ] `isValid(ongeldigeWaarde, context)` retourneert `false`.
- [ ] `isValid(null, context)` retourneert `true`.
- [ ] ConstraintValidatorContext gemockt met Mockito.
- [ ] Minstens 1 custom annotatie + validator volledig getest.

---

## Phase 7: REST Controller Tests

**User stories**: 2

### What To Build

Tests voor REST endpoints zodra die bestaan. MockMvc met JSON assertions.

### Acceptance Criteria

- [ ] GET op public REST endpoint retourneert 200 + JSON body.
- [ ] GET op niet-bestaande resource retourneert 404.
- [ ] POST/PUT/DELETE zonder authenticatie retourneert 401 of 403.
- [ ] POST met geldige JSON retourneert 201 of 200.
- [ ] POST met ongeldige JSON retourneert 400.
- [ ] JSON structuur geverifieerd met `jsonPath`.
