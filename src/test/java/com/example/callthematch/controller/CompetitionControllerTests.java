package com.example.callthematch.controller;

import com.example.callthematch.advice.CompetitionValidatorAdvice;
import com.example.callthematch.advice.TeamValidatorAdvice;
import com.example.callthematch.config.SecurityConfig;
import com.example.callthematch.dto.request.InputCompetitionDTO;
import com.example.callthematch.dto.request.InputCompetitionResultDTO;
import com.example.callthematch.dto.response.CompetitionDTO;
import com.example.callthematch.dto.request.InputPredictionDTO;
import com.example.callthematch.exception.CompetitionNotFound;
import com.example.callthematch.service.CompetitionService;
import com.example.callthematch.service.CountryService;
import com.example.callthematch.service.PredictionService;
import com.example.callthematch.service.StadiumService;
import com.example.callthematch.validator.CompetitionValidator;
import org.junit.jupiter.api.BeforeEach;
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


import java.util.Optional;

import static com.example.callthematch.support.TestCompetitions.competitionDto;
import static com.example.callthematch.support.TestCompetitions.countryDtos;
import static com.example.callthematch.support.TestCompetitions.inputCompetitionDTO;
import static com.example.callthematch.support.TestCompetitions.stadiumDtos;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(
        controllers = CompetitionController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = TeamValidatorAdvice.class))
