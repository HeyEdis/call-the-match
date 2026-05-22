package com.example.callthematch.validator;

import com.example.callthematch.dto.request.InputCompetitionDTO;
import com.example.callthematch.repository.CompetitionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompetitionValidatorTests {

    private final CompetitionRepository competitionRepository = mock(CompetitionRepository.class);
    private final CompetitionValidator competitionValidator = new CompetitionValidator(competitionRepository);

    @Test
    void fixtureRejectsSameCountryAndDateOutsideProjectPeriod() {
        InputCompetitionDTO input = fixture(null, 1L, 1L, LocalDate.of(2026, 5, 19));
        Errors errors = validate(input);

        assertThat(errors.hasFieldErrors("teamB")).isTrue();
        assertThat(errors.hasFieldErrors("date")).isTrue();
    }

    @Test
    void fixtureRejectsPersistedStadiumTimeConflict() {
        InputCompetitionDTO input = fixture(null, 1L, 2L, LocalDate.of(2026, 5, 20));
        when(competitionRepository.existsByStadiumIdAndDateAndTime(1L, input.date(), input.time()))
                .thenReturn(true);

        Errors errors = validate(input);

        assertThat(errors.hasFieldErrors("time")).isTrue();
    }

    private Errors validate(InputCompetitionDTO input) {
        Errors errors = new BeanPropertyBindingResult(input, "inputCompetitionDto");
        competitionValidator.validate(input, errors);
        return errors;
    }

    private InputCompetitionDTO fixture(Long id, Long teamA, Long teamB, LocalDate date) {
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
}
