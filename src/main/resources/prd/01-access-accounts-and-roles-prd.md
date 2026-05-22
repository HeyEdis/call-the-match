# PRD: Access, Accounts And Roles

## Problem Statement

De FIFA-opdracht heeft drie actoren met verschillende verantwoordelijkheden: guest, user en admin. De huidige applicatie heeft al een gebruikersentiteit en rollen in het domein, maar beschermt de flows nog niet met login, registratie en autorisatie. Daardoor zijn private teams, prognoses en admin-wedstrijdbeheer nog niet betrouwbaar af te schermen.

## Solution

Implementeer eerst een schoolconforme toegangslaag met Spring Security. Guests blijven publieke informatie zien, geregistreerde users krijgen hun private team- en prognoseflows, en admins beheren enkel wedstrijden en officiele resultaten. De oplossing kiest voor een minimaal veilige basis voor de deadline van 27 May 2026: email-login, duidelijke rolgrenzen, login/logout in de UI en de ingelogde user als bron voor user-owned acties.

## Current Codebase State

- Er bestaat een `User`-domeinmodel met email, password hash en een rolveld.
- Er bestaat een rol-enum en seeddata bevat zowel admin- als useraccounts.
- Spring Security dependencies staan al in de build, maar er is nog geen zichtbare securityconfiguratie, user details service, loginflow of registratieflow.
- Controllers voor teams en wedstrijden bestaan al, maar team join gebruikt nog een tijdelijke hardcoded user-id.
- De huidige testdekking bevat enkel een context-load test.

## School Requirements

- Spring Security met login, logout, rollen en 403-afhandeling.
- MVC en Thymeleaf voor login, registratie en gedeelde navigatie.
- JPA-backed user lookup en service/repository-scheiding.
- Password encoding; geen plain-text passwords opslaan.
- CSRF op muterende formulieren zodra security actief is.
- Resource bundles voor user-facing labels en foutmeldingen waar relevant.
- Securitytests later in het verplichte testblok.
- REST GET endpoints moeten later publiek toegelaten kunnen worden zonder deze feature nu met REST te vermengen.

## Role And Access Decisions

- **Guest**: mag publieke home, publieke ranking, publieke wedstrijdinfo, login, registratie, static resources, error pages en publieke REST GET endpoints bezoeken.
- **User**: mag teambeheer, private scoreboards en prognoses gebruiken.
- **Admin**: mag wedstrijdbeheer en officiele resultaten beheren.
- **Forbidden**: admin neemt niet deel aan team- of prognoseflows; guests gebruiken geen private of adminroutes; users gebruiken geen adminroutes.

## User Stories

1. As a guest, I want to register an account, so that I can join the prediction game.
2. As a guest, I want to log in with my email, so that I can access user functionality.
3. As a guest, I want public pages to stay accessible, so that the app remains browseable before login.
4. As a guest, I want protected routes to redirect or deny access clearly, so that the access model is understandable.
5. As a user, I want newly registered accounts to receive role `USER`, so that registration never grants admin rights.
6. As a user, I want logout to be visible across screens, so that I can end my session safely.
7. As a user, I want my active account and role context visible where useful, so that I know which actor flow I am in.
8. As a user, I want private actions linked to my authenticated identity, so that no temporary user id decides ownership.
9. As a user, I want admin pages blocked for me, so that I cannot edit official tournament data.
10. As an admin, I want access to match management, so that I can maintain matches and results.
11. As an admin, I want team and prediction actions blocked, so that admin remains a separate assignment role.
12. As the application, I want forbidden access handled with a school-style error page, so that users do not see raw framework errors.

## Implementation Decisions

- Use email as the login identifier.
- Register normal accounts with role `USER` by default.
- Keep authentication lookup separate from normal user/domain services.
- Convert domain roles to Spring Security authorities consistently.
- Use the authenticated principal/current user lookup for all user-owned actions.
- Permit public routes explicitly and protect user/admin route groups explicitly.
- Add a custom login screen and role-aware shared navigation.
- Keep CSRF enabled and include CSRF fields in mutating Thymeleaf forms.
- Use a school-style access denied page for forbidden MVC requests.
- Preserve the strict rule that admin is not a normal prediction user.

## Testing Decisions

- Verify guest access to public screens and login/registration.
- Verify guests cannot use user or admin flows.
- Verify users can use user flows but cannot use admin flows.
- Verify admins can use admin flows but not team/prediction actions.
- Verify login uses email and registration grants `USER`.
- This PRD contributes directly to the required security test category and to MVC controller coverage.
- Follow the local security examples with MockMvc and Spring Security test support.
- Tests are deferred to the late test block, but route boundaries should be designed to be easy to test.

## REST And WebClient Decisions

REST and WebClient are out of scope for this feature implementation. This PRD only decides that later public REST GET endpoints must be explicitly permitted by the security configuration.

## Out Of Scope

- Password reset and email verification.
- OAuth or external identity providers.
- Admin user management screens.
- Profile editing beyond what the minimum registration flow needs.
- Multi-role user accounts that blur the user/admin split.

## Sources

1. FIFA World Cup 2026 Team Prediction PDF, pages 2 and 6: roles and security requirements.
2. School guidelines: `Slides_Spring_Security.pdf` and `Slides_Spring_Security_JDBC.pdf`.
3. Lesson notes: `24-04-26-Security.md` and `Project.md`.
4. Exercise projects identified for security patterns: `Spring_Boot_security_JPA`, `Spring_Boot_security_Form`, and `Spring_Boot_security_roles`.
5. Existing `call-the-match` codebase: current user model, role model, build dependencies, seeded users and team controller state.
6. Git repository URL: `https://github.com/HeyEdis/call-the-match.git`.
7. User/project decisions from this conversation and local skills: email login, `USER` registration default, admin separation and real deadline 27 May 2026.

## Further Notes

Deze PRD is de eerste implementatieprioriteit. Team ownership, invite joins, private scoreboards and prediction writes become unreliable zolang de huidige tijdelijke user-id nog bestaat.
