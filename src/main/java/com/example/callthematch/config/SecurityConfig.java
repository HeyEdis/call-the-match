package com.example.callthematch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
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
                                "/js/**",
                                "/api/**",
                                "/403**",
                                "/404**",
                                "/500**"
                        ).permitAll()
                        .requestMatchers(
                                "/predictions/**",
                                "/team/*/scoreboard",
                                "/team/**").hasRole("USER")
                        .anyRequest().hasRole("USER"))
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .permitAll())
                .exceptionHandling(handler -> handler
                        .accessDeniedHandler((request, response, ex) -> response.sendError(403))
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }
}
