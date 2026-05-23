package com.example.callthematch.dto.response;

public record PredictionOverviewDTO(
        Long competitionId,
        String teamA,
        String teamB,
        Integer scoreA,
        Integer scoreB,
        Integer predictedScoreA,
        Integer predictedScoreB) {
}
