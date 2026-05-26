package com.example.callthematch.controller;

import com.example.callthematch.config.SecurityConfig;
import com.example.callthematch.dto.request.InputPredictionDTO;
import com.example.callthematch.exception.PredictionCutoffPassed;
import com.example.callthematch.service.PredictionService;
import com.example.callthematch.validator.CompetitionValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.callthematch.support.TestCompetitions.competitionDto;
import static com.example.callthematch.support.TestPredictions.inputPredictionDTO;
import static com.example.callthematch.support.TestPredictions.predictionOverviewDtos;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PredictionController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import(SecurityConfig.class)
@ImportAutoConfiguration({
        SecurityAutoConfiguration.class,
        ServletWebSecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
})
class PredictionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PredictionService predictionService;

    @MockitoBean
    private CompetitionValidator competitionValidator;

    @Test
    void showsCurrentUserPredictions() throws Exception {
        when(predictionService.getCurrentUserPredictions()).thenReturn(predictionOverviewDtos());

        mockMvc.perform(get("/predictions").with(user("user1@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("prediction/list"))
                .andExpect(model().attributeExists("predictionList"))
                .andExpect(content().string(containsString("Brazil")))
                .andExpect(content().string(containsString("Morocco")))
                .andExpect(content().string(containsString("4 - 3")))
                .andExpect(content().string(containsString("2 - 1")));

        verify(predictionService).getCurrentUserPredictions();
    }

    @Test
    void predictionFormRendersMatchAndPrefillsExistingPrediction() throws Exception {
        when(predictionService.findCompetitionDTOById(3L)).thenReturn(competitionDto(3L));
        when(predictionService.findCurrentUserInputByCompetitionId(3L)).thenReturn(inputPredictionDTO());
        when(predictionService.isCutoffPassed(3L)).thenReturn(false);

        mockMvc.perform(get("/predictions/3").with(user("user1@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("prediction/form"))
                .andExpect(model().attributeExists("competition", "inputPredictionDTO"))
                .andExpect(content().string(containsString("Belgium")))
                .andExpect(content().string(containsString("Canada")))
                .andExpect(content().string(containsString("value=\"2\"")));

        verify(predictionService).findCompetitionDTOById(3L);
        verify(predictionService).findCurrentUserInputByCompetitionId(3L);
        verify(predictionService).isCutoffPassed(3L);
    }

    @Test
    void guestIsRedirectedToLoginOnPredictionRoutes() throws Exception {
        mockMvc.perform(get("/predictions/3"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void adminIsForbiddenOnPredictionRoutes() throws Exception {
        mockMvc.perform(get("/predictions/3").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidPredictionReturnsFieldErrorsAndDoesNotSave() throws Exception {
        when(predictionService.findCompetitionDTOById(5L)).thenReturn(competitionDto(5L));
        when(predictionService.isCutoffPassed(5L)).thenReturn(false);

        mockMvc.perform(post("/predictions/5")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf())
                        .param("predictedScoreA", "-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("prediction/form"))
                .andExpect(model().attributeHasFieldErrors(
                        "inputPredictionDTO", "predictedScoreA", "predictedScoreB"));

        verify(predictionService, never()).saveCurrentUserPrediction(any(), any(InputPredictionDTO.class));
    }

    @Test
    void validPredictionRedirectsAndCallsService() throws Exception {
        when(predictionService.findCompetitionDTOById(5L)).thenReturn(competitionDto(5L));
        when(predictionService.isCutoffPassed(5L)).thenReturn(false);

        mockMvc.perform(post("/predictions/5")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf())
                        .param("predictedScoreA", "2")
                        .param("predictedScoreB", "1"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/competition/5"));

        verify(predictionService).saveCurrentUserPrediction(5L, inputPredictionDTO());
    }

    @Test
    void closedPredictionFormHidesFormAndShowsDeadlineMessage() throws Exception {
        when(predictionService.findCompetitionDTOById(3L)).thenReturn(competitionDto(3L));
        when(predictionService.findCurrentUserInputByCompetitionId(3L)).thenReturn(inputPredictionDTO());
        when(predictionService.isCutoffPassed(3L)).thenReturn(true);

        mockMvc.perform(get("/predictions/3").with(user("user1@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("cutoffPassed", true))
                .andExpect(content().string(containsString("prediction deadline")))
                .andExpect(content().string(not(containsString("predictedScoreA"))));
    }

    @Test
    void postToPastCutoffPredictionShowsCutoffError() throws Exception {
        when(predictionService.findCompetitionDTOById(3L)).thenReturn(competitionDto(3L));
        when(predictionService.isCutoffPassed(3L)).thenReturn(true);
        doThrow(new PredictionCutoffPassed())
                .when(predictionService).saveCurrentUserPrediction(3L, inputPredictionDTO());

        mockMvc.perform(post("/predictions/3")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf())
                        .param("predictedScoreA", "2")
                        .param("predictedScoreB", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("prediction/form"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(predictionService).saveCurrentUserPrediction(3L, inputPredictionDTO());
    }
}
