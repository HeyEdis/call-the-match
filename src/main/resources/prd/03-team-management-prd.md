# PRD: Team Management

## Problem Statement

Users moeten private teams met vrienden kunnen vormen via invitecodes. De repo bevat al team-, member- en invitecode-bouwstenen, maar de huidige flow toont nog alle teams, gebruikt nog een tijdelijke join-user en mist create-, privacy- en ownerchecks die nodig zijn voor een correct schoolproject.

## Solution

Bouw een user-only teamflow waarin een ingelogde user een uniek team maakt, automatisch eigenaar en lid wordt, invitecodes beheert, via invitecode kan joinen en alleen teams ziet waarvoor hij lid is. De teampagina blijft privaat en toont eigenaar, leden, persoonlijke scores en teamtotaal. Owner-only acties blijven beperkt tot invitecode regenereren en leden verwijderen. Voor de opdrachtregel `code delen (geen mailservice implementeren, enkel tonen)` volstaat een duidelijk invite-paneel dat de code toont in een copyable/read-only veld met een eenvoudige copy-knop.

## Current Codebase State

- Team, team member, team role, user relations and invitecode fields exist.
- Invitecode generation already produces eight characters in the domain model.
- Team score calculation already sums member scores.
- A team dashboard, detail page, invitecode regeneration action and join action exist.
- Dashboard and detail access are now tied to current-user membership checks.
- Join now resolves the authenticated user instead of relying on a temporary user id.
- Team creation, authenticated join, private team visibility, owner checks, member removal and invitecode regeneration are present.
- The team detail page shows the invitecode, but the "share code" requirement should be made explicit with a copyable/read-only invite field and copy button instead of only a plain paragraph.

## School Requirements

- MVC controllers and Thymeleaf forms/views for team dashboard, create and join actions.
- Service/repository/JPA separation for membership and invitecode decisions.
- Spring Security and authorization checks for user-only and member-only flows.
- DTO validation for team input and clear validation messages.
- Resource bundle flash/error messages.
- Exception handling for invalid invitecodes and missing teams.
- MVC, security and validation tests later.
- REST/WebClient is not mixed into team implementation.

## Role And Access Decisions

- **Guest**: mag geen teamdashboard, teampagina of invite actie gebruiken.
- **User**: mag teams maken, joinen, eigen teamdashboard bekijken and member-visible teampagina's openen.
- **Admin**: gebruikt deze flow niet.
- **Forbidden**: non-members zien geen private teamdata; non-owners beheren geen invitecode or leden; duplicate membership is blocked.

## User Stories

1. As a user, I want to create a team, so that I can compete with friends.
2. As a user, I want a unique team name, so that teams remain identifiable.
3. As a user, I want an automatically generated invite code of at least eight characters, so that friends can join.
4. As a team creator, I want to become owner and member, so that my team is immediately usable.
5. As a user, I want to join a team with an invite code, so that I can join a friend's group.
6. As a user, I want an invalid invite code reported clearly, so that I can correct the join attempt.
7. As a user, I want duplicate joins prevented, so that I appear once in a team.
8. As a user, I want my dashboard limited to my teams, so that private team data stays private.
9. As a team member, I want to see the team name, owner and member list, so that I understand the group.
10. As a team member, I want to see each member score and the team total, so that the competition is visible.
11. As a team owner, I want to regenerate the invite code, so that old codes can be invalidated.
12. As a team member, I want the invite code shown in a copyable share panel, so that I can share it with friends without a mail service.
13. As a team owner, I want to remove members, so that membership can be managed.
14. As a non-owner member, I want owner controls hidden and blocked, so that ownership is enforced.
15. As a non-member, I want private team pages denied, so that data is not leaked.

## Implementation Decisions

- Resolve owner and join actor from the authenticated user, never from a request-provided or temporary user id.
- Preserve unique team names and unique invitecodes at persistence boundaries.
- Create the owner membership record when a team is created.
- Keep invitecode generation in domain/service code and validate generated uniqueness before use.
- Filter dashboard data by membership.
- Enforce member checks before showing team detail and private score data.
- Enforce owner checks before invitecode regeneration and member removal.
- Satisfy `code delen` by visibly showing the invite code on the private team page in a read-only/copyable field with a small copy button; do not implement email delivery, mail templates or external share integrations.
- Use request DTO validation for create/join input where form validation is needed.
- Use resource-bundle flash and error messages instead of hardcoded controller messages.
- Keep team score display compatible with later scoring recalculation.

## Testing Decisions

- Verify create, join, duplicate join and invalid invitecode behavior.
- Verify owner membership is created with the team.
- Verify guest denial, non-member denial and non-owner denial.
- Verify team dashboard only exposes current-user memberships.
- Verify the private team page visibly exposes the current invitecode to members and keeps regeneration owner-only.
- Verify unique team name validation and required team inputs.
- This PRD contributes to MVC, security and validation test categories.
- Service tests are useful for membership rules even when final test work is late.

## REST And WebClient Decisions

REST and WebClient are out of scope for this feature implementation. Team privacy remains an MVC and service concern for the passing version.

## Out Of Scope

- Email invitations or mail delivery.
- Public team detail pages.
- Team avatars, chat or profile customization.
- Multiple team owners.
- Admin team participation.

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, pages 3 and 4: team creation, teampagina and invitecode actions.
2. School guidelines: `Slides_Spring&JPA_mySql.pdf`, `Slides_Spring_Web_Flow.pdf`, `Slides_Spring_Web_MVC_i18n.pdf` and security guidance.
3. Lesson notes: `13-03-26-Validation.md`, `03-04-2026-MySQL.md` and `Project.md`.
4. Exercise projects identified for JPA, validation and security patterns in the school reference map.
5. Existing `call-the-match` codebase: current team model, member model, invitecode behavior, team service and team screens.
6. Git repository URL: `https://github.com/HeyEdis/call-the-match.git`.
7. User/project decisions from this conversation and local skills: admin exclusion from teams and authenticated current-user lookup.

## Further Notes

Deze PRD hangt direct af van accounts and roles. Private membership checks are more important than expanding the dashboard UI because the existing dashboard currently risks showing too much.
