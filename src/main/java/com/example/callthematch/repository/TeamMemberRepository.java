package com.example.callthematch.repository;

import com.example.callthematch.model.Team;
import com.example.callthematch.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    boolean existsTeamMembersByUserIdAndTeamId(Long userId, long teamId);

    List<TeamMember> findAllByUserId(Long userId);

    List<TeamMember> findAllByTeam(Team team);

    Optional<TeamMember> findByIdAndTeamId(Long id, Long teamId);
}
