# Source Map

Use these sources in order when implementing `call-the-match`.

## Official Guidelines

Path: `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen`

Use for the school's expected approach:

- `Slides_Spring_Boot.pdf`: MVC basics, controllers, services, views.
- `Slides_Spring&JPA_mySql.pdf`: JPA, MySQL, repositories, services, CommandLineRunner seed data.
- `Slides_Spring_Web_Flow.pdf`: forms, DTOs, `@Valid`, validator classes, custom annotations.
- `Slides_Spring_Web_MVC_i18n.pdf`: resource bundles, validation messages, type mismatch, fragments, language switching.
- `Slides_Spring_Exceptions.pdf`: `@ControllerAdvice`, `@ExceptionHandler`, 404 pages, MVC tests.
- `Slides_Spring_MultipleRow.pdf`: path variables, not-found exceptions, type mismatch handling, MVC tests.
- `Slides_Spring_Security.pdf`: login page, CSRF, roles, username display, controller advice for model attributes.
- `Slides_Spring_Security_JDBC.pdf`: database/JPA-backed security, password encoder, 403, security tests.
- `webservices_REST.pdf`: REST controllers, REST advice, ErrorResponse, WebClient, REST tests.
- `Oneindige_lus_vermijden.pdf`: avoiding JSON serialization loops.

## User Notes

Path: `C:\Users\Armour\Documents\HOGENT\EWD\Notes`

Use these for project-specific reminders:

- `Project.md`: required setup, REST notes, typeMismatch, global exception advice.
- `24-04-26-Security.md`: custom login, CSRF, principal, role display, split security user service from normal user service.
- `13-03-26-Validation.md`: validation in records/DTOs, `@Valid`, `BindingResult`, `th:errors`, custom messages.
- `03-04-2026-ErrorMessageEnI18n.md`: resource bundle keys, type mismatch, `LocaleChangeInterceptor`.
- `03-04-2026-MySQL.md`: JPA repository and service-repository separation.
- `08-05-26-REST.md`: REST advice, WebClient demo, JSON loop choices.

## Exercise Projects

Path: `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij`

Most useful projects:

- Security/login: `EWDJ_Security/Spring_Boot_security_JPA`, `Spring_Boot_security_Form`, `Spring_Boot_security_roles`.
- Validation/i18n: `EWDJ_Spring/Spring_validatie/Spring_Boot_Validation`, `EWDJ_ErrorMessages/Spring_Boot_i18n_Product2`, `Spring_Boot_i18n_ErrorMessages-starter`.
- REST/WebClient primary pattern: `EWDJ_REST/Spring_Boot_rest_fruit_start`. Follow its `FruitRestController`, `FruitErrorAdvice`, `ErrorResponse`, `ClientRunner`, `RestClientDemo`, and `FruitRestControllerTest` shapes for the REST block in `call-the-match`.
- REST/WebClient secondary examples: `EWDJ_REST/Spring_Boot_rest_example`, `Spring_Boot_rest_example2` only when the fruit exercise does not answer the question.
- Exceptions/type mismatch: `EWDJ_Multirow` and `Slides_Spring_MultipleRow.pdf` patterns.
- JPA/MySQL: `EWDJ_SpringAndJPA`, `EWDJ_JPA`.

## Audit Memory Documents

Path: `C:\Users\Armour\Documents\HOGENT\EWD\call-the-match\src\main\resources\handoff`

Use these as conversation memory after checking the primary sources above:

- `accountability-audit-conversation-mode.md`: expected answer style for accountability questions.
- `rest-date-pathvariable-refactor.md`: decision to keep `GET /api/{date}/matches` and refactor WebClient/tests around it.
- `team-validator-advice-refactor.md`: move duplicate team name and unknown invite code form errors into validators/advice.
- `service-layer-audit-ralph-handoff.md`: service-layer audit rules and Ralph-ready task shape.
- `controller-tests-audit-handoff.md`: controller test keep/cut/missing-case audit.
- `security-tests-audit-handoff.md`: security test keep/cut/missing-case audit.
- `validation-tests-audit-handoff.md`: validation test keep/cut/missing-case audit.

Path: `C:\Users\Armour\Documents\HOGENT\EWD\call-the-match\src\main\resources\audit`

- `conversation-accountability-summary-2026-05-27.md`: broad audit decisions from the long accountability conversation.

These documents are not stronger than `Richtlijnen`, `Notes`, or `WorkspacesIntelij`; they help future chats remember what was already investigated and which tradeoffs were accepted.
