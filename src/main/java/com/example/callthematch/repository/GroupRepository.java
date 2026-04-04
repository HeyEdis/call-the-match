package com.example.callthematch.repository;

import com.example.callthematch.model.CountryGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<CountryGroup, Long> {
}
