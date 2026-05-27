package com.example.callthematch.advice;

import com.example.callthematch.controller.*;
import com.example.callthematch.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice(assignableTypes = {
        AccountController.class,
        CompetitionController.class,
        HomeController.class,
        LocaleController.class,
        PredictionController.class,
        TeamController.class
})
public class GlobalExceptionAdvice {

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            CompetitionNotFound.class,
            TeamNotFound.class,
            StadiumNotFound.class,
            CountryNotFound.class,
            InviteCodeNotFound.class,
            UserNotFound.class,
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound() {
        return "error/404";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied() {
        return "error/403";
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleServerError() {
        return "error/500";
    }
}
