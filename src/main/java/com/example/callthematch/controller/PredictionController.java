package com.example.callthematch.controller;

import com.example.callthematch.dto.request.InputPredictionDTO;
import com.example.callthematch.dto.response.CompetitionDTO;
import com.example.callthematch.exception.PredictionCutoffPassed;
import com.example.callthematch.service.CompetitionService;
import com.example.callthematch.service.PredictionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/predictions")
public class PredictionController {

    private final CompetitionService competitionService;
    private final PredictionService predictionService;
    private final MessageSource messageSource;

    @ModelAttribute("competition")
    public CompetitionDTO populateCompetition(@PathVariable("competitionId") Long competitionId) {
        return competitionService.findById(competitionId);
    }

    @ModelAttribute("cutoffPassed")
    public boolean populateCutoffPassed(@PathVariable("competitionId") Long competitionId) {
        return predictionService.isCutoffPassed(competitionId);
    }

    @GetMapping("/{competitionId}")
    public String form(@PathVariable Long competitionId, Model model) {
        model.addAttribute("inputPredictionDto",
                predictionService.findCurrentUserInputByCompetitionId(competitionId));
        return "prediction/form";
    }

    @PostMapping("/{competitionId}")
    public String save(@PathVariable Long competitionId,
                       @Valid @ModelAttribute("inputPredictionDto") InputPredictionDTO inputPredictionDTO,
                       BindingResult result, Model model, Locale locale) {

        if (result.hasErrors()) {
            return "prediction/form";
        }

        try {
            predictionService.saveCurrentUserPrediction(competitionId, inputPredictionDTO);
        } catch (PredictionCutoffPassed ex) {
            model.addAttribute("errorMessage",
                    messageSource.getMessage("prediction.cutoff.passed", null, locale));
            return "prediction/form";
        }

        return "redirect:/competition/{competitionId}";
    }
}
