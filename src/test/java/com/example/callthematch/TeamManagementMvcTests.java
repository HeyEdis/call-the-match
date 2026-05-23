package com.example.callthematch;

import com.example.callthematch.dto.response.TeamDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
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
class TeamManagementMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SuppressWarnings("unchecked")
    void dashboardShowsOnlyCurrentUsersTeamsAndFormModels() throws Exception {
        MvcResult result = mockMvc.perform(get("/team/dashboard")
                        .with(user("user1@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("team/dashboard"))
                .andExpect(model().attributeExists("teamList", "inputTeamDto", "inputTeamJoinDto"))
                .andReturn();

        List<TeamDTO> teams = (List<TeamDTO>) result.getModelAndView().getModel().get("teamList");

        assertThat(teams).hasSize(1);
        assertThat(teams).allSatisfy(team ->
                assertThat(team.owner().getEmail()).isEqualTo("user1@example.com"));
    }

    @Test
    void createAndJoinValidationReturnDashboardErrors() throws Exception {
        mockMvc.perform(post("/team/create")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf())
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("team/dashboard"))
                .andExpect(model().attributeHasFieldErrors("inputTeamDto", "name"));

        mockMvc.perform(post("/team/join")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf())
                        .param("inviteCode", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("team/dashboard"))
                .andExpect(model().attributeHasFieldErrors("inputTeamJoinDto", "inviteCode"));
    }

    @Test
    void validCreateRedirectsToDashboard() throws Exception {
        mockMvc.perform(post("/team/create")
                        .with(user("user2@example.com").roles("USER"))
                        .with(csrf())
                        .param("name", "MVC Team Test"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/team/dashboard"));
    }

    @Test
    void invalidInviteCodeReturnsDashboardError() throws Exception {
        mockMvc.perform(post("/team/join")
                        .with(user("user2@example.com").roles("USER"))
                        .with(csrf())
                        .param("inviteCode", "UNKNOWN1"))
                .andExpect(status().isOk())
                .andExpect(view().name("team/dashboard"))
                .andExpect(model().attributeHasFieldErrors("inputTeamJoinDto", "inviteCode"));
    }

    @Test
    void memberCanOpenPrivateTeamDetail() throws Exception {
        mockMvc.perform(get("/team/1")
                        .with(user("user11@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("team/show"))
                .andExpect(model().attributeExists("team", "isOwner"))
                .andExpect(content().string(not(containsString(">Actions</th>"))))
                .andExpect(content().string(not(containsString(">Remove</button>"))));
    }

    @Test
    void memberCanOpenPrivateTeamScoreboard() throws Exception {
        mockMvc.perform(get("/team/1/scoreboard")
                        .with(user("user11@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("team/scoreboard"))
                .andExpect(model().attributeExists("scoreboard"))
                .andExpect(content().string(containsString("Team total")))
                .andExpect(content().string(containsString("Member")))
                .andExpect(content().string(containsString("Score")));
    }

    @Test
    void ownerSeesTeamManagementActions() throws Exception {
        mockMvc.perform(get("/team/1")
                        .with(user("user1@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(">Actions</th>")))
                .andExpect(content().string(containsString(">Remove</button>")));
    }

    @Test
    void guestAndAdminStayOutsideTeamRoutes() throws Exception {
        mockMvc.perform(get("/team/dashboard"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(get("/team/dashboard")
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }

    @Test
    void nonMemberCannotReadPrivateTeamDetail() throws Exception {
        mockMvc.perform(get("/team/1")
                        .with(user("user2@example.com").roles("USER")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/team/1/scoreboard")
                        .with(user("user2@example.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void nonOwnerCannotRegenerateInviteCode() throws Exception {
        mockMvc.perform(post("/team/1/invite-code")
                        .with(user("user11@example.com").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void nonOwnerCannotRemoveTeamMembers() throws Exception {
        mockMvc.perform(post("/team/1/members/1/remove")
                        .with(user("user11@example.com").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
