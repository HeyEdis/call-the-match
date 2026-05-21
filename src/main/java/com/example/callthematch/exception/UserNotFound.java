package com.example.callthematch.exception;

public class UserNotFound extends RuntimeException {
    public UserNotFound(Long userId) {
        super("User not found with id ".formatted(userId));
    }
}
