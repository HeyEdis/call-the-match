package com.example.callthematch.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "competitionId", nullable = false)
    private Competition competition;

    @Column(nullable = false)
    private Integer minute;

    @Column(nullable = false)
    private Boolean isTeamA;

    private String playerName;

    private Integer scoreA;

    private Integer scoreB;
}
