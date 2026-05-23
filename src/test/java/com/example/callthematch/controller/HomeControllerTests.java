package com.example.callthematch.controller;

import com.example.callthematch.dto.response.CompetitionDTO;
import com.example.callthematch.service.CompetitionService;
import com.example.callthematch.validator.CompetitionValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.example.callthematch.support.TestCompetitions.competitionDto;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HomeController.class)
class HomeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompetitionService competitionService;

    @MockitoBean
    private CompetitionValidator competitionValidator;

    @Test
    void homeShowsPublicScheduleModel() throws Exception {
        List<CompetitionDTO> competitions = List.of(competitionDto(1L));
        when(competitionService.getAllCompetitions()).thenReturn(competitions);

        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("competitionList", competitions));

        verify(competitionService).getAllCompetitions();
    }

}
