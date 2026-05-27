package com.example.callthematch.dto.response;

public record ErrorResponseDTO(
        int status,
        String message,
        String timestamp) {
}
