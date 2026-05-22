package com.example.callthematch.repository;

import com.example.callthematch.model.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    boolean existsByStadiumIdAndDateAndTime(Long stadiumId, java.time.LocalDate date, java.time.LocalTime time);

    boolean existsByStadiumIdAndDateAndTimeAndIdNot(
            Long stadiumId,
            java.time.LocalDate date,
            java.time.LocalTime time,
            Long id);
}
