package com.github.schedule.workmonth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.schedule.workmonth.dto.request.workday.WorkDayRequestDto;
import com.github.schedule.workmonth.dto.request.workday.WorkDaysChangeDto;
import com.github.schedule.workmonth.dto.request.workday.WorkHourRequestDto;
import com.github.schedule.workmonth.dto.request.workmonth.WorkMonthCreateDto;
import com.github.schedule.workmonth.exception.WorkMonthExistsException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(profiles = "test")
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

    @Test
    @DisplayName("Should Change WorkDays Properly")
    void shouldChangeWorkDaysProperly() throws Exception {

        final YearMonth yearMonth = YearMonth.now();
        final String body = objectMapper.writeValueAsString(new WorkMonthCreateDto(UUID.randomUUID().toString(), yearMonth.getYear(), yearMonth.getMonthValue()));

        final MvcResult result = mockMvc.perform(post("/api/workmonth")
                                        .content(body).contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().is(201))
                                        .andReturn();

        final String id = result.getResponse().getRedirectedUrl().split("/")[3];

        final WorkDayRequestDto workDayRequestDto = new WorkDayRequestDto(LocalDate.now(), new WorkHourRequestDto(15, 15), new WorkHourRequestDto(15, 15), false);
        final String workDayBody = objectMapper.writeValueAsString(new WorkDaysChangeDto(Set.of(workDayRequestDto)));

        mockMvc.perform(put("/api/workmonth/%s".formatted(id))
                .content(workDayBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(200));

    }

    @Test
    @DisplayName("Should Not Change WorkDays When WorkMonth Not Exists")
    void shouldNotChangeWorkDaysWhenWorkMonthNotExists() throws Exception {

        final WorkDayRequestDto workDayRequestDto = new WorkDayRequestDto(LocalDate.now(), new WorkHourRequestDto(15, 15), new WorkHourRequestDto(15, 15), false);
        final String workDayBody = objectMapper.writeValueAsString(new WorkDaysChangeDto(Set.of(workDayRequestDto)));

        mockMvc.perform(put("/api/workmonth/%s".formatted(UUID.randomUUID()))
                .content(workDayBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(204));

    }

    @Test
    @DisplayName("Should Throw Exception When Id Is Not Valid")
    void shouldThrowExceptionWhenIdIsNotValid() throws Exception {

        final WorkDayRequestDto workDayRequestDto = new WorkDayRequestDto(LocalDate.now(), new WorkHourRequestDto(15, 15), new WorkHourRequestDto(15, 15), false);
        final String workDayBody = objectMapper.writeValueAsString(new WorkDaysChangeDto(Set.of(workDayRequestDto)));

        mockMvc.perform(put("/api/workmonth/%s".formatted("test"))
                .content(workDayBody).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));

    }
}
