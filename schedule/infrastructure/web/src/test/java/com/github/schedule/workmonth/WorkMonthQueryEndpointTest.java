package com.github.schedule.workmonth;

import com.github.schedule.workmonth.dto.WorkDayQueryDto;
import com.github.schedule.workmonth.dto.WorkHourQueryDto;
import com.github.schedule.workmonth.dto.WorkMonthQueryDto;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(profiles = "test")
public class WorkMonthQueryEndpointTest extends BaseEndpointTest {

    @MockBean
    private WorkMonthQueryRepository jpaWorkMonthQuery;

    /*
        Data
     */

    private final WorkHourQueryDto WORK_HOUR_QUERY_DTO = new WorkHourQueryDto(LocalTime.now().getHour(), LocalTime.now().getMinute());

    private final WorkDayQueryDto WORK_DAY_QUERY_DTO = new WorkDayQueryDto(LocalDate.now(), WORK_HOUR_QUERY_DTO, WORK_HOUR_QUERY_DTO, false);

    private final WorkMonthQueryDto WORK_MONTH_QUERY_DTO = new WorkMonthQueryDto(UUID.randomUUID(), UUID.randomUUID(), YearMonth.now(), WORK_HOUR_QUERY_DTO, Set.of(WORK_DAY_QUERY_DTO));

