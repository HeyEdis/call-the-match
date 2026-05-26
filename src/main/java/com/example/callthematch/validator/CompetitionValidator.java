package com.example.callthematch.validator;

import com.example.callthematch.dto.request.InputCompetitionDTO;
import com.example.callthematch.repository.CompetitionRepository;
import com.example.callthematch.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CompetitionValidator implements Validator {

    private static final LocalDate EARLIEST_START_DATE = LocalDate.of(2026, 5, 20);
    private static final LocalDate LATEST_START_DATE = LocalDate.of(2026, 6, 6);

    private final CompetitionRepository competitionRepository;
    private final StadiumRepository stadiumRepository;

    @Override
    public boolean supports(Class<?> klass) {
        return InputCompetitionDTO.class.isAssignableFrom(klass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        InputCompetitionDTO input = (InputCompetitionDTO) target;

        validateDifferentTeams(input, errors);
        validateDateScope(input, errors);
        validateStadiumTimeConflict(input, errors);
        validateSelectedStadiumCode(input, errors);
    }

    private void validateDifferentTeams(InputCompetitionDTO input, Errors errors) {
        if (input.teamA() == null || input.teamB() == null) {
            return;
        }

        if (input.teamA().equals(input.teamB())) {
            errors.rejectValue("teamB", "competition.teams.different");
        }
    }

    private void validateDateScope(InputCompetitionDTO input, Errors errors) {
        if (input.date() == null) {
            return;
        }

        if(input.date().isBefore(EARLIEST_START_DATE)){
            errors.rejectValue("date", "competition.date.before", new Object[] {EARLIEST_START_DATE}, null);
        }

        if (input.date().isAfter(LATEST_START_DATE)) {
            errors.rejectValue("date", "competition.date.after", new Object[] {LATEST_START_DATE}, null);
        }
    }

    private void validateStadiumTimeConflict(InputCompetitionDTO input, Errors errors) {
        if (input.stadium() == null || input.date() == null || input.time() == null) {
            return;
        }

        if (hasStadiumTimeConflict(input)) {
            errors.rejectValue("time", "competition.stadium.time.conflict");
        }
    }

    private void validateSelectedStadiumCode(InputCompetitionDTO input, Errors errors) {
        if (input.stadium() == null || input.stadiumCode() == null) {
            return;
        }

        stadiumRepository.findById(input.stadium())
                .filter(stadium -> !input.stadiumCode().equals(stadium.getCode()))
                .ifPresent(stadium ->
                        errors.rejectValue("stadiumCode", "competition.stadiumCode.selected"));
    }

    private boolean hasStadiumTimeConflict(InputCompetitionDTO input) {
        Long stadiumId = input.stadium();
        if (input.id() == null) {
            return competitionRepository.existsByStadiumIdAndDateAndTime(stadiumId, input.date(), input.time());
        }

        return competitionRepository.existsByStadiumIdAndDateAndTimeAndIdNot(
                stadiumId,
                input.date(),
                input.time(),
                input.id());
    }
}
