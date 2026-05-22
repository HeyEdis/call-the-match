package com.example.callthematch.controller;

import com.example.callthematch.exception.CompetitionNotFound;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class MVCExceptionHandler {

    @ExceptionHandler({CompetitionNotFound.class, MethodArgumentTypeMismatchException.class})
    public ModelAndView handleNotFound() {
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.setStatus(org.springframework.http.HttpStatus.NOT_FOUND);
        return modelAndView;
    }
}
