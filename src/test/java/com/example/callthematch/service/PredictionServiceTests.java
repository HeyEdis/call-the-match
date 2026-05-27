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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PredictionServiceTests {

    @Mock
    private PredictionRepository predictionRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PredictionService predictionService;

    @Test
    void savePredictionCreatesPredictionWithCreatedAt() {
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

    @Test
    void currentUserPredictionStatusReturnsPredictionForUserRole() {
        MyUser user = MyUser.builder().id(1L).email("user1@example.com").build();
        Competition competition = openCompetition();
        Prediction prediction = Prediction.builder()
                .user(user)
                .competition(competition)
                .predictedScoreA(2)
                .predictedScoreB(1)
                .build();

        when(userService.findByEmail("user1@example.com")).thenReturn(user);
        when(competitionRepository.findById(2L)).thenReturn(Optional.of(competition));
        when(predictionRepository.findByUserAndCompetition(user, competition)).thenReturn(Optional.of(prediction));

        Optional<InputPredictionDTO> result = predictionService.findPredictionStatusByCompetitionIdAndEmail(2L,"user1@example.com");

        assertThat(result).contains(new InputPredictionDTO(2, 1));
        verify(userService).findByEmail("user1@example.com");
        verify(predictionRepository).findByUserAndCompetition(user, competition);
    }

    @Test
    void currentUserPredictionStatusReturnsEmptyWhenUserHasNoPrediction() {
        MyUser user = MyUser.builder().id(1L).email("user1@example.com").build();
        Competition competition = openCompetition();

        when(userService.findByEmail("user1@example.com")).thenReturn(user);
        when(competitionRepository.findById(2L)).thenReturn(Optional.of(competition));
        when(predictionRepository.findByUserAndCompetition(user, competition)).thenReturn(Optional.empty());

        Optional<InputPredictionDTO> result = predictionService.findPredictionStatusByCompetitionIdAndEmail(2L, "user1@example.com");

        assertThat(result).isEmpty();
        verify(userService).findByEmail("user1@example.com");
        verify(competitionRepository).findById(2L);
        verify(predictionRepository).findByUserAndCompetition(user, competition);
    }

    private Competition openCompetition() {
        return Competition.builder()
                .id(2L)
                .date(LocalDate.now().plusDays(1))
                .time(LocalTime.NOON)
                .build();
    }

}
