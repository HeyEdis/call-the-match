package com.example.callthematch.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    @ManyToOne
    @JoinColumn(name = "teamA_id", nullable = false)
    private Country teamA;

    @ManyToOne
    @JoinColumn(name = "teamB_id", nullable = false)
    private Country teamB;

    @ManyToOne
    @JoinColumn(name = "stadiumId", nullable = false)
    private Stadium stadium;

    private Integer scoreA;

    private Integer scoreB;

    private LocalDate date;

    private LocalTime time;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
