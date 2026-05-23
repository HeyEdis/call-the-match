package com.example.callthematch.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRegisterReturnsFormWithModel() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/register"))
                .andExpect(model().attributeExists("inputRegistrationDto"));
    }

    @Test
    void getLoginReturnsLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/login"));
    }

    @Test
    void postRegisterWithValidDataRedirectsToLogin() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "Test")
                        .param("lastName", "User")
                        .param("userName", "testuser_unique1")
                        .param("email", "unique_register1@example.com")
                        .param("password", "ValidPass1!"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void postRegisterWithInvalidDataReturnsRegisterViewWithFieldErrors() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("userName", "")
                        .param("email", "not-an-email")
                        .param("password", "short"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/register"))
                .andExpect(model().attributeHasFieldErrors(
                        "inputRegistrationDto",
                        "firstName", "lastName", "userName", "email", "password"));
    }

    @Test
    void postRegisterWithoutCsrfTokenReturnsForbidden() throws Exception {
        mockMvc.perform(post("/register")
                        .param("firstName", "Test")
                        .param("lastName", "User")
                        .param("userName", "testuser")
                        .param("email", "test@example.com")
                        .param("password", "ValidPass1!"))
                .andExpect(status().isForbidden());
    }
}

