package com.example.callthematch.dto.request;

import com.example.callthematch.model.MyUser;
import com.example.callthematch.model.TeamMember;

import java.util.Set;

public record InputTeamDTO(Long id, String name, MyUser owner, Set<TeamMember> members, String inviteCode, Integer score) {
}
