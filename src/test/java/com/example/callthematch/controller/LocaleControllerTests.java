package com.example.callthematch.controller;

import com.example.callthematch.config.LocaleConfig;
import com.example.callthematch.validator.CompetitionValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocaleController.class)
@Import(LocaleConfig.class)
class LocaleControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompetitionValidator competitionValidator;

    @Test
    void changeLocaleRedirectsToReferer() throws Exception {
        mockMvc.perform(get("/changeLocale")
                        .param("lang", "en")
                        .header("Referer", "/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }
}
