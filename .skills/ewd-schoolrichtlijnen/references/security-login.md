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

## School Pattern

Use a `SecurityConfig` with:

- `BCryptPasswordEncoder` bean.
- `SecurityFilterChain` bean.
- `requestMatchers(...).permitAll()` for login, register, CSS, error pages, public pages, and public REST GET endpoints.
- `.formLogin(...)` with custom login page.
- `.exceptionHandling(...accessDeniedPage("/403"))`.

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
        .exceptionHandling(handling -> handling.accessDeniedPage("/403"));
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
