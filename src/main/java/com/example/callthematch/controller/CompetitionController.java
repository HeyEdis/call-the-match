package com.example.callthematch.controller;

import com.example.callthematch.dto.request.InputCompetitionDTO;
import com.example.callthematch.dto.request.InputCompetitionResultDTO;
import com.example.callthematch.service.CompetitionService;
import com.example.callthematch.service.CountryService;
import com.example.callthematch.service.StadiumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/competition")
public class CompetitionController {

    private final CompetitionService competitionService;
    private final StadiumService stadiumService;
    private final CountryService countryService;
    private final MessageSource messageSource;

    @GetMapping
    public String showAll(Model model) {
        model.addAttribute("competitionList", competitionService.getAllCompetitions());
        return "competition/list";
    }

    @GetMapping(value = "/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("competition", competitionService.findById(id));
        return "competition/show";
    }

    @GetMapping(value = "/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("stadiums", stadiumService.getAllStadiums());
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("inputCompetitionDto", competitionService.findInputById(id));
        return "competition/edit";
    }

    @GetMapping(value = "/add")
    public String addForm(Model model) {

        model.addAttribute("stadiums", stadiumService.getAllStadiums());
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("inputCompetitionDto", new InputCompetitionDTO());
        return "competition/add";
    }

    @GetMapping(value = "/{id}/result")
    public String resultForm(@PathVariable Long id, Model model) {
        model.addAttribute("competition", competitionService.findById(id));
        model.addAttribute("inputCompetitionResultDto", competitionService.findInputResultById(id));
        return "competition/result";
    }

    @PostMapping(value = "/add")
    public String add(@Valid @ModelAttribute("inputCompetitionDto") InputCompetitionDTO inputCompetitionDTO,
                      BindingResult result, Model model,
                      Locale locale, RedirectAttributes ra) {

        if (result.hasErrors()){
            log.error("Validation failed for new competition: {}", result.getAllErrors());
            String errorMessage = messageSource.getMessage("competition_save_fail", null, locale);
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("stadiums", stadiumService.getAllStadiums());
            model.addAttribute("countries", countryService.getAllCountries());
            return "competition/add";
        }

        Long competitionId = competitionService.add(inputCompetitionDTO);
        log.info("Competition added successfully with id {}", competitionId);

        String successMessage =
                messageSource.getMessage("competition_save_success", new Object[] {competitionId}, locale);
        ra.addFlashAttribute("successMessage", successMessage);

        return "redirect:/home";
    }

    @PostMapping(value = "/edit/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("inputCompetitionDto") InputCompetitionDTO inputCompetitionDTO,
                         BindingResult result, Model model,
                         Locale locale, RedirectAttributes ra) {

        if (result.hasErrors()){
            log.error("Validation failed for competition {}: {}", id, result.getAllErrors());
            String errorMessage = messageSource.getMessage("competition_save_fail", null, locale);
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("stadiums", stadiumService.getAllStadiums());
            model.addAttribute("countries", countryService.getAllCountries());
            return "competition/edit";
        }

        Long competitionId = competitionService.update(inputCompetitionDTO);
        log.info("Competition updated successfully with id {}", competitionId);

        String successMessage =
                messageSource.getMessage("competition_update_success", new Object[] {competitionId}, locale);
        ra.addFlashAttribute("successMessage", successMessage);

        return "redirect:/competition/" + competitionId;
    }

    @PostMapping(value = "/{id}/result")
    public String updateResult(@PathVariable Long id,
                               @Valid @ModelAttribute("inputCompetitionResultDto")
                               InputCompetitionResultDTO inputCompetitionResultDTO,
                               BindingResult result, Model model,
                               Locale locale, RedirectAttributes ra) {

        if (result.hasErrors()) {
            log.error("Validation failed for official result of competition {}: {}", id, result.getAllErrors());
            String errorMessage = messageSource.getMessage("competition_result_save_fail", null, locale);
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("competition", competitionService.findById(id));
            return "competition/result";
        }

        Long competitionId = competitionService.updateResult(id, inputCompetitionResultDTO);
        log.info("Official result updated successfully for competition {}", competitionId);

        String successMessage =
                messageSource.getMessage("competition_result_save_success", new Object[] {competitionId}, locale);
        ra.addFlashAttribute("successMessage", successMessage);

        return "redirect:/competition/" + competitionId;
    }
}
