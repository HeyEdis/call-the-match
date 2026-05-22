package com.example.callthematch.service;

import com.example.callthematch.model.Prediction;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class ScoringServiceTests {

    private final ScoringService scoringService = new ScoringService(messageSource());

    @Test
    void exactScoreAddsExactAndUniqueBonuses() {
        Prediction exact = prediction(2, 1);

        assertThat(scoringService.calculatePoints(exact, 2, 1, List.of(exact, prediction(0, 0))))
                .isEqualTo(9);
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

    private Prediction prediction(int scoreA, int scoreB) {
        return Prediction.builder()
                .predictedScoreA(scoreA)
                .predictedScoreB(scoreB)
                .build();
    }

    private StaticMessageSource messageSource() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("scoring.exactScore", Locale.getDefault(), "5");
        messageSource.addMessage("scoring.correctOutcome", Locale.getDefault(), "2");
        messageSource.addMessage("scoring.uniqueExactBonus", Locale.getDefault(), "3");
        messageSource.addMessage("scoring.uniqueOutcomeBonus", Locale.getDefault(), "1");
        return messageSource;
    }
}
