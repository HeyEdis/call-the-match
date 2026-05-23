package com.example.callthematch.controller;

import com.example.callthematch.dto.response.PublicRankingDTO;
import com.example.callthematch.service.TeamService;
import com.example.callthematch.validator.CompetitionValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(RankingController.class)
class RankingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeamService teamService;

    @MockitoBean
    private CompetitionValidator competitionValidator;

    @Test
    void rankingShowsPublicTopTenInScoreOrder() throws Exception {
        List<PublicRankingDTO> ranking = List.of(
                new PublicRankingDTO("Winners", 21, 4),
                new PublicRankingDTO("Chasers", 13, 3));
        when(teamService.getTop10Teams()).thenReturn(ranking);

        mockMvc.perform(get("/ranking"))
                .andExpect(status().isOk())
                .andExpect(view().name("ranking/list"))
                .andExpect(model().attribute("teamList", ranking));

        assertThat(ranking).hasSizeLessThanOrEqualTo(10);
        assertThat(ranking).extracting(PublicRankingDTO::score).isSortedAccordingTo((left, right) ->
                Integer.compare(right, left));
        verify(teamService).getTop10Teams();
    }

}
