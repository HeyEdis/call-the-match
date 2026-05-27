package com.example.callthematch.service;

import com.example.callthematch.dto.request.InputPredictionDTO;
import com.example.callthematch.dto.response.PredictionOverviewDTO;
import com.example.callthematch.exception.CompetitionNotFound;
import com.example.callthematch.exception.PredictionCutoffPassed;
import com.example.callthematch.model.Competition;
import com.example.callthematch.model.MyUser;
import com.example.callthematch.model.Prediction;
import com.example.callthematch.repository.CompetitionRepository;
import com.example.callthematch.repository.PredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final CompetitionRepository competitionRepository;
    private final UserService userService;

    private InputPredictionDTO toStatusDTO(Prediction prediction) {
        return new InputPredictionDTO(
                prediction.getPredictedScoreA(),
                prediction.getPredictedScoreB());
    }

    private PredictionOverviewDTO toOverviewDTO(Prediction prediction) {
        Competition competition = prediction.getCompetition();
        return new PredictionOverviewDTO(
                competition.getId(),
                competition.getTeamA().getName(),
                competition.getTeamB().getName(),
                competition.getScoreA(),
                competition.getScoreB(),
                prediction.getPredictedScoreA(),
                prediction.getPredictedScoreB());
    }

    private Competition findCompetitionById(Long id) {
        return competitionRepository.findById(id).orElseThrow(() -> new CompetitionNotFound(id));
    }

    public List<PredictionOverviewDTO> getCurrentUserPredictions(String email) {
        MyUser user = userService.findByEmail(email);
        return predictionRepository.findAllByUserOrderByCompetitionDateAscCompetitionTimeAsc(user)
                .stream()
                .map(this::toOverviewDTO)
                .toList();
    }

    public Optional<InputPredictionDTO> findPredictionStatusByCompetitionIdAndEmail(Long competitionId, String email) {
        MyUser user = userService.findByEmail(email);
        Competition competition = findCompetitionById(competitionId);

        return predictionRepository.findByUserAndCompetition(user, competition)
                .map(this::toStatusDTO);
    }

    public void saveCurrentUserPrediction(Long competitionId, InputPredictionDTO dto, String email) {
        MyUser user = userService.findByEmail(email);
        Competition competition = findCompetitionById(competitionId);

        savePrediction(user, competition, dto);
    }

    public void savePrediction(MyUser user, Competition competition, InputPredictionDTO dto) {
        if (isCutoffPassed(competition)) {
            throw new PredictionCutoffPassed();
        }

        Prediction prediction = predictionRepository.findByUserAndCompetition(user, competition)
                .orElseGet(() -> Prediction.builder()
                        .user(user)
                        .competition(competition)
                        .createdAt(LocalDateTime.now())
                        .build());

        prediction.setPredictedScoreA(dto.predictedScoreA());
        prediction.setPredictedScoreB(dto.predictedScoreB());

        predictionRepository.save(prediction);
    }

    public boolean isCutoffPassed(Long competitionId) {
        return isCutoffPassed(findCompetitionById(competitionId));
    }

    private boolean isCutoffPassed(Competition competition) {
        LocalDateTime kickoff = LocalDateTime.of(competition.getDate(), competition.getTime());
        return !LocalDateTime.now().isBefore(kickoff.minusHours(1));
    }
}
