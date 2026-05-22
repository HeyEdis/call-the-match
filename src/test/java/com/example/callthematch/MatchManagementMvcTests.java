package com.example.callthematch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class MatchManagementMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminAddEditAndResultFormsExposeSchoolMvcModels() throws Exception {
        mockMvc.perform(get("/competition/add").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/add"))
                .andExpect(model().attributeExists("countries", "stadiums", "inputCompetitionDto"))
                .andExpect(content().string(containsString("data-code=\"1001\"")))
                .andExpect(content().string(containsString("readonly")))
                .andExpect(content().string(containsString("/js/matchStadiumChecksum.js")))
                .andExpect(content().string(containsString("New York - MetLife Stadium")))
                .andExpect(content().string(not(containsString("MetLife Stadium - New York - 1001"))));

        mockMvc.perform(get("/competition/edit/3").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/edit"))
                .andExpect(model().attributeExists("countries", "stadiums", "inputCompetitionDto"));

        mockMvc.perform(get("/competition/3/result").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/result"))
                .andExpect(model().attributeExists("competition", "inputCompetitionResultDto"));
    }

    @Test
    void invalidAdminAddEditAndResultSubmissionsReturnFieldErrors() throws Exception {
        mockMvc.perform(post("/competition/add")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/add"))
                .andExpect(model().attributeExists("countries", "stadiums"))
                .andExpect(model().attributeHasFieldErrors(
                        "inputCompetitionDto", "teamA", "teamB", "stadium", "stadiumCode", "checksum", "date", "time"));

        mockMvc.perform(post("/competition/edit/3")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/edit"))
                .andExpect(model().attributeExists("countries", "stadiums"))
                .andExpect(model().attributeHasFieldErrors(
                        "inputCompetitionDto", "teamA", "teamB", "stadium", "stadiumCode", "checksum", "date", "time"));

        mockMvc.perform(post("/competition/3/result")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/result"))
                .andExpect(model().attributeExists("competition"))
                .andExpect(model().attributeHasFieldErrors("inputCompetitionResultDto", "scoreA", "scoreB"));
    }

    @Test
    void competitionAdminFormsKeepFriendlyNotFoundAndTypeMismatchErrors() throws Exception {
        mockMvc.perform(get("/competition/edit/999999").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));

        mockMvc.perform(get("/competition/not-a-number/result").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));
    }
}
