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

Good: use authentication/principal and resolve the domain user.

```java
@PostMapping("/join")
public String join(@RequestParam String inviteCode, Authentication authentication) {
    teamService.joinTeamWithInviteCode(inviteCode, authentication.getName());
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
public List<TeamDTO> populateTeamList() {
    return teamService.getCurrentUserTeams();
}
```

This follows the login/security exercise pattern that uses `@ModelAttribute("username")` so the same controller data is not requested in every handler. If a model attribute is only needed in one handler, keep the normal `model.addAttribute(...)` in that handler instead.

Bad: hide that repeated controller view setup in an ad hoc private helper when a course example already shows `@ModelAttribute`.

```java
private void addDashboardModel(Model model) {
    model.addAttribute("teamList", teamService.getCurrentUserTeams());
}
```

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

## REST

Good: REST returns DTO/JSON.

```java
@GetMapping("/{id}/capacity")
public StadiumCapacityDTO capacity(@PathVariable Long id) { ... }
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
