package com.example.callthematch.service;

import com.example.callthematch.dto.response.StadiumCapacityDTO;
import com.example.callthematch.dto.response.StadiumDTO;
import com.example.callthematch.exception.StadiumNotFound;
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

    private Stadium findById(Long id)
    {
        return stadiumRepository.findById(id).orElseThrow(() -> new StadiumNotFound(id));
    }

    private StadiumCapacityDTO toCapacityDTO(Stadium s) {
        return new StadiumCapacityDTO(s.getName(),s.getCapacity());
    }

    public StadiumCapacityDTO findCapacityById(Long id) {
        return toCapacityDTO(findById(id));
    }

    public List<StadiumDTO> getAllStadiums() {
        return stadiumRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::toDTO)
                .toList();
    }
}
