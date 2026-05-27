# Security And Login

## Sources To Check

- Notes: `24-04-26-Security.md`.
- Exercises: `EWDJ_Security/Spring_Boot_security_JPA`, `Spring_Boot_security_Form`, `Spring_Boot_security_roles`.
- Slides: `Slides_Spring_Security.pdf`, `Slides_Spring_Security_JDBC.pdf`.

## call-the-match Decisions

- Login with email.
- New registrations get role `USER`.
- Admin is not a normal user for team/prediction flows.
- Admin manages matches and official results only.
- Split security lookup service from normal user/domain service.
- `Principal.getName()` is the logged-in email in this project because the username parameter is `email`.
- Keep route-level security explicit enough that invalid public URLs can still reach MVC 404 handling.

## School Pattern

Use a `SecurityConfig` with:

- `BCryptPasswordEncoder` bean.
- `SecurityFilterChain` bean.
- `requestMatchers(...).permitAll()` for login, register, CSS, error pages, public pages, and public REST GET endpoints.
- `.formLogin(...)` with custom login page.
- error handling for 403. Use `.accessDeniedPage("/403")` only when a real `/403` route exists. If the project relies on `templates/error/403.html`, prefer `response.sendError(403)`.

Good:

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(requests -> requests
            .requestMatchers("/login**", "/register**", "/css/**", "/403**").permitAll()
            .requestMatchers("/competition/add", "/competition/edit/**").hasRole("ADMIN")
            .requestMatchers("/team/**", "/predictions/**").hasRole("USER")
            .anyRequest().permitAll())
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/home", true)
            .usernameParameter("email")
            .passwordParameter("password"))
        .exceptionHandling(handling -> handling
            .accessDeniedHandler((request, response, ex) -> response.sendError(403)));
    return http.build();
}
```

Good user details pattern:

```java
@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        MyUser user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return new User(user.getEmail(), user.getPassword(), authorities(user.getRole()));
    }
}
```

Bad:

```java
Long temporaryUserId = 1L; // never use this after auth exists
teamService.joinTeamWithInviteCode(inviteCode, temporaryUserId);
```

Good controller use of the principal:

```java
@PostMapping("/join")
public String join(@Valid InputTeamJoinDTO dto,
                   BindingResult result,
                   Principal principal) {
    if (result.hasErrors()) {
        return "team/dashboard";
    }
    teamService.joinTeamWithInviteCode(dto.inviteCode(), principal.getName());
    return "redirect:/team/dashboard";
}
```

## Config Placement

Keep simple security beans in `SecurityConfig` unless there is a real reason to split them.

Good:

```java
@Bean
PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Avoid a separate `SecurityBeansConfig` when it only contains the password encoder.

## Login Redirects

The school exercises use the built-in form login flow. Prefer:

```java
.defaultSuccessUrl("/home", true)
```

Avoid custom `AuthenticationSuccessHandler` beans for role-based redirects unless the assignment explicitly requires different landing pages.

## Fallback Routes And 404

Be careful with:

```java
.anyRequest().hasRole("USER")
```

For a guest, Spring Security can intercept an unknown URL before MVC can return the error page, causing a login redirect instead of 404. Protect concrete user/admin routes explicitly when public 404 behavior matters.

## Thymeleaf CSRF

Forms that mutate state must include CSRF when Spring Security is enabled:

```html
<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
```

## Role Access Checklist

- Guest: home, ranking, public match detail, login/register, static files, error pages.
- User: teams, predictions, private scoreboards.
- Admin: match add/edit/result management.
- Admin must not join teams or submit predictions.
