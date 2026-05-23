package com.example.callthematch.dto.response;

import java.util.List;

public record TeamScoreboardDTO(Long id, String name, Integer score, List<TeamMemberScoreDTO> members) {
}
