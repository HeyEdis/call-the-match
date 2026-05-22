# Plan: Testing

> Source PRD: `src/main/resources/prd/06-testing-prd.md`

## Sources

1. Existing `call-the-match` codebase – bestaande testklassen als referentiepatroon.
2. School guidelines uit `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen` – verplichte testcategorieën.
3. Exercise projects uit `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij` – testpatronen.
4. Git repository: https://github.com/HeyEdis/call-the-match.git.
5. User decisions uit huidige conversatie.

## Architectural Decisions

- **Routes**: public `/home`, `/ranking`, `/competition/{id}`, `/login`, `/register`; user-only `/team/**`, `/predictions/**`; admin-only `/competition/add`, `/competition/edit/**`, `/competition/*/result`.
- **Schema**: bestaande entities (`MyUser`, `Team`, `Competition`, `Country`, `Stadium`, `Prediction`, `Ranking`, `TeamMember`).
- **Key models**: request DTOs met Jakarta validation, response DTOs voor views.
- **Security**: email login, CSRF, role-based access (USER, ADMIN), 403 handler, form-login page.
- **Validation/i18n**: bestaande annotaties op DTOs, custom annotatie(s) en validator-klasse(n) worden later toegevoegd.
- **REST/WebClient**: REST controllers bestaan nog niet; tests worden geschreven zodra die bestaan.
- **Testing**: tests in aparte folders: `controller/`, `restcontroller/`, `security/`, `validation/`.

---

## Phase 1: MVC Controller Tests – AccountController

**User stories**: 1

### What To Build

Tests voor `AccountController` in `src/test/java/com/example/callthematch/controller/AccountControllerTests.java`. Dekt registratie-formulier tonen, succesvolle registratie redirect, en validatiefouten terug naar register view.

### Acceptance Criteria

- [ ] GET `/register` retourneert status 200, view `account/register`, model bevat `inputRegistrationDto`.
- [ ] GET `/login` retourneert status 200, view `account/login`.
- [ ] POST `/register` met geldige data redirectt naar `/login` (of verwachte success-route).
- [ ] POST `/register` met ongeldige data retourneert view `account/register` met field errors op `firstName`, `lastName`, `userName`, `email`, `password`.
- [ ] POST `/register` zonder CSRF token geeft 403.

---

## Phase 2: MVC Controller Tests – CompetitionController

**User stories**: 1

### What To Build

Tests voor `CompetitionController` in `src/test/java/com/example/callthematch/controller/CompetitionControllerTests.java`. Dekt publieke lijst/detail en admin add-flow.

### Acceptance Criteria

- [ ] GET `/competition` retourneert status 200, view `competition/list`, model bevat `competitionList`.
- [ ] GET `/competition/1` retourneert status 200, view `competition/show`, model bevat `competition`.
- [ ] GET `/competition/999999` retourneert status 404, view `error/404`.
- [ ] GET `/competition/add` als ADMIN retourneert status 200, view `competition/add`, model bevat `stadiums`, `countries`, `inputCompetitionDto`.
- [ ] POST `/competition/add` als ADMIN met geldige data redirectt naar `/home`.
- [ ] POST `/competition/add` als ADMIN met ongeldige data retourneert view `competition/add` met foutmelding.
- [ ] GET `/competition/add` als USER retourneert 403.

---

## Phase 3: MVC Controller Tests – HomeController

**User stories**: 1

### What To Build

Tests voor `HomeController` in `src/test/java/com/example/callthematch/controller/HomeControllerTests.java`. Dekt de publieke homepagina.

### Acceptance Criteria

- [ ] GET `/home` retourneert status 200, view `home`, model bevat `competitionList`.
- [ ] GET `/home` is toegankelijk als guest (geen authenticatie nodig).

---

## Phase 4: MVC Controller Tests – RankingController

**User stories**: 1

### What To Build

Tests voor `RankingController` in `src/test/java/com/example/callthematch/controller/RankingControllerTests.java`. Dekt de publieke ranking pagina.

### Acceptance Criteria

- [ ] GET `/ranking` retourneert status 200, view `ranking/list`, model bevat `teamList`.
- [ ] Ranking lijst bevat maximaal 10 teams, gesorteerd op score (aflopend).
- [ ] GET `/ranking` is toegankelijk als guest.

---

## Phase 5: MVC Controller Tests – TeamController

**User stories**: 1

### What To Build

Tests voor `TeamController` in `src/test/java/com/example/callthematch/controller/TeamControllerTests.java`. Dekt dashboard, create, join, detail, invite-code regeneratie en member removal.

### Acceptance Criteria

