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

    @GetMapping("/ranking")
    public String showRanking(Model model) {
        model.addAttribute("teamList", teamService.getTop10Teams());
        return "team/ranking";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("teamList", teamService.getCurrentUserTeams());
        model.addAttribute("inputTeamDto", new InputTeamDTO());
        model.addAttribute("inputTeamJoinDto", new InputTeamJoinDTO());
        return "team/dashboard";
    }

    @GetMapping(value = "/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("team", teamService.findById(id));
        model.addAttribute("isOwner", teamService.isCurrentUserOwner(id));
        //model.addAttribute("rank", teamService.getTeamRank(id));
        return "team/show";
    }

    @GetMapping(value = "/{id}/scoreboard")
    public String scoreboard(@PathVariable Long id, Model model) {
        model.addAttribute("scoreboard", teamService.findScoreboardById(id));
        return "team/scoreboard";
    }

    @PostMapping("/{id}/invite-code")
    public String regenerateInviteCode(@PathVariable Long id, RedirectAttributes ra, Locale locale) {
        teamService.regenerateInviteCode(id);
        ra.addFlashAttribute("message",
                messageSource.getMessage("team.inviteCode.regenerated", null, locale));
        return "redirect:/team/{id}";
    }

    @PostMapping("/{id}/members/{memberId}/remove")
    public String removeMember(@PathVariable Long id, @PathVariable Long memberId,
                               RedirectAttributes ra, Locale locale) {
        teamService.removeMember(id, memberId);
        ra.addFlashAttribute("message",
                messageSource.getMessage("team.member.removed", null, locale));
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
            model.addAttribute("teamList", teamService.getCurrentUserTeams());
            model.addAttribute("inputTeamJoinDto", new InputTeamJoinDTO());
            return "team/dashboard";
        }

        try {
            teamService.createTeam(inputTeamDTO);
        } catch (TeamNameAlreadyExists ex) {
            result.rejectValue("name", "team.name.duplicate",
                    messageSource.getMessage("team.name.duplicate", null, locale));
            model.addAttribute("teamList", teamService.getCurrentUserTeams());
            model.addAttribute("inputTeamJoinDto", new InputTeamJoinDTO());
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
            model.addAttribute("teamList", teamService.getCurrentUserTeams());
            model.addAttribute("inputTeamDto", new InputTeamDTO());
            return "team/dashboard";
        }

        try {
            teamService.joinTeamWithInviteCode(inputTeamJoinDTO.inviteCode());
        } catch (InviteCodeNotFound ex) {
            result.rejectValue("inviteCode", "team.inviteCode.invalid",
                    messageSource.getMessage("team.inviteCode.invalid", null, locale));
            model.addAttribute("teamList", teamService.getCurrentUserTeams());
            model.addAttribute("inputTeamDto", new InputTeamDTO());
            return "team/dashboard";
        }

        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("team.join.success", null, locale));
        return "redirect:/team/dashboard";
    }
}
