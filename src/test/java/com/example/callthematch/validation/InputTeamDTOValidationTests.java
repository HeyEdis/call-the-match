package com.example.callthematch.validation;

import com.example.callthematch.dto.request.InputTeamDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputTeamDTOValidationTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void createTeamRejectsMissingName() {
        assertThat(validator.validate(new InputTeamDTO("")))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("name");
    }

    @Test
    void validTeamNameProducesNoViolations() {
        assertThat(validator.validate(new InputTeamDTO("Red Lions"))).isEmpty();
    }
}
