package com.example.callthematch.exception;

public class CountryNotFound extends RuntimeException {
    public CountryNotFound(Long id) {
        super("Competition not found with id ".formatted(id));
    }
}
