# Validation, i18n, And Exceptions

## Sources To Check

- Notes: `13-03-26-Validation.md`, `03-04-2026-ErrorMessageEnI18n.md`, `Project.md`.
- Exercises: `Spring_Boot_Validation`, `Spring_Boot_i18n_Product2`, `Spring_Boot_i18n_ErrorMessages-starter`.
- Slides: `Slides_Spring_Web_Flow.pdf`, `Slides_Spring_Web_MVC_i18n.pdf`, `Slides_Spring_Exceptions.pdf`, `Slides_Spring_MultipleRow.pdf`.

## Validation Flow

1. Put annotations on request DTO/record fields.
2. Use `@Valid` in controller.
3. Put `BindingResult` immediately after the validated DTO.
4. On errors, return the same form view and reload dropdown/list model data.
5. Show field errors with `th:errors`.

Good:

```java
@PostMapping
public String save(@Valid InputMatchDTO dto, BindingResult result, Model model) {
    if (result.hasErrors()) {
        model.addAttribute("stadiums", stadiumService.getAllStadiums());
        return "competition/add";
    }
    matchService.save(dto);
    return "redirect:/home";
}
```

Bad:

```java
@PostMapping
public String save(@Valid InputMatchDTO dto, Model model, BindingResult result) { ... }
```

The `BindingResult` is in the wrong position.

## Validator Class With Advice

Use this pattern when validation needs cross-field checks or service/repository state.

```java
@ControllerAdvice(assignableTypes = CompetitionController.class)
@RequiredArgsConstructor
public class CompetitionValidatorAdvice {
    private final CompetitionValidator competitionValidator;

    @InitBinder("inputCompetitionDTO")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(competitionValidator);
    }
}
```

Use direct constructor injection for validators in advice classes. This is the school exercise style. Do not use `ObjectProvider` unless the dependency is truly optional or there is a real circular dependency to solve.

Good:

```java
@ControllerAdvice(assignableTypes = TeamController.class)
@RequiredArgsConstructor
public class TeamValidatorAdvice {
    private final InputTeamValidator inputTeamValidator;
    private final InputTeamJoinValidator inputTeamJoinValidator;

    @InitBinder("inputTeamDTO")
    public void initTeamBinder(WebDataBinder binder) {
        binder.addValidators(inputTeamValidator);
    }

    @InitBinder("inputTeamJoinDTO")
    public void initTeamJoinBinder(WebDataBinder binder) {
        binder.addValidators(inputTeamJoinValidator);
    }
}
```

Bad:

```java
private final ObjectProvider<InputTeamValidator> inputTeamValidator;
```

## Validator Guard Style

Use guard-first validation. If the custom rule depends on a value and that value is missing, return and let the DTO annotation produce the required-field error.

Good:

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

Good:

```java
private void validateDateScope(InputCompetitionDTO input, Errors errors) {
    if (input.date() == null) {
        return;
    }

    if (input.date().isBefore(EARLIEST_START_DATE)) {
        errors.rejectValue("date", "competition.date.before",
                new Object[] {EARLIEST_START_DATE}, null);
    }
}
```

This prevents duplicate messages and null pointer errors on empty form submissions.

## Validator Versus Service Exception

Use validator classes for normal field-level form feedback:

- duplicate team name
- unknown invite code
- same team selected twice
- selected stadium code mismatch
- stadium/date/time conflict

Keep service exceptions for rules that depend on route variables, authenticated users, or workflow state that is not naturally present in the DTO:

- prediction cutoff based on `competitionId`
- current user's membership or ownership
- access denied for private team detail
- missing aggregate/entity lookup

## Custom Annotation

Use a custom annotation for the project's required custom validation. For `call-the-match`, prefer `@ValidStadiumChecksum`.

```java
@Documented
@Constraint(validatedBy = StadiumChecksumValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface ValidStadiumChecksum {
    String message() default "{validator.stadiumChecksum}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

When the validator builds a custom violation, prefer the annotation's default message template instead of repeating the key in the validator.

Good:

```java
context.buildConstraintViolationWithTemplate(
        context.getDefaultConstraintMessageTemplate());
```

This keeps `{validator.stadiumChecksum}` in one place: the annotation.

## Resource Bundles

Use `src/main/resources/i18n/messages.properties`.

Include:

- Field labels for at least one full screen.
- Validation messages.
- `typeMismatch` messages.
- Score constants for exact score, correct outcome, and bonuses.
- Success/failure flash messages.

Good keys:

```properties
typeMismatch=Must be a valid number
typeMismatch.inputCompetitionDTO.date=Date must be valid
validator.stadiumChecksum=Checksum is not correct
competition.save.success=Competition saved successfully
score.exact=5
```

Exception messages may stay as simple technical strings when they are used for not-found or REST diagnostics. Validation errors, form labels, flash messages, and visible page text belong in the bundle.

## Error Handling

Use `@ControllerAdvice` for MVC exceptions and type mismatches.

Use custom error templates in `templates/error/404.html`, `403.html`, and `500.html`.

Handle path variable type mismatch with `MethodArgumentTypeMismatchException`, usually returning `error/404`.

Do not use global exception advice for normal recoverable form validation errors when a field error can be produced by a DTO annotation or validator.
