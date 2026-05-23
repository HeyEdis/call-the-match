package com.example.callthematch.controller;

import com.example.callthematch.dto.response.StadiumCapacityDTO;
import com.example.callthematch.service.StadiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/stadiums")
public class StadiumRestController {

    private final StadiumService stadiumService;

    @GetMapping("/{id}/capacity")
    public StadiumCapacityDTO getStadiumCapacity(@PathVariable Long id) {
        return stadiumService.findCapacityById(id);
    }
}
