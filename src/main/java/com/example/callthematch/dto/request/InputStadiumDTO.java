package com.example.callthematch.dto.request;

import com.example.callthematch.model.Location;

public record InputStadiumDTO(Long id, Location location, String name, Integer code, Integer capacity) {
}
