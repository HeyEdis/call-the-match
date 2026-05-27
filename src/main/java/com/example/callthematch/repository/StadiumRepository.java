package com.example.callthematch.repository;

import com.example.callthematch.model.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StadiumRepository extends JpaRepository<Stadium, Long> {

    List<Stadium> findAllByOrderByNameAsc();
}
