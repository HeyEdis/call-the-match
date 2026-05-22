package com.example.callthematch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class AccessSecurityMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void guestCanOpenPublicAccessScreens() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/ranking"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/competition/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @Test
    void guestIsRedirectedFromUserTeamDashboard() throws Exception {
        mockMvc.perform(get("/team/dashboard"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void userCanOpenUserRoutesButNotAdminMatchManagement() throws Exception {
        mockMvc.perform(get("/team/dashboard").with(user("user@example.com").roles("USER")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/competition/add").with(user("user@example.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanOpenMatchManagementButNotParticipationRoutes() throws Exception {
        mockMvc.perform(get("/competition/add").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/team/dashboard").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/predictions").with(user("admin@example.com").roles("ADMIN")))
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
                        "inputRegistrationDto",
                        "firstName",
                        "lastName",
                        "userName",
                        "email",
                        "password"));
    }
}
