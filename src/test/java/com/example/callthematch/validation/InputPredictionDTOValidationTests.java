package com.example.callthematch.validation;

import com.example.callthematch.dto.request.InputPredictionDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputPredictionDTOValidationTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void nullPredictedScoreAFailsValidation() {
        assertThat(validator.validate(new InputPredictionDTO()))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("predictedScoreA");
    }

    @Test
    void nullPredictedScoreBFailsValidation() {
        assertThat(validator.validate(new InputPredictionDTO()))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("predictedScoreB");
    }

    @Test
    void negativePredictedScoreAFailsMinValidation() {
        assertThat(validator.validate(new InputPredictionDTO(-1, 0)))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("predictedScoreA");
    }

    @Test
    void negativePredictedScoreBFailsMinValidation() {
        assertThat(validator.validate(new InputPredictionDTO(0, -1)))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("predictedScoreB");
    }

    @Test
    void validNonNegativeScoresPassValidation() {
        assertThat(validator.validate(new InputPredictionDTO(0, 2))).isEmpty();
    }
}

