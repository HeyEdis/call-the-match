# Conversation Accountability Summary - 2026-05-27

## Purpose

This document summarizes the audit conversation about why certain choices were made in the `call-the-match` codebase, whether they follow the school guidelines and exercise patterns, and what tradeoffs or follow-up changes were identified.

The audit used the project guideline source order from `.agents/skills/project-guidelines`:

1. Current `call-the-match` codebase
2. `C:\Users\Armour\Documents\HOGENT\EWD\Richtlijnen`
3. `C:\Users\Armour\Documents\HOGENT\EWD\Notes`
4. `C:\Users\Armour\Documents\HOGENT\EWD\WorkspacesIntelij`

## Related Handoff Documents

Two focused handoff documents were already created during the audit:

- `src/main/resources/handoff/rest-date-pathvariable-refactor.md`
- `src/main/resources/handoff/team-validator-advice-refactor.md`

This file does not replace those. It summarizes the broader conversation and decisions.

## Suggested Skills For Future Continuation

- `project-guidelines`: Use when judging code against the school style, guidelines, notes, and exercise projects.
- `diagnose`: Use when a runtime error or failing test appears and a reproduction/fix loop is needed.
- `handoff`: Use when the current audit needs to be compacted for another session.

## Controller Audit

### CompetitionController: `@ModelAttribute("inputCompetitionDto")`

Question: why was the POST `/add` method using an explicit model attribute name like `"inputCompetitionDto"` before `InputCompetitionDTO inputCompetitionDTO`?

Verdict:

- The explicit name was not necessary.
- It was also not ideal because the name differed from the DTO variable/class style.
- School examples favor simple, consistent names like `inputRegistrationDTO`.

Decision:

- Prefer `@ModelAttribute("inputCompetitionDTO") InputCompetitionDTO inputCompetitionDTO`, or rely on the conventional attribute name if that fits the binding setup.
- Avoid inventing a different name unless there is a real binding reason.

Tradeoff:

- Explicit names can prevent ambiguity and connect cleanly with `@InitBinder("...")`.
- But custom names should match the DTO name used in forms/tests to avoid confusion.

### CompetitionRestController: Request Parameter Vs Path Variable

Question: why was the REST endpoint originally shaped as a request parameter instead of `/api/{date}/matches`?

Verdict:

- Both are technically valid REST shapes.
- The user changed the endpoint to:

```java
@GetMapping("{date}/matches")
public List<MatchRestDTO> getMatchByDate(@PathVariable("date") String date, Locale locale) {
    return competitionService.findRestMatchesByDate(dateFormatter.parse(date, locale));
}
```

- This works, although `@GetMapping("/{date}/matches")` is visually more consistent with the exercise examples.

Reasoning:

- The school examples often use path variables for resource identity, for example `/{id}` or `/{comp_id}/places`.
- A date can reasonably be treated as part of the resource path for "matches on this date".

Tradeoff:

- Query parameter: `/api/matches?date=2026-05-20` is flexible for search/filter style endpoints.
- Path variable: `/api/2026-05-20/matches` reads more like a nested resource.

Follow-up:

- The REST client and REST controller tests need to be refactored to the path-variable route.
- Details were captured in `src/main/resources/handoff/rest-date-pathvariable-refactor.md`.

### PredictionController: `addCompetitionModel(competitionId, model)`

Question: why was `addCompetitionModel(competitionId, model);` called in the POST mapping if it did not look used?

Verdict:

- It populated model attributes needed when returning the same prediction form after validation failure or business-rule failure.
- It likely added values such as the `competition` and cutoff state back into the model.

Important distinction:

- It might not be used in the successful redirect path.
- It matters when returning a view, because Thymeleaf needs the same model data that the GET method had.

School-style concern:

- The abstraction is not wrong, but it hides what attributes are needed.
- School exercises tend to show `model.addAttribute(...)` directly in controller methods unless a repeated pattern is very obvious.

Decision:

- If only one or two methods need those attributes, inline the `model.addAttribute(...)` calls.
- If every method for that view needs them, a shared helper or `@ModelAttribute` method can make sense.

