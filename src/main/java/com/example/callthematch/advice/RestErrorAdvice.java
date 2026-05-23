package com.example.callthematch.advice;

import com.example.callthematch.dto.response.ErrorResponse;
import com.example.callthematch.exception.StadiumNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@RestControllerAdvice(assignableTypes = {
        com.example.callthematch.controller.CompetitionRestController.class,
        com.example.callthematch.controller.StadiumRestController.class
})
public class RestErrorAdvice {

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class,
            DateTimeParseException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(Exception exception) {
        return new ErrorResponse(
                400,
                "Invalid request",
                LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler(StadiumNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleStadiumNotFound(StadiumNotFound exception) {
        return new ErrorResponse(
                404,
                exception.getMessage(),
                LocalDateTime.now().toString()
        );
    }
}
