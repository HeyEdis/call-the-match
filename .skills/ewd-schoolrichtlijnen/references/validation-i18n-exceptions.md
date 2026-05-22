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

## Error Handling

Use `@ControllerAdvice` for MVC exceptions and type mismatches.

Use custom error templates in `templates/error/404.html`, `403.html`, and `500.html`.

Handle path variable type mismatch with `MethodArgumentTypeMismatchException`, usually returning `error/404`.