### Shared Model Attributes And TeamController

Question: how can repeated `model.addAttribute(...)` calls be reduced while following school style?

Verdict:

- `@ModelAttribute` methods are school-compatible when the attribute is truly needed by every relevant request.
- They are not a good fit if the same attribute name means different things in different methods.

TeamController verdict:

- A broad controller-level `@ModelAttribute` is not ideal if attributes like team lists, ranking data, dashboard data, or join/create DTOs are not needed everywhere.
- Use focused model population in the specific GET/POST methods.
- Extract a helper only when the same view needs the same set of model attributes in multiple return paths.

Tradeoff:

- `@ModelAttribute` reduces repetition.
- But it can add hidden queries and hidden model state to requests that do not need it.

### TeamController Create/Join Try-Catch Blocks

Question: are try-catch blocks in the create and join POST mappings necessary?

Verdict:

- They were necessary only while duplicate team names and invalid invite codes were thrown as service exceptions during normal form flow.
- For form validation, the cleaner school-style approach is to move those checks into validators.

Decision:

- Use `InputTeamValidator` for duplicate team names.
- Use `InputTeamJoinValidator` for invalid invite codes.
- Use `TeamValidatorAdvice` with `@InitBinder("inputTeamDTO")` and `@InitBinder("inputTeamJoinDTO")`.
- After that, remove normal-flow try-catch blocks from create/join POST methods.

Tradeoff:

- Service exceptions are fine for unexpected or cross-boundary failures.
- Field-level validation errors belong in validators because they attach naturally to form fields.

Handoff:

- Details were captured in `src/main/resources/handoff/team-validator-advice-refactor.md`.

### PredictionController Cutoff Rule

Question: why not move the prediction cutoff rule into a validator advice too?

Verdict:

- The cutoff rule depends on `competitionId` from the path, not only on `InputPredictionDTO`.
- A normal Spring `Validator` receives the DTO object, not the path variable.

Decision:

- Keeping the service exception/catch for prediction cutoff is cleaner unless the DTO is redesigned to include `competitionId`.

Tradeoff:

- Validator advice works best when the rule can be checked from the DTO itself.
- Controller/service checks are acceptable when the rule depends on route context, authenticated user, or database state not naturally represented in the form object.

## Validator Advice Audit

### TeamValidatorAdvice And `ObjectProvider`

Question: why did `TeamValidatorAdvice` use `ObjectProvider<InputTeamValidator>` and `ObjectProvider<InputTeamJoinValidator>`?

Verdict:

- That was not school-style.
- The exercise examples directly inject validators through constructor injection.

Evidence pattern:

- `RegistrationValidatorAdvice` injects `RegistrationValidator` directly.
- `AccountValidatorAdvice` injects `AccountValidator` directly.
- `PercentValidatorAdvice` injects `PercentValidator` directly.

Decision:

Use direct fields:

```java
private final InputTeamValidator inputTeamValidator;
private final InputTeamJoinValidator inputTeamJoinValidator;
```

Then:

```java
@InitBinder("inputTeamDTO")
public void initTeamBinder(WebDataBinder binder) {
    binder.addValidators(inputTeamValidator);
}

@InitBinder("inputTeamJoinDTO")
public void initTeamJoinBinder(WebDataBinder binder) {
    binder.addValidators(inputTeamJoinValidator);
}
```

Follow-up:

- Remove the unused `ObjectProvider` import if it is still present.
- Some slice tests may need mocks/imports because direct constructor injection requires the validator beans to exist in the test context.

## Exception Advice Audit

### GlobalExceptionAdvice

Question: does `GlobalExceptionAdvice` follow the guidelines?

Verdict:

- Broadly yes for MVC pages, but it should be kept clean and scoped.
- It should not absorb normal form validation problems that belong to validators.

Recommended shape:

