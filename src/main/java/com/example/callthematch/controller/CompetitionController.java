package com.example.callthematch.controller;

import com.example.callthematch.dto.request.InputCompetitionDTO;
import com.example.callthematch.dto.response.CountryDTO;
import com.example.callthematch.model.Competition;
import com.example.callthematch.model.Country;
import com.example.callthematch.model.Stadium;
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

    @GetMapping(value = "/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("competition", competitionService.findById(id));
        return "competition/show";
    }

    @GetMapping(value = "/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("competition", competitionService.findById(id));
        return "competition/edit";
    }

    @GetMapping(value = "/add")
    public String addForm(Model model) {

        Competition competition = new Competition();
        model.addAttribute("stadiums", stadiumService.getAllStadiums());
        model.addAttribute("countries", countryService.getAllCountries());
        model.addAttribute("inputCompetitionDto", competition);
        return "competition/add";
    }

    @PostMapping(value = "/add")
    public String add(@Valid InputCompetitionDTO inputCompetitionDTO, BindingResult result, Model model,
                      Locale locale, RedirectAttributes ra) {

        if (result.hasErrors()){
            log.error("Validation failed for new competition: {}", result.getAllErrors());
            String errorMessage = messageSource.getMessage("competition_save_fail", null, locale);
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("stadiums", stadiumService.getAllStadiums());
            model.addAttribute("countries", countryService.getAllCountries());
            return "competition/add";
        }

        Country teamA = countryService.findById(inputCompetitionDTO.teamA().getId());
        Country teamB = countryService.findById(inputCompetitionDTO.teamA().getId());
        Stadium stadium = stadiumService.findById(inputCompetitionDTO.stadium().getId());

        //Competition competition = inputCompetitionDTO


        competitionService.add(inputCompetitionDTO);
        log.info("Competition added successfully with id {}", inputCompetitionDTO.id());

        String successMessage =
                messageSource.getMessage("competition_save_success", new Object[] {inputCompetitionDTO.id()}, locale);
        ra.addFlashAttribute("successMessage", successMessage);

        return "redirect:/home";
    }
}
