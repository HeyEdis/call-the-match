package com.example.callthematch.exception;

public class TeamNameAlreadyExists extends RuntimeException {
    public TeamNameAlreadyExists(String teamName) {
        super("Team name already exists: %s".formatted(teamName));
    }
}
