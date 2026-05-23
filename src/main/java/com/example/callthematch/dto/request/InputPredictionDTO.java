package com.example.callthematch.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InputPredictionDTO(
        @NotNull(message = "{prediction.scoreA.required}")
        @Min(value = 0, message = "{prediction.scoreA.min}")
        Integer predictedScoreA,

        @NotNull(message = "{prediction.scoreB.required}")
        @Min(value = 0, message = "{prediction.scoreB.min}")
        Integer predictedScoreB
) {
    public InputPredictionDTO() {
        this(null, null);
    }
}
