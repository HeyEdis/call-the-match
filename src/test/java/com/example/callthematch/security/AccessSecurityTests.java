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

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.testSecurityContext;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccessSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithAnonymousUser
    void guestRootRedirectsToHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/home"))
                .andExpect(unauthenticated());
    }

    @Test
    @WithMockUser(username = "user1@example.com", roles = "USER")
    void userRootRedirectsToHome() throws Exception {
        mockMvc.perform(get("/")
                        .with(testSecurityContext()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void adminRootRedirectsToHome() throws Exception {
        mockMvc.perform(get("/")
                        .with(testSecurityContext()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/home"));
    }

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
    void guestCanChangeLocale() throws Exception {
        mockMvc.perform(get("/changeLocale")
                        .param("lang", "nl")
                        .header("Referer", "/home"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/home"))
                .andExpect(unauthenticated());
    }

    @Test
    @WithAnonymousUser
    void guestCanLoadStaticJavaScriptAssets() throws Exception {
        mockMvc.perform(get("/js/matchStadiumChecksum.js"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void guestCanLoadFavicon() throws Exception {
        mockMvc.perform(get("/favicon.ico"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", startsWith("image/")))
                .andExpect(unauthenticated());
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
        mockMvc.perform(get("/team/dashboard")
                        .with(testSecurityContext()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/predictions/3")
                        .with(testSecurityContext()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1@example.com", roles = "USER")
    void userCannotOpenAdminMatchManagement() throws Exception {
        mockMvc.perform(get("/competition/add")
                        .with(testSecurityContext()))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/competition/edit/3")
                        .with(testSecurityContext())
                        .with(csrf()))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/competition/3/result")
                        .with(testSecurityContext())
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void loggedInUserKeepsEmailPrincipalForCurrentUserRoutes() throws Exception {
        MvcResult login = mockMvc.perform(formLogin("/login")
                        .userParameter("email")
                        .user("user1@example.com")
                        .password("password"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/home"))
                .andExpect(authenticated().withUsername("user1@example.com"))
                .andReturn();

        mockMvc.perform(get("/team/dashboard")
                        .session((MockHttpSession) login.getRequest().getSession(false)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void adminCanOpenMatchManagementRoutes() throws Exception {
        mockMvc.perform(get("/competition/add")
                        .with(testSecurityContext()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/competition/edit/3")
                        .with(testSecurityContext()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/competition/3/result")
                        .with(testSecurityContext()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void adminCannotOpenTeamRoutes() throws Exception {
        mockMvc.perform(get("/team/dashboard")
                        .with(testSecurityContext()))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/team/1/scoreboard")
                        .with(testSecurityContext()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void adminCannotOpenPredictionRoutes() throws Exception {
        mockMvc.perform(get("/predictions")
                        .with(testSecurityContext()))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/predictions/3")
                        .with(testSecurityContext()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void adminCannotSubmitTeamFlow() throws Exception {
        mockMvc.perform(post("/team/create")
                        .with(testSecurityContext())
                        .with(csrf())
                        .param("name", "Admin Team"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void guestCanOpenPublicRestEndpoint() throws Exception {
        mockMvc.perform(get("/api/2026-05-20/matches"))
                .andExpect(status().isOk())
                .andExpect(unauthenticated());
    }

    @Test
    @WithAnonymousUser
    void guestErrorPagePathsAreNotInterceptedByLogin() throws Exception {
        mockMvc.perform(get("/403"))
                .andExpect(status().isNotFound())
                .andExpect(unauthenticated());

        mockMvc.perform(get("/404"))
                .andExpect(status().isNotFound())
                .andExpect(unauthenticated());

        mockMvc.perform(get("/500"))
                .andExpect(status().isNotFound())
                .andExpect(unauthenticated());
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
                        .with(testSecurityContext())
                        .param("name", "SomeTeam"))
                .andExpect(status().isForbidden());
    }
}
