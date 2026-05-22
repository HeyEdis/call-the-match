package com.example.callthematch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/home",
                                "/ranking",
                                "/competition",
                                "/competition/{id}",
                                "/login**",
                                "/register**",
                                "/css/**",
                                "/error",
                                "/403",
                                "/404",
                                "/500"
                        ).permitAll()
                        .requestMatchers("/team/**", "/predictions/**").hasRole("USER")
                        .anyRequest().permitAll())
                .formLogin(Customizer.withDefaults());

        return http.build();
    }
}
