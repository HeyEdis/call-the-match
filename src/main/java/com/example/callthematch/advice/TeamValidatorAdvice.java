package com.example.callthematch.advice;

import com.example.callthematch.controller.TeamController;
import com.example.callthematch.validator.InputTeamJoinValidator;
import com.example.callthematch.validator.InputTeamValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice(assignableTypes = TeamController.class)
@RequiredArgsConstructor
public class TeamValidatorAdvice {

    private final InputTeamValidator inputTeamValidator;
    private final InputTeamJoinValidator inputTeamJoinValidator;

    @InitBinder("inputTeamDTO")
    public void initTeamBinder(WebDataBinder binder) {
        binder.addValidators(inputTeamValidator);
    }

    @InitBinder("inputTeamJoinDTO")
    public void initTeamJoinBinder(WebDataBinder binder) {
        binder.addValidators(inputTeamJoinValidator);
    }
}
