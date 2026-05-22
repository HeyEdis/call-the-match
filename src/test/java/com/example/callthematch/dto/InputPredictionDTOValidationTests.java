package com.example.callthematch.dto;

import com.example.callthematch.dto.request.InputPredictionDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputPredictionDTOValidationTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void predictionRejectsMissingAndNegativeScores() {
        assertThat(validator.validate(new InputPredictionDTO()))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("predictedScoreA", "predictedScoreB");

        assertThat(validator.validate(new InputPredictionDTO(-1, -2)))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("predictedScoreA", "predictedScoreB");
    }
}
