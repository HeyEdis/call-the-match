package com.example.callthematch.controller;

import com.example.callthematch.dto.request.InputTeamDTO;
import com.example.callthematch.dto.request.InputTeamJoinDTO;
import com.example.callthematch.exception.InviteCodeNotFound;
import com.example.callthematch.exception.TeamNameAlreadyExists;
import com.example.callthematch.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;
    private final MessageSource messageSource;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        addDashboardModel(model);
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

    @PostMapping("/create")
    public String createTeam(
            @Valid @ModelAttribute("inputTeamDto") InputTeamDTO inputTeamDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        if (result.hasErrors()) {
            addDashboardModel(model);
            return "team/dashboard";
        }

        try {
            teamService.createTeam(inputTeamDTO);
        } catch (TeamNameAlreadyExists ex) {
            result.rejectValue("name", "team.name.duplicate",
                    messageSource.getMessage("team.name.duplicate", null, locale));
            addDashboardModel(model);
            return "team/dashboard";
        }

        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("team.create.success", null, locale));
        return "redirect:/team/dashboard";
    }

    @PostMapping("/join")
    public String joinTeam(
            @Valid @ModelAttribute("inputTeamJoinDto") InputTeamJoinDTO inputTeamJoinDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        if (result.hasErrors()) {
            addDashboardModel(model);
            return "team/dashboard";
        }

        try {
            teamService.joinTeamWithInviteCode(inputTeamJoinDTO.inviteCode());
        } catch (InviteCodeNotFound ex) {
            result.rejectValue("inviteCode", "team.inviteCode.invalid",
                    messageSource.getMessage("team.inviteCode.invalid", null, locale));
            addDashboardModel(model);
            return "team/dashboard";
        }

        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("team.join.success", null, locale));
        return "redirect:/team/dashboard";
    }

    private void addDashboardModel(Model model) {
        model.addAttribute("teamList", teamService.getCurrentUserTeams());

        if (!model.containsAttribute("inputTeamDto")) {
            model.addAttribute("inputTeamDto", new InputTeamDTO());
        }
        if (!model.containsAttribute("inputTeamJoinDto")) {
            model.addAttribute("inputTeamJoinDto", new InputTeamJoinDTO());
        }
    }
}
