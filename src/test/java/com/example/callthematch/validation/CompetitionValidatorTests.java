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
import java.util.Optional;

import static com.example.callthematch.support.TestCompetitions.inputCompetitionDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompetitionValidatorTests {

    private final CompetitionRepository competitionRepository = mock(CompetitionRepository.class);
    private final StadiumRepository stadiumRepository = mock(StadiumRepository.class);
    private final CompetitionValidator competitionValidator = new CompetitionValidator(competitionRepository, stadiumRepository);

    @Test
    void fixtureRejectsSameTeamAsOpponent() {
        InputCompetitionDTO input = inputCompetitionDto(null, 1L, 1L, LocalDate.of(2026, 5, 20));
        Errors errors = validate(input);

        assertThat(errors.hasFieldErrors("teamB")).isTrue();
    }

    @Test
    void fixtureRejectsDateOutsideProjectPeriod() {
        InputCompetitionDTO input = inputCompetitionDto(null, 1L, 2L, LocalDate.of(2026, 5, 19));
        Errors errors = validate(input);

        assertThat(errors.hasFieldErrors("date")).isTrue();
    }

    @Test
    void fixtureRejectsPersistedStadiumTimeConflict() {
        InputCompetitionDTO input = inputCompetitionDto(null, 1L, 2L, LocalDate.of(2026, 5, 20));
        when(competitionRepository.existsByStadiumIdAndDateAndTime(1L, input.date(), input.time()))
                .thenReturn(true);

        Errors errors = validate(input);

        assertThat(errors.hasFieldErrors("time")).isTrue();
    }

    @Test
    void fixtureRejectsCodeThatDoesNotBelongToSelectedStadium() {
        InputCompetitionDTO input = inputCompetitionDto(null, 1L, 2L, LocalDate.of(2026, 5, 20));
        when(stadiumRepository.findById(1L))
                .thenReturn(Optional.of(Stadium.builder().code(1010).build()));

        Errors errors = validate(input);

        assertThat(errors.hasFieldErrors("stadiumCode")).isTrue();
    }

    private Errors validate(InputCompetitionDTO input) {
        Errors errors = new BeanPropertyBindingResult(input, "inputCompetitionDto");
        competitionValidator.validate(input, errors);
        return errors;
    }
}
