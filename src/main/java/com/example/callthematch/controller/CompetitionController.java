package com.example.callthematch.controller;

import com.example.callthematch.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/competition")
public class CompetitionController {

    private final CompetitionService competitionService;

    @GetMapping(value="/list")
    public String listCompetitions(Model model) {
        model.addAttribute("competitionList", competitionService.getAllCompetitions());
        return "competition/list";
    }
}
