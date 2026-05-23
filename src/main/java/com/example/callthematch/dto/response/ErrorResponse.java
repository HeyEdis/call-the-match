package com.example.callthematch.dto.response;

public record ErrorResponse(
        int status,
        String message,
        String timestamp) {
}
