package com.example.callthematch.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record MatchRestDTO(
        Long id,
        String teamAName,
        String teamBName,
        LocalDate date,
        LocalTime time,
        String stadiumName,
        Integer scoreA,
        Integer scoreB) {
}
