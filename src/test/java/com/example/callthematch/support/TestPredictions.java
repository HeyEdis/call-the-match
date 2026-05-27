package com.example.callthematch.support;

import com.example.callthematch.dto.request.InputPredictionDTO;
import com.example.callthematch.dto.response.PredictionOverviewDTO;

import java.util.List;

public final class TestPredictions {

    private TestPredictions() {
    }

    public static InputPredictionDTO inputPredictionDTO() {
        return new InputPredictionDTO(2, 1);
    }

    public static List<PredictionOverviewDTO> predictionOverviewDtos() {
        return List.of(new PredictionOverviewDTO(3L, "Brazil", "Morocco", 4, 3, 2, 1));
    }
}
