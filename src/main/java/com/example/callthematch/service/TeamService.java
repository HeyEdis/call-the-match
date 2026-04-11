package com.example.callthematch.service;

import com.example.callthematch.dto.request.InputTeamDTO;
import com.example.callthematch.dto.response.TeamDTO;
import com.example.callthematch.exception.TeamNotFound;
import com.example.callthematch.model.Team;
import com.example.callthematch.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    private TeamDTO toDTO(Team t) {
        return new TeamDTO(t.getId(),t.getName(),t.getOwner(),t.getMembers(),t.getInviteCode(),t.calculateTeamScore(),t.getCreatedAt(),t.getUpdatedAt());
    }

    private InputTeamDTO toInputDTO(Team t) {
        return new InputTeamDTO(t.getId(),t.getName(),t.getOwner(),t.getMembers(),t.getInviteCode(),t.getScore(),t.getCreatedAt(),t.getUpdatedAt());
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

    public TeamDTO findById(Long id) {
        return toDTO(findTeamById(id));
    }
}
