# Good And Bad Examples

## Layering

Good: controller calls service.

```java
model.addAttribute("teamList", teamService.findTeamsForCurrentUser(email));
```

Bad: controller calls repository.

```java
model.addAttribute("teamList", teamRepository.findAll());
```

## Current User

Good: use `Principal` or authentication and pass the logged-in email to the service.

```java
@PostMapping("/join")
public String join(@Valid InputTeamJoinDTO inputTeamJoinDTO,
                   BindingResult result,
                   Principal principal) {
    if (result.hasErrors()) {
        return "team/dashboard";
    }
    teamService.joinTeamWithInviteCode(inputTeamJoinDTO.inviteCode(), principal.getName());
    return "redirect:/team/dashboard";
}
```

Bad: hardcoded user.

```java
Long temporaryUserId = 1L;
teamService.joinTeamWithInviteCode(inviteCode, temporaryUserId);
```

## Validation Errors

Good: return same form and reload dropdown data.

```java
if (result.hasErrors()) {
    model.addAttribute("countries", countryService.getAllCountries());
    model.addAttribute("stadiums", stadiumService.getAllStadiums());
    return "competition/add";
}
```

Bad: redirect on validation error, losing `BindingResult`.

```java
if (result.hasErrors()) return "redirect:/competition/add";
```

## Reused Controller Model Data

Good: use the school security pattern with `@ModelAttribute` only when the same model attribute is needed multiple times in the same controller file.

```java
@ModelAttribute("teamList")
public List<TeamDTO> populateTeamList(Principal principal) {
    return teamService.findTeamsForCurrentUser(principal.getName());
}
```

This follows the login/security exercise pattern that uses `@ModelAttribute("username")` so the same controller data is not requested in every handler. If a model attribute is only needed in one handler, keep the normal `model.addAttribute(...)` in that handler instead.

Risky: hide view setup in a private helper when it is only used once or when it makes the required model attributes unclear.

```java
private void addDashboardModel(Model model) {
    model.addAttribute("teamList", teamService.getCurrentUserTeams());
}
```

A private helper is acceptable when the same view needs the same attributes in multiple return paths and the helper only does view setup.

## Validator Advice

Good: register repository-backed form validators through advice.

```java
@ControllerAdvice(assignableTypes = TeamController.class)
@RequiredArgsConstructor
public class TeamValidatorAdvice {
    private final InputTeamValidator inputTeamValidator;

    @InitBinder("inputTeamDTO")
    public void initTeamBinder(WebDataBinder binder) {
        binder.addValidators(inputTeamValidator);
    }
}
```

Bad: use optional provider indirection when the validator is required.

```java
private final ObjectProvider<InputTeamValidator> inputTeamValidator;
```

Good validator guard:

```java
if (name == null || name.isBlank()) {
    return;
}
```

Let `@NotBlank` own the required-field message.

## Thymeleaf Field Errors

Good:

```html
<span th:if="${#fields.hasErrors('scoreA')}" th:errorclass="error" th:errors="*{scoreA}"></span>
```

Bad:

```html
<span th:text="'Score is wrong'"></span>
```

## Security

Good: encode passwords in seed data.

```java
.passwordHash(passwordEncoder.encode("password"))
```

Bad:

```java
.passwordHash("password")
```

Good: trigger the standard error template for forbidden access when no `/403` controller route exists.

```java
.exceptionHandling(handling -> handling
    .accessDeniedHandler((request, response, ex) -> response.sendError(403)))
```

Risky:

```java
.anyRequest().hasRole("USER")
```

This can make an unknown guest URL redirect to login before MVC can produce a 404.

## REST

Good: REST returns DTO/JSON.

```java
@GetMapping("/{id}/capacity")
public StadiumCapacityDTO capacity(@PathVariable Long id) { ... }
```

Good matches-by-date route for this project:

```java
@GetMapping("/{date}/matches")
public List<MatchRestDTO> getMatchByDate(@PathVariable("date") String date, Locale locale) {
    return competitionService.findRestMatchesByDate(dateFormatter.parse(date, locale));
}
```

Bad: REST controller returns a Thymeleaf view name.

```java
@GetMapping("/{id}")
public String show(@PathVariable Long id) { return "stadium/show"; }
```

## Tests

Good: assert route behavior.

```java
mockMvc.perform(get("/ranking"))
    .andExpect(status().isOk())
    .andExpect(view().name("ranking/list"));
```

Bad: only default context smoke test.

```java
@Test
void contextLoads() {}
```
