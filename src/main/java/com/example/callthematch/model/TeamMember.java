package com.example.callthematch.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Builder
@Table(name = "team_members")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private MyUser user;

    @ManyToOne
    @JoinColumn(name = "teamId")
    private Team team;

    @Enumerated(EnumType.STRING)
    private TeamRole role;

    private Integer score;

    private LocalDateTime joinedAt;

}
