package com.example.callthematch.controller;

import com.example.callthematch.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/dashboard")
    public String showHome(Model model) {
        model.addAttribute("teamList", teamService.getAllTeams());
        return "team/dashboard";
    }

    @GetMapping(value = "/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("team", teamService.findById(id));
        return "team/show";
    }


}
