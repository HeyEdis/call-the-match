package com.example.callthematch.controller;

import com.example.callthematch.config.SecurityConfig;
import com.example.callthematch.advice.TeamValidatorAdvice;
import com.example.callthematch.dto.request.InputTeamDTO;
import com.example.callthematch.dto.request.InputTeamJoinDTO;
import com.example.callthematch.dto.response.PublicRankingDTO;
import com.example.callthematch.dto.response.TeamDetailDTO;
import com.example.callthematch.dto.response.TeamDTO;
import com.example.callthematch.repository.TeamRepository;
import com.example.callthematch.service.TeamService;
import com.example.callthematch.validator.CompetitionValidator;
import com.example.callthematch.validator.InputTeamJoinValidator;
import com.example.callthematch.validator.InputTeamValidator;
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
@Import({
        SecurityConfig.class,
        TeamValidatorAdvice.class,
        InputTeamValidator.class,
        InputTeamJoinValidator.class
})
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

    @MockitoBean
    private TeamRepository teamRepository;

    @BeforeEach
    void setUp() {
        when(teamService.getCurrentUserTeams("user1@example.com")).thenReturn(List.of(teamDto()));
        when(teamService.getCurrentUserTeams("user2@example.com")).thenReturn(List.of(teamDto()));
    }

    @Test
    void rankingShowsPublicTopTenInScoreOrder() throws Exception {
        List<PublicRankingDTO> ranking = List.of(
                new PublicRankingDTO("Winners", 21, 4),
                new PublicRankingDTO("Chasers", 13, 3));
        when(teamService.getTop10Teams()).thenReturn(ranking);

        mockMvc.perform(get("/team/ranking"))
                .andExpect(status().isOk())
                .andExpect(view().name("team/ranking"))
                .andExpect(model().attribute("teamList", ranking));

        assertThat(ranking).hasSizeLessThanOrEqualTo(10);
        assertThat(ranking).extracting(PublicRankingDTO::score).isSortedAccordingTo((left, right) ->
                Integer.compare(right, left));
        verify(teamService).getTop10Teams();
    }

    @Test
    void dashboardShowsOnlyCurrentUsersTeamsAndFormModels() throws Exception {
        List<TeamDTO> teams = List.of(teamDto());
        when(teamService.getCurrentUserTeams("user1@example.com")).thenReturn(teams);

        mockMvc.perform(get("/team/dashboard")
                        .with(user("user1@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("team/dashboard"))
                .andExpect(model().attributeExists("teamList", "inputTeamDTO", "inputTeamJoinDTO"))
                .andExpect(model().attribute("teamList", teams));

        assertThat(teams).hasSize(1);
        assertThat(teams).allSatisfy(team ->
                assertThat(team.owner().getEmail()).isEqualTo("user1@example.com"));
        verify(teamService).getCurrentUserTeams("user1@example.com");
    }

    @Test
    void invalidCreateSubmissionReturnsDashboardFieldErrorAndDoesNotCallService() throws Exception {
        mockMvc.perform(post("/team/create")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf())
                        .flashAttr("inputTeamDTO", new InputTeamDTO()))
                .andExpect(status().isOk())
                .andExpect(view().name("team/dashboard"))
                .andExpect(model().attributeHasFieldErrors("inputTeamDTO", "name"));

        verify(teamService, never()).createTeam(any(InputTeamDTO.class), any());
    }

    @Test
    void invalidJoinSubmissionReturnsDashboardFieldErrorAndDoesNotCallService() throws Exception {
        mockMvc.perform(post("/team/join")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf())
                        .flashAttr("inputTeamJoinDTO", new InputTeamJoinDTO()))
                .andExpect(status().isOk())
                .andExpect(view().name("team/dashboard"))
                .andExpect(model().attributeHasFieldErrors("inputTeamJoinDTO", "inviteCode"));

        verify(teamService, never()).joinTeamWithInviteCode(any(), any());
    }

    @Test
    void validCreateRedirectsToDashboardAndCallsService() throws Exception {
        InputTeamDTO inputTeamDTO = new InputTeamDTO("MVC Team Test");
        doNothing().when(teamService).createTeam(inputTeamDTO, "user2@example.com");

        mockMvc.perform(post("/team/create")
                        .with(user("user2@example.com").roles("USER"))
                        .with(csrf())
                        .flashAttr("inputTeamDTO", inputTeamDTO))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/team/dashboard"));

        verify(teamService).createTeam(inputTeamDTO, "user2@example.com");
    }

    @Test
    void duplicateTeamNameReturnsDashboardFieldError() throws Exception {
        InputTeamDTO inputTeamDTO = new InputTeamDTO("Existing Team");
        when(teamRepository.existsByName(inputTeamDTO.name())).thenReturn(true);

        mockMvc.perform(post("/team/create")
                        .with(user("user2@example.com").roles("USER"))
                        .with(csrf())
                        .flashAttr("inputTeamDTO", inputTeamDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("team/dashboard"))
                .andExpect(model().attributeHasFieldErrors("inputTeamDTO", "name"));

        verify(teamService, never()).createTeam(any(InputTeamDTO.class), any());
    }

    @Test
    void validJoinRedirectsToDashboardAndCallsService() throws Exception {
        InputTeamJoinDTO inputTeamJoinDTO = new InputTeamJoinDTO("ABCD1234");
        when(teamRepository.existsByInviteCode(inputTeamJoinDTO.inviteCode())).thenReturn(true);
        doNothing().when(teamService).joinTeamWithInviteCode(inputTeamJoinDTO.inviteCode(), "user2@example.com");

        mockMvc.perform(post("/team/join")
                        .with(user("user2@example.com").roles("USER"))
                        .with(csrf())
                        .flashAttr("inputTeamJoinDTO", inputTeamJoinDTO))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/team/dashboard"));

        verify(teamService).joinTeamWithInviteCode("ABCD1234", "user2@example.com");
    }

    @Test
    void invalidInviteCodeReturnsDashboardError() throws Exception {
        InputTeamJoinDTO inputTeamJoinDTO = new InputTeamJoinDTO("UNKNOWN1");
        when(teamRepository.existsByInviteCode(inputTeamJoinDTO.inviteCode())).thenReturn(false);

        mockMvc.perform(post("/team/join")
                        .with(user("user2@example.com").roles("USER"))
                        .with(csrf())
                        .flashAttr("inputTeamJoinDTO", inputTeamJoinDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("team/dashboard"))
                .andExpect(model().attributeHasFieldErrors("inputTeamJoinDTO", "inviteCode"));

        verify(teamService, never()).joinTeamWithInviteCode(any(), any());
    }

    @Test
    void memberCanOpenPrivateTeamDetail() throws Exception {
        when(teamService.findDetailById(1L, "user11@example.com"))
                .thenReturn(new TeamDetailDTO(teamDto(), false, "1"));

        mockMvc.perform(get("/team/1")
                        .with(user("user11@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("team/show"))
                .andExpect(model().attributeExists("team", "isOwner"))
                .andExpect(content().string(not(containsString(">Actions</th>"))))
                .andExpect(content().string(not(containsString(">Remove</button>"))));

        verify(teamService).findDetailById(1L, "user11@example.com");
    }

    @Test
    void memberCanSeeInviteCodeSharePanel() throws Exception {
        when(teamService.findDetailById(1L, "user11@example.com"))
                .thenReturn(new TeamDetailDTO(teamDto(), false, "1"));

        mockMvc.perform(get("/team/1")
                        .with(user("user11@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("team"))
                .andExpect(content().string(containsString("Share invite code")))
                .andExpect(content().string(containsString("value=\"ABCD1234\"")))
                .andExpect(content().string(containsString("readonly")))
                .andExpect(content().string(containsString(">Copy</button>")))
                .andExpect(content().string(containsString("navigator.clipboard.writeText")))
                .andExpect(content().string(not(containsString("Regenerate code</button>"))));

        verify(teamService).findDetailById(1L, "user11@example.com");
    }

    @Test
    void memberCanOpenPrivateTeamScoreboard() throws Exception {
        when(teamService.findScoreboardById(1L, "user11@example.com")).thenReturn(scoreboardDto());

        mockMvc.perform(get("/team/1/scoreboard")
                        .with(user("user11@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("team/scoreboard"))
                .andExpect(model().attributeExists("scoreboard"))
                .andExpect(content().string(containsString("Team total")))
                .andExpect(content().string(containsString("Member")))
                .andExpect(content().string(containsString("Score")));

        verify(teamService).findScoreboardById(1L, "user11@example.com");
    }

    @Test
    void ownerSeesTeamManagementActions() throws Exception {
        when(teamService.findDetailById(1L, "user1@example.com"))
                .thenReturn(new TeamDetailDTO(teamDto(), true, "1"));

        mockMvc.perform(get("/team/1")
                        .with(user("user1@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(">Actions</th>")))
                .andExpect(content().string(containsString(">Remove</button>")));

        verify(teamService).findDetailById(1L, "user1@example.com");
    }

    @Test
    void guestIsRedirectedToLoginOnTeamRoutes() throws Exception {
        mockMvc.perform(get("/team/dashboard"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(get("/team/1"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void adminIsForbiddenOnTeamRoutes() throws Exception {
        mockMvc.perform(get("/team/dashboard")
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/team/1")
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }

    @Test
    void nonMemberCannotReadPrivateTeamDetail() throws Exception {
        when(teamService.findDetailById(1L, "user2@example.com"))
                .thenThrow(new AccessDeniedException("Team detail is only available to members"));

        mockMvc.perform(get("/team/1")
                        .with(user("user2@example.com").roles("USER")))
                .andExpect(status().isForbidden());

        verify(teamService).findDetailById(1L, "user2@example.com");
    }

    @Test
    void nonMemberCannotReadPrivateTeamScoreboard() throws Exception {
        when(teamService.findScoreboardById(1L, "user2@example.com"))
                .thenThrow(new AccessDeniedException("Team scoreboard is only available to members"));

        mockMvc.perform(get("/team/1/scoreboard")
                        .with(user("user2@example.com").roles("USER")))
                .andExpect(status().isForbidden());

        verify(teamService).findScoreboardById(1L, "user2@example.com");
    }

    @Test
    void ownerCanRegenerateInviteCode() throws Exception {
        doNothing().when(teamService).regenerateInviteCode(1L, "user1@example.com");

        mockMvc.perform(post("/team/1/invite-code")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/team/1"));

        verify(teamService).regenerateInviteCode(1L, "user1@example.com");
    }

    @Test
    void ownerCanRemoveTeamMember() throws Exception {
        doNothing().when(teamService).removeMember(1L, 2L, "user1@example.com");

        mockMvc.perform(post("/team/1/members/2/remove")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/team/1"));

        verify(teamService).removeMember(1L, 2L, "user1@example.com");
    }

    @Test
    void nonOwnerCannotRegenerateInviteCode() throws Exception {
        doThrow(new AccessDeniedException("Only the team owner can manage this team"))
                .when(teamService).regenerateInviteCode(1L, "user11@example.com");

        mockMvc.perform(post("/team/1/invite-code")
                        .with(user("user11@example.com").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(teamService).regenerateInviteCode(1L, "user11@example.com");
    }

    @Test
    void nonOwnerCannotRemoveTeamMember() throws Exception {
        doThrow(new AccessDeniedException("Only the team owner can manage this team"))
                .when(teamService).removeMember(1L, 2L, "user11@example.com");

        mockMvc.perform(post("/team/1/members/2/remove")
                        .with(user("user11@example.com").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(teamService).removeMember(1L, 2L, "user11@example.com");
    }

}
