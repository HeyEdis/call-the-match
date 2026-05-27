package com.example.callthematch.service;

import com.example.callthematch.dto.request.InputCompetitionDTO;
import com.example.callthematch.dto.request.InputCompetitionResultDTO;
import com.example.callthematch.dto.response.CompetitionDTO;
import com.example.callthematch.dto.response.MatchRestDTO;
import com.example.callthematch.exception.CompetitionNotFound;
import com.example.callthematch.exception.CountryNotFound;
import com.example.callthematch.exception.StadiumNotFound;
import com.example.callthematch.model.Competition;
import com.example.callthematch.model.Country;
import com.example.callthematch.model.Stadium;
import com.example.callthematch.repository.CompetitionRepository;
import com.example.callthematch.repository.CountryRepository;
import com.example.callthematch.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    private MatchRestDTO toRestDTO(Competition c) {
        return new MatchRestDTO(
                c.getId(),
                c.getTeamA().getName(),
                c.getTeamB().getName(),
                c.getDate(),
                c.getTime(),
                c.getStadium().getName(),
                c.getScoreA(),
                c.getScoreB());
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

    public List<CompetitionDTO> getAllCompetitions() {
        return competitionRepository.findAllByOrderByDateAscTimeAsc()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<MatchRestDTO> findRestMatchesByDate(LocalDate date) {
        return competitionRepository.findByDateOrderByTimeAsc(date)
                .stream()
                .map(this::toRestDTO)
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

        Country teamA = findCountryById(dto.teamA());
        Country teamB = findCountryById(dto.teamB());
        Stadium stadium = findStadiumById(dto.stadium());

        Competition competition = Competition.builder()
                .teamA(teamA)
                .teamB(teamB)
                .stadium(stadium)
                .date(dto.date())
                .time(dto.time())
                .build();

        return competitionRepository.save(competition).getId();
    }

    public Long update(InputCompetitionDTO dto) {

        Country teamA = findCountryById(dto.teamA());
        Country teamB = findCountryById(dto.teamB());
        Stadium stadium = findStadiumById(dto.stadium());

        Competition competition = findCompetitionById(dto.id());
        competition.setTeamA(teamA);
        competition.setTeamB(teamB);
        competition.setStadium(stadium);
        competition.setDate(dto.date());
        competition.setTime(dto.time());

        return competitionRepository.save(competition).getId();
    }

    public Long updateOfficialResult(Long id, InputCompetitionResultDTO dto) {
        Competition competition = findCompetitionById(id);
        competition.setScoreA(dto.scoreA());
        competition.setScoreB(dto.scoreB());

        Competition savedCompetition = competitionRepository.save(competition);
        teamMemberService.recalculateScoresAfterResult(savedCompetition);
        return savedCompetition.getId();
    }

    private Country findCountryById(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new CountryNotFound(id));
    }

    private Stadium findStadiumById(Long id) {
        return stadiumRepository.findById(id)
                .orElseThrow(() -> new StadiumNotFound(id));
    }

    private Competition findCompetitionById(Long id) {
        return competitionRepository.findById(id)
                .orElseThrow(() -> new CompetitionNotFound(id));
    }
}
