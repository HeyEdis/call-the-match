# MVC, JPA, And Layering

## School Pattern

Use this flow for MVC pages:

Request -> Controller -> Service -> Repository -> Entity/DTO -> Model -> Thymeleaf view

## Controller Rules

- Keep controllers thin.
- Controllers prepare model attributes, call services, handle `BindingResult`, and choose view/redirect names.
- Controllers should not contain scoring, membership, repository lookup, or authorization business rules beyond route-level checks.
- Use `Principal` or `Authentication` only to pass the authenticated email/name to the service. Do not use hardcoded user ids.
- Keep form DTO model attribute names consistent with the DTO and Thymeleaf form object. Avoid unnecessary custom names.
- Use `@ModelAttribute` methods only when the same attribute is needed by multiple handlers in the same controller flow.
- If one handler needs an attribute, prefer the explicit `model.addAttribute(...)` in that handler.

Good:

```java
@PostMapping("/add")
public String add(@Valid InputCompetitionDTO inputCompetitionDTO,
                  BindingResult result,
                  Model model,
                  RedirectAttributes ra) {
    if (result.hasErrors()) {
        model.addAttribute("stadiums", stadiumService.getAllStadiums());
        model.addAttribute("countries", countryService.getAllCountries());
        return "competition/add";
    }
    competitionService.add(dto);
    ra.addFlashAttribute("successMessage", "...");
    return "redirect:/home";
}
```

Bad:

```java
@PostMapping("/add")
public String add(InputCompetitionDTO dto) {
    competitionRepository.save(new Competition(...)); // repository in controller
    return "redirect:/home";
}
```

Good repeated model attribute:

```java
@ModelAttribute("teamList")
public List<TeamDTO> populateTeamList(Principal principal) {
    return teamService.findTeamsForCurrentUser(principal.getName());
}
```

Use this only when `teamList` is needed by the relevant handlers. Do not add hidden queries to handlers that do not need the data.

## Service Rules

- Services are the processing classes.
- Services coordinate repositories and domain decisions.
- Convert entities to response DTOs in services if the controller/view should not receive entities directly.
- Throw domain exceptions from services when something is not found.
- Public methods should represent real application use cases.
- Helper methods for lookup, mapping, and repeated calculations should normally be private.
- Do not merge separate use cases only because they touch the same entity. Editing match setup and entering official results can stay separate.
- Let repositories handle simple sorting/filtering when a Spring Data method name is clear.

Good repository-backed ordering:

```java
List<Competition> findAllByOrderByDateAscTimeAsc();

List<Competition> findByDateOrderByTimeAsc(LocalDate date);
```

## Repository Rules

- Use Spring Data `JpaRepository`.
- Add query methods such as `findByEmail`, `findByInviteCode`, `existsBy...` when needed.
- Do not call repositories directly from Thymeleaf or controllers.

## DTO Rules

- Request DTOs carry form input and validation annotations.
- Response DTOs shape data for views or REST.
- For forms with select fields, add converters/formatters so ids bind correctly to entities.

## Entity And Model Rules

- JPA entities need a no-args constructor. Prefer `@NoArgsConstructor(access = AccessLevel.PROTECTED)` when normal application code should not create empty entities.
- Protected no-args constructors do not block frontend form creation when forms bind to DTOs.
- Not every entity field needs a public setter.
- Remove setters from fields controlled by JPA, timestamps, or behavior, such as `id` and `createdAt`.
- Keep setters or explicit methods for fields services legitimately update.
- Builders are acceptable in this project because the codebase uses them, but avoid setting JPA-controlled fields from normal code.
- If a builder is used with initialized collections, use Lombok `@Builder.Default`.
- Entity behavior is acceptable when it belongs to the entity state, such as regenerating a team invite code or recalculating a team score.
- Repository uniqueness checks belong in services or validators, not in entity methods.

Good:

```java
@Setter(AccessLevel.NONE)
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

Good entity behavior:

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

## call-the-match Watchlist

- Replace hardcoded temporary user id with authenticated user lookup.
- Fix empty `CountryConverter` and `StadiumConverter` before relying on select binding.
- Keep official match scores nullable until admin enters results.
