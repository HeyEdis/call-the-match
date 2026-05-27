package com.example.callthematch.controller;

import com.example.callthematch.advice.CompetitionValidatorAdvice;
import com.example.callthematch.advice.TeamValidatorAdvice;
import com.example.callthematch.config.LocaleConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = LocaleController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        CompetitionValidatorAdvice.class,
                        TeamValidatorAdvice.class
                }))
@Import(LocaleConfig.class)
class LocaleControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void changeLocaleRedirectsToReferer() throws Exception {
        mockMvc.perform(get("/changeLocale")
                        .param("lang", "en")
                        .header("Referer", "/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }
}
