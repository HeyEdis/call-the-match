package com.example.callthematch.service;

import com.example.callthematch.dto.request.InputTeamDTO;
import com.example.callthematch.exception.InviteCodeNotFound;
import com.example.callthematch.exception.TeamNameAlreadyExists;
import com.example.callthematch.model.MyUser;
import com.example.callthematch.model.Team;
import com.example.callthematch.model.TeamMember;
import com.example.callthematch.model.TeamRole;
import com.example.callthematch.repository.TeamMemberRepository;
import com.example.callthematch.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTests {

    @Test
    void createTeamStoresAuthenticatedOwnerMembershipAndUniqueInviteCode() {
        TeamRepository teamRepository = mock(TeamRepository.class);
        TeamMemberRepository teamMemberRepository = mock(TeamMemberRepository.class);
        UserService userService = mock(UserService.class);
        TeamService teamService = new TeamService(teamRepository, teamMemberRepository, userService);
        MyUser owner = user(7L);

        when(userService.getCurrentUser()).thenReturn(owner);
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> {
            Team team = invocation.getArgument(0);
            team.setId(11L);
            return team;
        });

        teamService.createTeam(new InputTeamDTO("Boundary Team"));

        ArgumentCaptor<Team> teamCaptor = ArgumentCaptor.forClass(Team.class);
        verify(teamRepository).save(teamCaptor.capture());
        assertThat(teamCaptor.getValue().getOwner()).isEqualTo(owner);
        assertThat(teamCaptor.getValue().getInviteCode()).hasSize(8);
        verify(teamRepository).existsByInviteCode(teamCaptor.getValue().getInviteCode());

        ArgumentCaptor<TeamMember> memberCaptor = ArgumentCaptor.forClass(TeamMember.class);
        verify(teamMemberRepository).save(memberCaptor.capture());
        assertThat(memberCaptor.getValue().getUser()).isEqualTo(owner);
        assertThat(memberCaptor.getValue().getTeam().getId()).isEqualTo(11L);
        assertThat(memberCaptor.getValue().getRole()).isEqualTo(TeamRole.OWNER);
    }

    @Test
    void createTeamRejectsDuplicateNameBeforePersistence() {
        TeamRepository teamRepository = mock(TeamRepository.class);
        TeamMemberRepository teamMemberRepository = mock(TeamMemberRepository.class);
        UserService userService = mock(UserService.class);
        TeamService teamService = new TeamService(teamRepository, teamMemberRepository, userService);

        when(teamRepository.existsByName("Existing Team")).thenReturn(true);

        assertThatThrownBy(() -> teamService.createTeam(new InputTeamDTO("Existing Team")))
                .isInstanceOf(TeamNameAlreadyExists.class);

        verify(teamRepository, never()).save(any(Team.class));
        verify(teamMemberRepository, never()).save(any(TeamMember.class));
    }

    @Test
    void joinTeamSkipsDuplicateMembership() {
        TeamRepository teamRepository = mock(TeamRepository.class);
        TeamMemberRepository teamMemberRepository = mock(TeamMemberRepository.class);
        UserService userService = mock(UserService.class);
        TeamService teamService = new TeamService(teamRepository, teamMemberRepository, userService);
        Team team = Team.builder().id(13L).build();
        MyUser user = user(5L);

        when(teamRepository.findByInviteCode("JOIN2026")).thenReturn(Optional.of(team));
        when(userService.getCurrentUser()).thenReturn(user);
        when(teamMemberRepository.existsTeamMembersByUserIdAndTeamId(5L, 13L)).thenReturn(true);

        teamService.joinTeamWithInviteCode("JOIN2026");

        verify(teamMemberRepository, never()).save(any(TeamMember.class));
    }

    @Test
    void joinTeamRejectsUnknownInviteCode() {
        TeamRepository teamRepository = mock(TeamRepository.class);
        TeamMemberRepository teamMemberRepository = mock(TeamMemberRepository.class);
        UserService userService = mock(UserService.class);
        TeamService teamService = new TeamService(teamRepository, teamMemberRepository, userService);

        assertThatThrownBy(() -> teamService.joinTeamWithInviteCode("UNKNOWN1"))
                .isInstanceOf(InviteCodeNotFound.class);

        verify(teamMemberRepository, never()).save(any(TeamMember.class));
    }

    private MyUser user(Long id) {
        return MyUser.builder().id(id).email("user%d@example.com".formatted(id)).build();
    }
}
