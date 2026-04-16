package com.example.callthematch.service;

import com.example.callthematch.dto.response.CompetitionDTO;
import com.example.callthematch.dto.response.StadiumDTO;
import com.example.callthematch.exception.CompetitionNotFound;
import com.example.callthematch.exception.StadiumNotFound;
import com.example.callthematch.model.Competition;
import com.example.callthematch.model.Stadium;
import com.example.callthematch.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StadiumService {

    private final StadiumRepository stadiumRepository;

    private StadiumDTO toDTO(Stadium s) {
        return new StadiumDTO(s.getId(), s.getLocation(),s.getName(),s.getCode(),s.getCapacity());
    }

    private Stadium findStadiumById(Long id)
    {
        return stadiumRepository.findById(id).orElseThrow(() -> new StadiumNotFound(id));
    }

    public StadiumDTO findById(Long id) {
        return toDTO(findStadiumById(id));
    }

    public List<StadiumDTO> getAllStadiums() {
        return stadiumRepository.findAll()
                .stream()
                .map(s -> toDTO(s))
                .toList();
    }
}
