package com.example.callthematch.exception;

public class CompetitionNotFound extends RuntimeException {
    public CompetitionNotFound(Long id) {
        super("Competition not found with id ".formatted(id));
    }
}
