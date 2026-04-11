package com.example.callthematch.exception;

public class TeamNotFound extends RuntimeException {
    public TeamNotFound(Long id) {
        super("Team not found with id ".formatted(id));
    }
}