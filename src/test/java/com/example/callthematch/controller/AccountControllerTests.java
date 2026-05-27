package com.example.callthematch.controller;

import com.example.callthematch.advice.CompetitionValidatorAdvice;
import com.example.callthematch.advice.TeamValidatorAdvice;
import com.example.callthematch.config.SecurityConfig;
import com.example.callthematch.dto.request.InputRegistrationDTO;
import com.example.callthematch.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(
        controllers = AccountController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        CompetitionValidatorAdvice.class,
                        TeamValidatorAdvice.class
                }))
@AutoConfigureMockMvc(addFilters = true)
@Import(SecurityConfig.class)
@ImportAutoConfiguration({
        SecurityAutoConfiguration.class,
        ServletWebSecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
})
class AccountControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void registerViewExists() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/register"))
                .andExpect(model().attributeExists("inputRegistrationDTO"));
    }

    @Test
    void loginViewExists() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/login"));
    }

    @Test
    void validRegistrationRedirectsToLoginView() throws Exception {
        InputRegistrationDTO inputRegistrationDTO = new InputRegistrationDTO(
                "Test",
                "User",
                "testuser_unique1",
                "unique_register1@example.com",
                "ValidPass1!");

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .flashAttr("inputRegistrationDTO", inputRegistrationDTO))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));

        verify(userService).register(inputRegistrationDTO);
    }

    @Test
    void invalidRegistrationReturnsRegisterViewWithFieldErrors() throws Exception {
        InputRegistrationDTO inputRegistrationDTO = new InputRegistrationDTO(
                "",
                "",
                "",
                "not-an-email",
                "short");

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .flashAttr("inputRegistrationDTO", inputRegistrationDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("account/register"))
                .andExpect(model().attributeHasFieldErrors(
                        "inputRegistrationDTO",
                        "firstName", "lastName", "userName", "email", "password"));

        verify(userService, never()).register(any(InputRegistrationDTO.class));
    }

    @Test
    void forbiddenToRegisterWithoutCSRFToken() throws Exception {
        mockMvc.perform(post("/register")
                        .param("firstName", "Test")
                        .param("lastName", "User")
                        .param("userName", "testuser")
                        .param("email", "test@example.com")
                        .param("password", "ValidPass1!"))
                .andExpect(status().isForbidden());
    }
}
