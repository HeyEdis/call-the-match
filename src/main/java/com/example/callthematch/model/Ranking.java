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

    private Integer wins;

    private Integer losses;

    private Integer draws;

    private Integer points;

}
