package com.example.callthematch.controller;

import com.example.callthematch.dto.response.CompetitionDTO;
import com.example.callthematch.dto.response.PublicRankingDTO;
import com.example.callthematch.exception.CompetitionNotFound;
import com.example.callthematch.model.Country;
import com.example.callthematch.model.Location;
import com.example.callthematch.model.Stadium;
import com.example.callthematch.service.CompetitionService;
import com.example.callthematch.service.CountryService;
import com.example.callthematch.service.StadiumService;
import com.example.callthematch.service.TeamService;
import com.example.callthematch.validator.CompetitionValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest({HomeController.class, RankingController.class, CompetitionController.class})
class PublicBrowseControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompetitionService competitionService;

    @MockitoBean
    private TeamService teamService;

    @MockitoBean
    private StadiumService stadiumService;

    @MockitoBean
    private CountryService countryService;

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

    @Test
    @SuppressWarnings("unchecked")
    void rankingShowsPublicTopTenInScoreOrder() throws Exception {
        List<PublicRankingDTO> ranking = List.of(
                new PublicRankingDTO("Winners", 21, 4),
                new PublicRankingDTO("Chasers", 13, 3));
        when(teamService.getTop10Teams()).thenReturn(ranking);

        MvcResult result = mockMvc.perform(get("/ranking"))
                .andExpect(status().isOk())
                .andExpect(view().name("ranking/list"))
                .andExpect(model().attribute("teamList", ranking))
                .andReturn();

        List<PublicRankingDTO> teams =
                (List<PublicRankingDTO>) result.getModelAndView().getModel().get("teamList");

        assertThat(teams).hasSizeLessThanOrEqualTo(10);
        assertThat(teams).extracting(PublicRankingDTO::score).isSortedAccordingTo((left, right) ->
                Integer.compare(right, left));
        verify(teamService).getTop10Teams();
    }

    @Test
    void competitionDetailShowsPublicFixtureAndFriendlyErrors() throws Exception {
        CompetitionDTO competition = competitionDto(1L);
        when(competitionService.findById(1L)).thenReturn(competition);
        when(competitionService.findById(999999L)).thenThrow(new CompetitionNotFound(999999L));

        mockMvc.perform(get("/competition/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/show"))
                .andExpect(model().attribute("competition", competition));

        mockMvc.perform(get("/competition/999999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));

        mockMvc.perform(get("/competition/not-a-number"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));

        verify(competitionService).findById(1L);
        verify(competitionService).findById(999999L);
    }

    private CompetitionDTO competitionDto(Long id) {
        Country belgium = Country.builder().id(1L).name("Belgium").landCode(32).build();
        Country canada = Country.builder().id(2L).name("Canada").landCode(1).build();
        Location location = Location.builder().id(1L).city("Vancouver").build();
        Stadium stadium = Stadium.builder()
                .id(1L)
                .name("BC Place")
                .location(location)
                .code(1234)
                .capacity(54500)
                .build();

        return new CompetitionDTO(
                id,
                belgium,
                canada,
                stadium,
                null,
                null,
                LocalDate.of(2026, 5, 20),
                LocalTime.of(18, 0));
    }
}
