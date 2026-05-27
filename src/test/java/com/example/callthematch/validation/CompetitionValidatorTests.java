package com.example.callthematch.validation;

import com.example.callthematch.dto.request.InputCompetitionDTO;
import com.example.callthematch.model.Stadium;
import com.example.callthematch.repository.CompetitionRepository;
import com.example.callthematch.repository.StadiumRepository;
import com.example.callthematch.validator.CompetitionValidator;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static com.example.callthematch.support.TestCompetitions.inputCompetitionDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CompetitionValidatorTests {

    private final CompetitionRepository competitionRepository = mock(CompetitionRepository.class);
    private final StadiumRepository stadiumRepository = mock(StadiumRepository.class);
    private final CompetitionValidator competitionValidator = new CompetitionValidator(competitionRepository, stadiumRepository);

    @Test
    void fixtureRejectsSameTeamAsOpponent() {
        InputCompetitionDTO input = inputCompetitionDTO(null, 1L, 1L, LocalDate.of(2026, 5, 20));
        Errors errors = validate(input);

        assertThat(errors.hasFieldErrors("teamB")).isTrue();
    }

    @Test
    void fixtureRejectsDateOutsideProjectPeriod() {
        InputCompetitionDTO input = inputCompetitionDTO(null, 1L, 2L, LocalDate.of(2026, 5, 19));
        Errors errors = validate(input);

        assertThat(errors.hasFieldErrors("date")).isTrue();
    }

    @Test
    void fixtureRejectsDateAfterProjectPeriod() {
        InputCompetitionDTO input = inputCompetitionDTO(null, 1L, 2L, LocalDate.of(2026, 6, 7));
        Errors errors = validate(input);

        assertThat(errors.hasFieldErrors("date")).isTrue();
    }

    @Test
    void nullDateLeavesRequiredFieldValidationToDtoAnnotations() {
        InputCompetitionDTO input = inputCompetitionDTO(null, 1L, 2L, null);
        Errors errors = validate(input);

        assertThat(errors.hasFieldErrors("date")).isFalse();
    }

    @Test
    void fixtureRejectsPersistedStadiumTimeConflict() {
        InputCompetitionDTO input = inputCompetitionDTO(null, 1L, 2L, LocalDate.of(2026, 5, 20));
        when(competitionRepository.existsByStadiumIdAndDateAndTime(1L, input.date(), input.time()))
                .thenReturn(true);

        Errors errors = validate(input);

        assertThat(errors.hasFieldErrors("time")).isTrue();
    }

    @Test
    void fixtureSkipsStadiumTimeConflictLookupWhenRequiredValuesAreMissing() {
        validate(fixture(null, LocalDate.of(2026, 5, 20), LocalTime.NOON));
        validate(fixture(1L, null, LocalTime.NOON));
        validate(fixture(1L, LocalDate.of(2026, 5, 20), null));

        verify(competitionRepository, never()).existsByStadiumIdAndDateAndTime(1L, LocalDate.of(2026, 5, 20), LocalTime.NOON);
        verify(competitionRepository, never()).existsByStadiumIdAndDateAndTimeAndIdNot(1L, LocalDate.of(2026, 5, 20), LocalTime.NOON, 3L);
    }

    @Test
    void fixtureUsesUpdateAwareConflictLookupForExistingCompetition() {
        InputCompetitionDTO input = inputCompetitionDTO(3L, 1L, 2L, LocalDate.of(2026, 5, 20));
        when(competitionRepository.existsByStadiumIdAndDateAndTimeAndIdNot(1L, input.date(), input.time(), 3L))
                .thenReturn(true);

        Errors errors = validate(input);

        assertThat(errors.hasFieldErrors("time")).isTrue();
        verify(competitionRepository).existsByStadiumIdAndDateAndTimeAndIdNot(1L, input.date(), input.time(), 3L);
        verify(competitionRepository, never()).existsByStadiumIdAndDateAndTime(1L, input.date(), input.time());
    }

    @Test
    void fixtureRejectsCodeThatDoesNotBelongToSelectedStadium() {
        InputCompetitionDTO input = inputCompetitionDTO(null, 1L, 2L, LocalDate.of(2026, 5, 20));
        when(stadiumRepository.findById(1L))
                .thenReturn(Optional.of(Stadium.builder().code(1010).build()));

        Errors errors = validate(input);

        assertThat(errors.hasFieldErrors("stadiumCode")).isTrue();
    }

    @Test
    void validFixtureProducesNoCustomValidatorErrors() {
        InputCompetitionDTO input = inputCompetitionDTO(null, 1L, 2L, LocalDate.of(2026, 5, 20));
        when(stadiumRepository.findById(1L))
                .thenReturn(Optional.of(Stadium.builder().code(1001).build()));

        Errors errors = validate(input);

        assertThat(errors.hasErrors()).isFalse();
    }

    private InputCompetitionDTO fixture(Long stadium, LocalDate date, LocalTime time) {
        return new InputCompetitionDTO(
                3L,
                1L,
                2L,
                stadium,
                1001,
                31,
                date,
                time);
    }

    private Errors validate(InputCompetitionDTO input) {
        Errors errors = new BeanPropertyBindingResult(input, "inputCompetitionDTO");
        competitionValidator.validate(input, errors);
        return errors;
    }
}
