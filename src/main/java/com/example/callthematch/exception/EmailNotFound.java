package com.example.callthematch.exception;

public class EmailNotFound extends RuntimeException {
    public EmailNotFound(String email) {
        super("Email not found: %s".formatted(email));
    }
}
