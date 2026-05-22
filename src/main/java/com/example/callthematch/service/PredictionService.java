package com.example.callthematch.service;

import com.example.callthematch.dto.request.InputPredictionDTO;
import com.example.callthematch.exception.CompetitionNotFound;
import com.example.callthematch.model.Competition;
import com.example.callthematch.model.MyUser;
import com.example.callthematch.model.Prediction;
import com.example.callthematch.repository.CompetitionRepository;
import com.example.callthematch.repository.PredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final CompetitionRepository competitionRepository;
    private final UserService userService;

    public InputPredictionDTO findCurrentUserInputByCompetitionId(Long competitionId) {
        MyUser user = userService.getCurrentUser();
        Competition competition = findCompetitionById(competitionId);

        return predictionRepository.findByUserAndCompetition(user, competition)
                .map(prediction -> new InputPredictionDTO(
                        prediction.getPredictedScoreA(),
                        prediction.getPredictedScoreB()))
                .orElseGet(InputPredictionDTO::new);
    }

    public Prediction saveCurrentUserPrediction(Long competitionId, InputPredictionDTO dto) {
        MyUser user = userService.getCurrentUser();
        Competition competition = findCompetitionById(competitionId);

        return savePrediction(user, competition, dto);
    }

    public Prediction savePrediction(MyUser user, Competition competition, InputPredictionDTO dto) {
        Prediction prediction = predictionRepository.findByUserAndCompetition(user, competition)
                .orElseGet(() -> Prediction.builder()
                        .user(user)
                        .competition(competition)
                        .createdAt(LocalDateTime.now())
                        .build());

        prediction.setPredictedScoreA(dto.predictedScoreA());
        prediction.setPredictedScoreB(dto.predictedScoreB());

        return predictionRepository.save(prediction);
    }

    private Competition findCompetitionById(Long id) {
        return competitionRepository.findById(id).orElseThrow(() -> new CompetitionNotFound(id));
    }
}
