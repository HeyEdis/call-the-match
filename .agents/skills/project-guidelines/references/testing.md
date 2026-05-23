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

Good:

```java
mockMvc.perform(get("/competition/1"))
    .andExpect(status().isOk())
    .andExpect(view().name("competition/show"))
    .andExpect(model().attributeExists("competition"));
```

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

## Security Tests

Use `@WithMockUser`, `@WithAnonymousUser`, and form login helpers where appropriate.

Cover:

- Guest can access public pages.
- Guest is redirected for user/admin routes.
- User can access user routes.
- User cannot access admin routes.
- Admin can access admin routes.
- Admin cannot use team/prediction flows in this project.

## Validation Tests

For annotation validation, use a Jakarta `Validator`.

For validator classes, use `BeanPropertyBindingResult`.

Good validator class test shape:

```java
Errors errors = new BeanPropertyBindingResult(dto, "inputCompetitionDTO");
competitionValidator.validate(dto, errors);
assertThat(errors.hasFieldErrors("teamA")).isTrue();
```

## Test Philosophy

- Test observable behavior and required school outcomes.
- Do not test private methods.
- Keep tests focused and small.
- Prefer targeted tests over broad brittle integration tests, unless security config requires full context.
