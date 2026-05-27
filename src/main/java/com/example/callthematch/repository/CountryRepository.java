package com.example.callthematch.repository;

import com.example.callthematch.model.Country;
import com.example.callthematch.model.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Long> {

    List<Country> findAllByOrderByNameAsc();
}