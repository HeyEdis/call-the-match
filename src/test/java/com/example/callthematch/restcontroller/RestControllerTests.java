package com.example.callthematch.restcontroller;

import com.example.callthematch.controller.CompetitionRestController;
import com.example.callthematch.controller.StadiumRestController;
import com.example.callthematch.dto.response.MatchRestDTO;
import com.example.callthematch.dto.response.StadiumCapacityDTO;
import com.example.callthematch.exception.StadiumNotFound;
import com.example.callthematch.formatter.DateFormatter;
import com.example.callthematch.service.CompetitionService;
import com.example.callthematch.service.StadiumService;
import com.example.callthematch.advice.CompetitionValidatorAdvice;
import com.example.callthematch.advice.GlobalExceptionAdvice;
import com.example.callthematch.advice.RestErrorAdvice;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {CompetitionRestController.class, StadiumRestController.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {GlobalExceptionAdvice.class, CompetitionValidatorAdvice.class}))
@Import(RestErrorAdvice.class)
class RestControllerTests {

    private static final LocalDate MATCH_DATE = LocalDate.of(2026, 5, 20);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompetitionService competitionService;

    @MockitoBean
    private StadiumService stadiumService;

    @MockitoBean
    private DateFormatter dateFormatter;

    @Test
    void testGetMatchesByDate_returnsMatches() throws Exception {
        Mockito.when(dateFormatter.parse(eq("2026-05-20"), any(Locale.class))).thenReturn(MATCH_DATE);
        Mockito.when(competitionService.findRestMatchesByDate(MATCH_DATE)).thenReturn(List.of(
                new MatchRestDTO(
                        1L,
                        "Belgium",
                        "Canada",
                        MATCH_DATE,
                        LocalTime.of(18, 0),
                        "BC Place",
                        2,
                        1)
        ));

        mockMvc.perform(get("/api/matches").param("date", "2026-05-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].teamAName").value("Belgium"))
                .andExpect(jsonPath("$[0].teamBName").value("Canada"))
                .andExpect(jsonPath("$[0].date").value("2026-05-20"))
                .andExpect(jsonPath("$[0].time").value("18:00:00"))
                .andExpect(jsonPath("$[0].stadiumName").value("BC Place"))
                .andExpect(jsonPath("$[0].scoreA").value(2))
                .andExpect(jsonPath("$[0].scoreB").value(1));

        Mockito.verify(dateFormatter).parse(eq("2026-05-20"), any(Locale.class));
        Mockito.verify(competitionService).findRestMatchesByDate(MATCH_DATE);
    }

    @Test
    void testGetMatchesByDate_returnsEmptyList() throws Exception {
        Mockito.when(dateFormatter.parse(eq("2026-05-20"), any(Locale.class))).thenReturn(MATCH_DATE);
        Mockito.when(competitionService.findRestMatchesByDate(MATCH_DATE)).thenReturn(List.of());

        mockMvc.perform(get("/api/matches").param("date", "2026-05-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        Mockito.verify(dateFormatter).parse(eq("2026-05-20"), any(Locale.class));
        Mockito.verify(competitionService).findRestMatchesByDate(MATCH_DATE);
    }

    @Test
    void testGetMatchesByDate_invalidDateReturnsBadRequest() throws Exception {
        Mockito.when(dateFormatter.parse(eq("invalid"), any(Locale.class)))
                .thenThrow(new DateTimeParseException("Invalid date", "invalid", 0));

        mockMvc.perform(get("/api/matches").param("date", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        Mockito.verify(dateFormatter).parse(eq("invalid"), any(Locale.class));
    }

    @Test
    void testGetStadiumCapacity_returnsCapacity() throws Exception {
        Mockito.when(stadiumService.findCapacityById(1L))
                .thenReturn(new StadiumCapacityDTO(1L, "BC Place", 54500));

        mockMvc.perform(get("/api/stadiums/1/capacity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("BC Place"))
                .andExpect(jsonPath("$.capacity").value(54500));

        Mockito.verify(stadiumService).findCapacityById(1L);
    }

    @Test
    void testGetStadiumCapacity_notFound() throws Exception {
        Mockito.when(stadiumService.findCapacityById(999L)).thenThrow(new StadiumNotFound(999L));

        mockMvc.perform(get("/api/stadiums/999/capacity"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Stadium not found"))
                .andExpect(jsonPath("$.timestamp").exists());

        Mockito.verify(stadiumService).findCapacityById(999L);
    }
}

