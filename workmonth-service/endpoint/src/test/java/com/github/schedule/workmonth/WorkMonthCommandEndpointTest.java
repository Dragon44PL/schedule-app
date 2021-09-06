package com.github.schedule.workmonth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.schedule.workmonth.dto.request.workmonth.WorkMonthCreateDto;
import com.github.schedule.workmonth.exception.WorkMonthExistsException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.YearMonth;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WorkMonthCommandEndpointTest extends BaseEndpointTest {

    @Autowired
    private ObjectMapper objectMapper;

    /*
        WorkMonthFacade Exceptions
     */

    private final Class<WorkMonthExistsException> WORK_MONTH_EXISTS_EXCEPTION = WorkMonthExistsException.class;

    @Test
    @DisplayName("Should Create WorkMonth Properly")
    void shouldCreateWorkMonthProperly() throws Exception {

        final YearMonth yearMonth = YearMonth.now();
        final String body = objectMapper.writeValueAsString(new WorkMonthCreateDto(UUID.randomUUID().toString(), yearMonth.getYear(), yearMonth.getMonthValue()));

        mockMvc.perform(post("/api/workmonth")
                .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201));
    }

    @Test
    @DisplayName("Should Throw 'WorkMonthAlreadyExistsException' When WorkMonth Already Exists")
    void shouldThrowExceptionWhenAnotherWorkMonthAlreadyExists() throws Exception {

        final YearMonth yearMonth = YearMonth.now();
        final String body = objectMapper.writeValueAsString(new WorkMonthCreateDto(UUID.randomUUID().toString(), yearMonth.getYear(), yearMonth.getMonthValue()));

        mockMvc.perform(post("/api/workmonth")
                .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201));

        mockMvc.perform(post("/api/workmonth")
                .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(result -> assertEquals(WORK_MONTH_EXISTS_EXCEPTION, result.getResolvedException().getClass()));
    }

    @Test
    @DisplayName("Should Status Be 400 When UserId Is Not Valid")
    void shouldStatusBe400WhenUserIdIsNotValid() throws Exception {

        final YearMonth yearMonth = YearMonth.now();
        final String body = objectMapper.writeValueAsString(new WorkMonthCreateDto("test", yearMonth.getYear(), yearMonth.getMonthValue()));

        mockMvc.perform(post("/api/workmonth")
                .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }
}