```java
@ControllerAdvice(assignableTypes = {
    AccountController.class,
    CompetitionController.class,
    HomeController.class,
    LocaleController.class,
    PredictionController.class,
    TeamController.class
})
public class GlobalExceptionAdvice {
    @ExceptionHandler({
        MethodArgumentTypeMismatchException.class,
        CompetitionNotFound.class,
        TeamNotFound.class,
        StadiumNotFound.class,
        CountryNotFound.class,
        UserNotFound.class,
        InviteCodeNotFound.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound() {
        return "error/404";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied() {
        return "error/403";
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleServerError() {
        return "error/500";
    }
}
```

Tradeoff:

- Global exception advice is good for page-level failures.
- It is not the right place for duplicate team names or invalid invite codes because those are recoverable form errors.

### RestErrorAdvice

Question: is `RestErrorAdvice` conform with school guidelines?

Verdict:

- Yes, it matches the REST exercise style: `@RestControllerAdvice`, `@ExceptionHandler`, `@ResponseStatus`, and returning an error DTO.

User change:

- Changing `"Invalid request"` to `exception.getMessage()` for bad request responses is acceptable and closer to the REST examples.

Tradeoff:

- `exception.getMessage()` can expose framework-ish messages that are not always user-friendly.
- But it is useful for school/demo REST clients and test visibility.

Follow-up:

- After changing the date endpoint from request parameter to path variable, handlers for missing request parameters may no longer be relevant for that route.

## Rest Client Audit

Question: why does the school REST client deserialize into model objects while this codebase uses DTOs?

Verdict:

- The client must deserialize into the shape returned by the endpoint.
- School examples return entities like `Fruit` or `Employee`, so their clients use those classes.
- This project's REST endpoints return DTOs like `MatchRestDTO`, so the client should use DTOs.

Tradeoff:

- Returning entities is simpler for small exercises.
- Returning DTOs is safer and cleaner for a real project because it avoids exposing JPA internals and controls the API contract.

### ClientRunner Connection Refused

Question: why does `ClientRunner` work once and later fail with connection refused?

Verdict:

- `ClientRunner` is a separate Java main and does not start the Spring Boot server.
- `Connection refused: localhost/127.0.0.1:8080` means nothing is listening on port 8080.

Fix:

- Start the Spring Boot app first.
- Then run `ClientRunner`.
- Or adjust the base URL/port if the server runs elsewhere.

## Security Audit

### SecurityBeansConfig

Question: why was there a separate `SecurityBeansConfig`?

Verdict:

- It only existed to hold a `PasswordEncoder` bean.
- Technically valid, but not necessary and not the cleanest school style.

Decision:

- Move the `PasswordEncoder` bean into `SecurityConfig`.
- Remove the separate config if it contains nothing else.

### SecurityConfig: `anyRequest().hasRole("USER")`

Question: why does `.anyRequest().hasRole("USER")` redirect unknown URLs to login instead of showing a 404?

Verdict:

- Spring Security intercepts the request before MVC can resolve it as a missing page.
- For unauthenticated users, it sends them to login.
- That prevents the normal 404 handling from running.

Tradeoff:

- Strict security fallback protects everything by default.
- But it makes invalid public URLs behave like protected resources.

Decision:

- If the school/error-page behavior matters, keep the fallback permissive enough for MVC to produce 404s.
- Protect concrete application routes explicitly.

### Access Denied Page Vs `sendError(403)`

Question: why does `.accessDeniedPage("/403")` lead to a 404, while `sendError(403)` works?

Verdict:

- `.accessDeniedPage("/403")` redirects/forwards to the literal `/403` path.
- If no controller maps `/403`, that path becomes 404.
- `response.sendError(403)` triggers Spring Boot's normal error handling, so `templates/error/403.html` is used.

Decision:

Use:

```java
.accessDeniedHandler((request, response, ex) -> response.sendError(403))
```

This better matches the existing error-template setup.

### Role-Based AuthenticationSuccessHandler

Question: is this conform with guidelines?

```java
@Bean
public AuthenticationSuccessHandler roleBasedSuccessHandler() {
    return (request, response, authentication) -> {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        response.sendRedirect(isAdmin ? "/competition" : "/home");
    };
}
```

Verdict:

