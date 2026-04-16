package com.example.callthematch.service;

import com.example.callthematch.dto.request.InputCompetitionDTO;
import com.example.callthematch.dto.response.CompetitionDTO;
import com.example.callthematch.exception.CompetitionNotFound;
import com.example.callthematch.model.Competition;
import com.example.callthematch.repository.CompetitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionRepository competitionRepository;

    private CompetitionDTO toDTO(Competition c) {
        return new CompetitionDTO(c.getId(),c.getTeamA(),c.getTeamB(),c.getStadium(),c.getScoreA(),c.getScoreB(),c.getDate(),c.getTime());
    }

    private InputCompetitionDTO toInputDto(Competition c){
        return new InputCompetitionDTO(c.getId(),c.getTeamA(),c.getTeamB(),c.getStadium(),c.getScoreA(),c.getScoreB(),c.getDate(),c.getTime());
    }

    private Competition findCompetitionById(Long id)
    {
        return competitionRepository.findById(id).orElseThrow(() -> new CompetitionNotFound(id));
    }

    public List<CompetitionDTO> getAllCompetitions() {
        return competitionRepository.findAll()
                .stream()
                .map(c -> toDTO(c))
                .sorted(Comparator.comparing(CompetitionDTO::date))
                .toList();
    }

    public CompetitionDTO findById(Long id) {
        return toDTO(findCompetitionById(id));
    }

    public void add(InputCompetitionDTO dto) {
        Competition competition = Competition.builder()
                .teamA(dto.teamA())
                .teamB(dto.teamB())
                .stadium(dto.stadium())
                .scoreA(dto.scoreA())
                .scoreB(dto.scoreB())
                .date(dto.date())
                .time(dto.time())
                .build();

        competitionRepository.save(competition);
    }
}
