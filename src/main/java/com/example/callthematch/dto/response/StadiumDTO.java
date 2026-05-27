package com.example.callthematch.dto.response;

import com.example.callthematch.model.Location;

public record StadiumDTO(Long id, Location location, String name, Integer code, Integer capacity) {
}
