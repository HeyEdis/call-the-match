package com.example.callthematch.service;

import com.example.callthematch.dto.request.InputPredictionDTO;
import com.example.callthematch.exception.PredictionCutoffPassed;
import com.example.callthematch.model.Competition;
import com.example.callthematch.model.MyUser;
import com.example.callthematch.model.Prediction;
import com.example.callthematch.repository.CompetitionRepository;
import com.example.callthematch.repository.PredictionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PredictionServiceTests {

    @Test
    void savePredictionCreatesPredictionWithCreatedAt() {
        PredictionRepository predictionRepository = mock(PredictionRepository.class);
        PredictionService predictionService = predictionService(predictionRepository);
        MyUser user = MyUser.builder().id(1L).build();
        Competition competition = openCompetition();

        when(predictionRepository.save(any(Prediction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        predictionService.savePrediction(user, competition, new InputPredictionDTO(2, 1));

        ArgumentCaptor<Prediction> predictionCaptor = ArgumentCaptor.forClass(Prediction.class);
        verify(predictionRepository).save(predictionCaptor.capture());
        assertThat(predictionCaptor.getValue().getUser()).isEqualTo(user);
        assertThat(predictionCaptor.getValue().getCompetition()).isEqualTo(competition);
        assertThat(predictionCaptor.getValue().getPredictedScoreA()).isEqualTo(2);
        assertThat(predictionCaptor.getValue().getPredictedScoreB()).isEqualTo(1);
        assertThat(predictionCaptor.getValue().getCreatedAt()).isNotNull();
    }

    @Test
    void savePredictionUpdatesExistingPrediction() {
        PredictionRepository predictionRepository = mock(PredictionRepository.class);
        PredictionService predictionService = predictionService(predictionRepository);
        MyUser user = MyUser.builder().id(1L).build();
        Competition competition = openCompetition();
        Prediction existingPrediction = Prediction.builder()
                .user(user)
                .competition(competition)
                .predictedScoreA(0)
                .predictedScoreB(0)
                .build();

        when(predictionRepository.findByUserAndCompetition(user, competition))
                .thenReturn(Optional.of(existingPrediction));
        when(predictionRepository.save(any(Prediction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        predictionService.savePrediction(user, competition, new InputPredictionDTO(3, 4));

        assertThat(existingPrediction.getPredictedScoreA()).isEqualTo(3);
        assertThat(existingPrediction.getPredictedScoreB()).isEqualTo(4);
        verify(predictionRepository).save(existingPrediction);
    }

    @Test
    void savePredictionRejectsPredictionAfterOneHourCutoff() {
        PredictionRepository predictionRepository = mock(PredictionRepository.class);
        PredictionService predictionService = predictionService(predictionRepository);
        MyUser user = MyUser.builder().id(1L).build();
        Competition competition = Competition.builder()
                .id(2L)
                .date(LocalDate.now().minusDays(1))
                .time(LocalTime.NOON)
                .build();

        assertThatThrownBy(() -> predictionService.savePrediction(
                user, competition, new InputPredictionDTO(2, 1)))
                .isInstanceOf(PredictionCutoffPassed.class);
    }

    private PredictionService predictionService(PredictionRepository predictionRepository) {
        return new PredictionService(
                predictionRepository,
                mock(CompetitionRepository.class),
                mock(UserService.class));
    }

    private Competition openCompetition() {
        return Competition.builder()
                .id(2L)
                .date(LocalDate.now().plusDays(1))
                .time(LocalTime.NOON)
                .build();
    }
}
