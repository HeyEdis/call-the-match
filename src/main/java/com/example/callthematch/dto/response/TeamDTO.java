package com.example.callthematch.dto.response;

import com.example.callthematch.model.User;

import java.time.LocalDateTime;

public record TeamDTO(Long id, String name, User owner, String inviteCode, Integer score, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
