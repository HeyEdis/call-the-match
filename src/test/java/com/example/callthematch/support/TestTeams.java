package com.example.callthematch.support;

import com.example.callthematch.dto.response.TeamDTO;
import com.example.callthematch.dto.response.TeamMemberScoreDTO;
import com.example.callthematch.dto.response.TeamScoreboardDTO;
import com.example.callthematch.model.MyUser;
import com.example.callthematch.model.Role;
import com.example.callthematch.model.Team;
import com.example.callthematch.model.TeamMember;
import com.example.callthematch.model.TeamRole;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class TestTeams {

    private TestTeams() {
    }

    public static TeamDTO teamDto() {
        MyUser owner = myUser(1L, "captain", "user1@example.com");
        Team team = Team.builder()
                .id(1L)
                .name("Red Lions")
                .owner(owner)
                .inviteCode("ABCD1234")
                .score(24)
                .build();
        TeamMember ownerMember = member(1L, owner, team, TeamRole.OWNER, 14);
        TeamMember normalMember = member(2L, myUser(2L, "member", "user11@example.com"), team, TeamRole.MEMBER, 10);
        Set<TeamMember> members = new LinkedHashSet<>(List.of(ownerMember, normalMember));
        team.setMembers(members);

        return new TeamDTO(1L, "Red Lions", owner, members, "ABCD1234", 24);
    }

    public static TeamScoreboardDTO scoreboardDto() {
        return new TeamScoreboardDTO(1L, "Red Lions", 24, List.of(
                new TeamMemberScoreDTO("captain", 14),
                new TeamMemberScoreDTO("member", 10)));
    }

    public static MyUser myUser(Long id, String userName, String email) {
        return MyUser.builder()
                .id(id)
                .userName(userName)
                .email(email)
                .role(Role.USER)
                .build();
    }

    public static TeamMember member(Long id, MyUser user, Team team, TeamRole role, Integer score) {
        return TeamMember.builder()
                .id(id)
                .user(user)
                .team(team)
                .role(role)
                .score(score)
                .joinedAt(LocalDateTime.of(2026, 5, 20, 12, 0))
                .build();
    }
}
