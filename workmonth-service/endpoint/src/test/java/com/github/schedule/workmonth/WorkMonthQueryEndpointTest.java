package com.github.schedule.workmonth;

import com.github.schedule.workmonth.dto.WorkDayQueryDto;
import com.github.schedule.workmonth.dto.WorkHourQueryDto;
import com.github.schedule.workmonth.dto.WorkMonthQueryDto;
import com.github.schedule.workmonth.dto.converter.WorkMonthResponseConverter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Profile("dev")
@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { WorkMonthQueryEndpoint.class, WorkMonthResponseConverter.class, MappingJackson2HttpMessageConverter.class })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkMonthQueryEndpointTest extends BaseEndpointTest {

    @MockBean
    private WorkMonthQueryRepository workMonthQueryRepository;

    @Autowired
    private WorkMonthQueryEndpoint workMonthQueryEndpoint;

    /*
        Data
     */

    private final WorkHourQueryDto WORK_HOUR_QUERY_DTO = new WorkHourQueryDto(LocalTime.now().getHour(), LocalTime.now().getMinute());

    private final WorkDayQueryDto WORK_DAY_QUERY_DTO = new WorkDayQueryDto(LocalDate.now(), WORK_HOUR_QUERY_DTO, WORK_HOUR_QUERY_DTO, false);

    private final WorkMonthQueryDto WORK_MONTH_QUERY_DTO = new WorkMonthQueryDto(UUID.randomUUID(), UUID.randomUUID(), YearMonth.now(), WORK_HOUR_QUERY_DTO, Set.of(WORK_DAY_QUERY_DTO));

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(this.workMonthQueryEndpoint)
                .setMessageConverters(this.mappingJackson2HttpMessageConverter)
                .build();
    }

    @Test
    @DisplayName("Shoudl Find All WorkMonths")
    void shouldFindAllWorkMonths() throws Exception {
        when(workMonthQueryRepository.findAll()).thenReturn(List.of(WORK_MONTH_QUERY_DTO));

        this.mockMvc.perform(get("/api/workmonth").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                                             .andDo(print())
                                             .andExpect(status().isOk())
                                             .andReturn();

        //final String content = result.getResponse().getContentAsString();
    }

}
