package com.example.callthematch.validator;

import com.example.callthematch.dto.request.InputCompetitionDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StadiumChecksumValidator implements ConstraintValidator<ValidStadiumChecksum, InputCompetitionDTO> {

    private int divisor;
    private int codeMin;
    private int codeMax;

    @Override
    public void initialize(ValidStadiumChecksum constraintAnnotation) {
        divisor = constraintAnnotation.divisor();
        codeMin = constraintAnnotation.codeMin();
        codeMax = constraintAnnotation.codeMax();
    }

    @Override
    public boolean isValid(InputCompetitionDTO input, ConstraintValidatorContext context) {
        if (input == null || input.stadiumCode() == null || input.checksum() == null) {
            return true;
        }

        boolean validCodeLength = input.stadiumCode() >= codeMin && input.stadiumCode() <= codeMax;
        boolean validChecksum = input.checksum() == input.stadiumCode() % divisor;
        if (validCodeLength && validChecksum) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("{validator.stadiumChecksum}")
                .addPropertyNode("checksum")
                .addConstraintViolation();
        return false;
    }
}
