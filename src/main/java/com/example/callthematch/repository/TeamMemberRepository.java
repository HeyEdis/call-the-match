package com.example.callthematch.repository;

import com.example.callthematch.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    boolean existsTeamMembersByUserIdAndTeamId(Long userId, long teamId);

    List<TeamMember> findAllByUserId(Long userId);
}
