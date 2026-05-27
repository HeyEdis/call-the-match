package com.example.callthematch.controller;

import com.example.callthematch.dto.request.InputPredictionDTO;
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

import java.security.Principal;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/predictions")
public class PredictionController {

    private final PredictionService predictionService;
    private final MessageSource messageSource;
    private final CompetitionService competitionService;

    @GetMapping
    public String list(Model model, Principal principal) {
        model.addAttribute("predictionList", predictionService.getCurrentUserPredictions(principal.getName()));
        return "prediction/list";
    }

    @GetMapping("/{competitionId}")
    public String form(@PathVariable Long competitionId, Model model, Principal principal) {
        model.addAttribute("competition", competitionService.findById(competitionId));
        model.addAttribute("cutoffPassed", predictionService.isCutoffPassed(competitionId));
        model.addAttribute("inputPredictionDTO", predictionService.findPredictionStatusByCompetitionIdAndEmail(competitionId, principal.getName()));
        return "prediction/form";
    }

    @PostMapping("/{competitionId}")
    public String save(@PathVariable Long competitionId,
                       @Valid InputPredictionDTO inputPredictionDTO,
                       BindingResult result, Model model, Locale locale, Principal principal) {

        if (result.hasErrors()) {
            model.addAttribute("competition", competitionService.findById(competitionId));
            model.addAttribute("cutoffPassed", predictionService.isCutoffPassed(competitionId));
            return "prediction/form";
        }

        try {
            predictionService.saveCurrentUserPrediction(competitionId, inputPredictionDTO, principal.getName());
        } catch (PredictionCutoffPassed ex) {
            model.addAttribute("competition", competitionService.findById(competitionId));;
            model.addAttribute("cutoffPassed", predictionService.isCutoffPassed(competitionId));
            model.addAttribute("errorMessage",
                    messageSource.getMessage("prediction.cutoff.passed", null, locale));
            return "prediction/form";
        }

        return "redirect:/competition/{competitionId}";
    }
}