- [ ] GET `/team/dashboard` als USER retourneert status 200, view `team/dashboard`, model bevat `teamList`, `inputTeamDto`, `inputTeamJoinDto`.
- [ ] GET `/team/dashboard` toont alleen teams van de ingelogde user.
- [ ] POST `/team/create` als USER met geldige naam redirectt naar `/team/dashboard`.
- [ ] POST `/team/create` als USER met lege naam retourneert view `team/dashboard` met field error op `name`.
- [ ] POST `/team/create` met duplicate naam retourneert view `team/dashboard` met error op `name`.
- [ ] POST `/team/join` als USER met geldige invite-code redirectt naar `/team/dashboard`.
- [ ] POST `/team/join` als USER met ongeldige invite-code retourneert view `team/dashboard` met field error op `inviteCode`.
- [ ] GET `/team/{id}` als member retourneert status 200, view `team/show`, model bevat `team` en `isOwner`.
- [ ] GET `/team/{id}` als non-member retourneert 403.
- [ ] POST `/team/{id}/invite-code` als owner redirectt succesvol.
- [ ] POST `/team/{id}/invite-code` als non-owner retourneert 403.
- [ ] POST `/team/{id}/members/{memberId}/remove` als non-owner retourneert 403.

---

## Phase 6: Security Tests

**User stories**: 3

### What To Build

Security tests in `src/test/java/com/example/callthematch/security/AccessSecurityTests.java`. Verificatie van alle route-toegangsbeslissingen per rol.

### Acceptance Criteria

- [ ] Guest kan public routes openen: `/home`, `/ranking`, `/competition/{id}`, `/login`, `/register`.
- [ ] Guest wordt geredirect naar `/login` bij user-only routes (`/team/**`, `/predictions/**`).
- [ ] USER kan `/team/dashboard` en `/predictions` openen.
- [ ] USER krijgt 403 op admin-routes (`/competition/add`, `/competition/edit/**`, `/competition/*/result`).
- [ ] ADMIN kan `/competition/add` en `/competition/edit/{id}` openen.
- [ ] ADMIN krijgt 403 op user-only routes (`/team/**`, `/predictions/**`).
- [ ] Form-login met correcte credentials (email + password) resulteert in authenticated session.
- [ ] Form-login met onjuiste credentials redirect naar `/login?error`.
- [ ] Logout redirect naar `/login?logout`.
- [ ] POST endpoints zonder CSRF token geven 403.

---

## Phase 7: Validatie Tests – Bestaande Annotaties

**User stories**: 4

### What To Build

Validatie unit tests in `src/test/java/com/example/callthematch/validation/` voor alle request DTOs met `jakarta.validation.Validator`. Geen Spring context nodig.

### Acceptance Criteria

- [ ] `InputRegistrationDTOValidationTests`: ongeldige velden (blank, invalid email, kort wachtwoord) geven violations; geldige input geeft geen violations.
- [ ] `InputTeamDTOValidationTests`: lege naam geeft violation; geldige naam geeft geen violation.
- [ ] `InputTeamJoinDTOValidationTests`: lege invite-code geeft violation; geldige code geeft geen violation.
- [ ] `InputCompetitionDTOValidationTests`: ongeldige velden geven violations; geldige input geeft geen violations.
- [ ] Elke test verifieert zowel het negatieve (ongeldige input) als het positieve (geldige input) geval.

---

## Phase 8: Validatie Tests – Custom Annotatie(s) & Validator Klasse(n)

**User stories**: 5, 6

### What To Build

Tests in `src/test/java/com/example/callthematch/validation/` voor custom annotatie(s) en hun validator-klassen. Zodra de custom annotatie (bijv. `@ValidMatchDate`) en validator-klasse worden aangemaakt, schrijf een test die de validator direct instantieert.

### Acceptance Criteria

- [ ] Validator-klasse wordt direct geïnstantieerd (zonder Spring context).
- [ ] `isValid(geldigeWaarde, context)` retourneert `true`.
- [ ] `isValid(ongeldigeWaarde, context)` retourneert `false`.
- [ ] `isValid(null, context)` retourneert `true` (null-handling conventie).
- [ ] ConstraintValidatorContext mag gemockt worden met Mockito.
- [ ] Minstens 1 custom annotatie + validator is volledig getest.

---

## Phase 9: REST Controller Tests

**User stories**: 2

### What To Build

Tests in `src/test/java/com/example/callthematch/restcontroller/` voor REST endpoints zodra die bestaan. MockMvc met JSON assertions.

### Acceptance Criteria

- [ ] GET op public REST endpoint retourneert 200 + JSON body.
- [ ] GET op niet-bestaande resource retourneert 404.
- [ ] POST/PUT/DELETE zonder authenticatie retourneert 401 of 403.
- [ ] POST met geldige JSON body als geautoriseerde user retourneert 201 of 200.
- [ ] POST met ongeldige JSON body retourneert 400 met validation errors.
- [ ] JSON response structuur wordt geverifieerd met `jsonPath`.

