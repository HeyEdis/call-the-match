package com.example.callthematch.controller;

import com.example.callthematch.dto.request.InputPredictionDTO;
import com.example.callthematch.dto.response.CompetitionDTO;
import com.example.callthematch.exception.PredictionCutoffPassed;
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

    private final PredictionService predictionService;
    private final MessageSource messageSource;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("predictionList", predictionService.getCurrentUserPredictions());
        return "prediction/list";
    }

    @GetMapping("/{competitionId}")
    public String form(@PathVariable Long competitionId, Model model) {
        addCompetitionModel(competitionId, model);
        model.addAttribute("inputPredictionDTO", predictionService.findCurrentUserInputByCompetitionId(competitionId));
        return "prediction/form";
    }

    @PostMapping("/{competitionId}")
    public String save(@PathVariable Long competitionId,
                       @Valid InputPredictionDTO inputPredictionDTO,
                       BindingResult result, Model model, Locale locale) {

        addCompetitionModel(competitionId, model);

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

    private void addCompetitionModel(Long competitionId, Model model) {
        model.addAttribute("competition", predictionService.findCompetitionDTOById(competitionId));
        model.addAttribute("cutoffPassed", predictionService.isCutoffPassed(competitionId));
    }
}
