package com.example.callthematch.dto.request;

import com.example.callthematch.model.TeamMember;
import com.example.callthematch.model.User;

import java.time.LocalDateTime;
import java.util.Set;

public record InputTeamDTO(Long id, String name, User owner, Set<TeamMember> members, String inviteCode, Integer score) {
}
