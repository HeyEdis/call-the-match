package com.example.callthematch.advice;

import com.example.callthematch.controller.CompetitionController;
import com.example.callthematch.validator.CompetitionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice(assignableTypes = CompetitionController.class)
@RequiredArgsConstructor
public class CompetitionValidatorAdvice {

    private final CompetitionValidator competitionValidator;

    @InitBinder("inputCompetitionDTO")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(competitionValidator);
    }
}
