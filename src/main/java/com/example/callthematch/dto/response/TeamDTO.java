package com.example.callthematch.dto.response;

import com.example.callthematch.model.TeamMember;
import com.example.callthematch.model.User;

import java.time.LocalDateTime;
import java.util.Set;

public record TeamDTO(Long id, String name, User owner, Set<TeamMember> members, String inviteCode, Integer score, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
