package com.example.callthematch.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InputCompetitionResultDTO(
        @NotNull(message = "{competition.scoreA.required}")
        @PositiveOrZero(message = "{competition.scoreA.valid}")
        Integer scoreA,

        @NotNull(message = "{competition.scoreB.required}")
        @PositiveOrZero(message = "{competition.scoreB.valid}")
        Integer scoreB
) {
    public InputCompetitionResultDTO() {
        this(null, null);
    }
}
