package com.example.callthematch.dto.response;

import com.example.callthematch.model.Country;
import com.example.callthematch.model.Stadium;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record CompetitionDTO(Long id, Country teamA, Country teamB, Stadium stadium, Integer scoreA, Integer scoreB, LocalDate date, LocalTime time, LocalDateTime createdAt ) {
}
