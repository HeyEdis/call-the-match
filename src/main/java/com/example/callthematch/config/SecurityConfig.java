package com.example.callthematch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/competition",
                                "/competition/add",
                                "/competition/edit/**",
                                "/competition/{id}/result"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                "/home",
                                "/team/ranking",
                                "/competition/{id}",
                                "/login**",
                                "/register**",
                                "/css/**",
                                "/api/**",
                                "/error/**"
                        ).permitAll()
                        .requestMatchers("/predictions/**", "/team/*/scoreboard", "/team/**").hasRole("USER")
                        .anyRequest().permitAll())
                .formLogin(login -> login
                        .loginPage("/login")
                        .usernameParameter("email")
                        .successHandler(roleBasedSuccessHandler())
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .exceptionHandling(handling -> handling
                        .accessDeniedHandler((request, response, ex) -> response.sendError(403)));

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            response.sendRedirect(isAdmin ? "/competition" : "/home");
        };
    }
}
