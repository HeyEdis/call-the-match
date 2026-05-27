package com.example.callthematch.controller;

import com.example.callthematch.dto.response.MatchRestDTO;
import com.example.callthematch.formatter.DateFormatter;
import com.example.callthematch.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CompetitionRestController {

    private final CompetitionService competitionService;
    private final DateFormatter dateFormatter;

    @GetMapping("{date}/matches")
    public List<MatchRestDTO> getMatchByDate(@PathVariable("date") String date, Locale locale) {
        return competitionService.findRestMatchesByDate(dateFormatter.parse(date, locale));
    }
}
