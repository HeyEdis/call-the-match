package com.example.callthematch.controller;

import com.example.callthematch.advice.CompetitionValidatorAdvice;
import com.example.callthematch.config.SecurityConfig;
import com.example.callthematch.dto.request.InputCompetitionDTO;
import com.example.callthematch.dto.request.InputCompetitionResultDTO;
import com.example.callthematch.dto.response.CompetitionDTO;
import com.example.callthematch.dto.response.CountryDTO;
import com.example.callthematch.dto.response.StadiumDTO;
import com.example.callthematch.exception.CompetitionNotFound;
import com.example.callthematch.model.Country;
import com.example.callthematch.model.Location;
import com.example.callthematch.model.Stadium;
import com.example.callthematch.service.CompetitionService;
import com.example.callthematch.service.CountryService;
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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CompetitionController.class)
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
    private CompetitionValidator competitionValidator;

    @BeforeEach
    void setUp() {
        when(countryService.getAllCountries()).thenReturn(countryDtos());
        when(stadiumService.getAllStadiums()).thenReturn(stadiumDtos());
        when(competitionValidator.supports(InputCompetitionDTO.class)).thenReturn(true);
    }

    @Test
    void publicCompetitionListAndDetailAreAccessibleToGuest() throws Exception {
        List<CompetitionDTO> competitions = List.of(competitionDto(1L));
        when(competitionService.getAllCompetitions()).thenReturn(competitions);
        when(competitionService.findById(1L)).thenReturn(competitionDto(1L));

        mockMvc.perform(get("/competition"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/list"))
                .andExpect(model().attribute("competitionList", competitions));

        mockMvc.perform(get("/competition/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/show"))
                .andExpect(model().attributeExists("competition"));

        verify(competitionService).getAllCompetitions();
        verify(competitionService).findById(1L);
    }

    @Test
    void userIsForbiddenOnAdminCompetitionAddRoute() throws Exception {
        mockMvc.perform(get("/competition/add")
                        .with(user("user1@example.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminAddEditAndResultFormsExposeSchoolMvcModels() throws Exception {
        InputCompetitionDTO inputCompetitionDto = inputCompetitionDto(3L);
        InputCompetitionResultDTO inputCompetitionResultDto = new InputCompetitionResultDTO(2, 1);
        when(competitionService.findInputById(3L)).thenReturn(inputCompetitionDto);
        when(competitionService.findById(3L)).thenReturn(competitionDto(3L));
        when(competitionService.findInputResultById(3L)).thenReturn(inputCompetitionResultDto);

        mockMvc.perform(get("/competition/add").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/add"))
                .andExpect(model().attributeExists("countries", "stadiums", "inputCompetitionDto"))
                .andExpect(content().string(containsString("data-code=\"1001\"")))
                .andExpect(content().string(containsString("readonly")))
                .andExpect(content().string(containsString("/js/matchStadiumChecksum.js")))
                .andExpect(content().string(containsString("New York - MetLife Stadium")))
                .andExpect(content().string(not(containsString("MetLife Stadium - New York - 1001"))));

        mockMvc.perform(get("/competition/edit/3").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/edit"))
                .andExpect(model().attribute("inputCompetitionDto", inputCompetitionDto))
                .andExpect(model().attributeExists("countries", "stadiums"));

        mockMvc.perform(get("/competition/3/result").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/result"))
                .andExpect(model().attribute("inputCompetitionResultDto", inputCompetitionResultDto))
                .andExpect(model().attributeExists("competition"));

        verify(stadiumService, org.mockito.Mockito.times(2)).getAllStadiums();
        verify(countryService, org.mockito.Mockito.times(2)).getAllCountries();
        verify(competitionService).findInputById(3L);
        verify(competitionService).findById(3L);
        verify(competitionService).findInputResultById(3L);
    }

    @Test
    void validAdminAddEditAndResultSubmissionsCallServicesAndRedirect() throws Exception {
        InputCompetitionDTO newCompetition = inputCompetitionDto(null);
        InputCompetitionDTO existingCompetition = inputCompetitionDto(3L);
        InputCompetitionResultDTO resultDto = new InputCompetitionResultDTO(2, 1);
        when(competitionService.add(newCompetition)).thenReturn(4L);
        when(competitionService.update(existingCompetition)).thenReturn(3L);
        when(competitionService.updateResult(3L, resultDto)).thenReturn(3L);

        mockMvc.perform(post("/competition/add")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf())
                        .flashAttr("inputCompetitionDto", newCompetition))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        mockMvc.perform(post("/competition/edit/3")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf())
                        .flashAttr("inputCompetitionDto", existingCompetition))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/3"));

        mockMvc.perform(post("/competition/3/result")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf())
                        .flashAttr("inputCompetitionResultDto", resultDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/3"));

        verify(competitionService).add(newCompetition);
        verify(competitionService).update(existingCompetition);
        verify(competitionService).updateResult(3L, resultDto);
    }

    @Test
    void invalidAdminAddEditAndResultSubmissionsReturnFieldErrorsAndDoNotWrite() throws Exception {
        when(competitionService.findById(3L)).thenReturn(competitionDto(3L));

        mockMvc.perform(post("/competition/add")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf())
                        .flashAttr("inputCompetitionDto", new InputCompetitionDTO()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/add"))
                .andExpect(model().attributeExists("countries", "stadiums"))
                .andExpect(model().attributeHasFieldErrors(
                        "inputCompetitionDto", "teamA", "teamB", "stadium", "stadiumCode", "checksum", "date", "time"));

        mockMvc.perform(post("/competition/edit/3")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf())
                        .flashAttr("inputCompetitionDto", new InputCompetitionDTO()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/edit"))
                .andExpect(model().attributeExists("countries", "stadiums"))
                .andExpect(model().attributeHasFieldErrors(
                        "inputCompetitionDto", "teamA", "teamB", "stadium", "stadiumCode", "checksum", "date", "time"));

        mockMvc.perform(post("/competition/3/result")
                        .with(user("admin@example.com").roles("ADMIN"))
                        .with(csrf())
                        .flashAttr("inputCompetitionResultDto", new InputCompetitionResultDTO()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/result"))
                .andExpect(model().attributeExists("competition"))
                .andExpect(model().attributeHasFieldErrors("inputCompetitionResultDto", "scoreA", "scoreB"));

        verify(competitionService, never()).add(any(InputCompetitionDTO.class));
        verify(competitionService, never()).update(any(InputCompetitionDTO.class));
        verify(competitionService, never()).updateResult(any(), any(InputCompetitionResultDTO.class));
    }

    @Test
    void competitionAdminFormsKeepFriendlyNotFoundAndTypeMismatchErrors() throws Exception {
        when(competitionService.findInputById(999999L)).thenThrow(new CompetitionNotFound(999999L));

        mockMvc.perform(get("/competition/edit/999999").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));

        mockMvc.perform(get("/competition/not-a-number/result").with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));

        verify(competitionService).findInputById(999999L);
    }

    private InputCompetitionDTO inputCompetitionDto(Long id) {
        return new InputCompetitionDTO(
                id,
                1L,
                2L,
                1L,
                1001,
                31,
                LocalDate.of(2026, 5, 20),
                LocalTime.of(18, 0));
    }

    private CompetitionDTO competitionDto(Long id) {
        return new CompetitionDTO(
                id,
                country(1L, "Belgium", 32),
                country(2L, "Canada", 1),
                stadium(),
                null,
                null,
                LocalDate.of(2026, 5, 20),
                LocalTime.of(18, 0));
    }

    private List<CountryDTO> countryDtos() {
        return List.of(
                new CountryDTO(1L, 32, "Belgium"),
                new CountryDTO(2L, 1, "Canada"));
    }

    private List<StadiumDTO> stadiumDtos() {
        return List.of(new StadiumDTO(1L, location(), "MetLife Stadium", 1001, 82500));
    }

    private Country country(Long id, String name, Integer landCode) {
        return Country.builder().id(id).name(name).landCode(landCode).build();
    }

    private Stadium stadium() {
        return Stadium.builder()
                .id(1L)
                .location(location())
                .name("MetLife Stadium")
                .code(1001)
                .capacity(82500)
                .build();
    }

    private Location location() {
        return Location.builder().id(1L).city("New York").build();
    }
}
