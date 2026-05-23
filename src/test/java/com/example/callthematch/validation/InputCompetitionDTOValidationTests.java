package com.example.callthematch.validation;

import com.example.callthematch.dto.request.InputCompetitionDTO;
import com.example.callthematch.dto.request.InputCompetitionResultDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class InputCompetitionDTOValidationTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void fixtureRejectsMissingRequiredInput() {
        assertThat(validator.validate(new InputCompetitionDTO()))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("teamA", "teamB", "stadium", "stadiumCode", "checksum", "date", "time");
    }

    @Test
    void officialResultRejectsMissingAndNegativeScores() {
        assertThat(validator.validate(new InputCompetitionResultDTO()))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("scoreA", "scoreB");

        assertThat(validator.validate(new InputCompetitionResultDTO(-1, -2)))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("scoreA", "scoreB");
    }

    @Test
    void stadiumChecksumAcceptsValidRemainderAndRejectsInvalidRemainder() {
        InputCompetitionDTO valid = fixture(1001, 31);
        InputCompetitionDTO invalid = fixture(1001, 32);

        assertThat(validator.validate(valid))
                .extracting(violation -> violation.getPropertyPath().toString())
                .doesNotContain("checksum");

        assertThat(validator.validate(invalid))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("checksum");
    }

    private InputCompetitionDTO fixture(Integer stadiumCode, Integer checksum) {
        return new InputCompetitionDTO(
                null,
                1L,
                2L,
                1L,
                stadiumCode,
                checksum,
                LocalDate.of(2026, 5, 20),
                LocalTime.NOON);
    }
}

