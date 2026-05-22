package com.example.callthematch.service;

import com.example.callthematch.model.Prediction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ScoringService {

    private final MessageSource messageSource;

    public int calculatePoints(Prediction prediction, Integer officialScoreA, Integer officialScoreB,
                               List<Prediction> teamPredictions) {
        if (isExactScore(prediction, officialScoreA, officialScoreB)) {
            return score("scoring.exactScore")
                    + uniqueExactBonus(prediction, teamPredictions)
                    + uniqueOutcomeBonus(prediction, teamPredictions);
        }

        if (hasCorrectOutcome(prediction, officialScoreA, officialScoreB)) {
            return score("scoring.correctOutcome")
                    + uniqueOutcomeBonus(prediction, teamPredictions);
        }

        return 0;
    }

    private int uniqueExactBonus(Prediction prediction, List<Prediction> teamPredictions) {
        long sameExactScore = teamPredictions.stream()
                .filter(teamPrediction -> hasSamePredictionScore(teamPrediction, prediction))
                .count();
        return sameExactScore == 1 ? score("scoring.uniqueExactBonus") : 0;
    }

    private int uniqueOutcomeBonus(Prediction prediction, List<Prediction> teamPredictions) {
        long sameOutcome = teamPredictions.stream()
                .filter(teamPrediction -> outcome(teamPrediction.getPredictedScoreA(), teamPrediction.getPredictedScoreB())
                        == outcome(prediction.getPredictedScoreA(), prediction.getPredictedScoreB()))
                .count();
        return sameOutcome == 1 ? score("scoring.uniqueOutcomeBonus") : 0;
    }

    private boolean isExactScore(Prediction prediction, Integer officialScoreA, Integer officialScoreB) {
        return prediction.getPredictedScoreA().equals(officialScoreA)
                && prediction.getPredictedScoreB().equals(officialScoreB);
    }

    private boolean hasCorrectOutcome(Prediction prediction, Integer officialScoreA, Integer officialScoreB) {
        return outcome(prediction.getPredictedScoreA(), prediction.getPredictedScoreB())
                == outcome(officialScoreA, officialScoreB);
    }

    private boolean hasSamePredictionScore(Prediction first, Prediction second) {
        return first.getPredictedScoreA().equals(second.getPredictedScoreA())
                && first.getPredictedScoreB().equals(second.getPredictedScoreB());
    }

    private MatchOutcome outcome(Integer scoreA, Integer scoreB) {
        int comparison = scoreA.compareTo(scoreB);
        if (comparison > 0) {
            return MatchOutcome.TEAM_A_WIN;
        }
        if (comparison < 0) {
            return MatchOutcome.TEAM_B_WIN;
        }
        return MatchOutcome.DRAW;
    }

    private int score(String code) {
        return Integer.parseInt(messageSource.getMessage(code, null, Locale.getDefault()));
    }

    private enum MatchOutcome {
        TEAM_A_WIN,
        TEAM_B_WIN,
        DRAW
    }
}
