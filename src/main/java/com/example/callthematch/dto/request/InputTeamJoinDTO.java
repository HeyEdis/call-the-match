package com.example.callthematch.dto.request;

import jakarta.validation.constraints.NotBlank;

public record InputTeamJoinDTO(
        @NotBlank(message = "{team.inviteCode.required}")
        String inviteCode
) {
    public InputTeamJoinDTO() {
        this("");
    }
}
