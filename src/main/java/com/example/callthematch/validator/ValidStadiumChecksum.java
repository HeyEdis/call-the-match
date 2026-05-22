package com.example.callthematch.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = StadiumChecksumValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface ValidStadiumChecksum {
    String message() default "{validator.stadiumChecksum}";
    int divisor() default 97;
    int codeMin() default 1000;
    int codeMax() default 9999;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
