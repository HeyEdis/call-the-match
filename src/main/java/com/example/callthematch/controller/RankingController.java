package com.example.callthematch.controller;

import com.example.callthematch.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/rankings")
public class RankingController {

    private final TeamService teamService;

    public String showTeams(Model model) {
        model.addAttribute("teamList", teamService.getTop10Teams());
        return "ranking/list";
    }
}
