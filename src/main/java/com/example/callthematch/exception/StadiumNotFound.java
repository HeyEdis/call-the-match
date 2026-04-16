package com.example.callthematch.exception;

public class StadiumNotFound extends RuntimeException {
    public StadiumNotFound(Long id) {
        super("Competition not found with id ".formatted(id));
    }
}