@AutoConfigureMockMvc(addFilters = true)
@Import({SecurityConfig.class, CompetitionValidatorAdvice.class})
@ImportAutoConfiguration({
        SecurityAutoConfiguration.class,
        ServletWebSecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
})
class CompetitionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompetitionService competitionService;

    @MockitoBean
    private StadiumService stadiumService;

    @MockitoBean
    private CountryService countryService;

    @MockitoBean
    private PredictionService predictionService;

    @MockitoBean
    private CompetitionValidator competitionValidator;

    @BeforeEach
    void setUp() {
        when(countryService.getAllCountries()).thenReturn(countryDtos());
        when(stadiumService.getAllStadiums()).thenReturn(stadiumDtos());
        when(competitionValidator.supports(InputCompetitionDTO.class)).thenReturn(true);
    }

    @Test
    void guestIsRedirectedToLoginForCompetitionList() throws Exception {
        mockMvc.perform(get("/competition"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(competitionService, never()).getAllCompetitions();
    }

    @Test
    void guestCanAccessCompetitionDetail() throws Exception {
        CompetitionDTO competition = competitionDto(1L);
        when(competitionService.findById(1L)).thenReturn(competition);

        mockMvc.perform(get("/competition/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/show"))
                .andExpect(model().attribute("competition", competition))
                .andExpect(content().string(not(containsString("/competition/edit/1"))))
                .andExpect(content().string(not(containsString("Edit match"))))
                .andExpect(content().string(not(containsString("Your prediction"))));

        verify(competitionService).findById(1L);
    }

    @Test
    void adminSeesEditControlOnCompetitionDetail() throws Exception {
        CompetitionDTO competition = competitionDto(1L);
        when(competitionService.findById(1L)).thenReturn(competition);

        mockMvc.perform(get("/competition/1")
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/show"))
                .andExpect(content().string(containsString("/competition/edit/1")))
                .andExpect(content().string(containsString("Edit match")));

        verify(competitionService).findById(1L);
    }

    @Test
    void userSeesOwnPredictionOnCompetitionDetail() throws Exception {
        CompetitionDTO competition = competitionDto(1L);
        when(competitionService.findById(1L)).thenReturn(competition);
        when(predictionService.findPredictionStatusByCompetitionIdAndEmail(1L, "user1@example.com"))
                .thenReturn(Optional.of(new InputPredictionDTO(2, 1)));

        mockMvc.perform(get("/competition/1")
                        .with(user("user1@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/show"))
                .andExpect(model().attributeExists("currentUserPrediction"))
                .andExpect(content().string(containsString("Your prediction")))
                .andExpect(content().string(containsString("2 - 1")))
                .andExpect(content().string(containsString("/predictions/1")));

        verify(competitionService).findById(1L);
        verify(predictionService).findPredictionStatusByCompetitionIdAndEmail(1L, "user1@example.com");
    }

    @Test
    void userWithoutPredictionDoesNotSeePredictionStatusOnCompetitionDetail() throws Exception {
        CompetitionDTO competition = competitionDto(1L);
        when(competitionService.findById(1L)).thenReturn(competition);
        when(predictionService.findPredictionStatusByCompetitionIdAndEmail(1L, "user1@example.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/competition/1")
                        .with(user("user1@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("currentUserPrediction"))
                .andExpect(content().string(not(containsString("Your prediction"))))
                .andExpect(content().string(not(containsString("2 - 1"))))
                .andExpect(content().string(containsString("/predictions/1")));

        verify(competitionService).findById(1L);
        verify(predictionService).findPredictionStatusByCompetitionIdAndEmail(1L, "user1@example.com");
    }

    @Test
    void adminDoesNotReceivePredictionStatusOnCompetitionDetail() throws Exception {
        CompetitionDTO competition = competitionDto(1L);
        when(competitionService.findById(1L)).thenReturn(competition);

        mockMvc.perform(get("/competition/1")
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("currentUserPrediction"))
                .andExpect(content().string(not(containsString("Your prediction"))))
                .andExpect(content().string(not(containsString("/predictions/1"))));

        verify(competitionService).findById(1L);

    }

    @Test
    void userDoesNotSeeEditControlOnCompetitionDetail() throws Exception {
        CompetitionDTO competition = competitionDto(1L);
        when(competitionService.findById(1L)).thenReturn(competition);

        mockMvc.perform(get("/competition/1")
                        .with(user("user1@example.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/show"))
                .andExpect(content().string(not(containsString("/competition/edit/1"))))
                .andExpect(content().string(not(containsString("Edit match"))));

        verify(competitionService).findById(1L);
    }

    @Test
    void invalidCompetitionIdLookup() throws Exception {
        when(competitionService.findById(999999L)).thenThrow(new CompetitionNotFound(999999L));

        mockMvc.perform(get("/competition/999999"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));

        verify(competitionService).findById(999999L);
    }

    @Test
    void competitionTypeMismatchLookup() throws Exception {
        mockMvc.perform(get("/competition/not-a-number"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));
    }

    @Test
    void userIsForbiddenOnAdminCompetitionAddRoute() throws Exception {
        mockMvc.perform(get("/competition/add")
                        .with(user("user1@example.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminAddFormExposesModelAndRendersStadiumsCorrectly() throws Exception {
        mockMvc.perform(get("/competition/add").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/add"))
                .andExpect(model().attributeExists("countries", "stadiums", "inputCompetitionDTO"))
                .andExpect(content().string(containsString("data-code=\"1001\"")));
    }

    @Test
    void adminEditFormExposesCorrectModelAttributes() throws Exception {
        InputCompetitionDTO inputCompetitionDTO = inputCompetitionDTO(3L);
        when(competitionService.findInputById(3L)).thenReturn(inputCompetitionDTO);

        mockMvc.perform(get("/competition/edit/3").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/edit"))
                .andExpect(model().attribute("inputCompetitionDTO", inputCompetitionDTO))
                .andExpect(model().attributeExists("countries", "stadiums"));

        verify(competitionService).findInputById(3L);
    }

    @Test
    void adminResultFormExposesCorrectModelAttributes() throws Exception {
        InputCompetitionResultDTO inputCompetitionResultDTO = new InputCompetitionResultDTO(2, 1);
        when(competitionService.findById(3L)).thenReturn(competitionDto(3L));
        when(competitionService.findInputResultById(3L)).thenReturn(inputCompetitionResultDTO);

        mockMvc.perform(get("/competition/3/result").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/result"))
                .andExpect(model().attribute("inputCompetitionResultDTO", inputCompetitionResultDTO))
                .andExpect(model().attributeExists("competition"));

        verify(competitionService).findById(3L);
        verify(competitionService).findInputResultById(3L);
    }

    @Test
    void validAdminAddSubmissionCallsServiceRedirectsToHomeAndFlashesMessage() throws Exception {
        InputCompetitionDTO newCompetition = inputCompetitionDTO(null);
        when(competitionService.add(newCompetition)).thenReturn(4L);

        mockMvc.perform(post("/competition/add")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf())
                .flashAttr("inputCompetitionDTO", newCompetition))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"))
                .andExpect(flash().attribute("successMessage", "Competition 4 saved successfully"));

        verify(competitionService).add(newCompetition);
    }

    @Test
    void validAdminEditSubmissionCallsServiceRedirectsToCompetitionAndFlashesMessage() throws Exception {
        InputCompetitionDTO existingCompetition = inputCompetitionDTO(3L);
        when(competitionService.update(existingCompetition)).thenReturn(3L);

        mockMvc.perform(post("/competition/edit/3")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf())
                .flashAttr("inputCompetitionDTO", existingCompetition))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/3"))
                .andExpect(flash().attribute("successMessage", "Competition 3 updated successfully"));

        verify(competitionService).update(existingCompetition);
    }

    @Test
    void competitionDetailRendersEditFlashMessage() throws Exception {
        CompetitionDTO competition = competitionDto(3L);
        when(competitionService.findById(3L)).thenReturn(competition);

        mockMvc.perform(get("/competition/3")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .flashAttr("successMessage", "Competition 3 updated successfully"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Competition 3 updated successfully")));

        verify(competitionService).findById(3L);
    }

    @Test
    void validAdminResultSubmissionCallsServiceAndRedirectsToCompetition() throws Exception {
        InputCompetitionResultDTO resultDto = new InputCompetitionResultDTO(2, 1);
        when(competitionService.updateOfficialResult(3L, resultDto)).thenReturn(3L);

        mockMvc.perform(post("/competition/3/result")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf())
                        .flashAttr("inputCompetitionResultDTO", resultDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/3"));

        verify(competitionService).updateOfficialResult(3L, resultDto);
    }

    @Test
    void invalidAdminAddSubmissionReturnsFieldErrorsAndDoesNotWrite() throws Exception {
        mockMvc.perform(post("/competition/add")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf())
                        .flashAttr("inputCompetitionDTO", new InputCompetitionDTO()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/add"))
                .andExpect(model().attributeExists("countries", "stadiums"))
                .andExpect(model().attributeHasFieldErrors(
                        "inputCompetitionDTO", "teamA", "teamB", "stadium", "stadiumCode", "checksum", "date", "time"));

        verify(competitionService, never()).add(any(InputCompetitionDTO.class));
    }

    @Test
    void invalidAdminEditSubmissionReturnsFieldErrorsAndDoesNotWrite() throws Exception {
        mockMvc.perform(post("/competition/edit/3")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf())
                        .flashAttr("inputCompetitionDTO", new InputCompetitionDTO()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/edit"))
                .andExpect(model().attributeExists("countries", "stadiums"))
                .andExpect(model().attributeHasFieldErrors(
                        "inputCompetitionDTO", "teamA", "teamB", "stadium", "stadiumCode", "checksum", "date", "time"));

        verify(competitionService, never()).update(any(InputCompetitionDTO.class));
    }

    @Test
    void invalidAdminResultSubmissionReturnsFieldErrorsAndDoesNotWrite() throws Exception {
        when(competitionService.findById(3L)).thenReturn(competitionDto(3L));

        mockMvc.perform(post("/competition/3/result")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf())
                        .flashAttr("inputCompetitionResultDTO", new InputCompetitionResultDTO()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/result"))
                .andExpect(model().attributeExists("competition"))
                .andExpect(model().attributeHasFieldErrors("inputCompetitionResultDTO", "scoreA", "scoreB"));

        verify(competitionService, never()).updateOfficialResult(any(), any(InputCompetitionResultDTO.class));
    }

    @Test
    void adminEditFormReturns404ForUnknownCompetitionId() throws Exception {
        when(competitionService.findInputById(999999L)).thenThrow(new CompetitionNotFound(999999L));

        mockMvc.perform(get("/competition/edit/999999").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));

        verify(competitionService).findInputById(999999L);
    }

    @Test
    void adminResultFormReturns404ForTypeMismatchId() throws Exception {
        mockMvc.perform(get("/competition/not-a-number/result").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));
    }

}