    @Test
    @DisplayName("Endpoint Should Find All WorkMonths")
    void shouldFindAllWorkMonths() throws Exception {
        when(jpaWorkMonthQuery.findAll()).thenReturn(List.of(WORK_MONTH_QUERY_DTO));

        performComparingWorkMonthResultCollection(this.mockMvc.perform(get("/api/workmonth").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)));
    }

    @Test
    @DisplayName("Endpoint Should Not Find Any WorkMonth")
    void shouldNotFindAnyWorkMonth() throws Exception {
        this.mockMvc.perform(get("/api/workmonth").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is(200)).andExpect(jsonPath("$", is(new ArrayList<>())));
    }

    @Test
    @DisplayName("Endpoint Should Find WorkMonths By Date")
    void shouldFindWorkMonthsByDate() throws Exception {
        when(jpaWorkMonthQuery.findAllByDate(any(YearMonth.class))).thenAnswer((arg) -> {
            final YearMonth yearMonth = arg.getArgument(0);
            return yearMonth.equals(WORK_MONTH_QUERY_DTO.date()) ? List.of(WORK_MONTH_QUERY_DTO) : new ArrayList<>();
        });

        performComparingWorkMonthResultCollection(
                this.mockMvc.perform(get("/api/workmonth?year=%s&month=%s".formatted(WORK_MONTH_QUERY_DTO.date().getYear(), WORK_MONTH_QUERY_DTO.date().getMonthValue())))
        );
    }

    @Test
    @DisplayName("Endpoint Should Not Find WorkMonths By Date")
    void shouldNotFindWorkMonthsByDate() throws Exception {
        when(jpaWorkMonthQuery.findAllByDate(any(YearMonth.class))).thenReturn(new ArrayList<>());
        this.mockMvc.perform(get("/api/workmonth?year=%s&month=%s".formatted(WORK_MONTH_QUERY_DTO.date().getYear(), WORK_MONTH_QUERY_DTO.date().getMonthValue())))
                    .andExpect(status().is(200)).andExpect(jsonPath("$", is(new ArrayList<>())));
    }

    @Test
    @DisplayName("Endpoint Should Find All WorkMonths Without Date When Only Year Specified")
    void shouldNotFindWorkMonthsByDateWhenOnlyYearSpecified() throws Exception {
        when(jpaWorkMonthQuery.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/api/workmonth?year=%s&month=%s".formatted(WORK_MONTH_QUERY_DTO.date().getYear(), "")))
                    .andExpect(status().is(200)).andExpect(jsonPath("$", is(new ArrayList<>())));

        verify(jpaWorkMonthQuery, times(1)).findAll();
        verify(jpaWorkMonthQuery, times(0)).findAllByDate(any());
    }

    @Test
    @DisplayName("Endpoint Should Find All WorkMonths Without Date When Only Month Specified")
    void shouldNotFindWorkMonthsByDateWhenOnlyMonthSpecified() throws Exception {
        when(jpaWorkMonthQuery.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/api/workmonth?year=%s&month=%s".formatted("", WORK_MONTH_QUERY_DTO.date().getMonthValue())))
                    .andExpect(status().is(200)).andExpect(jsonPath("$", is(new ArrayList<>())));

        verify(jpaWorkMonthQuery, times(1)).findAll();
        verify(jpaWorkMonthQuery, times(0)).findAllByDate(any());
    }

    @Test
    @DisplayName("Endpoint Should Not Find WorkMonths When Year Is Invalid")
    void shouldNotFindWorkMonthsByDateWhenYearIsInvalid() throws Exception {

        this.mockMvc.perform(get("/api/workmonth?year=%s&month=%s".formatted("test", WORK_MONTH_QUERY_DTO.date().getMonthValue())))
                    .andExpect(status().is(400));
    }

    @Test
    @DisplayName("Endpoint Should Not Find WorkMonths When Month Is Invalid")
    void shouldNotFindWorkMonthsByDateWhenMonthIsInvalid() throws Exception {

        this.mockMvc.perform(get("/api/workmonth?year=%s&month=%s".formatted(WORK_MONTH_QUERY_DTO.date().getYear(), "test")))
                    .andExpect(status().is(400)).andDo(print());
    }

    @Test
    @DisplayName("Endpoint Should Find WorkMonth By UserId")
    void shouldFindWorkMonthByUserId() throws Exception {
        when(jpaWorkMonthQuery.findAllByUserId(any())).thenAnswer((arg) -> {
            final UUID userId = arg.getArgument(0);
            return userId.equals(WORK_MONTH_QUERY_DTO.userId()) ? List.of(WORK_MONTH_QUERY_DTO) : new ArrayList<>();
        });

        performComparingWorkMonthResultCollection(
                this.mockMvc.perform(get("/api/workmonth/user/%s".formatted(WORK_MONTH_QUERY_DTO.userId().toString())))
        );
    }

    @Test
    @DisplayName("Endpoint Should Not Find WorkMonth By UserId When Invalid UUID Format")
    void shouldNotFindWorkMonthByUserIdWhenInvalidFormat() throws Exception {
        this.mockMvc.perform(get("/api/workmonth/user/%s".formatted("test")))
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("Endpoint Should Not Find WorkMonth By UserId When Empty")
    void shouldNotFindWorkMonthByUserIdWhenEmpty() throws Exception {
        this.mockMvc.perform(get("/api/workmonth/user/%s".formatted("")))
                .andExpect(status().is(400));
    }

    @Test
    @DisplayName("Endpoint Should Not Find WorkMonth By UserId When Not Exists")
    void shouldNotFindWorkMonthByUserIdWhenNotExists() throws Exception {
        this.mockMvc.perform(get("/api/workmonth/user/%s".formatted(UUID.randomUUID().toString())))
                    .andExpect(status().is(200)).andExpect(jsonPath("$", is(new ArrayList<>())));
    }

    @Test
    @DisplayName("Endpoint Should Find WorkMonth By Id")
    void shouldFindWorkMonthById() throws Exception {
        when(jpaWorkMonthQuery.findById(any())).thenAnswer((arg) -> {
            final UUID userId = arg.getArgument(0);
            return userId.equals(WORK_MONTH_QUERY_DTO.id()) ? Optional.of(WORK_MONTH_QUERY_DTO) : Optional.empty();
        });

        performComparingWorkMonthResult(
                this.mockMvc.perform(get("/api/workmonth/%s".formatted(WORK_MONTH_QUERY_DTO.id().toString())))
        );
    }

    @Test
    @DisplayName("Endpoint Should Not Find WorkMonth By Id When Invalid UUID Format")
    void shouldNotFindWorkMonthByIdWhenInvalidFormat() throws Exception {
        this.mockMvc.perform(get("/api/workmonth/%s".formatted("test")))
                .andExpect(status().is(400)).andDo(print());
    }

    @Test
    @DisplayName("Endpoint Should Not Find WorkMonth By Id When Not Exists")
    void shouldNotFindWorkMonthByIdWhenNotExists() throws Exception {
        this.mockMvc.perform(get("/api/workmonth/%s".formatted(UUID.randomUUID().toString())))
                .andExpect(status().is(204));
    }

    private void performComparingWorkMonthResultCollection(ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(WORK_MONTH_QUERY_DTO.id().toString())))
                .andExpect(jsonPath("$[0].userId", is(WORK_MONTH_QUERY_DTO.userId().toString())))
                .andExpect(jsonPath("$[0].yearMonth", is(WORK_MONTH_QUERY_DTO.date().toString())))
                .andExpect(jsonPath("$[0].totalHours.hours", is(WORK_MONTH_QUERY_DTO.totalHours().hour())))
                .andExpect(jsonPath("$[0].totalHours.minutes", is(WORK_MONTH_QUERY_DTO.totalHours().minutes())))
                .andExpect(jsonPath("$[0].workDays", hasSize(1)))
                .andExpect(jsonPath("$[0].workDays[0].date", is(WORK_DAY_QUERY_DTO.date().toString())))
                .andExpect(jsonPath("$[0].workDays[0].startingHour.hour", is(WORK_DAY_QUERY_DTO.startingHour().hour())))
                .andExpect(jsonPath("$[0].workDays[0].startingHour.minute", is(WORK_DAY_QUERY_DTO.startingHour().minutes())))
                .andExpect(jsonPath("$[0].workDays[0].endingHour.hour", is(WORK_DAY_QUERY_DTO.endingHour().hour())))
                .andExpect(jsonPath("$[0].workDays[0].endingHour.minute", is(WORK_DAY_QUERY_DTO.endingHour().minutes())))
                .andExpect(jsonPath("$[0].workDays[0].leave", is(WORK_DAY_QUERY_DTO.isLeave())));
    }

    private void performComparingWorkMonthResult(ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(WORK_MONTH_QUERY_DTO.id().toString())))
                .andExpect(jsonPath("$.userId", is(WORK_MONTH_QUERY_DTO.userId().toString())))
                .andExpect(jsonPath("$.yearMonth", is(WORK_MONTH_QUERY_DTO.date().toString())))
                .andExpect(jsonPath("$.totalHours.hours", is(WORK_MONTH_QUERY_DTO.totalHours().hour())))
                .andExpect(jsonPath("$.totalHours.minutes", is(WORK_MONTH_QUERY_DTO.totalHours().minutes())))
                .andExpect(jsonPath("$.workDays", hasSize(1)))
                .andExpect(jsonPath("$.workDays[0].date", is(WORK_DAY_QUERY_DTO.date().toString())))
                .andExpect(jsonPath("$.workDays[0].startingHour.hour", is(WORK_DAY_QUERY_DTO.startingHour().hour())))
                .andExpect(jsonPath("$.workDays[0].startingHour.minute", is(WORK_DAY_QUERY_DTO.startingHour().minutes())))
                .andExpect(jsonPath("$.workDays[0].endingHour.hour", is(WORK_DAY_QUERY_DTO.endingHour().hour())))
                .andExpect(jsonPath("$.workDays[0].endingHour.minute", is(WORK_DAY_QUERY_DTO.endingHour().minutes())))
                .andExpect(jsonPath("$.workDays[0].leave", is(WORK_DAY_QUERY_DTO.isLeave())));
    }

}
