package com.example.callthematch.repository;

import com.example.callthematch.model.Prediction;
import com.example.callthematch.model.Competition;
import com.example.callthematch.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    Optional<Prediction> findByUserAndCompetition(MyUser user, Competition competition);

    List<Prediction> findAllByCompetition(Competition competition);

    List<Prediction> findAllByUser(MyUser user);
}
