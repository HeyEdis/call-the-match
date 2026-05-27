package com.example.callthematch.restcontroller;

import com.example.callthematch.advice.CompetitionValidatorAdvice;
import com.example.callthematch.advice.GlobalExceptionAdvice;
import com.example.callthematch.advice.RestErrorAdvice;
import com.example.callthematch.advice.TeamValidatorAdvice;
import com.example.callthematch.controller.StadiumRestController;
import com.example.callthematch.dto.response.StadiumCapacityDTO;
import com.example.callthematch.exception.StadiumNotFound;
import com.example.callthematch.service.StadiumService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = StadiumRestController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        GlobalExceptionAdvice.class,
                        CompetitionValidatorAdvice.class,
                        TeamValidatorAdvice.class
                }))
@Import(RestErrorAdvice.class)
class StadiumRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StadiumService stadiumService;

    @Test
    void getStadiumCapacityReturnsCapacityForValidId() throws Exception {
        Mockito.when(stadiumService.findCapacityById(1L))
                .thenReturn(new StadiumCapacityDTO("BC Place", 54500));

        mockMvc.perform(get("/api/stadiums/1/capacity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("BC Place"))
                .andExpect(jsonPath("$.capacity").value(54500));

        Mockito.verify(stadiumService).findCapacityById(1L);
    }

    @Test
    void getStadiumCapacityReturnsNotFoundForUnknownId() throws Exception {
        Mockito.when(stadiumService.findCapacityById(999L)).thenThrow(new StadiumNotFound(999L));

        mockMvc.perform(get("/api/stadiums/999/capacity"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Stadium not found"))
                .andExpect(jsonPath("$.timestamp").exists());

        Mockito.verify(stadiumService).findCapacityById(999L);
    }
}
