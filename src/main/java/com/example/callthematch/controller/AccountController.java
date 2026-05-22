package com.example.callthematch.controller;

import com.example.callthematch.dto.request.InputRegistrationDTO;
import com.example.callthematch.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "account/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("inputRegistrationDto", new InputRegistrationDTO());
        return "account/register";
    }

    @PostMapping("/register")
    public String validateRegistration(
            @Valid @ModelAttribute("inputRegistrationDto") InputRegistrationDTO inputRegistrationDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "account/register";
        }

        userService.register(inputRegistrationDto);
        redirectAttributes.addFlashAttribute("registrationSuccess", true);
        return "redirect:/login";
    }
}
