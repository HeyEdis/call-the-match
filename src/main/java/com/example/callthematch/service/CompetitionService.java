package com.example.callthematch.service;

import com.example.callthematch.dto.request.InputCompetitionDTO;
import com.example.callthematch.dto.response.CompetitionDTO;
import com.example.callthematch.exception.CompetitionNotFound;
import com.example.callthematch.exception.CountryNotFound;
import com.example.callthematch.exception.StadiumNotFound;
import com.example.callthematch.model.Competition;
import com.example.callthematch.repository.CompetitionRepository;
import com.example.callthematch.repository.CountryRepository;
import com.example.callthematch.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final CountryRepository countryRepository;
    private final StadiumRepository stadiumRepository;

    private CompetitionDTO toDTO(Competition c) {
        return new CompetitionDTO(c.getId(),c.getTeamA(),c.getTeamB(),c.getStadium(),c.getScoreA(),c.getScoreB(),c.getDate(),c.getTime());
    }

    private Competition findCompetitionById(Long id)
    {
        return competitionRepository.findById(id).orElseThrow(() -> new CompetitionNotFound(id));
    }

    public List<CompetitionDTO> getAllCompetitions() {
        return competitionRepository.findAll()
                .stream()
                .map(c -> toDTO(c))
                .sorted(Comparator.comparing(CompetitionDTO::date)
                        .thenComparing(CompetitionDTO::time))
                .toList();
    }

    public CompetitionDTO findById(Long id) {
        return toDTO(findCompetitionById(id));
    }

    public Long add(InputCompetitionDTO dto) {
        Competition competition = Competition.builder()
                .teamA(countryRepository.findById(dto.teamA()).orElseThrow(() -> new CountryNotFound(dto.teamA())))
                .teamB(countryRepository.findById(dto.teamB()).orElseThrow(() -> new CountryNotFound(dto.teamB())))
                .stadium(stadiumRepository.findById(dto.stadium()).orElseThrow(() -> new StadiumNotFound(dto.stadium())))
                .date(dto.date())
                .time(dto.time())
                .build();

        return competitionRepository.save(competition).getId();
    }
}