- Technically valid Spring Security.
- Not found in the school exercise style.
- It is also redundant/confusing when `.defaultSuccessUrl("/home", true)` is configured.

Decision:

- For school conformity, remove the custom `AuthenticationSuccessHandler`.
- Use `defaultSuccessUrl(...)` unless the assignment explicitly requires role-based landing pages.

Tradeoff:

- Role-based handler gives nicer behavior for admins.
- But it adds custom security logic that is harder to justify against the exercises.

### `throws Exception` On `securityFilterChain`

Question: should the `Exception` in the method declaration be used?

Verdict:

- It is standard for Spring Security config methods.
- The exercises also declare `throws Exception`.
- It supports the DSL/build chain, especially `http.build()`.

Decision:

- Keep it.
- No manual throw is needed.

## Exception Messages And Resource Bundles

Question: should exception messages be hardcoded or resource bundle keys?

Verdict:

- Hardcoded messages are acceptable in exception classes.
- Resource bundle keys should be used for validation messages, form labels, visible page text, and flash messages.

Reasoning:

- REST examples often return `ex.getMessage()`.
- Validation examples use resource bundle keys for user-facing form errors.

Problems found:

- Some exception messages used `.formatted(id)` without a `%s`, so the id was never inserted.
- Some messages had copy/paste mistakes, such as country exceptions saying competition.

Recommended style:

```java
super("Competition not found with id %s".formatted(id));
super("Country not found with id %s".formatted(id));
super("Team not found with id %s".formatted(id));
super("User not found with id %s".formatted(userId));
super("Stadium not found with id %s".formatted(id));
```

Tradeoff:

- Exception messages can stay technical and simple.
- Validation messages should be localized and field-specific.

## Model Audit

### Getters, Setters, And Protected Fields

Question: should every entity field have a getter and setter?

Verdict:

- No. Not every column needs a public setter.
- Exercise examples restrict access to identity fields and sometimes collection fields.

School evidence pattern:

- Entities commonly use `@NoArgsConstructor(access = AccessLevel.PROTECTED)` for JPA.
- `id` often has no setter or no public getter/setter.
- Collections are sometimes exposed through unmodifiable getters plus add/remove behavior methods.

Decision:

- Use `@Setter(AccessLevel.NONE)` on fields that application code should not change directly, such as `id` and `createdAt`.
- Keep setters for fields that forms/services legitimately update.

Tradeoff:

- Class-level `@Setter` is quick.
- Field-level `@Setter(AccessLevel.NONE)` protects important fields while keeping the code simple.

### Protected No-Args Constructor

Question: when should `@NoArgsConstructor(access = AccessLevel.PROTECTED)` be used?

Verdict:

- Use it on JPA entities that should not be created empty by normal application code.
- JPA still needs a no-args constructor, but it does not need to be public.

Decision:

Good candidates:

- `Competition`
- `Country`
- `Location`
- `MyUser`
- `Prediction`
- `Stadium`
- `Team`
- `TeamMember`

Not candidates:

- DTO records
- enums
- exception classes
- utility classes

Concern addressed:

- Protected no-args does not break frontend form creation if forms bind to DTOs.
- The service can still create entities through builders or explicit constructors.

### Entity Creation: Constructor Vs Builder

Question: should competitions be built in the model instead of the service?

Verdict:

- Exercise examples often use explicit constructors in entities.
- This codebase uses builders.
- Sticking with builders is acceptable if used consistently.

Tradeoff:

- Constructors can enforce required fields more directly.
- Builders are readable when there are many fields, but can accidentally allow incomplete objects unless service code is disciplined.

Decision:

- The user chose to keep builders.
- Therefore protected no-args remains fine, and builder/service creation remains the current pattern.

### `@AllArgsConstructor`

Concern:

- `@AllArgsConstructor` on entities may allow constructing entities with fields that should be controlled by JPA or the database, such as `id` and `createdAt`.

Verdict:

- It works, but is less protective.
- If keeping builders, watch that `id` and audit fields are not set from normal app code.

## Team Model Behavior

