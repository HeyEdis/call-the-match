package com.example.callthematch.service;

import com.example.callthematch.model.Prediction;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScoringServiceTests {

    private final ScoringService scoringService = new ScoringService();

    @Test
    void exactScoreAddsExactAndUniqueBonuses() {
        Prediction exact = prediction(2, 1);

        assertThat(scoringService.calculatePoints(exact, 2, 1, List.of(exact, prediction(0, 0))))
                .isEqualTo(9);
    }

    @Test
    void exactScoreWithoutUniqueBonusesEarnsExactScorePoints() {
        Prediction exact = prediction(2, 1);

        assertThat(scoringService.calculatePoints(exact, 2, 1, List.of(exact, prediction(2, 1))))
                .isEqualTo(5);
    }

    @Test
    void correctOutcomeWithoutExactScoreAddsOutcomePoints() {
        Prediction correctOutcome = prediction(3, 1);

        assertThat(scoringService.calculatePoints(correctOutcome, 2, 1,
                List.of(correctOutcome, prediction(4, 2))))
                .isEqualTo(2);
    }

    @Test
    void wrongOutcomeEarnsNoPoints() {
        Prediction wrongOutcome = prediction(1, 2);

        assertThat(scoringService.calculatePoints(wrongOutcome, 2, 1, List.of(wrongOutcome)))
                .isZero();
    }

    @Test
    void uniqueOutcomeAddsBonusWhenOutcomeIsUniqueInTeamPredictions() {
        Prediction draw = prediction(1, 1);

        assertThat(scoringService.calculatePoints(draw, 0, 0, List.of(draw, prediction(2, 1))))
                .isEqualTo(3);
    }

    @Test
    void drawScenarioScoresCorrectOutcome() {
        Prediction draw = prediction(2, 2);

        assertThat(scoringService.calculatePoints(draw, 1, 1, List.of(draw, prediction(3, 3))))
                .isEqualTo(2);
    }

    @Test
    void noUniqueBonusWhenMultipleMembersShareSamePrediction() {
        Prediction first = prediction(2, 1);
        Prediction second = prediction(2, 1);

        assertThat(scoringService.calculatePoints(first, 2, 1, List.of(first, second)))
                .isEqualTo(5);
    }

    private Prediction prediction(int scoreA, int scoreB) {
        return Prediction.builder()
                .predictedScoreA(scoreA)
                .predictedScoreB(scoreB)
                .build();
    }
}
