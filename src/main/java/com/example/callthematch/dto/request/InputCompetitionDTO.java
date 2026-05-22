package com.example.callthematch.dto.request;

import com.example.callthematch.validator.ValidStadiumChecksum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.time.LocalTime;

@ValidStadiumChecksum
public record InputCompetitionDTO(
        Long id,

        @NotNull(message = "{competition.teamA.required}")
        Long teamA,

        @NotNull(message = "{competition.teamB.required}")
        Long teamB,

        @NotNull(message = "{competition.stadium.required}")
        Long stadium,

        @NotNull(message = "{competition.stadiumCode.required}")
        @PositiveOrZero(message = "{competition.stadiumCode.valid}")
        Integer stadiumCode,

        @NotNull(message = "{competition.checksum.required}")
        @PositiveOrZero(message = "{competition.checksum.valid}")
        Integer checksum,

        @NotNull(message = "{competition.date.required}")
        LocalDate date,

        @NotNull(message = "{competition.time.required}")
        LocalTime time

) {
    public InputCompetitionDTO() {
        this(null, null, null, null, null, null, null, null);
    }
}
