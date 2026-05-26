package com.example.callthematch.validation;

import com.example.callthematch.dto.request.InputCompetitionDTO;
import com.example.callthematch.validator.StadiumChecksumValidator;
import com.example.callthematch.validator.ValidStadiumChecksum;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StadiumChecksumValidatorTests {

    private StadiumChecksumValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new StadiumChecksumValidator();

        ValidStadiumChecksum annotation = mock(ValidStadiumChecksum.class);
        when(annotation.divisor()).thenReturn(97);
        validator.initialize(annotation);

        context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);

        lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        lenient().when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        lenient().when(nodeBuilder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void validChecksumReturnsTrue() {
        // 1001 % 97 = 31
        InputCompetitionDTO input = fixture(1001, 31);
        assertThat(validator.isValid(input, context)).isTrue();
    }

    @Test
    void invalidChecksumReturnsFalse() {
        // 1001 % 97 = 31, not 32
        InputCompetitionDTO input = fixture(1001, 32);
        assertThat(validator.isValid(input, context)).isFalse();
    }

    @Test
    void nullInputReturnsTrue() {
        assertThat(validator.isValid(null, context)).isTrue();
    }

    @Test
    void nullStadiumCodeReturnsTrue() {
        InputCompetitionDTO input = new InputCompetitionDTO(null, 1L, 2L, 1L, null, 31,
                LocalDate.of(2026, 5, 20), LocalTime.NOON);
        assertThat(validator.isValid(input, context)).isTrue();
    }

    @Test
    void nullChecksumReturnsTrue() {
        InputCompetitionDTO input = new InputCompetitionDTO(null, 1L, 2L, 1L, 1001, null,
                LocalDate.of(2026, 5, 20), LocalTime.NOON);
        assertThat(validator.isValid(input, context)).isTrue();
    }

    private InputCompetitionDTO fixture(Integer stadiumCode, Integer checksum) {
        return new InputCompetitionDTO(null, 1L, 2L, 1L, stadiumCode, checksum,
                LocalDate.of(2026, 5, 20), LocalTime.NOON);
    }
}

