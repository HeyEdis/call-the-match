package com.example.callthematch.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "ranking")
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teamId", nullable = false)
    private Team team;

    private Integer wins;

    private Integer losses;

    private Integer draws;

    private Integer points;

}
