package com.example.callthematch.controller;

import com.example.callthematch.dto.request.InputPredictionDTO;
import com.example.callthematch.service.CompetitionService;
import com.example.callthematch.service.PredictionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/predictions")
public class PredictionController {

    private final CompetitionService competitionService;
    private final PredictionService predictionService;

    @GetMapping("/{competitionId}")
    public String form(@PathVariable Long competitionId, Model model) {
        model.addAttribute("competition", competitionService.findById(competitionId));
        model.addAttribute("inputPredictionDto",
                predictionService.findCurrentUserInputByCompetitionId(competitionId));
        return "prediction/form";
    }

    @PostMapping("/{competitionId}")
    public String save(@PathVariable Long competitionId,
                       @Valid @ModelAttribute("inputPredictionDto") InputPredictionDTO inputPredictionDTO,
                       BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("competition", competitionService.findById(competitionId));
            return "prediction/form";
        }

        predictionService.saveCurrentUserPrediction(competitionId, inputPredictionDTO);

        return "redirect:/competition/{competitionId}";
    }
}
