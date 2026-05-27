package com.example.callthematch.dto.response;

public record TeamDetailDTO(
        TeamDTO team,
        boolean owner,
        String rank
) {
}
