package com.example.callthematch.dto.request;

import com.example.callthematch.model.Country;
import com.example.callthematch.model.Stadium;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record InputCompetitionDTO(
        Long id,

        @NotNull
        Country teamA,

        @NotNull
        Country teamB,

        @NotNull
        Stadium stadium,

        @NotNull
        @Min(0)
        Integer scoreA,

        @NotNull
        @Min(0)
        Integer scoreB,

        @NotNull
        LocalDate date,

        @NotNull
        LocalTime time

) {
    public InputCompetitionDTO() {
        this(null, null, null, null, null, null, null, null);
    }
}
