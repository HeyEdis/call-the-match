package com.example.callthematch.controller;

import com.example.callthematch.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController  {

    private final CompetitionService competitionService;

    @GetMapping
    public String showHome(Model model) {
        model.addAttribute("competitionList", competitionService.getAllCompetitions());
        return "home";
    }
}
