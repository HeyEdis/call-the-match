package com.example.callthematch.dto;

import com.example.callthematch.dto.request.InputRegistrationDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputRegistrationDTOValidationTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void registrationRejectsMissingAndInvalidFields() {
        InputRegistrationDTO input = new InputRegistrationDTO("", "", "", "not-an-email", "short");

        assertThat(validator.validate(input))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("firstName", "lastName", "userName", "email", "password");
    }
}
