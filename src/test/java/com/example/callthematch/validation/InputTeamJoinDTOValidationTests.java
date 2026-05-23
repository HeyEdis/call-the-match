package com.example.callthematch.validation;

import com.example.callthematch.dto.request.InputTeamJoinDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputTeamJoinDTOValidationTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void blankInviteCodeFailsValidation() {
        assertThat(validator.validate(new InputTeamJoinDTO("")))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("inviteCode");
    }

    @Test
    void validInviteCodeProducesNoViolations() {
        assertThat(validator.validate(new InputTeamJoinDTO("ABCD1234"))).isEmpty();
    }
}