Question: should team score calculation and invite-code generation live in the `Team` model?

Verdict:

- Yes, behavior that mutates or derives `Team` state belongs naturally in `Team`.
- Exercise examples put domain behavior inside model classes, such as add/remove methods and calculations.

Good model behavior:

- `generateInviteCode()`
- `regenerateInviteCode()`
- `addMember(MyUser user)`
- score calculation

Boundary:

- Checking repository uniqueness for invite codes belongs in the service.
- The entity can generate a code, but the service should ensure it is unique.

### `calculateTeamScore()` Vs `recalculateScore()`

Concern:

- A method named `calculateTeamScore()` currently both calculates and mutates `this.score`.

Verdict:

- That name is misleading.

Recommended split:

```java
public int calculateTeamScore() {
    return members.stream()
            .mapToInt(TeamMember::getScore)
            .sum();
}

public void recalculateScore() {
    this.score = calculateTeamScore();
}
```

How to use:

- Use `calculateTeamScore()` when you only need a value.
- Use `recalculateScore()` before saving a team whose persisted score must be updated.

Tradeoff:

- One method is shorter.
- Two methods make side effects explicit.

### Builder Default On `members`

Observation:

- Lombok warned that `@Builder` ignores the `new HashSet<>()` field initializer.

Recommended fix:

```java
@Builder.Default
private Set<TeamMember> members = new HashSet<>();
```

Reason:

- Without `@Builder.Default`, a builder-created team may have `members == null` unless explicitly set.

## Validator Package Audit

### CompetitionValidator

Question: is `CompetitionValidator` convoluted and does it follow guidelines?

Verdict:

- It follows the school validator pattern, but it was too dense.
- It combined four rules in one `validate()` method.

Rules:

1. Team A and Team B must be different.
2. Date must be within the tournament range.
3. Stadium/date/time must not conflict with another match.
4. Stadium code must match the selected stadium.

Decision:

- Keep the validator.
- Split `validate()` into private methods:
  - `validateDifferentTeams`
  - `validateDateScope`
  - `validateStadiumTimeConflict`
  - `validateSelectedStadiumCode`

School-style principle:

- Guard null or blank values first.
- Let `@NotNull`, `@NotBlank`, and other field annotations own required-field messages.
- Custom validators should only run their business rule when the needed values are present.

### CompetitionValidator Null Date Bug

Runtime error:

```text
Cannot invoke "java.time.LocalDate.isBefore(...)" because InputCompetitionDTO.date() is null
```

Diagnosis:

- Empty form submission still runs custom validators.
- `validateDateScope` called `input.date().isBefore(...)` without checking for `null`.

Fix applied:

```java
if (input.date() == null) {
    return;
}
```

Verification:

- `.\mvnw.cmd -Dtest=CompetitionValidatorTests test` passed.

Full test caveat:

- `.\mvnw.cmd test` was blocked by an unrelated `TeamValidatorAdvice` slice-test bean issue after switching away from `ObjectProvider`.

### Stadium Time Conflict

Question: how should the time-conflict check be extracted?

Decision:

```java
private void validateStadiumTimeConflict(InputCompetitionDTO input, Errors errors) {
    if (input.stadium() == null || input.date() == null || input.time() == null) {
        return;
    }

    if (hasStadiumTimeConflict(input)) {
        errors.rejectValue("time", "competition.stadium.time.conflict");
    }
}
```

Reason:

- The custom check needs all three values.
- If any are missing, field-level annotations should handle the required messages.

Create vs edit logic:

- If `input.id() == null`, it is a create operation and should check whether a stadium/date/time already exists.
- If `input.id() != null`, it is an edit operation and should exclude the current competition id.

### InputTeamJoinValidator

Question: is the validation convoluted?

Verdict:

- Correct, but too compressed in one `if`.

Original idea:

```java
if (input.inviteCode() != null && !input.inviteCode().isBlank()
        && !teamRepository.existsByInviteCode(input.inviteCode())) {
    errors.rejectValue("inviteCode", "team.inviteCode.invalid");
}
```

