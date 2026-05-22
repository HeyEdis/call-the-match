package com.example.callthematch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/competition/add",
                                "/competition/edit/**",
                                "/competition/*/result"
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                "/home",
                                "/ranking",
                                "/competition",
                                "/competition/{id}",
                                "/login**",
                                "/register**",
                                "/css/**",
                                "/error/**"
                        ).permitAll()
                        .requestMatchers("/team/**", "/predictions/**").hasRole("USER")
                        .anyRequest().permitAll())
                .formLogin(login -> login
                        .loginPage("/login")
                        .usernameParameter("email")
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .exceptionHandling(handling -> handling
                        .accessDeniedHandler((request, response, ex) -> response.sendError(403)));

        return http.build();
    }
}
