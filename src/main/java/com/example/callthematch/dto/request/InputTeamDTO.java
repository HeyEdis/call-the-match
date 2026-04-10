package com.example.callthematch.dto.request;

import com.example.callthematch.model.User;

import java.time.LocalDateTime;

public record InputTeamDTO(Long id, String name, User owner, String inviteCode, Integer score, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
