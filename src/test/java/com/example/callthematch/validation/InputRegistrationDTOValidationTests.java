package com.example.callthematch.validation;

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

    @Test
    void blankPasswordFailsValidation() {
        InputRegistrationDTO input = new InputRegistrationDTO("Jan", "Peeters", "janpeeters", "jan@example.com", "");

        assertThat(validator.validate(input))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("password");
    }

    @Test
    void blankEmailFailsValidation() {
        InputRegistrationDTO input = new InputRegistrationDTO("Jan", "Peeters", "janpeeters", "", "ValidPass1");

        assertThat(validator.validate(input))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("email");
    }

    @Test
    void invalidEmailFailsValidation() {
        InputRegistrationDTO input = new InputRegistrationDTO("Jan", "Peeters", "janpeeters", "not-an-email", "ValidPass1");

        assertThat(validator.validate(input))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("email");
    }

    @Test
    void validRegistrationProducesNoViolations() {
        InputRegistrationDTO input = new InputRegistrationDTO("Jan", "Peeters", "janpeeters", "jan@example.com", "ValidPass1");

        assertThat(validator.validate(input)).isEmpty();
    }
}
