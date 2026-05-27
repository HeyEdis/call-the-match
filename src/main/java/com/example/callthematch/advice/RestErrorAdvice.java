package com.example.callthematch.advice;

import com.example.callthematch.controller.CompetitionRestController;
import com.example.callthematch.controller.StadiumRestController;
import com.example.callthematch.dto.response.ErrorResponseDTO;
import com.example.callthematch.exception.StadiumNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@RestControllerAdvice(assignableTypes = {CompetitionRestController.class, StadiumRestController.class})
public class RestErrorAdvice {

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            DateTimeParseException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleBadRequest(Exception exception) {
        return new ErrorResponseDTO(
                400,
                exception.getMessage(),
                LocalDateTime.now().toString()
        );
    }

    @ExceptionHandler(StadiumNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleStadiumNotFound(StadiumNotFound exception) {
        return new ErrorResponseDTO(
                404,
                exception.getMessage(),
                LocalDateTime.now().toString()
        );
    }
}
