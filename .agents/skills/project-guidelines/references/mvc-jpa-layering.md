# MVC, JPA, And Layering

## School Pattern

Use this flow for MVC pages:

Request -> Controller -> Service -> Repository -> Entity/DTO -> Model -> Thymeleaf view

## Controller Rules

- Keep controllers thin.
- Controllers prepare model attributes, call services, handle `BindingResult`, and choose view/redirect names.
- Controllers should not contain scoring, membership, repository lookup, or authorization business rules beyond route-level checks.

Good:

```java
@PostMapping("/add")
public String add(@Valid InputCompetitionDTO dto, BindingResult result, Model model, RedirectAttributes ra) {
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

## Service Rules

- Services are the processing classes.
- Services coordinate repositories and domain decisions.
- Convert entities to response DTOs in services if the controller/view should not receive entities directly.
- Throw domain exceptions from services when something is not found.

## Repository Rules

- Use Spring Data `JpaRepository`.
- Add query methods such as `findByEmail`, `findByInviteCode`, `existsBy...` when needed.
- Do not call repositories directly from Thymeleaf or controllers.

## DTO Rules

- Request DTOs carry form input and validation annotations.
- Response DTOs shape data for views or REST.
- For forms with select fields, add converters/formatters so ids bind correctly to entities.

## call-the-match Watchlist

- Replace hardcoded temporary user id with authenticated user lookup.
- Fix empty `CountryConverter` and `StadiumConverter` before relying on select binding.
- Keep official match scores nullable until admin enters results.
