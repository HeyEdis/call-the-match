# Accountability Audit Playbook

Use this reference when the user asks why code was written a certain way, whether it follows the school guidelines, or how to rewrite it in the EWD style.

## Answer Shape

Use this order:

1. Verdict first.
2. Evidence from the current codebase, `Richtlijnen`, `Notes`, or `WorkspacesIntelij`.
3. Explain how the code works.
4. Name the tradeoff or risk.
5. Give the school-style recommendation or code shape.

If a pattern is not found in the guidelines or exercises, say that plainly. Do not defend a generic Spring pattern as school-conform unless the local sources support it.

## Source Discipline

Use the required source order from `SKILL.md`:

1. Current project code.
2. `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen`.
3. `C:\Users\Armour\Documents\HOGENT\EWD\Notes`.
4. `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij`.

The user's handoff docs in `src/main/resources/handoff` are useful memory, but they do not replace the source order. Treat them as audit context and then cite the concrete project/guideline/exercise evidence when answering.

## Controller Audit Rules

- Controllers stay thin: model setup, `BindingResult`, service calls, redirect/view names.
- Keep repository access out of controllers.
- Put `BindingResult` immediately after the `@Valid` DTO.
- Use form DTO names consistently. Avoid invented names such as `inputCompetitionDto` when `inputCompetitionDTO` or the default DTO model name would be clearer.
- Use `@ModelAttribute` methods only when the same model attribute is needed by multiple handlers in the same controller flow.
- Inline `model.addAttribute(...)` when only one handler needs the data or when hiding it would make the view requirements unclear.
- A private helper for repeated model setup is acceptable, but it should not hide business logic or repository access.
- Use `Principal` or `Authentication` in controllers only to pass the authenticated email/name into the service. Do not use hardcoded ids.

## Service Audit Rules

- Services are processing classes.
- Services coordinate repositories, domain decisions, access checks, and DTO mapping.
- Public service methods should represent real application use cases.
- Repeated lookup and mapping helpers should normally be private.
- Keep separate service methods for separate use cases, even when they update the same entity. For example, editing match setup and editing official results can stay separate.
- Put simple ordering/filtering in repository query methods when Spring Data can express it clearly, such as `findAllByOrderByDateAscTimeAsc()`.
- Keep current-user, membership, owner, cutoff, and scoring decisions in the service layer unless they are pure entity behavior.
- Service exceptions are fine for route/user/context-dependent business rules that do not map cleanly to one form field.

## Validation Audit Rules

- Request DTO annotations own simple field rules: required, size, email, min, positive, and similar constraints.
- Spring `Validator` classes own cross-field or repository-backed form rules.
- Custom Bean Validation annotations own reusable object-level or field-level validation rules.
- Validator classes belong in the `validator` package.
- Validator advice belongs in the `advice` package.
- Register validators with `@ControllerAdvice` plus `@InitBinder("exactModelAttributeName")`.
- Inject validators directly in advice classes with constructor injection. Do not use `ObjectProvider` unless there is a real circular or optional dependency reason.
- Use guard-first validation: if the custom rule needs a field and the field is null or blank, return and let annotations own the required-field message.
- Do not convert normal form validation cases into controller `try/catch` blocks when a field error can be produced by a validator.
- Keep service-level exceptions for rules that depend on route variables, authenticated users, or workflow state not naturally present in the DTO.

## Security Audit Rules

- Users log in with email, so `principal.getName()` is the email in this project.
- Keep `MyUserDetailsService` focused on Spring Security lookup.
- Keep normal domain user operations in `UserService`.
- Put the `PasswordEncoder` bean in `SecurityConfig` unless there is a clear reason for a separate config class.
- Prefer `.defaultSuccessUrl("/home", true)` over a custom role-based success handler unless the assignment explicitly requires role-specific landing pages.
- Protect concrete user/admin routes explicitly. Be careful with `.anyRequest().hasRole("USER")`, because it can make unknown public URLs redirect to login instead of reaching MVC 404 handling.
- If the project uses `templates/error/403.html`, `response.sendError(403)` can fit better than `.accessDeniedPage("/403")` unless a real `/403` controller route exists.
- Admin is not a normal user for team or prediction flows. Do not let admin join teams or submit predictions.

## Model And Entity Audit Rules

- JPA entities need a no-args constructor, but it can usually be `@NoArgsConstructor(access = AccessLevel.PROTECTED)` so normal application code does not create empty entities.
- Protected no-args constructors do not block frontend form creation when forms bind to DTOs.
- Not every entity field needs a public setter.
- Remove setters from fields controlled by JPA, timestamps, or entity behavior, such as `id` and `createdAt`.
- Keep setters or explicit methods for fields that services legitimately update from a use case.
- Builders are acceptable in this project because the codebase uses them, but watch for incomplete entities and avoid setting JPA-controlled fields from normal code.
- If a builder is used with initialized collections, use Lombok `@Builder.Default`.
- Entity behavior is acceptable when it belongs to the entity state, for example generating/regenerating a team invite code or recalculating a team score.
- Repository uniqueness checks belong in services or validators, not in entity methods.

## REST And WebClient Audit Rules

- Follow the fruit REST exercise style unless the project needs DTOs to avoid JSON loops or leaking entity internals.
- REST endpoints return JSON data, not Thymeleaf view names.
- For `call-the-match`, the chosen matches-by-date endpoint is `GET /api/{date}/matches`. Do not silently revert it to `GET /api/matches?date=...`.
- When the endpoint route changes, update the REST controller test and WebClient demo together.
- The WebClient demo does not start the Spring Boot server. `Connection refused` means the app is not running on the configured port.
- REST error advice should use `@RestControllerAdvice`, typed `@ExceptionHandler`, `@ResponseStatus`, and an `ErrorResponse` DTO with status, message, and timestamp.

## Test Audit Rules

- Test observable behavior and required school outcomes.
- Controller tests should assert route, status, view, model, redirect, flash attributes, and useful service delegation.
- Service tests should own sorting, limiting, current-user filtering, membership/owner checks, scoring, and repository coordination.
- Validator tests should own field errors, duplicate checks, invite-code checks, checksum rules, null guards, and date/time conflict rules.
- Security tests should own the guest/user/admin matrix, login/logout behavior, CSRF, public error pages, and public REST access.
- REST controller tests should use `@WebMvcTest`, `MockMvc`, mocked services, `jsonPath`, and service verification.
- Do not use `contextLoads` as meaningful coverage.
- Do not test private methods directly.
- Avoid brittle controller tests that assert incidental HTML or JavaScript details unless the project requirement depends on that exact output.
- In security tests, do not set the authenticated user twice. Use either `@WithMockUser` on the method or `.with(user(...))` on the request, not both.

## Common Accountability Verdicts

- `ObjectProvider` in validator advice: not school-style here. Use direct constructor injection.
- Duplicate team name or invalid invite code in controller `try/catch`: works, but validator classes are the cleaner school-style form flow.
- Prediction cutoff in validator advice: usually not ideal, because it depends on path/user/workflow context. Keep it in service/controller flow unless the DTO includes the needed context.
- Hardcoded exception messages: acceptable for technical/domain exceptions. Validation, labels, form messages, and flash messages belong in resource bundles.
- REST client DTOs: correct when the endpoint returns DTOs. The school examples use model objects because those endpoints return model objects.
- `@NoArgsConstructor(access = PROTECTED)`: good for JPA entities, not for DTO records.
