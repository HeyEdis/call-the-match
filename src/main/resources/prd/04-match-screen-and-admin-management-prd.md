# PRD: Match Screen And Admin Management

## Problem Statement

De applicatie moet wedstrijden publiek tonen en admins wedstrijddata plus officiele uitslagen laten beheren. De repo heeft al wedstrijdentiteiten, views and een gedeeltelijke add-flow, maar de rolgrens, edit/result flows and verplichte validaties zijn nog niet volledig zichtbaar.

## Solution

Lever een publieke wedstrijddetailpagina en admin-only formulieren voor toevoegen, aanpassen and officiele resultaten. Valideer verplichte matchdata, verschillende landen, de gekozen projectperiode, stadium checksum and stadium/time conflicts. Gebruik de adminflow als sterk bewijs voor MVC, validation, i18n, error handling and role-based security.

## Current Codebase State

- Competition, country, stadium and location data already exist in the domain.
- Match list, detail, add and edit entry points exist in MVC.
- The add form already uses a request DTO with basic null and non-negative annotations.
- Existing resource bundle keys cover several competition labels and save messages.
- Result scores are nullable in the entity, which supports future matches without official results.
- Country and stadium form binding still needs a reliable finished path.
- There is no visible custom annotation, validator class, admin-only security boundary or complete official result flow yet.

## School Requirements

- MVC controllers, Thymeleaf form views and service/repository/JPA layering.
- Jakarta Validation with existing annotations.
- At least one custom annotation plus validator class in the project.
- Validation messages and one full screen text coverage through resource bundles.
- Redirect feedback after add/edit.
- Exception and error-page behavior for missing matches and bad ids.
- Spring Security for admin-only match mutations.
- MVC, security and validation tests later.
- REST/WebClient remains a separate late block, though match data will feed it.

## Role And Access Decisions

- **Guest**: mag wedstrijddetail and publieke matchinformatie bekijken.
- **User**: mag publieke matchinfo zien and later zijn eigen prognose context bij een match zien.
- **Admin**: mag wedstrijden toevoegen, aanpassen and officiele resultaten registreren.
- **Forbidden**: guests/users muteren geen wedstrijddata; admin match management is not prediction participation.

## User Stories

1. As anyone, I want to open a match screen, so that I can inspect one fixture.
2. As anyone, I want to see both countries, date, time, stadium and location, so that match context is complete.
3. As anyone, I want to see the official final score when known, so that completed matches show results.
4. As a user, I want the match screen ready to show my own prediction context, so that match and prediction flows meet naturally.
5. As an admin, I want an edit entry point on match screens, so that fixture corrections are practical.
6. As an admin, I want to add matches with required match fields, so that the schedule can be maintained.
7. As an admin, I want countries to differ, so that impossible fixtures are rejected.
8. As an admin, I want match dates limited to 20 May 2026 through 6 June 2026, so that demo data follows the chosen project period.
9. As an admin, I want stadium code and checksum validated, so that assignment validation evidence is present.
10. As an admin, I want duplicate stadium/time matches rejected, so that schedule conflicts are prevented.
11. As an admin, I want to edit fixture data, so that corrections do not require reseeding.
12. As an admin, I want to enter official scores after a played match, so that scoring has real outcomes.
13. As an admin, I want successful writes confirmed after redirect, so that form work is clear.
14. As an admin, I want invalid form fields explained near the input, so that correction is efficient.
15. As the application, I want missing matches and invalid path ids handled cleanly, so that public and admin routes fail politely.

## Implementation Decisions

- Keep match detail public; keep add/edit/result writes admin-only.
- Use dedicated request DTOs when fixture input and official result input need different validation rules.
- Keep official scores nullable until an admin records a result.
- Use the chosen project period 20 May 2026 through 6 June 2026 for match date validation.
- Prefer `@ValidStadiumChecksum` for the custom annotation requirement.
- Use a validator class for cross-field and repository-backed rules such as different countries and stadium/time conflicts.
- Put validation messages and visible form labels in resource bundles.
- Keep `BindingResult` directly after validated DTOs and reload select options on invalid submissions.
- Use converters/formatters or another school-style binding solution for country and stadium selections.
- Trigger shared MVC error handling for missing matches and argument type mismatches.

## Testing Decisions

- Verify existing annotation validation for required match inputs and non-negative official scores where applicable.
- Verify custom checksum annotation behavior.
- Verify validator class rules for different countries, date range and same stadium/time conflicts.
- Verify add/edit/result MVC flows for valid and invalid submissions.
- Verify only admins can reach match mutation routes.
- Verify public detail routes and not-found/type mismatch behavior.
- This PRD carries required MVC, security, existing-validation, custom-annotation and validator-class test evidence.
- Tests are planned in the late test block; validators should stay isolated for quick unit coverage.

## REST And WebClient Decisions

REST and WebClient are deferred. Match data defined here is partially relevant because the later REST block must expose matches for a given date and stadium capacity through DTO boundaries.

## Out Of Scope

- Match deletion unless deadline pressure allows it safely.
- Automatic FIFA import and live result feeds.
- Tournament bracket or group-standings generation.
- Prediction write UI beyond the match-page integration point.

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, page 4 and page 6: match screen, admin match management and technical validation requirements.
2. School guidelines: `Slides_Spring_Web_Flow.pdf`, `Slides_Spring_Web_MVC_i18n.pdf`, `Slides_Spring_Exceptions.pdf` and `Slides_Spring_MultipleRow.pdf`.
3. Lesson notes: `13-03-26-Validation.md`, `03-04-2026-ErrorMessageEnI18n.md` and `Project.md`.
4. Exercise projects identified for validators, form binding, i18n and exception handling in the school reference map.
5. Existing `call-the-match` codebase: current competition domain, DTO annotations, competition MVC flow, messages and error templates.
6. Git repository URL: `https://github.com/HeyEdis/call-the-match.git`.
7. User/project decisions from this conversation and local skills: chosen date range, admin-only official result management and safe passing first.

## Further Notes

Deze PRD is technisch rijk en daarom exam-proof: it proves admin authorization, forms, validation, resource bundles and error handling in one coherent feature.
