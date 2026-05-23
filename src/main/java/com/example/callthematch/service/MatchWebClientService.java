package com.example.callthematch.service;

import com.example.callthematch.dto.response.MatchRestDTO;
import com.example.callthematch.dto.response.StadiumCapacityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MatchWebClientService {

    private final WebClient webClient;

    public Flux<MatchRestDTO> getMatchesByDate(LocalDate date) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/matches")
                        .queryParam("date", date)
                        .build())
                .retrieve()
                .bodyToFlux(MatchRestDTO.class);
    }

    public Mono<StadiumCapacityDTO> getStadiumCapacity(Long stadiumId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/stadiums/{id}/capacity")
                        .build(stadiumId))
                .retrieve()
                .bodyToMono(StadiumCapacityDTO.class);
    }
}
