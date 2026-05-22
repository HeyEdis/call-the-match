package com.example.callthematch.service;

import com.example.callthematch.dto.request.InputTeamDTO;
import com.example.callthematch.dto.response.TeamDTO;
import com.example.callthematch.exception.InviteCodeNotFound;
import com.example.callthematch.exception.TeamNotFound;
import com.example.callthematch.model.Team;
import com.example.callthematch.model.TeamMember;
import com.example.callthematch.model.User;
import com.example.callthematch.repository.TeamMemberRepository;
import com.example.callthematch.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final CurrentUserService currentUserService;

    private TeamDTO toDTO(Team t) {
        return new TeamDTO(t.getId(),t.getName(),t.getOwner(),t.getMembers(),t.getInviteCode(),t.calculateTeamScore());
    }

    private InputTeamDTO toInputDTO(Team t) {
        return new InputTeamDTO(t.getId(),t.getName(),t.getOwner(),t.getMembers(),t.getInviteCode(),t.getScore());
    }

    private Team findTeamById(Long id)
    {
        return teamRepository.findById(id).orElseThrow(() -> new TeamNotFound(id));
    }

    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(c -> toDTO(c))
                .toList();
    }

    public List<TeamDTO> getTop10Teams() {
        return teamRepository.findAll()
                .stream()
                .map(c -> toDTO(c))
                .sorted(Comparator.comparing(TeamDTO::score).reversed())
                .limit(10)
                .toList();
    }

    public TeamDTO findById(Long id) {
        return toDTO(findTeamById(id));
    }

    public void regenerateInviteCode(Long id) {
        Team team = findTeamById(id);
        team.regenerateInviteCode();
        teamRepository.save(team);
    }


    public void joinTeamWithInviteCode(String inviteCode) {
        Team team = teamRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new InviteCodeNotFound(inviteCode));

        User user = currentUserService.getCurrentUser();

        if (teamMemberRepository.existsTeamMembersByUserIdAndTeamId(user.getId(), team.getId())) {
            return;
        }

        TeamMember teamMember = team.addMember(user);
        teamMemberRepository.save(teamMember);

    }

}
