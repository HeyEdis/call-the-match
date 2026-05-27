package com.example.callthematch.controller;

import com.example.callthematch.advice.CompetitionValidatorAdvice;
import com.example.callthematch.advice.TeamValidatorAdvice;
import com.example.callthematch.dto.response.CompetitionDTO;
import com.example.callthematch.service.CompetitionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.example.callthematch.support.TestCompetitions.competitionDto;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(
        controllers = HomeController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        CompetitionValidatorAdvice.class,
                        TeamValidatorAdvice.class
                }))
class HomeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompetitionService competitionService;

    @Test
    void homePageLoadsWithCompetitionList() throws Exception {
        List<CompetitionDTO> competitions = List.of(competitionDto(1L));
        when(competitionService.getAllCompetitions()).thenReturn(competitions);

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("competitionList", competitions));

        verify(competitionService).getAllCompetitions();
    }

    @Test
    void homePageRendersAddCompetitionFlashMessage() throws Exception {
        when(competitionService.getAllCompetitions()).thenReturn(List.of());

        mockMvc.perform(get("/home")
                        .flashAttr("successMessage", "Competition 4 saved successfully"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Competition 4 saved successfully")));

        verify(competitionService).getAllCompetitions();
    }

}
