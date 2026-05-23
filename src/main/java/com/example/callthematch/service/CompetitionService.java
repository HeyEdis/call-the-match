package com.example.callthematch.service;

import com.example.callthematch.dto.request.InputCompetitionDTO;
import com.example.callthematch.dto.request.InputCompetitionResultDTO;
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
    private final TeamMemberService teamMemberService;

    private CompetitionDTO toDTO(Competition c) {
        return new CompetitionDTO(c.getId(),c.getTeamA(),c.getTeamB(),c.getStadium(),c.getScoreA(),c.getScoreB(),c.getDate(),c.getTime());
    }

    private InputCompetitionDTO toInputDTO(Competition c) {
        Integer stadiumCode = c.getStadium().getCode();
        return new InputCompetitionDTO(
                c.getId(),
                c.getTeamA().getId(),
                c.getTeamB().getId(),
                c.getStadium().getId(),
                stadiumCode,
                stadiumCode % 97,
                c.getDate(),
                c.getTime());
    }

    private InputCompetitionResultDTO toInputResultDTO(Competition c) {
        return new InputCompetitionResultDTO(c.getScoreA(), c.getScoreB());
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

    public InputCompetitionDTO findInputById(Long id) {
        return toInputDTO(findCompetitionById(id));
    }

    public InputCompetitionResultDTO findInputResultById(Long id) {
        return toInputResultDTO(findCompetitionById(id));
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

    public Long update(InputCompetitionDTO dto) {
        Competition competition = findCompetitionById(dto.id());
        competition.setTeamA(countryRepository.findById(dto.teamA()).orElseThrow(() -> new CountryNotFound(dto.teamA())));
        competition.setTeamB(countryRepository.findById(dto.teamB()).orElseThrow(() -> new CountryNotFound(dto.teamB())));
        competition.setStadium(stadiumRepository.findById(dto.stadium()).orElseThrow(() -> new StadiumNotFound(dto.stadium())));
        competition.setDate(dto.date());
        competition.setTime(dto.time());

        return competitionRepository.save(competition).getId();
    }

    public Long updateResult(Long id, InputCompetitionResultDTO dto) {
        Competition competition = findCompetitionById(id);
        competition.setScoreA(dto.scoreA());
        competition.setScoreB(dto.scoreB());

        Competition savedCompetition = competitionRepository.save(competition);
        teamMemberService.recalculateScoresAfterResult(savedCompetition);
        return savedCompetition.getId();
    }
}
