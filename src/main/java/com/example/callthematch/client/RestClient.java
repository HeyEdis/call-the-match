package com.example.callthematch.client;

import com.example.callthematch.dto.response.MatchRestDTO;
import com.example.callthematch.dto.response.StadiumCapacityDTO;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public class RestClient {

    private static final String SERVER_URI = "http://localhost:8080";

    private final WebClient webClient = WebClient.builder()
            .baseUrl(SERVER_URI)
            .build();

    public RestClient() {
        System.out.println("------- GET MATCHES BY DATE -------");
        getMatchesByDate(LocalDate.of(2026, 5, 20))
                .doOnNext(this::printMatchData)
                .blockLast();

        System.out.println("\n------- GET STADIUM CAPACITY -------");
        getStadiumCapacity(1L)
                .doOnNext(this::printStadiumCapacity)
                .doOnError(e -> System.out.println(e.getMessage()))
                .onErrorResume(e -> Mono.empty())
                .block();

        System.out.println("\n------- GET UNKNOWN STADIUM CAPACITY -------");
        getStadiumCapacity(999L)
                .doOnNext(this::printStadiumCapacity)
                .doOnError(e -> System.out.println(e.getMessage()))
                .onErrorResume(e -> Mono.empty())
                .block();
    }

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

    private void printMatchData(MatchRestDTO match) {
        System.out.printf(
                "ID=%s, %s vs %s, Date=%s, Time=%s, Stadium=%s, Score=%s-%s%n",
                match.id(),
                match.teamAName(),
                match.teamBName(),
                match.date(),
                match.time(),
                match.stadiumName(),
                match.scoreA(),
                match.scoreB());
    }

    private void printStadiumCapacity(StadiumCapacityDTO stadium) {
        System.out.printf(
                "ID=%s, Stadium=%s, Capacity=%s%n",
                stadium.id(),
                stadium.name(),
                stadium.capacity());
    }
}
