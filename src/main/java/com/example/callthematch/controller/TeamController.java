package com.example.callthematch.controller;

import com.example.callthematch.dto.request.InputTeamDTO;
import com.example.callthematch.dto.request.InputTeamJoinDTO;
import com.example.callthematch.dto.response.TeamDetailDTO;
import com.example.callthematch.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
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
    public String showDashboard(Model model, Principal principal) {
        model.addAttribute("teamList", teamService.getCurrentUserTeams(principal.getName()));
        model.addAttribute("inputTeamDTO", new InputTeamDTO());
        model.addAttribute("inputTeamJoinDTO", new InputTeamJoinDTO());
        return "team/dashboard";
    }

    @GetMapping(value = "/{id}")
    public String show(@PathVariable Long id, Model model, Principal principal) {
        TeamDetailDTO teamDetail = teamService.findDetailById(id, principal.getName());

        model.addAttribute("team", teamDetail.team());
        model.addAttribute("isOwner", teamDetail.owner());
        model.addAttribute("rank", teamDetail.rank());
        return "team/show";
    }

    @GetMapping(value = "/{id}/scoreboard")
    public String scoreboard(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("scoreboard", teamService.findScoreboardById(id, principal.getName()));
        return "team/scoreboard";
    }

    @PostMapping("/{id}/invite-code")
    public String regenerateInviteCode(@PathVariable Long id, RedirectAttributes ra, Locale locale, Principal principal) {
        teamService.regenerateInviteCode(id,principal.getName() );
        ra.addFlashAttribute("message",
                messageSource.getMessage("team.inviteCode.regenerated", null, locale));
        return "redirect:/team/{id}";
    }

    @PostMapping("/{id}/members/{memberId}/remove")
    public String removeMember(@PathVariable Long id, @PathVariable Long memberId,
                               RedirectAttributes ra, Locale locale, Principal principal) {
        teamService.removeMember(id, memberId, principal.getName());
        ra.addFlashAttribute("message",
                messageSource.getMessage("team.member.removed", null, locale));
        return "redirect:/team/{id}";
    }

    @PostMapping("/create")
    public String createTeam(
            @Valid InputTeamDTO inputTeamDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Principal principal) {
        if (result.hasErrors()) {
            model.addAttribute("teamList", teamService.getCurrentUserTeams(principal.getName()));
            model.addAttribute("inputTeamJoinDTO", new InputTeamJoinDTO());
            return "team/dashboard";
        }

        teamService.createTeam(inputTeamDTO, principal.getName());

        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("team.create.success", null, locale));
        return "redirect:/team/dashboard";
    }

    @PostMapping("/join")
    public String joinTeam(
            @Valid InputTeamJoinDTO inputTeamJoinDTO,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale,
            Principal principal) {
        if (result.hasErrors()) {
            model.addAttribute("teamList", teamService.getCurrentUserTeams(principal.getName()));
            model.addAttribute("inputTeamDTO", new InputTeamDTO());
            return "team/dashboard";
        }

        teamService.joinTeamWithInviteCode(inputTeamJoinDTO.inviteCode(), principal.getName());

        redirectAttributes.addFlashAttribute("message",
                messageSource.getMessage("team.join.success", null, locale));
        return "redirect:/team/dashboard";
    }
}
