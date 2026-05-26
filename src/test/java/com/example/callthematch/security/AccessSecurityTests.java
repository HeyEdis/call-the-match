package com.example.callthematch.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class AccessSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithAnonymousUser
    void guestCanOpenPublicAccessScreens() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/team/ranking"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/competition/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void guestIsRedirectedFromUserTeamDashboard() throws Exception {
        mockMvc.perform(get("/team/dashboard"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(get("/predictions/3"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(get("/team/1/scoreboard"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithAnonymousUser
    void guestIsRedirectedFromAdminMatchWrites() throws Exception {
        mockMvc.perform(post("/competition/add").with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(post("/competition/edit/3").with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(post("/competition/3/result").with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "user1@example.com", roles = "USER")
    void userCanOpenUserRoutes() throws Exception {
        mockMvc.perform(get("/team/dashboard").with(user("user1@example.com").roles("USER")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/predictions/3").with(user("user1@example.com").roles("USER")))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1@example.com", roles = "USER")
    void userCannotOpenAdminMatchManagement() throws Exception {
        mockMvc.perform(get("/competition/add").with(user("user1@example.com").roles("USER")))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/competition/edit/3")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/competition/3/result")
                        .with(user("user1@example.com").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void loggedInUserKeepsEmailPrincipalForCurrentUserRoutes() throws Exception {
        MvcResult login = mockMvc.perform(formLogin("/login")
                        .userParameter("email")
                        .user("user1@example.com")
                        .password("password"))
                .andExpect(authenticated().withUsername("user1@example.com"))
                .andReturn();

        mockMvc.perform(get("/team/dashboard")
                        .session((MockHttpSession) login.getRequest().getSession(false)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void adminCanOpenMatchManagementRoutes() throws Exception {
        mockMvc.perform(get("/competition/add").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/competition/edit/3").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/competition/3/result").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void adminCannotOpenTeamRoutes() throws Exception {
        mockMvc.perform(get("/team/dashboard").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/team/1/scoreboard").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void adminCannotOpenPredictionRoutes() throws Exception {
        mockMvc.perform(get("/predictions").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/predictions/3").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidRegistrationReturnsFieldErrors() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("userName", "")
                        .param("email", "invalid")
                        .param("password", "short"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/register"))
                .andExpect(model().attributeHasFieldErrors(
                        "inputRegistrationDTO",
                        "firstName",
                        "lastName",
                        "userName",
                        "email",
                        "password"));
    }

    @Test
    void incorrectCredentialsRedirectToLoginError() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .userParameter("email")
                        .user("user1@example.com")
                        .password("wrongpassword"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void logoutRedirectsToLoginLogout() throws Exception {
        mockMvc.perform(logout("/logout"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    @WithMockUser(username = "user1@example.com", roles = "USER")
    void postWithoutCsrfReturnsForbidden() throws Exception {
        mockMvc.perform(post("/team/create")
                        .with(user("user1@example.com").roles("USER"))
                        .param("name", "SomeTeam"))
                .andExpect(status().isForbidden());
    }
}
