package com.example.callthematch.service;

import com.example.callthematch.model.MatchOutcome;
import com.example.callthematch.model.Prediction;
import com.example.callthematch.model.ScoringPoints;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoringService {

    public int calculatePoints(Prediction prediction, Integer officialScoreA, Integer officialScoreB,
                               List<Prediction> teamPredictions) {
        int basePoints = calculateBasePoints(prediction, officialScoreA, officialScoreB);

        if (basePoints == 0) {
            return 0;
        }

        if (isExactScore(prediction, officialScoreA, officialScoreB)) {
            return basePoints
                    + uniqueExactBonus(prediction, teamPredictions)
                    + uniqueOutcomeBonus(prediction, teamPredictions);
        }

        return basePoints + uniqueOutcomeBonus(prediction, teamPredictions);
    }

    public int calculateBasePoints(Prediction prediction, Integer officialScoreA, Integer officialScoreB) {
        if (isExactScore(prediction, officialScoreA, officialScoreB)) {
            return ScoringPoints.EXACT_SCORE;
        }

        if (hasCorrectOutcome(prediction, officialScoreA, officialScoreB)) {
            return ScoringPoints.CORRECT_OUTCOME;
        }
        return 0;
    }

    private int uniqueExactBonus(Prediction prediction, List<Prediction> teamPredictions) {
        long sameExactScore = teamPredictions.stream()
                .filter(teamPrediction -> hasSamePredictionScore(teamPrediction, prediction))
                .count();
        return sameExactScore == 1 ? ScoringPoints.UNIQUE_EXACT_BONUS : 0;
    }

    private int uniqueOutcomeBonus(Prediction prediction, List<Prediction> teamPredictions) {
        MatchOutcome predictedOutcome = outcome(prediction);

        long sameOutcome = teamPredictions.stream()
                .filter(teamPrediction -> outcome(teamPrediction) == predictedOutcome)
                .count();

        return sameOutcome == 1 ? ScoringPoints.UNIQUE_OUTCOME_BONUS : 0;
    }

    private boolean hasSameScore(Integer firstScoreA, Integer firstScoreB,
                                 Integer secondScoreA, Integer secondScoreB) {
        return firstScoreA.equals(secondScoreA)
                && firstScoreB.equals(secondScoreB);
    }

    private boolean hasCorrectOutcome(Prediction prediction, Integer officialScoreA, Integer officialScoreB) {
        return outcome(prediction) == outcome(officialScoreA, officialScoreB);
    }

    private boolean isExactScore(Prediction prediction, Integer officialScoreA, Integer officialScoreB) {
        return hasSameScore(
                prediction.getPredictedScoreA(),
                prediction.getPredictedScoreB(),
                officialScoreA,
                officialScoreB);
    }

    private boolean hasSamePredictionScore(Prediction first, Prediction second) {
        return hasSameScore(
                first.getPredictedScoreA(),
                first.getPredictedScoreB(),
                second.getPredictedScoreA(),
                second.getPredictedScoreB());
    }

    private MatchOutcome outcome(Prediction prediction) {
        return outcome(prediction.getPredictedScoreA(), prediction.getPredictedScoreB());
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

}
