package com.example.callthematch.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PredictionServiceTests {

   /* @Test
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

    @Test
    void currentUserPredictionStatusReturnsPredictionForUserRole() {
        PredictionRepository predictionRepository = mock(PredictionRepository.class);
        CompetitionRepository competitionRepository = mock(CompetitionRepository.class);
        UserService userService = mock(UserService.class);
        PredictionService predictionService = new PredictionService(
                predictionRepository, competitionRepository, userService);
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

        Optional<PredictionStatusDTO> result = predictionService.findPredictionStatusByCompetitionIdAndEmail(2L,"user1@example.com");

        assertThat(result).contains(new PredictionStatusDTO(2, 1));
        verify(userService).findByEmail("user1@example.com");
        verify(predictionRepository).findByUserAndCompetition(user, competition);
    }

    @Test
    void currentUserPredictionStatusReturnsEmptyWhenUserHasNoPrediction() {
        PredictionRepository predictionRepository = mock(PredictionRepository.class);
        CompetitionRepository competitionRepository = mock(CompetitionRepository.class);
        UserService userService = mock(UserService.class);
        PredictionService predictionService = new PredictionService(
                predictionRepository, competitionRepository, userService);
        MyUser user = MyUser.builder().id(1L).email("user1@example.com").build();
        Competition competition = openCompetition();

        when(userService.findByEmail("user1@example.com")).thenReturn(user);
        when(competitionRepository.findById(2L)).thenReturn(Optional.of(competition));
        when(predictionRepository.findByUserAndCompetition(user, competition)).thenReturn(Optional.empty());

        Optional<PredictionStatusDTO> result = predictionService.findPredictionStatusByCompetitionIdAndEmail(2L, "user1@example.com");

        assertThat(result).isEmpty();
        verify(userService).findByEmail("user1@example.com");
        verify(competitionRepository).findById(2L);
        verify(predictionRepository).findByUserAndCompetition(user, competition);
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
    }*/

}
