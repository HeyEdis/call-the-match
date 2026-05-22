package com.example.callthematch.validator;

import com.example.callthematch.dto.request.InputCompetitionDTO;
import com.example.callthematch.repository.CompetitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CompetitionValidator implements Validator {

    private static final LocalDate FIRST_MATCH_DATE = LocalDate.of(2026, 5, 20);
    private static final LocalDate LAST_MATCH_DATE = LocalDate.of(2026, 6, 6);

    private final CompetitionRepository competitionRepository;

    @Override
    public boolean supports(Class<?> klass) {
        return InputCompetitionDTO.class.isAssignableFrom(klass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        InputCompetitionDTO input = (InputCompetitionDTO) target;

        if (input.teamA() != null && input.teamB() != null
                && input.teamA().equals(input.teamB())) {
            errors.rejectValue("teamB", "competition.teams.different");
        }

        if (input.date() != null
                && (input.date().isBefore(FIRST_MATCH_DATE) || input.date().isAfter(LAST_MATCH_DATE))) {
            errors.rejectValue("date", "competition.date.period",
                    new Object[] {FIRST_MATCH_DATE, LAST_MATCH_DATE}, null);
        }

        if (input.stadium() != null && input.date() != null && input.time() != null
                && hasStadiumTimeConflict(input)) {
            errors.rejectValue("time", "competition.stadium.time.conflict");
        }
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
