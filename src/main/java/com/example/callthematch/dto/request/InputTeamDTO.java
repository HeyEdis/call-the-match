package com.example.callthematch.dto.request;

import jakarta.validation.constraints.NotBlank;

public record InputTeamDTO(
        @NotBlank(message = "{team.name.required}")
        String name
) {
    public InputTeamDTO() {
        this("");
    }
}
