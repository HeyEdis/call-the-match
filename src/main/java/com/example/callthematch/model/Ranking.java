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
    @JoinColumn(name = "countryId", nullable = false)
    private Country country;

    @ManyToOne
    @JoinColumn(name = "groupId", nullable = true)
    private CountryGroup group;

    private Integer position;

    private Integer wins;

    private Integer losses;

    private Integer draws;

    private Integer goalsScored;

    private Integer goalsAgainst;

    private Integer points;

}
