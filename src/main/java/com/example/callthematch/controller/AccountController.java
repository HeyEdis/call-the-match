package com.example.callthematch.controller;

import com.example.callthematch.dto.request.InputRegistrationDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccountController {

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
            Model model) {
        if (result.hasErrors()) {
            return "account/register";
        }

        model.addAttribute("registrationValidated", true);
        return "account/register";
    }
}
