package com.example.callthematch;

import com.example.callthematch.model.Prediction;
import com.example.callthematch.repository.CompetitionRepository;
import com.example.callthematch.repository.PredictionRepository;
import com.example.callthematch.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

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

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private PredictionRepository predictionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void userPredictionListShowsCurrentUserPredictions() throws Exception {
        var user = userRepository.findByEmail("user1@example.com").orElseThrow();
        var competition = competitionRepository.findById(3L).orElseThrow();
        predictionRepository.findByUserAndCompetition(user, competition)
                .orElseGet(() -> predictionRepository.save(Prediction.builder()
                        .user(user)
                        .competition(competition)
                        .predictedScoreA(2)
                        .predictedScoreB(1)
                        .createdAt(LocalDateTime.now())
                        .build()));

        mockMvc.perform(get("/predictions").with(user("user1@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("prediction/list"))
                .andExpect(model().attributeExists("predictionList"))
                .andExpect(content().string(containsString("Brazil")))
                .andExpect(content().string(containsString("Morocco")))
                .andExpect(content().string(containsString("4 - 3")))
                .andExpect(content().string(containsString("2 - 1")));
    }

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
