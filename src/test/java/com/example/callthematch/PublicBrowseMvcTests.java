package com.example.callthematch;

import com.example.callthematch.dto.response.PublicRankingDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class PublicBrowseMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homeShowsPublicScheduleModel() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("competitionList"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void rankingShowsPublicTopTenInScoreOrder() throws Exception {
        MvcResult result = mockMvc.perform(get("/ranking"))
                .andExpect(status().isOk())
                .andExpect(view().name("ranking/list"))
                .andExpect(model().attributeExists("teamList"))
                .andReturn();

        List<PublicRankingDTO> teams =
                (List<PublicRankingDTO>) result.getModelAndView().getModel().get("teamList");

        assertThat(teams).hasSizeLessThanOrEqualTo(10);
        assertThat(teams).extracting(PublicRankingDTO::score).isSortedAccordingTo((left, right) ->
                Integer.compare(right, left));
    }

    @Test
    void competitionDetailShowsPublicFixtureAndFriendlyErrors() throws Exception {
        mockMvc.perform(get("/competition/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/show"))
                .andExpect(model().attributeExists("competition"));

        mockMvc.perform(get("/competition/999999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));

        mockMvc.perform(get("/competition/not-a-number"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));
    }
}
