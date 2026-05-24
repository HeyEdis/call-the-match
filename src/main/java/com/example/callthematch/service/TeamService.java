package com.example.callthematch.service;

import com.example.callthematch.dto.request.InputTeamDTO;
import com.example.callthematch.dto.response.PublicRankingDTO;
import com.example.callthematch.dto.response.TeamDTO;
import com.example.callthematch.dto.response.TeamMemberScoreDTO;
import com.example.callthematch.dto.response.TeamScoreboardDTO;
import com.example.callthematch.exception.InviteCodeNotFound;
import com.example.callthematch.exception.TeamNameAlreadyExists;
import com.example.callthematch.exception.TeamNotFound;
import com.example.callthematch.model.MyUser;
import com.example.callthematch.model.Team;
import com.example.callthematch.model.TeamMember;
import com.example.callthematch.model.TeamRole;
import com.example.callthematch.repository.TeamMemberRepository;
import com.example.callthematch.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserService userService;

    private TeamDTO toDTO(Team t) {
        return new TeamDTO(t.getId(),t.getName(),t.getOwner(),t.getMembers(),t.getInviteCode(),t.calculateTeamScore());
    }

    private PublicRankingDTO toPublicRankingDTO(Team t) {
        return new PublicRankingDTO(t.getName(), t.calculateTeamScore(), t.getMembers().size());
    }

    private TeamMemberScoreDTO toTeamMemberScoreDTO(TeamMember member) {
        return new TeamMemberScoreDTO(member.getUser().getUserName(), member.getScore());
    }

    private TeamScoreboardDTO toScoreboardDTO(Team team) {
        List<TeamMemberScoreDTO> members = teamMemberRepository.findAllByTeamOrderByScoreDesc(team)
                .stream()
                .map(this::toTeamMemberScoreDTO)
                .toList();
        return new TeamScoreboardDTO(team.getId(), team.getName(), team.getScore(), members);
    }

    private Team findTeamById(Long id)
    {
        return teamRepository.findById(id).orElseThrow(() -> new TeamNotFound(id));
    }

    public List<TeamDTO> getCurrentUserTeams() {
        MyUser user = userService.getCurrentUser();

        return teamMemberRepository.findAllByUserId(user.getId())
                .stream()
                .map(TeamMember::getTeam)
                .map(this::toDTO)
                .toList();
    }

    public List<PublicRankingDTO> getTop10Teams() {
        return teamRepository.findAll()
                .stream()
                .map(this::toPublicRankingDTO)
                .sorted(Comparator.comparing(PublicRankingDTO::score).reversed())
                .limit(10)
                .toList();
    }

    public TeamDTO findById(Long id) {
        Team team = findTeamById(id);
        requireCurrentUserMembership(team);
        return toDTO(team);
    }

    public TeamScoreboardDTO findScoreboardById(Long id) {
        Team team = findTeamById(id);
        requireCurrentUserMembership(team);
        return toScoreboardDTO(team);
    }

    public String getTeamRank(Long id) {
        Team team = findTeamById(id);
        if (team.getScore() == null) {
            return "unknown";
        }
        long teamsAbove = teamRepository.countByScoreGreaterThan(team.getScore());
        return String.valueOf(teamsAbove + 1);
    }

    public boolean isCurrentUserOwner(Long id) {
        Team team = findTeamById(id);
        MyUser user = userService.getCurrentUser();
        return team.getOwner().getId().equals(user.getId());
    }

    public void createTeam(InputTeamDTO inputTeamDTO) {
        if (teamRepository.existsByName(inputTeamDTO.name())) {
            throw new TeamNameAlreadyExists(inputTeamDTO.name());
        }

        MyUser owner = userService.getCurrentUser();
        LocalDateTime createdAt = LocalDateTime.now();
        Team team = Team.builder()
                .name(inputTeamDTO.name())
                .owner(owner)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();

        do {
            team.generateInviteCode();
        } while (teamRepository.existsByInviteCode(team.getInviteCode()));

        Team savedTeam = teamRepository.save(team);
        TeamMember ownerMember = TeamMember.builder()
                .user(owner)
                .team(savedTeam)
                .role(TeamRole.OWNER)
                .score(0)
                .joinedAt(createdAt)
                .build();
        teamMemberRepository.save(ownerMember);
    }

    public void regenerateInviteCode(Long id) {
        Team team = findTeamById(id);
        requireCurrentUserOwner(team);

        do {
            team.regenerateInviteCode();
        } while (teamRepository.existsByInviteCode(team.getInviteCode()));

        teamRepository.save(team);
    }

    public void removeMember(Long teamId, Long memberId) {
        Team team = findTeamById(teamId);
        requireCurrentUserOwner(team);

        TeamMember teamMember = teamMemberRepository.findByIdAndTeamId(memberId, teamId)
                .orElseThrow(() -> new AccessDeniedException("Team member not available"));

        if (teamMember.getRole() == TeamRole.OWNER) {
            throw new AccessDeniedException("The team owner cannot be removed");
        }

        teamMemberRepository.delete(teamMember);
    }

    public void joinTeamWithInviteCode(String inviteCode) {
        Team team = teamRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new InviteCodeNotFound(inviteCode));

        MyUser user = userService.getCurrentUser();

        if (teamMemberRepository.existsTeamMembersByUserIdAndTeamId(user.getId(), team.getId())) {
            return;
        }

        TeamMember teamMember = team.addMember(user);
        teamMemberRepository.save(teamMember);

    }

    private void requireCurrentUserMembership(Team team) {
        MyUser user = userService.getCurrentUser();
        if (!teamMemberRepository.existsTeamMembersByUserIdAndTeamId(user.getId(), team.getId())) {
            throw new AccessDeniedException("Team detail is only available to members");
        }
    }

    private void requireCurrentUserOwner(Team team) {
        MyUser user = userService.getCurrentUser();
        if (!team.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Only the team owner can manage this team");
        }
    }

}
