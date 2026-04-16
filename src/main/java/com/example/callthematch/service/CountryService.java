package com.example.callthematch.service;

import com.example.callthematch.dto.response.CompetitionDTO;
import com.example.callthematch.dto.response.CountryDTO;
import com.example.callthematch.exception.CompetitionNotFound;
import com.example.callthematch.exception.CountryNotFound;
import com.example.callthematch.model.Competition;
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


    private Country findCountryById(Long id)
    {
        return countryRepository.findById(id).orElseThrow(() -> new CountryNotFound(id));
    }

    public CountryDTO findById(Long id) {
        return toDTO(findCountryById(id));
    }

    public List<CountryDTO> getAllCountries() {
        return countryRepository.findAll()
                .stream()
                .map(c -> toDTO(c))
                .toList();
    }
}
