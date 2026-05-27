package com.example.callthematch.service;

import com.example.callthematch.dto.response.CountryDTO;
import com.example.callthematch.model.Country;
import com.example.callthematch.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    private CountryDTO toDTO(Country c) {
        return new CountryDTO(c.getId(), c.getLandCode(), c.getName());
    }

    public List<CountryDTO> getAllCountries() {
        return countryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::toDTO)
                .toList();
    }
}
