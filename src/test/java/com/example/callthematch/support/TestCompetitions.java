package com.example.callthematch.support;

import com.example.callthematch.dto.request.InputCompetitionDTO;
import com.example.callthematch.dto.response.CompetitionDTO;
import com.example.callthematch.dto.response.CountryDTO;
import com.example.callthematch.dto.response.StadiumDTO;
import com.example.callthematch.model.Country;
import com.example.callthematch.model.Location;
import com.example.callthematch.model.Stadium;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class TestCompetitions {

    private TestCompetitions() {
    }

    public static InputCompetitionDTO inputCompetitionDTO(Long id) {
        return inputCompetitionDTO(id, 1L, 2L, LocalDate.of(2026, 5, 20));
    }

    public static InputCompetitionDTO inputCompetitionDTO(Long id, Long teamA, Long teamB, LocalDate date) {
        return new InputCompetitionDTO(
                id,
                teamA,
                teamB,
                1L,
                1001,
                31,
                date,
                LocalTime.NOON);
    }

    public static CompetitionDTO competitionDto(Long id) {
        return new CompetitionDTO(
                id,
                country(1L, "Belgium", 32),
                country(2L, "Canada", 1),
                stadium(),
                null,
                null,
                LocalDate.of(2026, 5, 20),
                LocalTime.of(18, 0)
        );
    }

    public static List<CountryDTO> countryDtos() {
        return List.of(
                new CountryDTO(1L, 32, "Belgium"),
                new CountryDTO(2L, 1, "Canada")
        );
    }

    public static List<StadiumDTO> stadiumDtos() {
        return List.of(new StadiumDTO(1L, location(), "MetLife Stadium", 1001, 82500));
    }

    public static Country country(Long id, String name, Integer landCode) {
        return Country.builder().id(id).name(name).landCode(landCode).build();
    }

    public static Stadium stadium() {
        return Stadium.builder()
                .id(1L)
                .location(location())
                .name("MetLife Stadium")
                .code(1001)
                .capacity(82500)
                .build();
    }

    public static Location location() {
        return Location.builder().id(1L).city("New York").build();
    }
}
