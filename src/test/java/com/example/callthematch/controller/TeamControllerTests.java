package com.example.callthematch.controller;

import com.example.callthematch.config.SecurityConfig;
import com.example.callthematch.dto.request.InputTeamDTO;
import com.example.callthematch.dto.request.InputTeamJoinDTO;
import com.example.callthematch.dto.response.TeamDTO;
import com.example.callthematch.exception.InviteCodeNotFound;
import com.example.callthematch.exception.TeamNameAlreadyExists;
import com.example.callthematch.service.TeamService;
import com.example.callthematch.validator.CompetitionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.example.callthematch.support.TestTeams.scoreboardDto;
import static com.example.callthematch.support.TestTeams.teamDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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

@WebMvcTest(TeamController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import(SecurityConfig.class)
@ImportAutoConfiguration({
        SecurityAutoConfiguration.class,
        ServletWebSecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
})
class TeamControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeamService teamService;

    @MockitoBean
    private CompetitionValidator competitionValidator;

    @BeforeEach
    void setUp() {
        when(teamService.getCurrentUserTeams()).thenReturn(List.of(teamDto()));
    }

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
        verify(teamService).getCurrentUserTeams();
    }

    @Test
    void createAndJoinValidationReturnDashboardErrorsAndDoNotCallServices() throws Exception {
        mockMvc.perform(post("/team/create")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf())
                        .flashAttr("inputTeamDto", new InputTeamDTO()))
                .andExpect(status().isOk())
                .andExpect(view().name("team/dashboard"))
                .andExpect(model().attributeHasFieldErrors("inputTeamDto", "name"));

        mockMvc.perform(post("/team/join")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf())
                        .flashAttr("inputTeamJoinDto", new InputTeamJoinDTO()))
                .andExpect(status().isOk())
                .andExpect(view().name("team/dashboard"))
                .andExpect(model().attributeHasFieldErrors("inputTeamJoinDto", "inviteCode"));

        verify(teamService, never()).createTeam(any(InputTeamDTO.class));
        verify(teamService, never()).joinTeamWithInviteCode(any());
    }

    @Test
    void validCreateRedirectsToDashboardAndCallsService() throws Exception {
        InputTeamDTO inputTeamDto = new InputTeamDTO("MVC Team Test");
        doNothing().when(teamService).createTeam(inputTeamDto);

        mockMvc.perform(post("/team/create")
                        .with(user("user2@example.com").roles("USER"))
                        .with(csrf())
                        .flashAttr("inputTeamDto", inputTeamDto))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/team/dashboard"));

        verify(teamService).createTeam(inputTeamDto);
    }

    @Test
    void duplicateTeamNameReturnsDashboardFieldError() throws Exception {
        InputTeamDTO inputTeamDto = new InputTeamDTO("Existing Team");
        doThrow(new TeamNameAlreadyExists(inputTeamDto.name()))
                .when(teamService).createTeam(inputTeamDto);

        mockMvc.perform(post("/team/create")
                        .with(user("user2@example.com").roles("USER"))
                        .with(csrf())
                        .flashAttr("inputTeamDto", inputTeamDto))
                .andExpect(status().isOk())
                .andExpect(view().name("team/dashboard"))
                .andExpect(model().attributeHasFieldErrors("inputTeamDto", "name"));

        verify(teamService).createTeam(inputTeamDto);
    }

    @Test
    void validJoinRedirectsToDashboardAndCallsService() throws Exception {
        InputTeamJoinDTO inputTeamJoinDto = new InputTeamJoinDTO("ABCD1234");
        doNothing().when(teamService).joinTeamWithInviteCode(inputTeamJoinDto.inviteCode());

        mockMvc.perform(post("/team/join")
                        .with(user("user2@example.com").roles("USER"))
                        .with(csrf())
                        .flashAttr("inputTeamJoinDto", inputTeamJoinDto))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/team/dashboard"));

        verify(teamService).joinTeamWithInviteCode("ABCD1234");
    }

    @Test
    void invalidInviteCodeReturnsDashboardError() throws Exception {
        InputTeamJoinDTO inputTeamJoinDto = new InputTeamJoinDTO("UNKNOWN1");
        doThrow(new InviteCodeNotFound(inputTeamJoinDto.inviteCode()))
                .when(teamService).joinTeamWithInviteCode(inputTeamJoinDto.inviteCode());

        mockMvc.perform(post("/team/join")
                        .with(user("user2@example.com").roles("USER"))
                        .with(csrf())
                        .flashAttr("inputTeamJoinDto", inputTeamJoinDto))
                .andExpect(status().isOk())
                .andExpect(view().name("team/dashboard"))
                .andExpect(model().attributeHasFieldErrors("inputTeamJoinDto", "inviteCode"));

        verify(teamService).joinTeamWithInviteCode("UNKNOWN1");
    }

    @Test
    void memberCanOpenPrivateTeamDetail() throws Exception {
        when(teamService.findById(1L)).thenReturn(teamDto());
        when(teamService.isCurrentUserOwner(1L)).thenReturn(false);

        mockMvc.perform(get("/team/1")
                        .with(user("user11@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("team/show"))
                .andExpect(model().attributeExists("team", "isOwner"))
                .andExpect(content().string(not(containsString(">Actions</th>"))))
                .andExpect(content().string(not(containsString(">Remove</button>"))));

        verify(teamService).findById(1L);
        verify(teamService).isCurrentUserOwner(1L);
    }

    @Test
    void memberCanOpenPrivateTeamScoreboard() throws Exception {
        when(teamService.findScoreboardById(1L)).thenReturn(scoreboardDto());

        mockMvc.perform(get("/team/1/scoreboard")
                        .with(user("user11@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("team/scoreboard"))
                .andExpect(model().attributeExists("scoreboard"))
                .andExpect(content().string(containsString("Team total")))
                .andExpect(content().string(containsString("Member")))
                .andExpect(content().string(containsString("Score")));

        verify(teamService).findScoreboardById(1L);
    }

    @Test
    void ownerSeesTeamManagementActions() throws Exception {
        when(teamService.findById(1L)).thenReturn(teamDto());
        when(teamService.isCurrentUserOwner(1L)).thenReturn(true);

        mockMvc.perform(get("/team/1")
                        .with(user("user1@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(">Actions</th>")))
                .andExpect(content().string(containsString(">Remove</button>")));

        verify(teamService).findById(1L);
        verify(teamService).isCurrentUserOwner(1L);
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
        when(teamService.findById(1L)).thenThrow(new AccessDeniedException("Team detail is only available to members"));
        when(teamService.findScoreboardById(1L))
                .thenThrow(new AccessDeniedException("Team scoreboard is only available to members"));

        mockMvc.perform(get("/team/1")
                        .with(user("user2@example.com").roles("USER")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/team/1/scoreboard")
                        .with(user("user2@example.com").roles("USER")))
                .andExpect(status().isForbidden());

        verify(teamService).findById(1L);
        verify(teamService).findScoreboardById(1L);
    }

    @Test
    void ownerCanRegenerateInviteCodeAndRemoveTeamMembers() throws Exception {
        doNothing().when(teamService).regenerateInviteCode(1L);
        doNothing().when(teamService).removeMember(1L, 2L);

        mockMvc.perform(post("/team/1/invite-code")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/team/1"));

        mockMvc.perform(post("/team/1/members/2/remove")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/team/1"));

        verify(teamService).regenerateInviteCode(1L);
        verify(teamService).removeMember(1L, 2L);
    }

    @Test
    void nonOwnerCannotRegenerateInviteCodeOrRemoveTeamMembers() throws Exception {
        doThrow(new AccessDeniedException("Only the team owner can manage this team"))
                .when(teamService).regenerateInviteCode(1L);
        doThrow(new AccessDeniedException("Only the team owner can manage this team"))
                .when(teamService).removeMember(1L, 2L);

        mockMvc.perform(post("/team/1/invite-code")
                        .with(user("user11@example.com").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/team/1/members/2/remove")
                        .with(user("user11@example.com").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(teamService).regenerateInviteCode(1L);
        verify(teamService).removeMember(1L, 2L);
    }

}
