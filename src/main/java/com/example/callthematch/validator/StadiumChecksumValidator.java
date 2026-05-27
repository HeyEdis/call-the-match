package com.example.callthematch.validator;

import com.example.callthematch.dto.request.InputCompetitionDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StadiumChecksumValidator implements ConstraintValidator<ValidStadiumChecksum, InputCompetitionDTO> {

    private int divisor;

    @Override
    public void initialize(ValidStadiumChecksum constraintAnnotation) {
        divisor = constraintAnnotation.divisor();
    }

    @Override
    public boolean isValid(InputCompetitionDTO input, ConstraintValidatorContext context) {

        if (input == null || input.stadiumCode() == null || input.checksum() == null) {
            return true;
        }

        if(input.checksum().equals(input.stadiumCode() % divisor)) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("checksum")
                .addConstraintViolation();
        return false;
    }
}