Recommended school-style rewrite:

```java
InputTeamJoinDTO input = (InputTeamJoinDTO) target;
String inviteCode = input.inviteCode();

if (inviteCode == null || inviteCode.isBlank()) {
    return;
}

if (!teamRepository.existsByInviteCode(inviteCode)) {
    errors.rejectValue("inviteCode", "team.inviteCode.invalid");
}
```

Important clarification:

- Empty input can be submitted.
- If joining requires an invite code, `@NotBlank` should produce the required-field error.
- The custom validator should return early so it does not also show "invalid invite code".
- If joining without an invite code is actually allowed, remove `@NotBlank`.

### InputTeamValidator

Question: same audit for team creation validation.

Verdict:

- Correct, but too compressed.

Recommended rewrite:

```java
InputTeamDTO input = (InputTeamDTO) target;
String name = input.name();

if (name == null || name.isBlank()) {
    return;
}

if (teamRepository.existsByName(name)) {
    errors.rejectValue("name", "team.name.duplicate");
}
```

Reason:

- `@NotBlank` owns the "team name required" message.
- `InputTeamValidator` only owns the duplicate-name business rule.

### ValidStadiumChecksum And StadiumChecksumValidator

Question: do `ValidStadiumChecksum` and `StadiumChecksumValidator` follow the guidelines and exercises?

Verdict:

- Yes. This follows the custom Bean Validation annotation pattern.

Evidence pattern:

- Exercise `ValidPasswords` uses `@Constraint`, `@Target(TYPE)`, `@Retention(RUNTIME)`, `message`, `groups`, and `payload`.
- Exercise `PasswordConstraintValidator` implements `ConstraintValidator<ValidPasswords, InputRegistrationDTO>`.
- This project mirrors that with `ValidStadiumChecksum` and `StadiumChecksumValidator`.

Why `@Target(TYPE)` is correct:

- The checksum rule depends on two fields: `stadiumCode` and `checksum`.
- Field-level validation is not enough because the validator needs the whole DTO.

Null handling:

```java
if (input == null || input.stadiumCode() == null || input.checksum() == null) {
    return true;
}
```

This is correct because missing fields should be handled by `@NotNull`.

Recommended cleanup:

Use the annotation's default message template instead of hardcoding the key inside the validator.

Prefer:

```java
context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
```

over:

```java
context.buildConstraintViolationWithTemplate("{validator.stadiumChecksum}")
```

Reason:

- The annotation already defines:

```java
String message() default "{validator.stadiumChecksum}";
```

This keeps the message source in one place and matches the exercise style.

Also prefer `.equals(...)` for comparing wrapper values:

```java
if (input.checksum().equals(input.stadiumCode() % divisor)) {
    return true;
}
```

## Open Follow-Ups

1. Refactor REST client and REST controller tests for `/api/{date}/matches`.
2. Clean `TeamValidatorAdvice` imports and ensure controller slice tests provide validator beans or narrow advice scanning.
3. Decide whether to remove custom `AuthenticationSuccessHandler` for stricter school conformity.
4. Decide whether `.anyRequest()` should permit unknown paths so 404 pages work for guests.
5. Replace `accessDeniedPage("/403")` with `sendError(403)` if relying on `templates/error/403.html`.
6. Fix exception messages that call `.formatted(...)` without placeholders.
7. Add `@Builder.Default` to collection fields initialized inline.
8. Split `calculateTeamScore()` and `recalculateScore()` if persisting team score remains part of the domain model.
9. Apply guard-first style consistently across custom validators.

## Core Audit Principle Learned

The main pattern repeated throughout the audit:

- Use annotations for simple field validation.
- Use custom `Validator` classes for DTO-level or repository-backed form rules.
- Use custom `ConstraintValidator` annotations for reusable field/type validation rules.
- Use service exceptions for route/user/context-dependent business rules that do not map cleanly to a single form field.
- Keep controller code explicit unless a helper or `@ModelAttribute` is clearly reused across the same view flow.
- Prefer school-style clarity over clever Spring abstractions unless the abstraction earns its place.
