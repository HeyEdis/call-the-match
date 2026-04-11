package com.example.callthematch.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    private User owner;

    @OneToMany(mappedBy = "team")
    private Set<TeamMember> members = new HashSet<>();

    @Column(nullable = false, unique = true)
    private String inviteCode;

    private Integer score;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public int calculateTeamScore() {
        return this.score = members.stream()
                .mapToInt(TeamMember::getScore)
                .sum();
    }

}
