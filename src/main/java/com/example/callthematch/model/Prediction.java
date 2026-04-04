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
@Table(name = "predictions")
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "competitionId", nullable = false)
    private Competition competition;

    private Integer predictedScoreA;

    private Integer predictedScoreB;

    private Integer pointsEarned;

    private LocalDateTime createdAt;

}
