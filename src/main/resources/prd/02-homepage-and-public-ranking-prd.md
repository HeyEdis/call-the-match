# PRD: Homepage And Public Ranking

## Problem Statement

De FIFA-opdracht vraagt een publieke startpagina met wedstrijden en een publieke top-10 van teams. De repo heeft al home-, competition- en rankingflows, maar de publieke ervaring moet nog aansluiten op rollen, i18n en latere scorecorrectheid zonder private teamdata te lekken.

## Solution

Maak de publieke shell bruikbaar en toetsbaar: een homepagina met wedstrijden in datumvolgorde, links naar login/registratie en ranking, een publieke wedstrijddetailroute, en een top-10 ranking met teamnaam, totaalscore en ledenaantal. Logged-in users zien toegang tot hun private flows; admins zien wedstrijdbeheer. Deze feature blijft bewust licht zodat verplichte functionele blokken voor teams, matches en scoring niet worden verdrongen door polish.

## Current Codebase State

- Er bestaat een publieke homecontroller die een wedstrijdlijst toont.
- Er bestaan wedstrijdlijst- en wedstrijddetailviews met wedstrijddata uit services.
- Er bestaat een rankingcontroller die topteams opvraagt.
- Team scoreberekening bestaat al op het teammodel en seeded data vult teamleden met scores.
- Resource bundles bevatten al enkele competition labels en datumpatronen, maar nog geen volledige publieke schermdekking.
- Error templates voor 403, 404 en 500 bestaan al, terwijl globale advice en type mismatch handling nog niet zichtbaar zijn.

## School Requirements

- MVC controller en Thymeleaf views voor publieke pagina's.
- Service/repository/JPA-laag als bron voor wedstrijd- en teamdata.
- Resource bundle gebruik voor labels, datumformaat en minstens een volledig scherm binnen het project.
- Error handling voor onbestaande routes en ongeldige path variables.
- Security moet publieke routes expliciet bereikbaar houden.
- MVC en securitytests later.
- REST/WebClient blijft een apart laat blok.

## Role And Access Decisions

- **Guest**: mag home, publieke ranking en publieke wedstrijddetail bekijken.
- **User**: krijgt dezelfde publieke info plus links naar eigen teams en prognoses.
- **Admin**: krijgt dezelfde publieke info plus een duidelijke ingang naar wedstrijdbeheer.
- **Forbidden**: publieke ranking toont geen private scoreboard-details en publieke pagina's voeren geen muterende acties uit.

## User Stories

1. As a guest, I want to see the World Cup matches on the home page, so that I can inspect the schedule.
2. As a guest, I want matches sorted by date and time, so that the schedule reads naturally.
3. As a guest, I want each match summary to show countries, date, time, stadium and location, so that I have enough context.
4. As anyone, I want to open public match detail, so that one fixture can be inspected directly.
5. As a guest, I want links to login and registration, so that I can enter the private part of the app.
6. As a guest, I want a link to the public top-10 ranking, so that I can compare teams.
7. As anyone, I want the public ranking to show team name, total score and member count, so that rankings are meaningful.
8. As anyone, I want only the top 10 teams sorted by score, so that the assignment requirement is met.
9. As a user, I want navigation toward my teams and predictions, so that the public shell leads into my workflow.
10. As an admin, I want a match-management entry point, so that official fixture work is discoverable.
11. As anyone, I want unplayed matches to render without fake final scores, so that schedule data stays honest.
12. As anyone, I want friendly not-found behavior for invalid public detail URLs, so that public browsing is robust.

## Implementation Decisions

- Keep home, public ranking and public match detail accessible to guests.
- Sort home matches by date and time before rendering.
- Render official result values only when result data is available.
- Keep ranking summary public and private team scoreboards separate.
- Use role-aware navigation instead of exposing irrelevant private/admin actions.
- Use resource bundle keys for visible copy selected for i18n coverage.
- Keep controllers thin and let services shape lists for views.
- Use the eventual scoring source consistently so ranking order reflects team totals after result entry.
- Let not-found and type mismatch behavior use shared MVC error handling.

## Testing Decisions

- Verify the home controller returns the expected view and match model.
- Verify public ranking returns at most 10 teams in score order.
- Verify guests can access public home, ranking and match detail routes.
- Verify invalid public detail identifiers map to friendly error behavior.
- This PRD contributes to required MVC controller tests and public-route security tests.
- Use model/view assertions instead of fragile pixel-level assertions.
- Tests are deferred to the late test block.

## REST And WebClient Decisions

REST and WebClient are out of scope for this feature implementation. The later match-by-date REST endpoint may reuse the same public match data boundary.

## Out Of Scope

- Schedule filtering, pagination and live FIFA synchronization.
- Advanced ranking tie-breakers beyond a stable score-based top-10.
- Private member score breakdowns.
- Visual redesign beyond a coherent shared public navigation.

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, pages 3 and 5: homepagina and publieke top-10 requirements.
2. School guidelines: `Slides_Spring_Boot.pdf`, `Slides_Spring_Web_MVC_i18n.pdf`, `Slides_Spring_Exceptions.pdf` and `Slides_Spring_MultipleRow.pdf`.
3. Lesson notes: `03-04-2026-ErrorMessageEnI18n.md` and `Project.md`.
4. Exercise projects identified for MVC, i18n and exception patterns in the school reference map.
5. Existing `call-the-match` codebase: current home, competition, ranking, message bundle and error template state.
6. Git repository URL: `https://github.com/HeyEdis/call-the-match.git`.
7. User/project decisions from this conversation and local skills: guest public access and safe passing before polish.

## Further Notes

Deze feature is deels aanwezig. De grootste resterende risico's zitten niet in de lijstweergave zelf, maar in access-aware navigation, ranking correctness after scoring and consistent error/i18n evidence.
