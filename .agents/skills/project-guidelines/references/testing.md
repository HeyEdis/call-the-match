# Testing

## Required Categories

The project requires tests for:

- MVC controllers.
- REST controllers.
- Security.
- Validation: existing annotations, custom annotations, and validator classes.

`contextLoads` alone is not meaningful coverage.

## MVC Controller Tests

Use MockMvc and assert behavior:

- HTTP status.
- View name.
- Model attributes.
- Redirects.
- Service interaction when useful.
- flash attributes when the controller sets user-visible success/failure messages.
- form redisplay and model reload when validation fails.

Good:

```java
mockMvc.perform(get("/competition/1"))
    .andExpect(status().isOk())
    .andExpect(view().name("competition/show"))
    .andExpect(model().attributeExists("competition"));
```

Controller tests should not prove service-owned facts such as top-10 limiting, sorting, current-user filtering, membership checks, or score calculations. Verify that the controller calls the service and passes the returned data to the model.

## REST Controller Tests

Use `Spring_Boot_rest_fruit_start/src/test/java/com/example/spring_boot_rest_fruit_start/controller/FruitRestControllerTest.java` as the primary REST test example for `call-the-match`.

Keep that structure:

- `@WebMvcTest(TheRestController.class)`.
- autowired `MockMvc`.
- `@MockitoBean` for the service.
- `mockMvc.perform(get(...))`.
- status and JSON assertions with `jsonPath`.
- `Mockito.verify` for service calls.
- one detail GET success case.
- one REST not-found error response case with `status`, `message`, and `timestamp`.
- empty and non-empty list GET cases when the endpoint returns a collection.

Good:

```java
mockMvc.perform(get("/competitions/" + ID))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.id").value(ID));
```

For REST errors:

```java
mockMvc.perform(get("/competitions/" + ID))
    .andExpect(status().isNotFound())
    .andExpect(jsonPath("$.status").value(404))
    .andExpect(jsonPath("$.message").exists())
    .andExpect(jsonPath("$.timestamp").exists());
```

For the matches-by-date endpoint in this project, use the selected path route in tests:

```java
mockMvc.perform(get("/api/2026-05-20/matches"))
    .andExpect(status().isOk());
```

## Security Tests

Use `@WithMockUser`, `@WithAnonymousUser`, and form login helpers where appropriate.

Cover:

- Guest can access public pages.
- Guest is redirected for user/admin routes.
- User can access user routes.
- User cannot access admin routes.
- Admin can access admin routes.
- Admin cannot use team/prediction flows in this project.
- Public REST GET endpoints stay accessible to guests.
- Public error pages stay accessible to guests.
- Unknown public URLs should be checked if a security fallback could turn them into login redirects instead of 404s.

Use one mock-user setup style per test. Do not combine `@WithMockUser(...)` with `.with(user(...))` on the same request unless there is a very explicit reason to override the method-level user.

Keep validation behavior out of the security matrix. For example, invalid registration field errors belong in controller or validation tests, not in `AccessSecurityTests`.

## Validation Tests

For annotation validation, use a Jakarta `Validator`.

For validator classes, use `BeanPropertyBindingResult`.

Good validator class test shape:

```java
Errors errors = new BeanPropertyBindingResult(dto, "inputCompetitionDTO");
competitionValidator.validate(dto, errors);
assertThat(errors.hasFieldErrors("teamA")).isTrue();
```

Validation tests should include null/blank guard cases for custom validators so empty form submissions do not throw and do not produce duplicate custom errors.

Good edge cases for this project:

- competition date before and after the allowed tournament period.
- null competition date does not throw.
- stadium/time conflict create and update paths.
- duplicate team name.
- unknown invite code.
- checksum valid, invalid, and null-skipping cases.

## Test Philosophy

- Test observable behavior and required school outcomes.
- Do not test private methods.
- Keep tests focused and small.
- Prefer targeted tests over broad brittle integration tests, unless security config requires full context.
- Avoid brittle exact HTML or JavaScript assertions unless the requirement depends on that exact output.
- Combine duplicate success-path controller tests when one test can clearly assert service call, redirect, and flash message.

## Responsibility Split

- Controller tests: route, status, view, model, redirect, flash, service delegation.
- REST controller tests: JSON status/body/advice response and service delegation.
- Security tests: guest/user/admin access matrix, login/logout, CSRF, public endpoints.
- Validation tests: DTO annotations, custom annotations, validator classes, error fields.
- Service tests: sorting, limiting, current-user filtering, membership/owner checks, scoring, repository coordination.
