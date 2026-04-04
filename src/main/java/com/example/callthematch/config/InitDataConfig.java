package com.example.callthematch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("dev") // laten weten enkel in development
public class InitDataConfig implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {

    }
}
