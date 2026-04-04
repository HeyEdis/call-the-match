package com.example.callthematch.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "competitions")
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stage stage;

    @ManyToOne
    @JoinColumn(name = "teamA_id", nullable = false)
    private Country teamA;

    @ManyToOne
    @JoinColumn(name = "teamB_id", nullable = false)
    private Country teamB;

    @ManyToOne
    @JoinColumn(name = "stadiumId", nullable = false)
    private Stadium stadium;

    @ManyToOne
    @JoinColumn(name = "groupId")
    private Group group;

    private Integer scoreA;

    private Integer scoreB;

    private LocalDate date;

    private LocalTime time;

    private LocalDateTime createdAt;
}
