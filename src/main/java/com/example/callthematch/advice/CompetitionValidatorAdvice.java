package com.example.callthematch.advice;

import com.example.callthematch.controller.CompetitionController;
import com.example.callthematch.validator.CompetitionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

// De advice klasse aan de juiste controller koppelen!
@ControllerAdvice(assignableTypes = CompetitionController.class)
@RequiredArgsConstructor
public class CompetitionValidatorAdvice {

    // injecteren an de validator constructor
    private final CompetitionValidator competitionValidator;

    // Hier wordt de koppeling gemaakt, wordt gebruikt in deze controller klasse,
    // gekoppeld met deze record (inputRegistrionDTO)
    // als je foutmeldingen dubbel worden getoond komt dat doordat je geen naam naast de initbinder hebt gezet
    @InitBinder("inputCompetitionDTO")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(competitionValidator);
    }
}
