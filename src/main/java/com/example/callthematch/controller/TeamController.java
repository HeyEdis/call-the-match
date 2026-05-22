package com.example.callthematch.controller;

import com.example.callthematch.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("teamList", teamService.getCurrentUserTeams());
        return "team/dashboard";
    }

    @GetMapping(value = "/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("team", teamService.findById(id));
        return "team/show";
    }

    @PostMapping("/{id}/invite-code")
    public String regenerateInviteCode(@PathVariable Long id, RedirectAttributes ra) {
        teamService.regenerateInviteCode(id);
        ra.addFlashAttribute("message", "Invite code regenerated");
        return "redirect:/team/{id}";
    }

    @PostMapping("/join")
    public String joinTeam(@RequestParam String inviteCode, RedirectAttributes ra) {
        teamService.joinTeamWithInviteCode(inviteCode);
        ra.addFlashAttribute("message", "Team joined");
        return "redirect:/team/dashboard";
    }
}
