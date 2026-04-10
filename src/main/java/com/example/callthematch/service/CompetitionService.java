package com.example.callthematch.service;

import com.example.callthematch.dto.request.InputCompetitionDTO;
import com.example.callthematch.dto.response.CompetitionDTO;
import com.example.callthematch.model.Competition;
import com.example.callthematch.repository.CompetitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionRepository competitionRepository;

    private CompetitionDTO toDTO(Competition c) {
        return new CompetitionDTO(c.getId(),c.getTeamA(),c.getTeamB(),c.getStadium(),c.getScoreA(),c.getScoreB(),c.getDate(),c.getTime(),c.getCreatedAt());
    }

    private InputCompetitionDTO toInputDto(Competition c){
        return new InputCompetitionDTO(c.getId(),c.getTeamA(),c.getTeamB(),c.getStadium(),c.getScoreA(),c.getScoreB(),c.getDate(),c.getTime(),c.getCreatedAt());
    }

    public List<CompetitionDTO> getAllCompetitions() {
        return competitionRepository.findAll()
                .stream()
                .map(c -> toDTO(c))
                .toList();
    }


}
