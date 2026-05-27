package com.example.callthematch.repository;

import com.example.callthematch.model.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    List<Competition> findAllByOrderByDateAscTimeAsc();

    List<Competition> findByDateOrderByTimeAsc(LocalDate date);

    boolean existsByStadiumIdAndDateAndTime(Long stadiumId, LocalDate date, LocalTime time);

    boolean existsByStadiumIdAndDateAndTimeAndIdNot(
            Long stadiumId,
            java.time.LocalDate date,
            java.time.LocalTime time,
            Long id);
}
