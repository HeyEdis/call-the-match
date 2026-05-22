package com.example.callthematch.dto.response;

import com.example.callthematch.model.MyUser;
import com.example.callthematch.model.TeamMember;

import java.util.Set;

public record TeamDTO(Long id, String name, MyUser owner, Set<TeamMember> members, String inviteCode, Integer score) {
}
