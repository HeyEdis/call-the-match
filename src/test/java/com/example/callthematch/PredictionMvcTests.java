package com.example.callthematch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class PredictionMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void userPredictionFormShowsMatchAndPrefillsExistingPrediction() throws Exception {
        mockMvc.perform(get("/predictions/3").with(user("user1@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("prediction/form"))
                .andExpect(model().attributeExists("competition", "inputPredictionDto"))
                .andExpect(content().string(containsString("Brazil")))
                .andExpect(content().string(containsString("Morocco")))
                .andExpect(content().string(containsString("value=")));
    }

    @Test
    void invalidPredictionReturnsFieldErrorsAndValidPredictionRedirects() throws Exception {
        mockMvc.perform(post("/predictions/5")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf())
                        .param("predictedScoreA", "-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("prediction/form"))
                .andExpect(model().attributeHasFieldErrors(
                        "inputPredictionDto", "predictedScoreA", "predictedScoreB"));

        mockMvc.perform(post("/predictions/5")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf())
                        .param("predictedScoreA", "2")
                        .param("predictedScoreB", "1"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/competition/5"));
    }

    @Test
    void closedPredictionFormDisablesSubmitAndShowsCutoffErrorOnSave() throws Exception {
        mockMvc.perform(get("/predictions/3").with(user("user1@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("disabled")));

        mockMvc.perform(post("/predictions/3")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf())
                        .param("predictedScoreA", "2")
                        .param("predictedScoreB", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("prediction/form"))
                .andExpect(model().attributeExists("errorMessage"));
    }
}
