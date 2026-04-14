package com.example.callthematch.model;

import jakarta.persistence.*;
import lombok.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "teams")
public class Team {

    private static final SecureRandom r = new SecureRandom();

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

    public void generateInviteCode() {
        String chars   = generateCharSegment();
        String digits  = generateDigitSegment();
        this.inviteCode = chars.concat(digits);
    }

    private String generateCharSegment() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 4;i++){
            sb.append((char) ('A' + r.nextInt(26)));
        }

        return sb.toString();
    }

    private String generateDigitSegment() {
        StringBuilder sb = new StringBuilder();

        for (int i =0; i < 4;i++){
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

}
